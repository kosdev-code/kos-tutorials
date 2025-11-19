/*------------------------------------------------------------*/
/* File : arduinoAdapter.c                                    */
/*------------------------------------------------------------*/
/* Author      : Samuel Burac                                */
/* Date        : 2025-01-27                                   */
/* Description : Arduino Adapter                              */
/*------------------------------------------------------------*/
/* (C) Copyright 2025, Kondra                                 */
/* All rights reserved.                                       */
/*------------------------------------------------------------*/
#include <ctype.h>
#include <fcntl.h>
#include <kos/board.h>
#include <kos/kos.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <termios.h>
#include <unistd.h>

/*------------------------------------------------------------*/
/* Define a few constants...                                  */
/*------------------------------------------------------------*/

// Blink protocol name
#define BLINK_NAME "arduino.iface"

// Revision number
#define REVISION 1

// Iface version
#define IFACE_VERSION 1

// Api numbers
#define API_ILLUMINATE_LED 0
#define API_BUTTON_PRESSED 1

/*------------------------------------------------------------*/
/* Global state...                                            */
/*------------------------------------------------------------*/

static blinkClient *client; // blink client
static int serialFd;        // file descriptor for serial port
static int verbose = 0;     // verbose flag
static pthread_t buttonListeningThread;
blinkIface *arduinoIface;

/*------------------------------------------------------------*/
/* Code...                                                    */
/*------------------------------------------------------------*/

/**
 * usage() : Print the usage of the tool.
 */
static int usage() {
  printf("Usage: arduinoAdapter [-h][-v][-c][-d dev] "
         "(rev=%d)\n",
         REVISION);
  printf("   -h : Display this usage information.\n");
  printf("   -v : Enable verbose output for debugging.\n");
  printf("   -c : Use console for output.\n");
  printf("   -d : Specify the serial device.\n");
  return -1;
}

/**
 * command handler, just passes user command onto the arduino
 */
static int sendLedCommand(blinkApiArg *arg) {
  // drain any input
  kosDrain(serialFd);

  int illuminate = decodeBool(&arg->dec);
  int charCount;

  if (illuminate) {
    // send the ILLUMINATE command to the arduino
    charCount = dprintf(serialFd, "ILLUMINATE");
  } else {
    charCount = dprintf(serialFd, "EXTINGUISH");
  }

  if (charCount < 0) {
    kosLog("failed to send message to arduino");
  }

  return encoderSize(&arg->enc);
}

/**
 * Get input from arduino and send blink event
 */
void *listenToArduino() {
  struct pollfd fds;
  char buf[256];
  int count;

  // wait for a response... should get the entire line at once
  fds.fd = serialFd;
  fds.events = POLLIN; // stands for poll input

  for (;;) {

    if (poll(&fds, 1, 500)) {

      // check for device node unplugged
      if ((fds.revents & POLLHUP) || (fds.revents & POLLERR)) {
        break;
      }

      // read the bytes
      count = read(serialFd, buf, sizeof(buf) - 1);

      if (count > 0) {

        buf[count] = '\0'; // add a null terminator

        // If recieved button message from arduino send
        if (strstr(buf, "BUTTON")) {
          blinkSendMsg(arduinoIface, API_BUTTON_PRESSED, NULL, 0);
        }

        if (verbose) { // if verbose display what the response is
          kosLog("Recieved button press");
        }
      }
    }
  }
  return NULL;
}

static void cleanup() {
  pthread_cancel(buttonListeningThread);
  pthread_join(buttonListeningThread, NULL);
}

/**
 * main() : Main program entry point.
 */
int main(int argc, char *argv[]) {
  struct boardIfaceData boardData; // board data for registering iface
  struct termios tio;
  char *dev = NULL;
  int console = 0;

  atexit(cleanup);

  // process options
  signed char c;
  while ((c = getopt(argc, argv, "hvc:d:")) != -1) {
    switch (c) {
    case 'h': //
      return usage();
    case 'v':
      verbose = 1;
      break;
    case 'c':
      console = 1;
      break;

    case 'd':
      dev = optarg; // specify the serial device
      break;
    case '?':
      return 1;
    }
  }

  // if console not set, log to syslog
  if (!console)
    kosLogToSyslog();

  // if no device was specified, exit
  if (dev == NULL) {
    kosLog("No serial device specified");
    return -1;
  }

  // open the serial port with read write permissions
  if ((serialFd = open(dev, O_RDWR | O_NOCTTY)) < 0) {
    kosLog("Failed to open serial device : %s", dev);
    return -1;
  }

  // configure the port
  tcgetattr(serialFd, &tio);
  tio.c_cflag = CS8 | CLOCAL | CREAD; //  Sets 8 bits per character | ignores modem control lines | enables reading from the port
  tio.c_oflag &= ~OPOST;  // Disables all output processing data is sent raw without translation
  tio.c_iflag = IGNBRK;   // Ignores break conditions on the line 
  tio.c_lflag = ICANON;   // input is only available when a complete line is recieved terminated by new line, EOF, or EOL
  cfsetispeed(&tio, B115200); // Set the input baud rate to 115200
  cfsetospeed(&tio, B115200); // Set the output baud rate to 115200
  tcsetattr(serialFd, TCSANOW, &tio); // apply configuration now
  tcflush(serialFd, TCIFLUSH); // Flush the input buffer

  // create a client to connect to a SockEndpoint running
  // in a JVM on the local machine... override via properties
  client = blinkCreate(BLINK_NAME, REVISION, NULL, NULL, 0, 0);

  // zero the memory in boardData
  memset(&boardData, 0, sizeof(boardData));

  // initialize the board data
  boardData.type = "arduino";

  // register the board iface
  blinkAddBoardIface(client, &boardData);

  // register handlers
  arduinoIface = client->coreIface;
  arduinoIface->version = IFACE_VERSION;

  // register the handler to API number and pass the user data
  blinkRegisterApi(arduinoIface, API_ILLUMINATE_LED, sendLedCommand, NULL);

  // listen for events from the arduino
  pthread_create(&buttonListeningThread, NULL, listenToArduino, NULL);

  // process connections indefinitely
  blinkDispatch(client);
}
