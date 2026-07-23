---
title: Backend Wiring and Reactive UI
excerpt: Learn how a board communicates with hardware through IfaceClient, enabling a reactive and decoupled backend and UI.
categories: [Backend]
tags: [Tutorial]
status: publish
author: sneh
---

## Introduction 

This section focuses on preparing the `ThermostatBoard` to communicate with hardware using an `IfaceClient`. It also ensures that the UI and backend are fully wired, connected, and reactive.

## Iface

The `ThermostatBoard` is a purely logical component. The question, then, is how the logical board integrates with the physical board. In KOS, the boundary between Java and a native program is defined by a Link. A Link acts as a protocol gateway between Java and a native program. Each Link maintains one or more sessions, which run over a network connection. A session exposes a bundle of supported features, which corresponds to a particular iface responsible for a singular function. An iface defines a contract: it specifies what operations are available over that connection and how they are invoked.

Multiple ifaces can operate concurrently over the same session. On the Java side, these ifaces are bound to boards or services, allowing different parts of the system to interact with native code, such as a physical board or the filesystem.

The diagram below illustrates this idea. A single network connection to a board can carry multiple ifaces, such as a thermostat iface, a humidity iface, and a board iface. Each iface is routed to the appropriate board or service on the Java side. The board iface plays a special role: it connects to `HardwareService` and enables KOS to automatically discover and link all other ifaces on that connection to their correct targets. The diagram also shows a separate network connection that links the `FilesystemService` on the Java side to the native filesystem.

<image title="" name="native-to-java.png" caption="" />

An iface is a protocol that describes how KOS communicates with an external system. Rather than embedding hardware-specific logic inside services or boards, ifaces cleanly separate what the system can do from how it is implemented. This keeps the system modular and allows hardware implementations to change independently from the Java application.

In many KOS deployments, ifaces are implemented by native adapters that communicate with Java using BLINK. In this tutorial, the physical board is an Arduino, and KOS provides out-of-the-box support for Arduino boards, which removes the complexity involved in writing an adapter. Regardless of the firmware, the Java side remains the same.

### Accessing Hardware with IfaceClient

Ifaces are inherently transient: they only exist while the associated hardware or adapter is connected. If a device disconnects, restarts, or reconnects, the previous iface instance becomes invalid and a new one is created. Managing this lifecycle manually leads to race conditions and error handling.

To address this, KOS provides `IfaceClient`, a long-living client wrapper around an iface. An `IfaceClient` allows an object to safely interact with the hardware at any time, even when the hardware is temporarily unavailable. The client automatically tracks the active iface instance and handles lifecycle events internally.

As a result, the `ThermostatBoard` holds an instance of `IfaceClient`, which it uses to communicate with the physical hardware.

<snippet-viewer source="tutorials-private" snippet="thermostat-backend-board@ControlBoard.java"></snippet-viewer>

Optionally, the board can implement `IfaceConnectAware` to receive callbacks when the iface connects and disconnects. These hooks can be used to perform setup, cleanup, or validation as needed.

### Implementing Board Operations

To retrieve data from the hardware, the board uses the `IfaceClient`'s `from()` methods. These methods safely return a value when the iface is connected and handle error cases consistently.

<snippet-viewer source="tutorials-public" snippet="thermostat-backend-s1@ControlBoard.java"></snippet-viewer>

To send commands to the hardware, the board uses the `with()` methods, which safely sends values to the iface.

<snippet-viewer source="tutorials-public" snippet="thermostat-backend-s2@ControlBoard.java"></snippet-viewer>

With this approach the board remains a clean abstraction, while all hardware communication, lifecycle management, and error handling are delegated to the `IfaceClient`.

### Defining the ThermostatIface

The `ThermostatIface` defines the thermostat-specific messaging protocol used to communicate with the hardware. An iface groups a related set of operations into a single interface, with each iface defining its own namespace of API numbers (0 to 255). This keeps different areas of functionality isolated and easier to evolve independently. These API numbers form a contract between the Java side and the firmware running on the device.

<snippet-viewer source="tutorials-public" snippet="thermostat-backend-iface@ThermostatIface.java"></snippet-viewer>

The iface extends `BinaryMsgIface`, the base abstraction for message based communication over a `BinaryMsgSession`. When a session is established, Java and the adapter exchange the list of supported ifaces. This negotiation step decouples Java code from transport details and enables the same iface to be reused across different hardware implementations.

Inside the iface, hardware operations are exposed as typed Java methods. These methods construct `BinaryMsg` instances with the appropriate API numbers and send them through the session using helpers like `send()` or `sendAndRecvInt()`. All protocol and serialization logic lives entirely in the iface layer, allowing boards and services to interact with hardware through iface methods without needing to understand messaging details.

## Connecting Java UI to Java Backend

At this point, the thermostat has a UI, logical hardware, and associated service to manage everything. The next step is to connect Java UI with the Java backend so that the system behaves as an application.

