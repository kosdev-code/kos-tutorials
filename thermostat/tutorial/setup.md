---
title: Backend Setup
excerpt: The system application, user interface, assembly, logical board, coordinating service, and persistent configuration all exist and are wired together using standard KOS patterns.
categories: [Backend]
tags: [Tutorial]
status: publish
author: sneh
---

## Introduction

This is the first section of the thermostat tutorial, which demonstrates end-to-end development of a KOS application. This section focuses on stubbing out the backend for the thermostat system application. The goal is to define the system structure and understand why all the pieces exist, without yet connecting to real or simulated hardware.

## System App

The thermostat backend is implemented as a system application, since it is the main application that will run on the thermostat device. After the application starts, it launches a Java Swing UI on the UI thread. This Java Swing UI is a placeholder, and will be replaced by a real browser based UI later in the tutorial.

<snippet-viewer source="tutorials-private" snippet="thermostat-setup-s1@ThermostatApp.java"></snippet-viewer>

## Java Swing UI

A simple Java Swing UI is created in the `ThermostatUI` class. Please set the screen size to be 900 x 450 in the hardware profile of the image for the UI to display correctly. At this stage, the UI displays dummy values only and does not yet communicate with the backend.

The UI includes:

- The environment temperature
- The user defined set temperatures
- A visual indicator for the current [mode](https://kosdev.com/articles/authority-and-mode/) (heating, cooling, or off) represented as a colored strip. In the future this should change depending on whether the environment temp is above, below or within the set temperatures

This establishes user facing components early, allowing the backend to be wired incrementally.

## Logical Board

The first component a thermostat needs is a temperature sensor. In KOS, physical [hardware is abstracted](https://kosdev.com/articles/hardware-abstraction/) logically as a `Board`. A `Board` can be created without connected hardware. Initially, the logical board is unlinked, and any operations on it will fail gracefully, indicating that no hardware is connected. Once linked, the board begins handling events and sending operations to the physical device. This allows the system to be developed and tested safely, even before hardware is available. Additionally, this approach defines which hardware is expected, letting KOS detect missing or non-functional devices. Even without hardware, the logical `Board` allows event handlers to be wired, simplifying setup and enabling full support for hot-swappable devices.

Using this approach, a `ControlBoard` can be created by extending the `Board` class and defining two key identifiers: the board type and the instance ID. These identifiers ensure that, when linking to real hardware, the system can correctly match logical boards to their physical counterparts. The board type differentiates different types of physical boards, while the instance ID differentiates multiple instances of the same board type.

<snippet-viewer source="tutorials-public" snippet="thermostat-setup-board@ControlBoard.java"></snippet-viewer>

At this stage, the board does not perform any real actions and is not linked to a physical device. Its purpose is to define the contract for future hardware integration. For a thermostat, this contract is simple: the system must be able to read the current environment temperature and send a mode change (off, heating, or cooling) to the hardware. To capture this intent, stub methods are defined on the board.

<snippet-viewer source="tutorials-public" snippet="thermostat-setup-s3@ControlBoard.java"></snippet-viewer>

Defining these methods early allows the backend logic, services, and UI to be developed incrementally, without coupling the overall system structure to the availability of hardware.

## **Assembly**

With a logical board defined, the next question is how does the system discover and manage hardware in a reliable way. In KOS, related hardware components are grouped into an `Assembly`, which represents a logical view of a single-use device. An `Assembly` acts as a single atomic unit of hardware, allowing boards to be created and managed together.

In this tutorial, the thermostat application installs a single assembly, `ThermostatAssembly`, which contains the thermostat board. When an assembly is installed, KOS invokes its lifecycle methods. This ensures that boards are created predictably and helps avoid race conditions that occur when hardware is installed manually. The assembly's `load()` method is responsible for instantiating the `ControlBoard`. The assembly implements `CoreAssembly`, indicating that it represents the device's core functionality and cannot be uninstalled.

<snippet-viewer source="tutorials-public" snippet="thermostat-setup-assembly@ThermostatAssembly.java"></snippet-viewer>

For the assembly's lifecycle events to be triggered, it must be installed by the system application. When the `ThermostatApp` starts, it installs the thermostat assembly. The system application provides a convenience method, `installAssembly()`, which handles installation.

<snippet-viewer source="tutorials-public" snippet="thermostat-setup-install-assembly@ThermostatApp.java"></snippet-viewer>

At this point, the thermostat's logical hardware is fully defined and registered with the system. Even though no physical device is connected yet, KOS now has a clear expectation of what hardware should exist, allowing higher-level components, such as services and listeners, to be wired safely and consistently.

## Thermostat Service

With a basic user interface defined and the logical hardware instantiated, consider where the behavior of the thermostat should live. In KOS, system coordination and control logic are placed in services. The `ThermostatService` acts as a coordinator of the thermostat. Its role is to read the environment temperature from the thermostat board, compare that value against the user defined temperature range, determine the correct operating mode, and send that mode to the board. By centralizing this logic in a service, the system remains easy to understand and modify, while the UI and hardware layers remain focused and modular.

At this stage of the tutorial, the service cannot yet perform meaningful work, because the board is not yet linked to physical hardware. However, the expected behavior can still be defined by stubbing out the service methods. This allows the full Java-side system to exist and compile safely, even in the absence of hardware.

<snippet-viewer source="tutorials-public" snippet="thermostat-setup-service@ThermostatService.java"></snippet-viewer>

Since the service must interact with the thermostat hardware, it needs a reference to the thermostat board. Rather than manually wiring this dependency, the service listens for the installation of the assembly that contains the board. By implementing `AssemblyListener`, the service is notified when the `ThermostatAssembly` is installed and can safely retrieve the board instance at the appropriate point in the lifecycle.

<snippet-viewer source="tutorials-public" snippet="thermostat-setup-get-board@ThermostatService.java"></snippet-viewer>

This pattern ensures that the service only accesses hardware once it is known to exist, reducing race conditions and tightly coupling service behavior to the assembly lifecycle. Even though the board is not yet linked to real hardware, the service can now be fully structured.

## Configurations

Another essential part of a thermostat is the ability for users to configure their desired temperature range. These values must persist across reboots. KOS handles this using configs.

Using configs for temperature set points is a deliberate design choice. Configs values represent long-lived user intent, rather than transient system state. In contrast, the environment temperature is inherently ephemeral and should not be modeled as a config. By clearly separating persistent data from real-time or event-driven data, the system becomes easier to reason about and avoids bugs.

To define persistent [configuration](https://kosdev.com/articles/configuration/), a `ConfigBean` is created. Each configuration field includes a description and a default value. The bean is annotated with `@Getter` and `@Setter` Lombok annotations to allow the values to be accessed and updated at runtime. These annotations also ensure the fields appear in the "Configuration Viewer" tool in KOS Studio, making them visible and adjustable during development and testing.

<snippet-viewer source="tutorials-public" snippet="thermostat-setup-config@ThermostatServiceConfig.java"></snippet-viewer>

Next, the config bean is owned by the service that coordinates thermostat logic. By extending `AbstractConfigurableService`, the `ThermostatService` can access its configuration by calling `getConfig()`. Configs are not available in the service constructor, since configuration initialization occurs later in the lifecycle. Once the service is fully initialized, the config values can be safely read and updated, ensuring consistent behavior across restarts while keeping runtime logic clean and predictable.

## Implement ThermostatService Logic

Underneath the hood, a thermostat is fundamentally a control loop: it periodically samples the environment, evaluates conditions, and reacts.

This tutorial implements the logic behind the `ThermostatService`, which defines a periodic callback that re-evaluates system state once per second. Each time the callback fires OR the configurations change, the service reads the current environment temperature from the thermostat board, compares that value against the configured minimum and maximum set points, and determines the appropriate operating mode. The selected mode is then sent back to the board.

<snippet-viewer source="tutorials-public" snippet="thermostat-setup-timer@ThermostatService.java"></snippet-viewer>

Even though no real or simulated hardware is connected yet, this control loop can still be defined structurally. Doing so allows the behavior of the service to be reasoned about independently from the physical hardware implementation. The logic exists, compiles, and can be reviewed and tested as part of the Java backend, despite the `ControlBoard` methods themselves currently being stubs.

The implementation uses `AdjustableCallback`, one of several built-in timer utilities provided by KOS. These utilities abstract away the complexity of thread management and lifecycle handling. KOS is responsible for creating, scheduling, and cleaning up callbacks, allowing services to focus purely on behavior instead of infrastructure.

## Conclusion

At the end of this section, the complete logical structure of the thermostat system is in place. The system application, user interface, assembly, logical board, coordinating service, and persistent configuration all exist and are wired together using standard KOS patterns.

However, it should be noted that nothing is functional at this stage. The logical board is not linked to hardware, so readings cannot be obtained, nor can mode changes be sent. This represents a critical milestone. By separating structural correctness from physical execution, KOS allows complex systems to be designed, reasoned about, and validated before any hardware is introduced. The next step is to finish the backend and hook it up to the UI so the Java Swing interface responds dynamically.