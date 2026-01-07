/*------------------------------------------------------------*/
/* File : exampleAdapter.c                                           */
/*------------------------------------------------------------*/
/* Author      : Samuel Burac                                 */
/* Date        : 2026-01-06                                   */
/* Description : An adapter to show off functionality            */
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
#include <time.h>
#include <unistd.h>

/*------------------------------------------------------------*/
/* Define a few constants...                                  */
/*------------------------------------------------------------*/

// Blink protocol name
#define BLINK_NAME "nasa.navigationModuleIface"

// Revision number
#define REVISION 1

// Java Api numbers
#define API_JAVA_SEND                  0 // java sends a command / event decode input
#define API_JAVA_SEND_AND_RECEIVE      1 // java sends a command / event and recieves a response encode input
#define API_JAVA_SEND_STRUCT           2 // cast recieved data to a struct

// Native Api numbers
#define API_NATIVE_SEND                0 // native sends a command / event to java

/*------------------------------------------------------------*/
/* Global state...                                            */
/*------------------------------------------------------------*/

static blinkClient *client;          // blink client
static int verbose = 0;              // verbose flag
static pthread_t programEventThread; // A thread so that this program will use
                                     // to send events to java
blinkIface *navigationIface;

/*------------------------------------------------------------*/
/* Code...                                                    */
/*------------------------------------------------------------*/

// A struct that java will pass to the adapter
typedef struct NavigationModuleDestination {
  // coordinates of destination
  double xCoordinate;
  double yCoordinate;
  double zCoordinate;
  // name of destination
  char name[100];
} NavigationModuleDestination;

/**
 * usage() : Print the usage of the tool.
 */
static int usage() {
  printf("Usage: adapter [-h][-v][-c]"
         "(rev=%d)\n",
         REVISION);
  printf("   -h : Display this usage information.\n");
  printf("   -v : Enable verbose output for debugging.\n");
  printf("   -c : Use console for output.\n");
  return -1;
}

/**
 * This function simulates a fire and forget api
 * This funcion will perfrom some action and doesn't
 * need to give a response back to java
 */
static int processJavaSendAPI(blinkApiArg *arg) {

  // Get data from Java
  int isFullReset = decodeBool(&arg->dec);

  if (isFullReset) {
    // perform a full reset of the Navigation Module
  } else {
    // perform a soft reset of the Navigation Module
  }

  kosLog("Received message from Java performing full reset: %s ",
         isFullReset ? "true" : "false");

  // Return size of the response since this API doesn't
  // expect a response we can safely return 0
  return 0;
}

/**
 *  This API behaves like a GET as java is requesting
 *  data and this function will return the requested data
 */
static int processJavaSendAndRecvAPI(blinkApiArg *arg) {

  // encode data and return it
  // encode x coordinate
  double xCoord = drand48() * 1000;
  encodeDouble(&arg->enc, xCoord);

  // encode y coordinate
  double yCoord = drand48() * 1000;
  encodeDouble(&arg->enc, yCoord);

  // encode z coordinate
  double zCoord = drand48() * 1000;
  encodeDouble(&arg->enc, zCoord);

  kosLog("Were currently at (x,y,z) (%f,%f,%f)", xCoord, yCoord, zCoord);

  // Use of kos library to easily calculate the
  // size of the encoded response
  return encoderSize(&arg->enc);
}

static int processJavaSendStructAPI(blinkApiArg *arg) {

  // Get data from Java
  NavigationModuleDestination *destinationPtr =
      (NavigationModuleDestination *)arg->inBuf;

  char *name = destinationPtr->name;
  double x = destinationPtr->xCoordinate;
  double y = destinationPtr->yCoordinate;
  double z = destinationPtr->zCoordinate;

  int isConfigurationSucessful = rand() % 2;

  if (isConfigurationSucessful) {
    kosLog("We're going to %s which is located at (x,y,z) (%f,%f,%f)", name, x,
           y, z);
  } else {
    kosLog("Failed to configure new destination");
  }

  // encode success status and return it to java
  encodeBool(&arg->enc, isConfigurationSucessful);

  return encoderSize(&arg->enc);
}

/**
 * Get input from arduino and send blink event
 */
void *simulateEvents() {

  // every random amount of time from 1-20 seconds send an event to java

  for (;;) {
    int waitTime = (rand() % 20) + 1;

    sleep(waitTime);

    int severity = rand() % 4;

    // Simulates the hardware recieving an event and sending it
    blinkSendMsg(navigationIface, API_NATIVE_SEND, &severity, sizeof(int));
  }

  return NULL;
}

static void cleanup() {
  pthread_cancel(programEventThread);
  pthread_join(programEventThread, NULL);
}

/**
 * main() : Main program entry point.
 */
int main(int argc, char *argv[]) {
  struct boardIfaceData boardData; // board data for linking to java
  int console = 0;

  atexit(cleanup);

  // process options
  signed char c;
  while ((c = getopt(argc, argv, "hvc:")) != -1) {
    switch (c) {
    case 'h': //
      return usage();
    case 'v':
      verbose = 1;
      break;
    case 'c':
      console = 1;
      break;
    case '?':
      return usage();
    }
  }

  // Seed randomness with current time
  srand(time(NULL));

  // if console not set, log to syslog
  // Syslog is accessable through the log viewer tool in studio
  // or at /mnt/datafs/logs/live/systlog*
  if (!console)
    kosLogToSyslog();

  // create a client to connect to a SockEndpoint running
  // in a JVM on the local machine... override via properties
  client = blinkCreate(BLINK_NAME, REVISION, NULL, NULL, 0, 0);

  // zero the memory in boardData
  memset(&boardData, 0, sizeof(boardData));

  // initialize the board data
  boardData.type = "navigationModule";

  // register the board iface
  blinkAddBoardIface(client, &boardData);

  // get iface
  navigationIface = client->coreIface;

  // register the handler to API number and pass the user data
  blinkRegisterApi(navigationIface, API_JAVA_SEND, processJavaSendAPI, NULL);
  blinkRegisterApi(navigationIface, API_JAVA_SEND_AND_RECEIVE,
                   processJavaSendAndRecvAPI, NULL);
  blinkRegisterApi(navigationIface, API_JAVA_SEND_STRUCT,
                   processJavaSendStructAPI, NULL);

  // listen for events from the arduino
  pthread_create(&programEventThread, NULL, simulateEvents, NULL);

  // process connections from java indefinitely
  blinkDispatch(client);
}
