#include <blink.h>

// baud rates for blink 
#define BLINK_BAUD 115200

// event api numbers 
#define API_EVENT_EXAMPLE 7

// the maximum number which can be stored 
#define MAX_OVERRIDES 4

struct override{
  char name[16]; 
  uint8_t level;
};

// and track the current number of overrides and overrides themselves
uint8_t numOverrides = 0;   
override overrides[MAX_OVERRIDES];

// setup blink using Serial as the transport
SerialBlinkComm comm(&Serial);
BlinkService blink(&comm);

// the index of the arduino iface 
int arduinoIfaceNum;

// dummy information that has been received from iface
char receivedString1[64] = "Default String 1";
char receivedString2[64] = "Default String 2";
int receivedNum;

// handler declarations 
static void handler0(BlinkService *s);  // part 1.1
static void handler1(BlinkService *s);  // part 1.2
static void handler2(BlinkService *s);  // part 2.1
static void handler3(BlinkService *s);  // part 2.2
static void handler4(BlinkService *s);  // part 3.1
static void handler5(BlinkService *s);  // part 4.1
static void handler6(BlinkService *s);  // part 5.1


// override callback declaration 
static void sampleOverrideCallback(char* name, uint8_t level, bool add);

// handlers table for the java-side iface methods
const blinkHandler handlers[] = {
    handler0, // api - 0
    handler1, // api - 1
    handler2, // api - 2
    handler3, // api - 3
    handler4, // api - 4
    handler5, // api - 5
    handler6, // api - 6
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
  blink.log(4, "intitating handler 4"); 

  int msgSize = strlen(receivedString1) + 1;\

  // char buf[32];
  // sprintf(buf, "msg: %s, msgSize: %d", receivedString1, msgSize);
  // blink.log(4, buf);

  // sprintf(buf, "arduino iface num: %d", arduinoIfaceNum);
  // blink.log(4, buf);

  // generate the log event and write the log
	int eventStatus = s->event(arduinoIfaceNum, API_EVENT_EXAMPLE, msgSize);

  //write the information to the stream 
	s->write(receivedString1, msgSize);

  // sprintf(buf, "event status: %d", eventStatus);
  // blink.log(4, buf);
}

static void handler5(BlinkService *s){
  s->reply(0);

  log("group1", 1, "sample log 1");
  log("group1", 2, "sample log 2");
  log("group2", 3, "sample log 3");
  log("group2", 4, "sample log 4");
  log("group3", 5, "sample log 5");
  log("group3", 6, "sample log 6");
}

static void handler6(BlinkService *s){
  uint8_t errCode = 128;

  s->replyError(errCode);
}

/* This is a wrapper class for the log function of blink service.
This implementation allows for a group name to be attached to the 
log so that the group names of overrides can be used to filter 
the various logs. */
static void log(const char* group, uint8_t level, const char* msg){
  for(int i = 0; i < numOverrides; i++){
    if(!strcmp(overrides[i].name, group) && overrides[i].level < level){
      return;
    } 
  }
  blink.log(level, msg);
}

/* Implementation of override callback which can account for multiple
groups. This is a more advanced use of the override system, and a user
could just as easily only have one callback at any given time. We are
using this implementation as an example to demonstrate the purpose
of overrides group and how they can be used. */
static void sampleOverrideCallback(char* name, uint8_t level, bool add){  

  /* Iterate through the existing overrides to see
  if any have a matching name, if so then either 
  remove it or alter it. If you are removing it then
  decrement the number of overrides. */
  for(int i = 0; i < numOverrides; i++){
    if(!strcmp(overrides[i].name, name)){
      if(add){  
        log("overrides", 4, "altering existing override...");
        overrides[i].level = level;
      }else{
        log("overrides", 4, "removing existing override...");
        overrides[i].name[0] = '\0';
        overrides[i].level = 0;
        numOverrides--;
      }
      return;
    }
  } 

  /* Determine whether there is room for an additional
  override, if so then add it and increment the number
  of overrides. */
  if(add){
    if(numOverrides != MAX_OVERRIDES){
      log("overrides", 4, "creating new override...");

      for(int i = 0; i < MAX_OVERRIDES; i++){
        if(overrides[i].name[0] == '\0'){
          strncpy(overrides[i].name, name, strlen(name) + 1);
          overrides[i].level = level;
          numOverrides++;
          return;
        }
      }
    }else{
      log("overrides", 4, "failed to create new override, max number of overrides reached");
    }
  }
}