The UI should interact with the backend in several ways:

- When a user clicks on the increase or decrease buttons, the configured set points should be updated.
- When the ambient temperature changes, the thermostat service should notify the UI so it can refresh its display.
- When the operating mode changes, the thermostat service should inform both the UI and the hardware.

This reinforces the role of the ThermostatService as the coordinator in the system. The UI need not know how the hardware works, and the hardware need not know that the UI even exists. This separation of concerns makes the system easier to develop and modify.

### BeanContext and Autowiring

For the UI to interact with the service, it needs a reference to the `ThermostatService`. While it is possible to pass references manually, this approach tightly couples components and quickly becomes unmanageable. KOS avoids this by using [dependency injection](https://kosdev.com/articles/dependency-injection/), referred to as autowiring.

KOS provides contexts, which act as containers for shared objects (beans). Components declare their dependencies with the `@Autowired` annotation, and KOS injects the correct instance automatically. For a bean to be autowired, it must be in the context, and its dependents will receive the instance added to the context, ensuring consistency across the system.

In this system, both the `ThermostatService` and the `ThermostatUI` are placed in the `ThermostatApp` context.

The system application lifecycle is important here:

- Beans added to the context during `load()` are automatically autowired before `start()` is called.
- Beans added after `load()` must be manually autowired by calling `update()` on the context.

<snippet-viewer source="tutorials-private" snippet="thermostat-backend-autowire@ThermostatApp.java"></snippet-viewer>

Inside the UI, the `ThermostatService` can now be injected directly

<snippet-viewer source="tutorials-public" snippet="thermostat-backend-autowire@ThermostatUI.java"></snippet-viewer>

This allows the UI to invoke service methods, for example updating [configuration](https://kosdev.com/articles/configuration/) values when a button is pressed.

### Ready Protocol

Because KOS initializes systems asynchronously, a dependency may be used before it is initialized and fully setup. For example, when the UI is created, the `ThermostatService` may exist, but its configuration may not yet be initialized.

To handle this safely, KOS uses a "ready" protocol. Components that require set up implement `Ready`, and components that depend on them implement `ReadyListener`. KOS services that extend `AbstractConfigurableService` already implement both.

Implementing `Ready`: If a component itself has initialization steps and needs to notify others when it is ready, it implements `Ready` and exposes a `ReadyIndicator`.

<snippet-viewer source="tutorials-private" snippet="thermostat-backend-ready@ThermostatUI.java"></snippet-viewer>

Implementing `ReadyListener`: If a component depends on one or more `Ready` objects, for example the UI depends on the `ThermostatService`, it implements `ReadyListener`. KOS will automatically detect will when all dependencies have become ready and invoke `onBeanReady()`.

<snippet-viewer source="tutorials-private" snippet="thermostat-backend-on-ready@ThermostatUI.java"></snippet-viewer>

This avoids race conditions, ensures configuration values are valid before being accessed, and removes the need for manual dependency logic.

## Listeners

Now the UI needs to react whenever the service detects a temperature or mode change. Instead of having the `ThermostatService` call the UI directly, which would tightly couple the two, a listener pattern is used. A `ThermostatListener` interface is defined with callbacks for temperature and mode updates. Any component that wants to receive those updates simply implements this interface.

<snippet-viewer source="tutorials-public" snippet="thermostat-backend-listener@ThermostatListener.java"></snippet-viewer>

Inside the `ThermostatService`, a `ListenerList<ThermostatListener>` holds every registered listener. 

<snippet-viewer source="tutorials-public" snippet="thermostat-backend-listener-list@ThermostatService.java"></snippet-viewer>

Whenever the thermostat encounters new data, it notifies all the listeners by calling their `onTemperatureChange()` and `onModeChange()` methods. The service does not need to know who these listeners are, it just broadcasts updates to the list.

<snippet-viewer source="tutorials-public" snippet="thermostat-backend-notify@ThermostatService.java"></snippet-viewer>

The UI becomes a listener by implementing `ThermostatListener`. This gives it callbacks invoked by the service. As stated earlier, both the service and UI live in the application context. By design, the `ListenerList<T>` is autowired, and any object that implements `T` is automatically registered as a listener, no manual registration or cleanup is required.

<snippet-viewer source="tutorials-public" snippet="thermostat-backend-updates@ThermostatUI.java"></snippet-viewer>

KOS handles the entire lifecycle: components appear, are recognized as listeners, are registered, and those destroyed are unregistered. This keeps the design simple, and fully decoupled.

## Conclusion

At the end of this section, the thermostat backend is fully structured and connected to the UI. The `ThermostatBoard` now has an instance of `IfaceClient`, allowing communication with hardware or a simulator. The `ThermostatService` coordinates the system, updating both the UI and hardware when temperature or mode changes occur. By using autowiring, the ready protocol, and listeners, KOS ensures that components are loosely coupled and safe from race conditions.

With these foundations in place, the system is prepared to integrate with a hardware simulator or a real device.