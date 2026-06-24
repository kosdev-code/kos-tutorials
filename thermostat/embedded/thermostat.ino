/*------------------------------------------------------------*/
/* File : adapter.c                                          */
/*------------------------------------------------------------*/
/* Author      : Sneh Gupta                                   */
/* Date        : 2025-12                                      */
/* Description : Thermostat adapter.                          */
/*------------------------------------------------------------*/
/* (C) Copyright 2025, TCCC                                   */
/* All rights reserved.                                       */
/*------------------------------------------------------------*/
#include <blink.h>

// setup blink using Serial as the transport
SerialBlinkComm comm(&Serial);
BlinkService blink(&comm);

// baud rates for blink
#define BLINK_BAUD           115200
#define BOARD_NAME           "kos.tutorial.thermostat"
#define BOARD_TYPE           "kos.tutorial.thermostat"
#define INSTANCE             1

// API numbers
#define API_GET_TEMP         0
#define API_GET_MODE         1
#define API_SET_MODE         2

// the pins for each piece of hardware on the device
#define OFF                  20
#define HEATING              21
#define COOLING              22
#define TEMP_SENSOR_PIN      5

#define NUM_MODES            3

static const int modePins[] = { OFF, HEATING, COOLING };

// Store state: Current thermostat mode (0=OFF, 1=HEATING, 2=COOLING)
static int8_t currentMode = 0;

// Function declarations
static void getTemp(BlinkService *s);
static void getMode(BlinkService *s);
static void setMode(BlinkService *s);

// handlers table for java side iface methods
const blinkHandler handlers[] = {
    (blinkHandler) getTemp, // api 0
    (blinkHandler) getMode, // api 1
    (blinkHandler) setMode, // api 2
    NULL
};

// Setup function
void setup() {
    Serial.begin(BLINK_BAUD);

    blink.setBoardType(BOARD_TYPE);
    blink.setBoardInstanceId(INSTANCE);
    blink.addIface(BOARD_NAME, 1, handlers);

    // Initialize mode LEDs
    for (int i = 0; i < NUM_MODES; i++) {
        pinMode(modePins[i], OUTPUT);
        digitalWrite(modePins[i], LOW);
    }

    // Temperature sensor input
    pinMode(TEMP_SENSOR_PIN, INPUT);

    // Default mode = OFF
    digitalWrite(OFF, HIGH);
}

// Main loop
void loop() {
    blink.poll();
}

// API 0: get temperature (returns °F)
static void getTemp(BlinkService *s) {
    int raw = analogRead(TEMP_SENSOR_PIN);

    float voltage = raw * (5.0 / 1023.0);
    float celsius = (voltage - 0.5) * 100.0;
    float fahrenheit = (celsius * 9.0 / 5.0) + 32.0;
    int32_t temp = (int32_t)round(fahrenheit);

    //s->write(&fahrenheit, sizeof(fahrenheit));

    /* Determine how many bytes you are going to
    be sending back, in this case it is the number
    of bytes in the int */
    int msgSize = sizeof(temp);

    /* Call reply with how many bytes you are going to
    be sending total. This method is used to generate
    the binary message header and write it to the
    stream, so it must be called before writing
    any information to the stream. Once it has been
    called you can make a call to write and pass
    what you want to send along with the size in bytes
    of what you are writing to the stream. */
    s->reply(msgSize);
    s->write(&temp, msgSize);
}

// API 1: get current mode
static void getMode(BlinkService *s) {
    int msgSize = sizeof(currentMode);
    s->reply(msgSize);
    s->write(&currentMode, msgSize);
}

// API 2: set mode
static void setMode(BlinkService *s) {
    // Read mode: 1 byte from the incoming stream
    int8_t newMode;
    s->read(&newMode, 1);

    // Validate input against the number of supported modes
    if (newMode < 0 || newMode >= NUM_MODES) {
        return;
    }

    // Turn all modes off
    for (int i = 0; i < NUM_MODES; i++) {
        digitalWrite(modePins[i], LOW);
    }

    // Activate selected mode
    digitalWrite(modePins[newMode], HIGH);

    currentMode = newMode;
}
