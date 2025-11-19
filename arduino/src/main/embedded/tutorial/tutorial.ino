#include <Vector.h> //3rd part libary used exclusivly for convenience 
#include <blink.h>

#include <BlinkComm.h>
#include <BlinkService.h>

// baud rates for blink and debug
#define BLINK_BAUD 115200

#define API_HANDLER_0 0
#define API_HANDLER_1 1
#define API_HANDLER_2 2
#define API_HANDLER_3 3
#define API_HANDLER_4 4
#define API_HANDLER_5 5

#define API_EVENT_EXAMPLE 0

#define MAX_OVERRIDES 8

struct override{
  char* name;       // name of the override 
  uint8_t level;    // log level of the override 
};

// setup blink using Serial as the transport
SerialBlinkComm comm(&Serial);
BlinkService blink(&comm);

// the index of the arduino iface 
int arduinoIfaceNum;

// and track the current number of overrides and overrides themselves
uint8_t numOverrides = 0;   
Vector<override> overrides; 

// dummy information that has been received from iface
char receivedString1[64] = "Default String 1";
char receivedString2[64] = "Default String 2";
int receivedNum;

// handlers
static void handler0(BlinkService *s);  // part 1.1
static void handler1(BlinkService *s);  // part 1.2
static void handler2(BlinkService *s);  // part 2.1
static void handler3(BlinkService *s);  // part 2.2
static void handler4(BlinkService *s);  // part 3.1
static void handler5(BlinkService *s);  // part 4.1

static void sampleOverrideCallback(char* name, uint8_t level, bool add);

// handlers table for the java-side iface methods
const blinkHandler handlers[] = {
    handler0, //api - 0
    handler1, //api - 1
    handler2, //api - 2
    handler3, //api - 3
    handler4, //api - 4
    handler5, //api - 5
    NULL
};

// init and config connection
void setup() {
  // setup the blink board type
  blink.setBoardType("kondra.arduino");
  blink.setBoardInstanceId(1);

  // add iface for our custom handlers
  arduinoIfaceNum = blink.addIface("kondra.arduino", 1, handlers);

  // add iface for embedded logging
  blink.addLoggerIface("kondra.arduino", "1", sampleOverrideCallback);  
  
  // configure baud rates
  Serial.begin(BLINK_BAUD);

  Serial1.println("Setting up arduino");
}

/**
 * Standard loop method
 */
void loop() {
  // begin polling for api calls
  blink.poll();
}

static void handler0(BlinkService *s){
    // read the C string that was sent
    s->read(receivedString1, s->remaining());
}

static void handler1(BlinkService *s){    
    int recvStr1Size;

    // rad the C string that was sent
    s->read(&recvStr1Size, 4); //read the size of the first string 
    s->read(receivedString1, recvStr1Size);    //read the first string 
    s->read(&receivedNum, 4);                  //read the int we sent
    s->read(receivedString2, s->remaining());  //read what is remaining into the string 
}

static void handler2(BlinkService *s){
    /* Determine how many bytes you are going to 
    be sending back, in this case it is the number
    of characters in the string +1 to account for 
    the null terminator at the end. */
    int msgSize = strlen(receivedString1) + 1;
    
    /* Call reply with how many bytes you are going to
    be sending total. This method is used to generate 
    the binary message header and write it to the 
    stream, so it must be called before writing 
    any information to the stream. Once it has been 
    called you can make a call to write and pass
    what you want to send, in this case a string,
    along with the size in bytes of what you are 
    writing to the stream. */
    s->reply(msgSize);
    s->write(receivedString1, msgSize);
}

static void handler3(BlinkService *s){
    /* Determine how many bytes you are going to 
    be sending back including both strings, their 
    null terminators, and the int that we received. */
    int msgSize = strlen(receivedString1) + strlen(receivedString2) + 6;
    
    /* Once again we are going to call reply with the
    total message size, but this time we are going to
    send all of information that we have received in
    the past from the iface. */
    s->reply(msgSize);
    s->write(receivedString1, strlen(receivedString1) + 1);
    s->write(receivedString2, strlen(receivedString2) + 1);
    s->write(&receivedNum, 4);
}

static void handler4(BlinkService *s){
  int msgSize = strlen(receivedString1) + 1;

  // generate the log event and write the log
	int logEventStatus = s->event(arduinoIfaceNum, 0, msgSize);

  //write the information to the stream 
	s->write(receivedString1, msgSize);
  s->reply(0);
}

static void handler5(BlinkService *s){
  s->reply(0);

  log("group1", 1, "sample log 1");
  log("group2", 2, "sample log 2");
  log("group3", 3, "sample log 3");
  log("group4", 4, "sample log 4");
  log("group5", 5, "sample log 5");
  log("group6", 6, "sample log 6");
}

static void log(const char* group, uint8_t level, const char* msg){
  for(int i = 0; i < numOverrides; i++){
    if(!strcmp(overrides[i].name, group) && overrides[i].level >= level){
      blink.log(level, msg);
    } 
  }
}

static void sampleOverrideCallback(char* name, uint8_t level, bool add){
  // iterate through the exisiting overrides 
  for(int i = 0; i < numOverrides; i++){
    if(!strcmp(overrides[i].name, name)){
      if(add){  
        overrides[i].level = level;
      }else{
        overrides.remove(i);
      }
      return;
    }
  } 

  //determine wether there is room for another override
  if(add && (numOverrides != MAX_OVERRIDES)){
    overrides.push_back({name, level});
  }
}





