---
title: Hardware Simulator
excerpt: Learn how the backend is connected to a thermostat simulator, using KOS applications, SDK dependencies, and the BLINK protocol to emulate real hardware behavior.
categories: [Backend]
tags: [Tutorial]
status: publish
author: sneh
---

## Introduction

In this section, the backend will be hooked up to a thermostat simulator. Once this step is complete, the backend becomes fully functional: temperature readings can be observed, operating modes can be changed, and the entire system behaves as if it were connected to real hardware. The simulator allows the app to be validated without requiring a physical thermostat.

## Hardware Simulator

The hardware simulator emulates a real thermostat by acting in place of a physical board that links to the Java backend through an iface. From the backend's perspective, the simulator is indistinguishable from real hardware.

KOS is application-based platform. Applications, packaged as KABs, can be added or removed from an image in KOS Studio, which makes simulation straightforward. When no physical board is available, the simulator application can be added to the image. When a physical board is present, the simulator application can be removed. No backend code changes are required. This allows anyone to run the thermostat application, regardless of whether they have the physical hardware.

As discussed earlier, the thermostat application is a system application, Every image must contain exactly one system app, which represents the primary function of the device. The simulator, in contrast, is a regular application. The simulator exists only to support development and testing.

### Simulator Application

The simulator begins as a regular KOS application, `SimulatorApp`.

<snippet-viewer source="tutorials-public" snippet="thermostat-sim-app@SimulatorApp.java"></snippet-viewer>

To represent the state of the simulated thermostat, a simple data object is implemented as follows:

<snippet-viewer source="tutorials-public" snippet="thermostat-sim-thermostat@Thermostat.java"></snippet-viewer>

This class models the physical state of the thermostat: the current temperature and the current operating mode. Its structure mirrors what a real device would expose.

KOS applications are structured around services, which manage state and behavior. In this case, `SimulatorService` is defined as a service responsible for running the thermostat simulation.

The responsibilities of this service includes:

- Creating and owning a `Thermostat` instance
- Periodically updating the temperature to simulate realistic environment behavior

For example, when the thermostat is off, the ambient temperature drifts slightly. When heating and cooling, the temperature increases and decreases respectively.

Initialization of the service looks like this:

<snippet-viewer source="tutorials-public" snippet="thermostat-sim-service-init@SimulatorService.java"></snippet-viewer>

Once the dependencies of the service are ready, a recurring task is scheduled to update the temperature:

<snippet-viewer source="tutorials-public" snippet="thermostat-sim-service-ready@SimulatorService.java"></snippet-viewer>

The temperature update logic is simple:

<snippet-viewer source="tutorials-public" snippet="thermostat-sim-service-temp@SimulatorService.java"></snippet-viewer>

The key takeaway is how concerns are separated within the simulator application: the service owns behavior, not communication to the backend.

### Application Dependency

The `SimulatorService` references `Mode`, which is defined within the thermostat application. This creates a dependency from the simulator on the system app. This dependency is intentional and acceptable for two reasons:

- The simulator never runs without the thermostat system application
- Sharing `Mode` ensures that the concept and values of operational mode remain consistent across applications, without duplication or hardcoding.

This leads to the question: how are application dependencies established in KOS?

#### SDK-Based Application Dependencies

KOS supports multiple collaboration mechanisms between applications. In this case, an SDK based dependency is used.

An SDK allows one application to reference types and methods from another application, similar to a standard Maven dependency. The difference is that KOS SDKs are generated from application source code and only contain method signatures and type definitions, not implementations.

To set up the repository so that it can generate an SDK, the associated Maven archetype must be used. This archetype can be found [here](https://kosdev.com/articles/kos_maven_archetypes/) .

To expose classes or types through the SDK, they must be annotated with @Sdk. In this case:

<snippet-viewer source="tutorials-public" snippet="thermostat-sim-sdk@Mode.java"></snippet-viewer>

To ensure that any dependents use the actual implementation at runtime, rather than the stripped SDK version, the following entry is added to `descriptor.json`. This specifies which packages or classes should be included in the application class loader:

```
"sdkClassPrefixes": ["com.kos.tutorial"]
```

Finally, the thermostat SDK is added as a dependency in the simulator's `pom.xml`.

```
<dependency>
    <groupId>com.kos.tutorial</groupId>
    <artifactId>tutorial-thermostat.sdk</artifactId>
    <version>0.0.0-SNAPSHOT</version>
</dependency>
```

### Image Setup

The image can now be configured to include both KABs. Sections are used to organize KABs: the `kos.system` section is special because every image must contain exactly one system application, while sections that start with `kos.autostart`. will automatically launch their applications after the system app starts. Therefore, the thermostat system application KAB should go in the `kos.system` section, and the simulator KAB should go in the `kos.autostart.simulator` section.

<image title="" name="image-sections.png" caption="" />

### The BLINK Protocol

At this stage, the simulator has state and behavior, but it still needs to communicate with the Java backend.

This communication uses BLINK, the binary link protocol that KOS employs to communicate with physical boards. KOS provides a Java client for BLINK, `BinaryMsgClient`, allowing simulators to be written entirely in Java. From the backend's perspective, this client behaves exactly like a hardware device on the network. This is an illustration of the linkage:

<image title="" name="single-session.png" caption="" />

The thermostat simulator must expose two ifaces:

- `ThermostatIface`, which is the core iface. This links to the logical board in the Java backend.
- The `BoardIface`, which connects to the `HardwareService` and enables KOS core to handle discovery and linking of other ifaces on the network connection.

Ifaces represent contracts. Each iface defines a set of API numbers, and each API number must have a corresponding handler. This is the implementation of the `ThermostatClient` which bridges the gap between the simulated thermostat and the backend.

<snippet-viewer source="tutorials-public" snippet="thermostat-sim-client@ThermostatClient.java"></snippet-viewer>

The `SimulatorService` creates and starts the `ThermostatClient` as follows:

<snippet-viewer source="tutorials-public" snippet="thermostat-sim-blink@SimulatorService.java"></snippet-viewer>

At this point, the backend believes it is communicating with a real thermostat board.

## Conclusion

The backend is now fully connected to a thermostat simulator. This section introduced many core concepts including application based system design, application dependencies and BLINK protocol communication.

With a fully functional backend in place, the system is ready to be connected to a real, browser-based user interface in the next section.