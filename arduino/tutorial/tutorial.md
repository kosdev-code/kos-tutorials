---
title: Using Serial BLINK in KOS 
excerpt: Build an app in KOS that interacts with an external Arduino board, via the serial BLINK library. 
categories: [Arduino]
tags: [Tutorial]
status: publish
author: james
_yoast_wpseo_focuskw: Arduino
---

## Introduction

One of the great things about KOS is the plethera of tooling and support that makes it easy to work with. A great example of this would be the native serial BLINK library. The inclusion of this library allows users to easily integrate with almost any microcontroller that supports the Arduino.h library. This library allows users to take advantage of a pre-built adapter and other surrounding infrastructure, dramatically decreasing the amount of work involved in integrating new devices. This tutorial will explore this library by covering the process of getting an Arduino Mega to connect communicate with an application. The assumption will be that you have read the previous tutorial on adapters. For this reason, most of what will be discussed here will revolve around the embedded code. It is recommended that you view the full tutorial code in the [kos-tutorials](https://github.com/kosdev-code/kos-tutorials) repository. That being said, let’s begin. 

## Java Code

### `SerialBlinkMatcher`

While in a typical KOS hardware pattern you would have to write a custom adapter and corresponding adapter factory, for Arduinos and other such boards implementing the serial BLINK library, this is not the case. The adapter and its related infrastructure has been handled for you behind the scenes. All that is left to you, as the developer, is to implement a `SerialBlinkMatcher`.

The `SerialBlinkMatcher` is an interface which is used to find devices running the serial BLINK library. When such a device is found, a call will be made to `matchSerialBlinkDevice()` which will be passed as arguments the usbId; containing the usbId and product, and the `SerialDevice` in question. Here a quick check will be performed to see if the vendor and product ids match that of Arduino. These ids vary by the specific Arduino board, but you can find them by either looking them up on the internet, or plugging in your Arduino and checking them through your computer. Below is a sample implementation of a `SerialBlinkMatcher` for connecting to an Arduino Mega:

<snippet-viewer source="tutorials-public" snippet="arduino-s1@TutorialAssembly.java"></snippet-viewer>

You will notice there is a call on the `SerialBlinkMatch` to `setPostOpenDelayMs()`. This method creates a specified delay between when the serial connection is opened, and when the device is probed. This is necessary because in some models of Arduino, when the serial connection is opened the device is still rebooting. If there were no delay the payload sent to probe the device would never get a response because of this, and thus the connection would fail. 

You may also notice that the class which implements `SerialBlinkMatcher` is the `Assembly`. It is a common pattern in KOS to have the `Assembly` implement `SerialBlinkMatcher`, as whatever class implements `SerialBlinkMatcher` must be added to the context for it to work. Having the `Assembly` implement `SerialBlinkMatcher` ensures that it will always be added to the context. It is worth noting that there can be multiple instances of `SerialBlinkMatcher`. So long as each is added to the context, they will be called every time a new serial device is detected. Beyond implementing `SerialBlinkMatcher`, much of the Java code is as one might expect; an App, Assembly, and board class along with an accompanying iface. The only exclusion is the `SerialAdapterFactory` which does not need to be directly defined for devices which implement the serial BLINK library. All of these are available in the project repository linked above. With that out of the way, it is time to dive into the embedded side.

## Embedded Code

### Setup

Before going further, it is important that you import the serial BLINK library into your Arduino IDE. To do so, download the zip, before going to the Arduino IDE and clicking Sketch>Include Library>Add .ZIP Library and selecting the previously downloaded file. You will now be able to include the necessary BLINK headers in your .ino file. 

Shifting focus back to the embedded code, this tutorial will begin with the bare minimum of what is required to get the Arduino to connect to the Java iface. Starting with headers, the `blink.h` header file will need to be included. This will provide access to `BlinkService` and `BlinkComm` classes. The former contains most of the core implementation necessary for using the BLINK protocol, while the latter is an abstraction of the communication channel that BLINK will run over. The `BlinkService` will be used more heavily, while `BlinkComm` only needs to be initialized with the serial port to use and passed to the constructor of `BlinkService`. This tutorial will cover `BlinkComm` in more detail later, but for now one may initialize these two classes globally so that they are always accessible like so…

<snippet-viewer source="tutorials-public" snippet="arduino-s2@tutorial.ino"></snippet-viewer>
<snippet-viewer source="tutorials-public" snippet="arduino-s3@tutorial.ino"></snippet-viewer>

Now that the `BlinkService` has been instantiated, it is time to configure it so that it can link to the Java board. Toward this end, the board type and instance id will have to be initialized. This can be done using their respective setters within the `BlinkService` like so…

<snippet-viewer source="tutorials-public" snippet="arduino-s4@tutorial.ino"></snippet-viewer>

If you are not familiar with the concept of a board type and instance id, please reference this [tutorial](https://kosdev.com/articles/docs_topics_adapter_blink_ifaces_md/). For the purposes of this tutorial, it is only necessary to know that they are used to resolve the link between the embedded code and an instance of the Java `Board` class. Care must be taken to ensure that the board type and instance id are consistent across both the Java and Embedded sides, otherwise the `Board` instance will fail to link. The final step in ensuring that embedded can link, is telling the `BlinkService` what iface is being targeted. This is done by calling the `addIface()` function. This function takes three arguments; the unique name of the iface, the version, and an array of handlers. It then returns an integer iface number whose use will be explored later in this tutorial.

<snippet-viewer source="tutorials-public" snippet="arduino-s5@tutorial.ino"></snippet-viewer>

The aforementioned handlers are callbacks, which are defined by the developer and called when a binary message is sent with the API number corresponding to the handler’s index in the specified array. This handler array should always terminate with `NULL`. Each handler is passed the `BlinkService` which can be used to communicate Java. Shown below is the code which demonstrate this process. Further on sample handlers will be provided. It is important to note that it is up to developer whether they want to use handlers. If no handlers are desired `NULL` can be passed in place of the handlers to `addIface()`.

<snippet-viewer source="tutorials-public" snippet="arduino-s6@tutorial.ino"></snippet-viewer>

Now that the handlers have been declared and added, it is time to configure the baud rate. It is vital that the baud rate you defined on the embedded side remain consistent with that specified in the `SerialBlinkMatcher`. Failing to do so will prevent the Arduino from properly communicating with your iface. The baud rate is set by making a call to the Arduino `Serial` object like so. This part is standard to Arduino and not KOS specific. 

<snippet-viewer source="tutorials-public" snippet="arduino-s7@tutorial.ino"></snippet-viewer>
<snippet-viewer source="tutorials-public" snippet="arduino-s8@tutorial.ino"></snippet-viewer>

At this point, the only remaining step is for `BlinkService` to begin polling. To do this the `poll()` function of `BlinkService` is called in `loop()`. Every call to `poll()` will check if any new information has arrived in the receive buffer.  Because we want the service continually checking for incoming messages, it is placed in the Arduino loop() function. Consequently, it is important that `loop()` does not get blocked for any significant amount of time by any other process running on the Arduino. Blocking the `BlinkService` from polling can cause the embedded and Java side to stop communicating.

<snippet-viewer source="tutorials-public" snippet="arduino-s9@tutorial.ino"></snippet-viewer>

### Communicating Over BLINK

#### Part 1.1: Reading Basic Information

To demonstrate the different ways you can communicate over BLINK, this section will be split into a number of parts, each of which will cover a different feature. This will begin with the discussion of how you can read information over BLINK from the iface. Below is an instance of a handler that reads a single string sent from the iface:

<snippet-viewer source="tutorials-public" snippet="arduino-s10@tutorial.ino"></snippet-viewer>

Within the iface, the string is sent as it would normally be, by constructing an instance of `BinaryMsg` with the appropriate API number, before calling `writeCString()` and `send()` respectively. On the embedded side you call the read function of `BlinkService`. The function is passed two arguments. The first is a pointer to the location in memory where the received information will be stored, and the second is the number of bytes that you intend to read. To get the number of bytes, you can use the `remaining()` function of `BlinkService`. This will tell us how many bytes in the current message have yet to be read. The number of total bytes in the message is sent as part of the incoming message’s header. Because the one string is the only information being sent in this message, you know that `remaining()` will return the size of the string. 


#### Part 1.2: Reading Multiple Pieces of information 

Reading multiple pieces of information is much the same, with a few more considerations. Consider the following example:

<snippet-viewer source="tutorials-public" snippet="arduino-s11@tutorial.ino"></snippet-viewer>

Here a string is read, then an integer, followed by another string. Because there is other information in the buffer, `remaining()` cannot be exclusively used to determine the size of the string. Rather, when sending the string from the iface, the size of the string in bytes is sent first. Following the first string, a 32-bit integer is read, followed by another string. Notice that this time, since all the other data in the buffer has been read, you can use the `remaining()` function to find the size of the string. For this reason it is convention that if you must send a string you send it last. 

#### Part 2.1: Writing Basic Information

Having demonstrated how to read information, this tutorial will now explain how to write information back to the iface. In this case, the information being written back will simply be the information that was read and stored in the previous two parts. This is demonstrated in the following handler:

<snippet-viewer source="tutorials-public" snippet="arduino-s11@tutorial.ino"></snippet-viewer>

You will notice the introduction of two new functions. The first of these is `reply()`. The `reply()` function is used to generate the message header for the response and is passed the total size in bytes of the response. In this case, because only the one string is being written back, the message size is just the size of the string plus one to account for the null terminator. The other function; `write()`, is responsible for writing the various other pieces of information back and, much like the read function, takes a pointer to the location in memory being written from, and the number of bytes being written. It is vital that the `reply()` function is called prior to any writing calls so that the header is sent first. This header is necessary, for the Java side knows how many bytes to expect back. Additionally, if all you want is confirmation your message was received, then calling reply() with a message size of zero will send an empty response. 

#### Part 2.2: Writing Multiple Pieces of Information 

Writing multiple pieces of information is really not much different. The only difference is that the size passed to `reply()` will no longer equal what is being passed to a single `write()` call. Below is a sample handler which wrights multiple pieces of information back as a response. 

<snippet-viewer source="tutorials-public" snippet="arduino-s12@tutorial.ino"></snippet-viewer>

#### Part 3.1: Generating Events

There will be situations where instead of requesting information and receiving a response, you will want the Arduino to send you information unprompted. Take, for instance, a scenario where you have your Arduino connected to a sensor, and you want to be notified if the sensor's reading has changed. For such scenarios the BLINK library supports what are known as events. Observe the following handler. 

<snippet-viewer source="tutorials-public" snippet="arduino-s13@tutorial.ino"></snippet-viewer>

An event is generated by making a call to the event function of the `BlinkService` and passing it the identifying number of the iface which will receive it. This iface number is the same one that was generated in the setup by `addIface()`. In addition to the iface number, the API number of the event, and the total size of any attached message is passed. It is worth noting that `event()` is similar to `reply()` in that it is generating a header. Unlike `reply()` however, `event()` will return an integer indicating whether the iface number that you passed is valid; zero for yes, and negative one otherwise. Additionally, like `reply()` once `event()` has been called you can begin writing. Similarly, you don’t have to write anything to an event. If all you want is a notification that something has occurred, you can specify zero for the message size. Going back to the Java end for a bit, you will need to add request handlers to the iface for any events generated on the embedded side in order to receive them. One can do this like so:

<snippet-viewer source="tutorials-public" snippet="arduino-s14@ArduinoIface.java"></snippet-viewer>
<snippet-viewer source="tutorials-public" snippet="arduino-s15@ArduinoIface.java"></snippet-viewer>

Notice `EVENT_SAMPLE` matches the api number passed to the `event()` function in the embedded code. This request handler will need to be added anytime the iface is initialized, so it is recommended to add it in either the iface constructor or in `onConnect()`. 

#### Part 4.1: Embedded Logging (KOS v1.9.x+)

Finally, the `BlinkService` supports embedded logging. This enables log statements on the embedded side to show up in the log viewer. Additionally, the `BlinkService` lets users leverage KOS’s existing logging override infrastructure, allowing them to apply select overrides to embedded logs to control which logs are visible. To use embedded logging with the BlinkService, you first have to add the logger iface to `setup()` like so:

<snippet-viewer source="tutorials-public" snippet="arduino-s16@tutorial.ino"></snippet-viewer>

Here you are passing the log type and log id respectively to the service. These have been made to match the board type and instance id in this case, but these can be anything. They are for the convenience of the user, allowing them to see where a log is coming from as they are displayed alongside every embedded log message in the viewer. You also have the option of passing an overrride callback. This will be covered in more detail shorty.

For now, to create an embedded log all you have to do is call `log()` with `BlinkService` and pass it the log level (1-6) along with the message that you want to display. These log levels, in their respective order are `FATAL`, `ERROR`, `WARN`, `INFO`, `DEBUG`, and `TRACE`. What follows is an example of an embedded logging statement. 

<snippet-viewer source="tutorials-public" snippet="arduino-s17@tutorial.ino"></snippet-viewer>

When it comes to using overrides, things get a bit more complicated. Because of the inherent memory limitations of working with Arduino and similar microcontrollers, the same system of overrides that exists in KOS Java is not implemented by default in `BlinkService`. Instead, it is up to the user to determine how overrides are handled. They can do this by defining an override callback and passing it when adding the logger iface. The override callback is called whenever an override is created or removed. Below is an example implementation of an override callback which functions similar to the normal KOS Java override system. Additionally, for this tutorial a wrapper has been created for the basic embedded log function which allows us to easily apply groups to log statements. 

<snippet-viewer source="tutorials-public" snippet="arduino-s18@tutorial.ino"></snippet-viewer>
<snippet-viewer source="tutorials-public" snippet="arduino-s19@tutorial.ino"></snippet-viewer>

This is a rather complex example of what you could do, but ultimately how far the developer wants to go with overrides is up to them. In some situations you might only want to store one override at a time, or maybe you don’t want to use overrides at all. In that case you can specify `NULL` when passing the override callback to `addLoggerIface()`. It is entirely up to the user and their hardware constraints. 

To view embedded logs, you can navigate to the log viewer and in the selection menu, select the “embedded” option. Finally, you will notice a large integer value on the left-hand side of each log in the viewer, just after the timestamp. This is the number of milliseconds that the Arduino had been running when the log was created, obtained using the Arduino `millis()` function. This number will continue to increase so long as the Arduino is running uninterrupted until about 50 days, at which point it will overflow. 

To test this, you can run the tutorial and hit the part 4 endpoint. This will make a call to the fifth handler which will produce a series of logs in groups. From there you can add or remove the overrides using the `LogService` endpoints visible in the API Browser tool of KOS Studio. You should be able to see certain logs appear and disappear depending on the override name and level. It is encouraged for you to play around with the override system and write your own. 

#### Part 6.1: Other 

This final section will cover the functions that are present in the `BlinkService` that are not present in the tutorial. These are `setBoardSerialNum()` and `available()`. The former allows you to initialize the serial number of the board within the `BlinkService`. To use it, pass the function a character pointer pointing to the serial number in question. The use of this function is optional, but should you choose to do so, the serial number will be available within the board class on the Java end. The latter function; `available()`, is a bit more nuanced. The `available()` function returns the number of bytes currently stored in the receive buffer but will return zero once the end of the message is reached (i.e remaining() returns 0). In practice, besides a couple of niche use cases, this function will probably not be useful. 

### `BlinkComm`

It was mentioned earlier that `BlinkComm` was an abstraction of the communication channel, but you may ask yourself why this is necessary. The reason is that it offers the ability to use other boards, so long as you define your own class which extends `BlinkComm`, the user can define how things are read from and written to the stream. To do this, all the user must do is override three functions: `available()`, `read()`, and `write()` This allows users to use boards that are not Arduino. A good example of where you might use this is with a Teensy board. Unlike a typical Arduino, the Teensy communicates at much higher rates with a much larger read buffer, since it uses Usb instead of Serial. 











