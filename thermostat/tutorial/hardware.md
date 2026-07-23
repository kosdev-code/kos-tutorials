---
title: Hardware Integration
excerpt: Firmware required to connect a physical Arduino to the backend using BLINK
categories: ["Thermostat Backend"]
tags: [Tutorial]
date: 2026-07-14 00:00:00
status: publish
author: sneh
---

## Introduction

At this stage, the backend system is fully functional and already connected to a browser-based UI. The UI can receive real-time updates and react immediately to changes in device state.

With this foundation in place, the next step is to replace the thermostat simulator with a real physical device. In this tutorial, the physical thermostat is represented by an Arduino Mega equipped with a temperature sensor for measuring ambient temperature and LEDs for indicating the current operating mode. The LEDs are white, red, and blue to represent the off, heating, and cooling modes respectively.

## Java Side: SerialBlinkMatcher

In a typical KOS hardware integration, developers are responsible for writing a custom adapter. However, for Arduino boards programmed with the Serial BLINK library, KOS provides the necessary adapter infrastructure to simplify the process.

On the Java side, the developer simply needs to identify which serial devices should be connected. This is done by implementing the SerialBlinkMatcher interface. The class that implements this interface must be added to the application context, which is why `ThermostatAssembly` will implement `SerialBlinkMatcher`.

A `SerialBlinkMatcher` is responsible for detecting serial devices that are running BLINK. When such a device is discovered, KOS invokes the `matchSerialBlinkDevice()` callback. This method provides a connected device identifying information, so that it can be determined whether it is the target device in question. This is most commonly done by checking the vendor ID and product ID of the connected device. Note, these values can be looked up for a specific board or obtained by monitoring the connection via a connected device.

In this example, the matcher simply checks whether the detected device is an Arduino Mega. If it is, the matcher returns a `SerialBlinkMatch` object set with the baud rate expected by the firmware.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-blink@ThermostatAssembly.java"></snippet-viewer>

The call to `setPostOpenDelayMs()` introduces a delay between opening the serial connection and sending the probe payload. This delay is required because the Arduino Mega automatically resets when a serial connection is opened. Without this pause, the probe would be sent while the board is still rebooting, causing the connection attempt to fail.

## Embedded Side

### Setting up BLINK

This section implements the Arduino firmware that allows the physical thermostat board to communicate with the Java backend using the BLINK protocol.

The first step is to import the BLINK library. Download the library as a ZIP file from the KOS documentation. In the Arduino IDE, select "Sketch" → "Include Library" → "Add .ZIP Library", then choose the downloaded file. Once installed, the BLINK header can be included in the sketch.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-clib@thermostat.ino"></snippet-viewer>

BLINK is composed of two core components:

- `SerialBlinkComm`, which defines the transport BLINK runs on, in this case `Serial` port
- `BlinkService`, which implements the BLINK protocol logic.

The following code initializes BLINK so it can communicate over the Arduino's serial connection.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-serial@thermostat.ino"></snippet-viewer>

### Board Identification

Next, the firmware must identify itself so that it can be correctly matched with the logical board defined on the Java side.

The firmware specifies the serial baud rate, the board name and type, and the board instance ID. These values must match the expectations of the Java backend.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-constants@thermostat.ino"></snippet-viewer>

In the `setup()` function, the serial connection is initialized and the BLINK service is configured with this metadata.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-setup@thermostat.ino"></snippet-viewer>

This information is used during device discovery to ensure that the firmware corresponds to the correct logical board in the system.

As discussed earlier in the tutorial, the logical board communicates with the physical board through an **iface**. On the Arduino side, this means registering that iface with the `BlinkService`. The `addIface()` call specifies the iface name (which must match the Java side), the version, and an array of handler functions.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-add-iface@thermostat.ino"></snippet-viewer>

Once registered, BLINK knows how to route incoming API calls to the appropriate handler functions on the Arduino.

<image title="" name="blink-connection.png" caption="" />

### Defining Handler Functions

Each method exposed by the Java-side iface is assigned an API number. On the Arduino side, each API number maps to a handler function. The position of the handler in the array passed to `addIface()` corresponds directly to its API number.

First the handler function signatures are declared.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-functions@thermostat.ino"></snippet-viewer>

Next, the handler table is defined. The final `NULL` entry marks the end of the handler table.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-handlers@thermostat.ino"></snippet-viewer>

In order to get API requests, BLINK uses polling. Each call to `blink.poll()` checks for incoming messages and routes them to the appropriate handler. To continuously poll, this call is placed inside the `loop()` function.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-loop@thermostat.ino"></snippet-viewer>

### Implementing the Handlers

The current operating mode will be stored locally on the Arduino. When the Java side requests the current mode (API 1), the firmware writes the value directly to the BLINK response buffer. To send a response via BLINK, the handler must first calculate the total size of the message in bytes. The `reply()` method is called to generate the BLINK message headers and prepare the message for transmission. This must be executed before any data is written. Once the header is sent, the `write()` method transmits the actual data.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-get-mode@thermostat.ino"></snippet-viewer>

When the Java backend sends an updated mode (API 2), the firmware reads the incoming value from the buffer. After validating the input, the handler updates the physical hardware, in this case, toggling the mode LEDs, and stores the new state locally.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-set-mode@thermostat.ino"></snippet-viewer>

The temperature handler (API 0) bridges the analog hardware and the digital protocol. It reads the raw value from the analog pin, performs the necessary mathematical conversions to determine the temperature in Fahrenheit, and returns the result as a 32-bit integer.

<snippet-viewer source="tutorials-public" snippet="thermostat-hardware-get-temp@thermostat.ino"></snippet-viewer>

## Conclusion

This section completes the Java side and firmware required to connect a physical Arduino based thermostat to the backend using BLINK. The board now has API handlers that connect to the Java-side iface, allowing temperature readings and mode changes to flow through the system in real time.

With the Java backend, Arduino firmware, and browser-based UI all in place, the system is now connected end to end. This architecture, separating hardware-specific handlers from high-level application logic, serves as the standard pattern for building scalable, maintainable solutions in KOS.

This concludes the Thermostat Integration tutorial. The principles demonstrated here can now be applied to a wide range of custom hardware projects that use KOS.
