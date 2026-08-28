---
title: Creating and using an Adapter with KOS
excerpt: Build an adapter with KOS. . 
categories: [Arduino]
tags: [Tutorial]
status: publish
author: james
_yoast_wpseo_focuskw: Arduino
---

## Introduction
As mentioned in [Hardware Abstraction](https://kosdev.com/articles/hardware-abstraction/)  KOS runs in the JVM (Java Virtual Machine), which has limited hardware and low level integrations. Additionally, technologies, such as JNI (Java Native Interface), are brittle and can crash the entire Java virtual machine. To solve for this KOS introduces the concept of adapters, which are native programs that bridge the gap between Java and low-level native functionality. This article will explain how to create adapters and integrate them into a KOS application. It will also explain the Blink protocol, and related concepts for the creation of adapters.

## How does an adapter communicate with KOS?

An adapter is primarily a native program. What differentiates a native program and an adapter is that an adapter establishes a network connection with KOS, using the BLINK protocol.

### What is the BLINK protocol? 

BLINK (Binary Link) is a network protocol that packs data in a binary format, making data transfer extremely efficient. This allows data that is received from the Java side to be cast directly into C structs. Additionally the protocol is endian neutral where the Java side will adapt to the endianness of the client. 

### How does the BLINK protocol work? 

A blink connection is made up of Ifaces, An Iface acts as a contract that defines API’s between the two sides of a BLINK connection. Iface stands for interface but was renamed to distinguish them from Java interfaces. A BLINK connection can have up to 16 Ifaces, where each Iface has a name and a list of defined APIs. A BLINK connection must have at least one Iface with the first Iface referred to as the core Iface. The core Iface encapsulates the main purpose of the BLINK connection. 

An Iface API is defined by a number between 0 and 255. It is up to the developer to give a meaning to these APIs. When a message is sent over BLINK the header defines which Iface and which API the message is intended for. The receiver of the message can then decode the body of the message in the way particular to the API.  For specifics on the BLINK header refer to the Javadocs for BinaryMsgSession.

<image title="iface diagram" name="iface_diagram.png" caption="" />

Iface APIs can optionally expect a response, effectively creating two types of APIs, request-response APIs and event APIs.

An adapter will create a BLINK client that registers listeners for the various APIs which is how it receives messages from Java. Additionally, the BLINK client can send messages to Java, and Java will have to register listeners for those messages. Since each side of the BLINK connection has to register listeners, API numbers can be reused. Take API number 55, for instance, both the Java side and the adapter side can register listeners for that API and interpret them differently.  It’s best to think of an API as a two-lane road, where Java and the adapter can send requests and receive responses without conflict. This then opens up the total number of APIs possible to 512, 256 for each side of the connection for a single Iface.

<image title="iface diagram" name="BLINK_connection_diagram.png" caption="" />

### How are BLINK connections established?

When a blink client establishes a network connection to KOS it sends an identity packet that contains information about the blink connection and provides a list of declared Ifaces. These are Ifaces that the client has implemented and wants a connection for. KOS will continuously query all of the Iface factories trying to bind the client Iface to one in Java.

If an adapter includes a board Iface KOS will link the adapter's BLINK connection to the board that has the same type and instanceId. 

### How do board play into all of this?

Boards are one of the chief features of KOS; a way to abstract physical hardware to reduce the potential of race conditions. Most times a board has a core purpose, for instance an RFID module reads and writes RFID tags, the functions of this core purpose are placed in the core Iface. Each blink connection has a core Iface, acting as the primary interface and reason for the BLINK connection. Resulting in 15 Ifaces left available for the BLINK connection. One of those slots can be used by the board  Iface.

The board Iface is simple; the name of the Iface is kos.board and it has 4 apis defined. 


| API | Number | Description                                                                                                                                                                |
|--|--------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| get board type | 1      | The adapter returns the type of board that it is as a string. Used to link boards in conjunction with instance ID.                                                         | 
| get instance ID | 2      | The adapter returns a string used to differentiate between multiple instances of the same type of board.                                                                   |
| get identity | 3      | Expects the adapter to return a unique string to represent the board. Can be used to link a board to an adapter as opposed to using board type and instance ID.            |
| get manufacturing serial number | 4      | Expects the adapter to return the serial number of the physical board that it is communicating with. This is the serial number that the manufacturer of the board gave it. |

KOS has great integrations to streamline the development and integration of adapters that link to boards. The KOS C library has plenty of helper functions to easily connect an adapter to a board instance. Additionally the Java API provides solutions for many common use cases such as starting an adapter when a USB device has been inserted. Many of these will be covered in the following reference project. 

## Reference Project

To make things more concrete, a reference project has been provided here. This tutorial will go over the parts that are pertinent to the development and integration of adapters. The whole project can be found [here](https://github.com/kosdev-code/kos-tutorials/tree/main/adapter). This project demonstrates the most common reason for creating an adapter, which is communicating with hardware. Consequently, this project has an [assembly](https://kosdev.com/articles/assembly-model-and-hardware-topology/) and a [board](https://kosdev.com/articles/docs_topics_board_md/) to abstract the hardware in Java. This project will make use of two Ifaces, the board Iface, and a custom Iface that has been implemented. 

### Custom Iface 

We’ll first start with the defining the Iface and then it can be implemented on both the Java side and the adapter side. For this Iface we’ll define 4 APIs to show off the variety of ways that the Iface can be used. 

API Definition

| API Name              | API Number | Description                                                                                                                                 | Example |
|-----------------------|------------|---------------------------------------------------------------------------------------------------------------------------------------------| ----- |
| Java Send             | 0          | Java will send a BLINK message with data to the adapter but not receive any data back from the adapter.                                     | Streaming configurations for LEDs, which LEDs are on showing what color at what brightness. There’s no error states with LED’s they’re either on or off, so Java doesn’t need a response just to send the requested state. |
| Java Send and Receive | 1          | Java sends a BLINK message and expects data to be returned.                                                                                 | Requesting a payment terminal to charge a specific amount. Java would want to receive the status of the request and handle failure or success appropriately. | 
| Java Send Struct      | 2          | Java sends a BLINK message with structured data that can be converted to a C struct. For this API Java will also expect data to be returned. | Configuring the state of the board. Java would send many pieces of data that the adapter can use for configuration and the adapter can return the status of the configuration. |
| Adapter Send          | 0          | The adapter sends an BLINK message to Java. | A door switch detects the door opened and would send that message to java.      |  

These are the different cases of BLINK messages Java can send a message with or without data, it can wait for a response and retrieve data from the response. Or the adapter can send a BLINK message to Java with or without data.

Notice that the API number 0 has been reused. When Java sends a BLINK message with API number 0 only the adapter will receive that message, so Java can safely listen for messages from the adapter sent over API 0 without conflict.

The name for this Iface is arbitrary, but it must match up on both the adapter and Java side to establish a connection. For this example it will be kondra.exampleIface. 

### Iface Implementation in an Adapter

Starting with the native C side, a minimal adapter that creates a BLINK client and processes BLINK connections can be created like so. From here the full implementation can be built.  

<snippet-viewer source="tutorials-public" snippet="adapter-s1@exampleAdapter.c"></snippet-viewer>

This program makes use of the KOS C library that can be found on a KOS device by selecting the developer tools options. The developer tools option is only necessary when using a KOS device for active development and should be unchecked otherwise. Since KOS is an operating system, it is expected that any native programs that are developed to run on KOS will be built on KOS, as it guarantees compatibility of the binaries. For more information on developing native programs for KOS check out this article [Native Development for KOS](https://kosdev.com/articles/native-development-for-kos/).

From the KOS library we’ll make use of many different functions the first being `blinkCreate()`, which creates a blink client struct used for communicating to Java.

<snippet-viewer source="tutorials-private" snippet="adapter-s2@exampleAdapter.c"></snippet-viewer>

Looking at the minimal implementation, one cas see that the name of the Iface defined as the first argument is passed to the function, declaring it the core Iface. Each BLINK connection has a core Iface, or the core reason for this BLINK connection. The second argument, the version number, can be used for backwards compatibility. The third argument in the function is the server where you can specify the URL of the blink server you want to connect to, useful in multi-node situations. For most cases you just want to connect to the JVM running on the same machine, so passing NULL is fine for the third argument. The fourth argument is a `properties` struct where you can specify properties to override defaults. The last two arguments are the size of the input buffer and output buffer, which can be overridden as needed, but default to 8192 bytes. 

The second function used from the KOS library is the `blinkDispatch()` method which takes in a `blinkClient` struct, this function will continuously attempt to connect to Java as opposed to just ending the program whenever Java disconnects.

### Registering callbacks for BLINK APIs

To register callbacks for BLINK API’s a blinkIface struct is required. The reference to the core iface’s struct can be obtained from the blinkClient like so. 

<snippet-viewer source="tutorials-private" snippet="adapter-s3@exampleAdapter.c"></snippet-viewer>

When additional Ifaces are needed, they should be obtained via the blinkRegisterInterface() method. 

<snippet-viewer source="tutorials-private" snippet="adapter-s4@exampleAdapter.c"></snippet-viewer>

Now that the Iface struct has been defined it can be register callbacks for the different API’s that have been defined using the `blinkRegisterApi()` function which takes in as arguments a `blinkIface` struct, the API number were registering against, the `blinkApiHandler()` function to be called when the BLINK message is received, and then a void pointer to pass any additional data that may be needed for the handler. The adapter can be updated like so. 

<snippet-viewer source="tutorials-public" snippet="adapter-s5@exampleAdapter.c"></snippet-viewer>

Now let's look into implementing the callback functions. These functions take in a single argument, a `blinkApiArg` struct. This struct is used to decode data from the BLINK message and encode data for the response message if the API was defined that way. Both the KOS Java sdk and the KOS C library have helper functions to encode and decode BLINK messages. To make this concrete let's look at an implementation of the "Java Send" API that was defined for our Iface. 

<snippet-viewer source="tutorials-public" snippet="adapter-s6@exampleAdapter.c"></snippet-viewer>

A couple of things

The `decodeBool()` function is being used on a pointer to a decoder struct. This is decoding the first byte in the BLINK message data buffer. There are many different helper functions available to decode all of the standard data types.

Another function from the KOS c library is `kosLog()` this will by default output logs to stdout which can be useful for debugging. But to have the logs show up in the standard KOS location the `kosLogToSyslog()` function needs to be called. This way KOS will process the logs and they’ll be accessible through the “Log Viewer” tool in KOS Studio or at `/mnt/datafs/logs/live/syslog*` on the running device.

The value that the function returns is zero. This value is the size of the data in the response message. But since, for this API, Java isn’t expecting a response we can safely return zero.

Now let's look into encoding data into the response message, with an implementation of the second API 

<snippet-viewer source="tutorials-public" snippet="adapter-s7@exampleAdapter.c"></snippet-viewer>

Here you can see the use of one of the encode helper functions, in this case encoding three doubles to the data buffer that will be sent in the response message that the Java side can consume. The other thing to note is that the return value of the function is returned from a call to another helper function `encoderSize()` built for this very use case where multiple pieces of data are placed into the response buffer.

This encoding and decoding works well for a few data points, but BLINK really shines in the ability to cast the data received into a C struct greatly enhancing the developer experience. An example of this can be seen below where a new struct is defined and the BLINK data buffer pointer is cast into a pointer of our new type. When dealing with structs, beware of any padding that the compiler may add, as this may cause issues.

<snippet-viewer source="tutorials-public" snippet="adapter-s8@exampleAdapter.c"></snippet-viewer>

### Sending Events from The Adapter 

This tutorial has explored the features around receiving and dealing with a BLINK message that was sent from Java to native code, but what if the adapter is interfacing with hardware and needs to surface an event to Java? This can be done with the `blinkSendMsg()` function. Here's an example that sends an event every twenty seconds.

<snippet-viewer source="tutorials-public" snippet="adapter-s9@exampleAdapter.c"></snippet-viewer>

The `blinkSendMsg()` function takes four arguments, the Iface which the message is for, the API for that Iface, data, and the size of the data that is being sent with the message.

### Implementing the Board Iface

One of the core tenets of KOS is the logical abstraction of hardware, which is done through boards and assemblies on the Java side. Since an adapter is often the connection to a piece of hardware, the status of a board's connectivity is the status of the BLINK connection. So if the BLINK connection is no longer connected, the board will become unlinked on the Java side and can be dealt with accordingly at that level of abstraction. 

The convention in KOS is that adapters are written to run indefinitely; the Java side determines when to start the adapter and when to kill the adapter. When an adapter is started, KOS will restart it if the process is ever terminated. So when it comes to a board's connection status, it is linked to whether the adapter is running. 

Since USB is often used to connect hardware to the KOS node, KOS has standard methods to start adapters when a USB is inserted. KOS will kill the adapter when that USB is removed, greatly reducing the amount of code required to manage hardware connections. 

To link an adapter to the board object in Java the BLINK connection must include the board Iface. The board Iface has a simple API that retrieves data about the board. This makes adding the board Iface to a BLINK connection simple, get all of the data about the board and put it into an `boardIfaceData` struct and call `blinkAddBoardIface()` which takes in the `blinkClient` and a pointer to the `boardIfaceData` struct. Now the adapter is linked to a board, below is an example of this process.

<snippet-viewer source="tutorials-public" snippet="adapter-s10@exampleAdapter.c"></snippet-viewer>

With this the adapter is complete, although it still needs to be compiled put it into a KAB. This tutorial describes how you can do so, along with information on how to set up build and publishing automaton for your native artifacts.

## Integration with Java

To integrate the adapter in Java there are a couple of things that your KOS application will have to do.
- Establish Iface connections and handle them according the Iface contract.
- Make the adapter binary available to system by mounting the kab on disk.
- Starting the adapter binary and passing in program arguments as required 

### Establishing Iface Connections

For a KOS application to interact with an Iface, it needs to define a representation of the Iface in Java, and a way to interact with it and dynamically create an instance when a BLINK connection with that Iface is established. To define the Iface KOS has the `BinaryMsgIface` class, which contains the methods that sends and receives messages sent over the BLINK connection. 

#### Creating the Iface

Implementing an Iface on the Java side is simple, `BinaryMsgIface` provides methods to interact with the APIs defined by the Iface, as well as handle incoming messages. Below is the minimum required to extend the `BinaryMsgIface` class. All that is required is to implement the constructor. Here constants that define the Iface’s APIs have also been included.

<snippet-viewer source="tutorials-public" snippet="adapter-s11@ExampleIface.java"></snippet-viewer>

The super constructor of `BinaryMsgIface` takes four arguments.

- The name of the Iface, used to establish the Iface connection within the BLINK connection
- `BinaryMsgSession` which is an abstraction of the BLINK network connection.
- `IfaceClient` is a wrapper around the `BinaryMsgIface` that provides many helper functions to safely call methods on the `BinaryMsgIface` and handle `null` references and exceptions.
- `BinaryMsgIfaceListener` is listener for connect and disconnect events of the Iface.

The `BinaryMsgIface` class provides the necessary methods to transmit and receive BLINK messages. To send a message over BLINK, an instance of `BinaryMsg` is created, this object manages the payload buffer for a BLINK message, it has methods to write and read data from the buffer. When the `BinaryMsg` is ready it can be sent via the `send()` method or if expecting a response with the `sendAndRecv()`. Below are two methods, one is an example of an Iface method that sends a BLINK message with a boolean encoded in the BLINK message. The other shows how a BLINK response can be received and data retrieved from it.

<snippet-viewer source="tutorials-public" snippet="adapter-s12@ExampleIface.java"></snippet-viewer>

<snippet-viewer source="tutorials-public" snippet="adapter-s13@ExampleIface.java"></snippet-viewer>

#### Receiving BLINK Messages

To handle BLINK messages coming from the adapter a listener needs to be registered for the specific API number that one is listening for. This is done by calling the addRequestHandler method and registering a callback, like the example below.

<snippet-viewer source="tutorials-public" snippet="adapter-s14@ExampleIface.java"></snippet-viewer>

### Creating Instances of BinaryMsgIface 

There are a number of ways to create an instance of a `BinaryMsgIface` given a blink connection. Fundamentally an instance of `BinaryMsgIface` is expected to be created by a class that implements the `BinaryMsgIfaceFactory` interface. For KOS to get access to an instance of the Iface factory it needs to be added to the context. When KOS receives a blink connection request it will attempt to create and connect the Iface when it receives a connection request, with the `IfaceFactories` available in the context. 

Typically Ifaces are not standalone and are used within a service or as an interface to a board. KOS has `IfaceAware` interfaces, that implement `BinaryMsgIfaceFactory` for these patterns. For services there is the `IfaceAwareNodeService` and `IfaceAwareService`. The `IfaceAwareNodeService` can be used for interacting with an adapter on one or more nodes in a KOS device, it provides additional methods for caching node-specific data. The `IfaceAwareService` does not have the additional caching methods and is not recommended to be used in multi-node configurations.

Following the KOS hardware abstraction model a `Board` class will contain functionality related to the physical board it represents. As mentioned before, KOS defines an iface for boards, the `Board` class includes the necessary information to link a board to the adapter that implments its iface. KOS will raise the `BoardNotLinked` trouble until that boards Iface connects. For cases where the board is not communicating to hardware via an adapter, a Board should implement the `SelfLinkingBoard` interface and call the `selfLink()` and `selfUnlink()` methods at appropriate times. 

To facilitate other Iface connections, a board can implement the `IfaceAwareBoard` interface, which provides the `onLinkSession()` method which is called when the board Iface connection is established. This method is the place to create instances of the other Iface that a board may use when interacting with an adapter. Below is an example implementation of a board, showing how the Iface is linked and used with a `IfaceClient`.

<snippet-viewer source="tutorials-public" snippet="adapter-s15@ExampleIface.java"></snippet-viewer>

#### Why IfaceClient?

The `IfaceClient` abstraction was created to remove boilerplate code around `null` checks and handling of exceptions. Thus `IfaceClient` has methods for handling the different scenarios, there are two basic methods `from()` and `with()`, and variations with additional arguments for handling exceptions and in case the Iface is null. The `from()` method is used to receive a value from the Iface and `with()` is for safely using the Iface, without triggering a null pointer exception. 

### Starting an Adapter

At this point we have created an adapter that uses our custom Iface, and have integrated that Iface into our KOS application. Following [Native Development for KOS](https://kosdev.com/articles/native-development-for-kos/) we have packaged our adapter binary into a KAB. The last missing piece is to run the adapter binary on our KOS device. We have to mount the binary into the filesystem and then start it. 

#### Mounting files on disk

To preserve immutability KOS runs in a read only file system. To make the contents of a KAB available to the rest of the system KOS provides FuseService to mount and unmount KABs to the local filesystem. This is how we can take the KAB with our adapter and add it to the local filesystem. 

<snippet-viewer source="tutorials-public" snippet="adapter-s16@ExampleBoard.java"></snippet-viewer>

#### Stating the Adapter

Adapters can be started explicitly using SpawnService or by using a `SerialAdapterFactory`. Both methods require the use of an `Adapter` object. This object specifies the different configurations for running the program, such as specifying program arguments, the working directory, setting the group the process should run as, see more in the [Javadocs](https://api.kosdev.com/v0.0.0-SNAPSHOT/api-core/com/kosdev/kos/core/service/spawn/Adapter.html).  Below is a minimal implementation. 

<snippet-viewer source="tutorials-public" snippet="adapter-s17@TutorialApp.java"></snippet-viewer>

Once an adapter is started KOS will ensure it continuously runs, so if the program exists or is killed, KOS will restart it. To start an adapter using the `SpawnService` the `addProcess()` method is called with an Adapter instance as an argument, like so.

<snippet-viewer source="tutorials-public" snippet="adapter-s18@ExampleAdapter.java"></snippet-viewer>

Alternatively the adapter can be started by a `SerialAdapterFactory`. This class provides a method to match serial devices, allowing the app to start the adapter when the hardware is attached to the KOS device. The method provides an instance of a SerialDevice object which is used to identify the device. Like in the example below it provides the vendor ID and product ID of the device. It provides additional methods for interacting with the serial device directly permitting the application to probe the serial device to confirm it’s identity, for situations where the VID/PID may not be robust enough. 

<snippet-viewer source="tutorials-public" snippet="adapter-s19@ArduinoSerialAdapterFactory.java"></snippet-viewer>

As alluded to in the code above, Arduinos are a common USB device used for development, so common in fact that KOS has added first class support when it comes to interacting with them. An example of how they can be integrated into KOS can be found in the Serial BLINK and Thermostat Tutorials. 
