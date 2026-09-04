---
title: Creating and using an Adapter with KOS
excerpt: Build an adapter with KOS.
categories: [Adapter]
tags: [Tutorial]
status: publish
author: james
_yoast_wpseo_focuskw: Adapter
---

## Integrating Custom Hardware Tutorial

In this tutorial we’re going to learn how to properly integrate custom hardware into a KOS device. It’s assumed that you’ve read the first [tutorial](https://kosdev.com/articles/digital-product-rack/). The tutorial aims to be a minimal example that makes the [Hardware Abstraction Article](https://kosdev.com/articles/hardware-abstraction/) concrete. The hardware we’ll be connecting to is an Arduino type micro-controller, connected via USB. The Arduino will be connected to a button and an LED, this way we can receive events in java and send down commands to the arduino. This exercise will explain how KOS models hardware and how to write an adapter to integrate with hardware. 

While most development with KOS is done with Java on the backend, there are scenarios, like communicating with hardware, that are best suited for lower level languages. Technologies like JNI (Java Native Interface) have significant limitations when it comes to interacting with hardware, as it might crash the whole Java virtual machine. The solution that KOS provides is BLINK (Binary Link) a simple binary network protocol that provides a structured way to interact with native programs, which in KOS are called adapters. There is a kos library for Arduino that completely eliminates the need for an adapter. It’s important to learn how KOS interacts with and abstracts hardware, and the role that adapter play before moving onto the arduino [tutorial](https://kondra.atlassian.net/wiki/x/DYAxS).

You can check out the final tutorial code [here](https://github.com/kosdev-code/kos-tutorials/tree/main/adapter).

## Project Setup

KOS is built with Maven, and rather than having to hand-craft the project structure, we provide maven archetypes to get started with development quickly. You can check out the archetypes repo and install the archetypes locally by executing the following commands in the terminal.

```bash
git clone https://github.com/kosdev-code/kos-maven-archetypes
cd kos-maven-archetyps

./install_archetypes.sh
```

Now that you have the archetypes installed, navigate to a directory where you want to create your project and generate a new project using the system-app archetype to get started. 

```bash
mvn archetype:generate -DarchetypeGroupId=com.kos.archetypes -DarchetypeArtifactId=system-app
```

## Java Implementation

### Creating the System App

Every project using KOS starts with the `SystemApplication` which is a Java class and a concept.

In concept, all the Java code that will be written in this tutorial is part of the system app and represents the foundation of the device being built. Additional applications can later be added to the image to provide additional functionality, see [Applications](https://kosdev.com/articles/applications/). As a class the `SystemApplication` is the starting point of the code, and it is the first thing that runs after KOS has booted.

So to get started we create the following class. 

<snippet-viewer source="tutorials-public" snippet="adapter-s1@TutorialApp.java"></snippet-viewer>

Now we have a class with two methods `load()` and `start()`. The former is where we do some initial setup for the device before the system app is fully started. KOS has many services that make use of listeners; where you have a class that extends an interface, you then place an instance of that class into the context, and the class will receive the interface's callbacks. We’ll make use of this later in the tutorial. The `load()` method is where you would instantiate services and add listeners to the context; see [Dependency Injection](https://kosdev.com/articles/dependency-injection/). The next method, `start()`, is called right before the application is actually started, and is in place to do any final initialization. For instance, in `load()` you might set up some services, and then in `start()` you might need to make use of those services, but the device still isn’t ready to fully start yet. 

We will make use of the `start()` method in a moment, but first we must update the `descriptor.json` so that KOS knows which class is the system app and to start calling these lifecycle methods. The descriptor specifies information about the KAB, and for applications it specifies the Java `Application` class and appId. The descriptor can also hold additional data as desired, which you access within the application class by calling the `getDescriptor()` method. 

```json
{
  "kos": {
    "app": {
      "appClass": "com.kos.tutorial.TutorialApp",
      "appId": "system"
    }
  }
} 
```

### Creating the Arduino Board and Assembly

KOS abstracts hardware into logical boards which are then stored within a unified group of hardware components known as an Assembly; see [Hardware Abstraction](https://kosdev.com/articles/hardware-abstraction/). For this tutorial we only need one board. We create it like so.

<snippet-viewer source="tutorials-public" snippet="adapter-s2@ArduinoBoard.java"></snippet-viewer>

The `Board` class takes in an `Assembly` and a name in the constructor. The name is used for creating the handle, a unique name to specify a specific instance of a Java object; see [Handle Paths](https://kosdev.com/articles/handle-path/).  The board type is used to match the board to the correct BLINK connection. We will delve into this in a moment. The `instanceId` differentiates multiple instances of the same type of board. For instance, if we had two Arduinos we could have them read the input of a GPIO pin to detect which board it is. This way we know exactly which physical board we’re communicating with. But for this tutorial, since we only have one Arduino board, we can return null for the `instanceId`. 

We can now create an `Assembly` into which our board can be integrated. We do this by instantiating it in the `load()` method of the assembly class as demonstrated below. It is important that boards are instantiated in the `load()` method of the assembly, as opposed to `start()` or `started()`. Doing otherwise will result in the board not being properly integrated into the greater KOS system. Another thing to notice is that this `Assembly` implements the `CoreAssembly` interface. This means that it is integral to the KOS device and without this hardware the device would not be functional. In KOS devices there can and must be a single `CoreAssembly`.

<snippet-viewer source="tutorials-public" snippet="adapter-s3@TutorialAssembly.java"></snippet-viewer>

We have now properly structured the logical hardware components of our device and can integrate the assembly into the system app by installing the assembly in the `start()` method like so. The assembly must be installed in the `start()` method as the installation process relies on the Application’s `load()` method to complete. 

<snippet-viewer source="tutorials-public" snippet="adapter-s4@TutorialApp.java"></snippet-viewer>

### Integrating with Native Programs

We now have the virtual abstractions created and can start integrating with the hardware through an adapter, but what is an adapter? An adapter is simply a native program that uses BLINK to communicate with Java. An adapter will create a BLINK client that will manage the connection with the Java side. The adapter can then register up to 16 iface’s with the BLINK client. An iface is like a sub protocol in which you have up to 256 api numbers that you can use and give meaning to. This structure allows you to better maintain a separation of concerns if your adapter needs to make use of different parts of the Java application. You can send multiple messages concurrently using the different ifaces over the same blink connection. In this tutorial we’ll only create one iface but will make use of another, the board iface which is defined by KOS. The board iface is the way KOS links your logical board to the adapter and consequently the physical board. The board iface will be the first to connect and facilitate the connection of our own iface to the board. An iface doesn’t have to be linked to a board, and adapters are not limited to programs that interact with hardware. 

Let’s make this more concrete by creating an iface in Java like so. 

<snippet-viewer source="tutorials-public" snippet="adapter-s5@ArduinoIface.java"></snippet-viewer>

Let’s break this down, starting from the top. 

We can see that there are some `static final` fields, the first being the name of the iface, this name is used to match up the Java iface to the native C iface. On the C side we will be able to reference this iface to register handlers for when we receive a message for a particular API number. The next two fields are the API numbers. Zero represents the LED request we’re sending to the adapter and one is the event we expect to receive from the adapter when the button is pressed. The last field is a boolean to hold the state of the LED.

In the constructor we see how a request handler is registered against the API for a button press. This is the lambda that will be executed when the adapter sends a request with API number one. The handle button method is very simple we just send a message back to the adapter to turn on the LED or turn it off depending on the current state. One interesting thing of note is the way that we pass data with the request. We have to write the data exactly as we want to receive it on the adapter side. You could write additional data using other helper methods like `writeInt(int v)`, `writeCString(String s)`, `writeLong(long v)`, etc. Once you’ve written your data you can send the request with `send()`. If you’re anticipating a response from the adapter you can use `sendAndRecv()` which will return a BinaryMsg response. You can then use the correlating methods `readInt()` , `readCString()`, etc. to read data from this response. 

At the time of this writing the best way to connect an iface to a board is to have your board implement the `IfaceAwareBoard<T extends BinaryMsgIface>` interface, this will give us three new methods, one to instantiate the iface, and two life cycle events, when the iface connects and disconnects. You will not get the life cycle events if you do not pass the board as a listener in the `createIface` method. 

<snippet-viewer source="tutorials-public" snippet="adapter-s6@ArduinoBoard.java"></snippet-viewer>

We’ve now setup the Java side of the BLINK protocol setup we can proceed to write the adapter. 

## Writing the Adapter

An adapter is first and foremost a native program. It becomes an adapter when we integrate BLINK to communicate with Java. In that spirit we will get started with a simple program that, given a device name, communicates with an Arduino over serial. The protocol we’re expecting the Arduino to follow is that the adapter will send the commands `ILLUMINATE` and `EXTINGUISH` to the Arduino to turn the LED on and off respectively. In response the `Arduino` will send a message, `BUTTON`, when the button has been pressed. The code for the Arduino and a schematic can be found at the end of the tutorial.

Below is the C program that implements this simple protocol, it will turn the LED on and off every 500ms and log every time that the button is pressed.

<snippet-viewer source="tutorials-public" snippet="adapter-s7@exampleAdapter.java"></snippet-viewer>

At the top we can see that we pull in two `kos` header files, how did we get these? These are available on any KOS device that has the developer tools flag checked. Developer tools includes a bunch of tools needed for native development, including git, make, gdb, etc. see [Native Development of KOS](https://kosdev.com/articles/native-development-for-kos/). To confirm that your native programs will work on a KOS device you must compile on a KOS device.

With the KOS library we have access to some helper methods, one of which is `kosLog()`, this function will log your statements in a standard way, and can be seen in KOS Studio through the log viewer tool by selecting syslog. Alternatively you can view the logs on the device at `/mnt/datafs/logs/live/syslog*`. 

Great! We have this basic program and can now start integrating BLINK into it so that we can connect from the Arduino to the Java side. Firstly let's create the `blinkClient`, this will be the struct that represents the connection to the Java side, and is used to communicate with the Java side.  Here’s how we can do that and start processing connections from the JVM. 

<snippet-viewer source="tutorials-private" snippet="adapter-s8@arduinoAdapter.c"></snippet-viewer>

Looking at the constants we see that the name that we are passing into the blinkCreate() function is the same name as the iface we created on the Java side. This is because each BLINK connection has a core iface, or the core reason for this BLINK connection, and the core reason for this BLINK connection is to bridge the gap between the Arduino and Java. The revision number is the version of the adapter. The other arguments in the create method are server where you can specify the ip and port number of the blink server you want to connect to but for most cases you just want to connect to the JVM running on the same machine so passing null is fine. The next argument is a properties struct where you can specify properties to override defaults. The last two arguments are the size of the input buffer and output buffer, which you could override as needed, but there’s a reasonable minimum in place. After getting the blink client we can store the core iface so that we can register the APIs with a handler function and so that we can send events to Java using the iface. 

After the client has been created, we can start processing requests to connect to Java. In theory the Arduino iface could establish a connection, but the Arduino iface is only created in Java when it gets the callback for it in the board class, so how is the board supposed to know about this BLINK client? The answer is the board iface. While the core iface for this adapter is the Arduino iface, in order for this adapter to link directly to a board it needs to add the board iface to the blink client. The board iface will use the board type and instanceId to link up this adapter with the board on the Java side, since we only have one board we only have to specify the board type. We can add the board iface by using the `blinkAddBoardIface()` helper function that takes in the client and `boardIfaceData` struct. 

<snippet-viewer source="tutorials-public" snippet="adapter-s9@arduinoAdapter.c"></snippet-viewer>

Great now when we run our program while running our KOS app the board will link and will connect to our arduino iface, but how do we actually use the arduino iface to send and receive events? To receive an event we can register a function to act as a handler for a specific API using the `blinkRegisterApi` function. Let's register our sendLedCommand function to handle the `ILLUMINATE_LED` api we defined in our Java iface. We’re going to need to refactor the `sendLedCommand` function to be a `blinkApiHandler` so that it can take in the binary message, decode it and react appropriately. 

<snippet-viewer source="tutorials-public" snippet="adapter-s10@arduinoAdapter.c"></snippet-viewer>

Now we are able to receive events and react to them, we can even read data from the request as we can see with the `decodeBool()` function call. Just as we saw on the Java side where you can write data of different types into the message you can do the same on the adapter side. When decoding values from the binary message you must do so in the order you sent it from Java. If you sent two ints and a string you must decode them in that order on the C side. In response we could also encode data with a similar helper functions as we used in Java, `encodeBool()`, `encodeInt()`, etc. It’s worth noting that we are now returning an int or the length of the response, which in this case is nothing. 

What about sending an event when a button is pressed? How can we send events over the Arduino iface? This can be done very easily using  the `blinkSendMsg` function and replacing our log command in the `listenToArduino()` function. When calling the function we specify the iface over which we’re sending the event, the API number we want to use, and message data and size. 

<snippet-viewer source="tutorials-public" snippet="adapter-s11@arduinoAdapter.c"></snippet-viewer>

We now have completed the adapter, and it should function as desired. We just need to compile it and then… run it? How are we going to go about that? Firstly since everything is a KAB, see [Everything is a KAB](https://kosdev.com/articles/everything-is-a-kab/), we need to compile the program and put it in a KAB. This article shows how to set up build automation for continuously building and publishing the adapter [Native Development for KOS](https://kosdev.com/articles/native-development-for-kos/). The article also goes into detail on how to setup a shared drive with samba between your development machine and a kos device. This way you can compile the adapter on the KOS device and have the executable accessible on your development machine. Once you’ve compiled the program, you need to make a KAB and specify that the program has executable permissions this is done by creating a directory and placing the compiled executable and a .perm file which specifies the permissions. The `.perm` file would look like

```txt
755:1:1:ArduinoAdapter 
```

The final folder structure would look like this.

```txt
adapter
├── .perm
└── ArduinoAdapter
```

And you can build a kab by using kabtool on a development machine that has kosStudio installed. 

```bash
kabtool -b -t kos.adapter -dir adapter -v "0.0.0" arduino-adapter-arm64.kab
```

Now that you have a kab you can add it to your image as a local artifact. Where do I put the kab? How is the adapter going to be extracted from the kab? And how is the adapter going to be started?

The KAB can be placed in the same section as the system app; `kos.system`, it is then very easy to get the KAB in the system app by calling `getKabByType(“kos.adapter”)` This method will return the adapter KAB if it can find it. Now to extract the KAB and make it available to the system, you can autowire `FuseService` and mount the KAB like so.

<snippet-viewer source="tutorials-public" snippet="adapter-s12@TutorialApp.java"></snippet-viewer>

After mounting KAB the adapter is now on the file system at an actual location, and runnable. We can get the directory where it was mounted by accessing it from the `FuseMount` returned from the mounting call. Then there is the instantiation of a new Java object the `ArduinoAdapterFactory` this answers the last question of how is the adapter started? The `ArduinoAdapterFactory` extends `SerialAdapterFactory` which has a call to match a serial device and return an `Adapter` to be started. You can match the device any way that you'd like as you can probe the device my sending messages directly, or you can use any of the given properties and methods available in the `SerialDevice` object. In order for a `SerialAdapterFactory` to get the callback all that needs to be done is to put it in the context, and KOS will find it and notify it when there’s a new serial device. Below is the implementation of the `ArduinoAdapterFactory`, the matching logic could be more complicated, but for this example the `vendorId` an `productId` are enough to identify the Arduino.

<snippet-viewer source="tutorials-public" snippet="adapter-s13@ArduinoAdapterFactory.java"></snippet-viewer>

We now have a kos app that interacts with hardware. There are many concepts but not that much code. Arduino’s are so useful for prototyping and development that we have made it even easier to interact with them by creating an Arduino library for BLINK so you can communicate directly to Java from an Arduino. 

## Embedded Code 

Here is the schematic for the esp32. The resistor for the led was 220 Ohms and the resistor for the button was 10k Ohms.  

<image title="Esp32 Wiring Schematic" name="esp32_schematic.png" caption="" />

The code to drive the esp32 is very simple, it sets up the gpio pins for input and output, processes messages from serial and prints `BUTTON` to serial whenever the button is pressed. 

<snippet-viewer source="tutorials-public" snippet="adapter-s14@tutorial.ino"></snippet-viewer>




