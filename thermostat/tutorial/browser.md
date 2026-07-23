---
title: Connecting to a Browser-Based UI
excerpt: The backend sends real-time state updates to a browser-based UI using KOS message broker topics, enabling a reactive, event-driven interface.
categories: ["Thermostat Backend"]
tags: [Tutorial]
date: 2026-07-13 00:00:00
status: publish
author: sneh
---

## Introduction

With a fully functional backend and a working simulator in place, the final step is to connect the system to a real, browser-based user interface. A browser-based UI is more flexible and is typically how development teams build user interfaces, as opposed to the Java Swing UI used so far.

Before diving into implementation, it is helpful to review what information the UI needs and how that information should flow through the system.

## UI Requirements

At a high level, the UI needs access to the following

- User defined temperature set points: These values are changed by the user via the UI and must be sent to the backend
- Current thermostat state: This includes the current temperature and operating mode of the device, which are displayed by the UI.

Although both appear in the UI, they behave differently in the system and should be modeled differently.

## Configurations

Temperature set points represent long-lived user intent. That is, these values should persist across reboots and reflect what the user specifies.

For this reason, they are modeled as [configuration](https://kosdev.com/articles/configuration/) values on the backend. As discussed in earlier sections, we have specified configs for the minimum and maximum temperature set points. KOS provides built-in support for UI to access configuration values through the `ConfigurationService`, allowing the UI to read and update these values directly with no additional backend code required.

## Controllers and Polling

The next piece of data the UI team needs is the thermostat state. This state is represented as a simple data object that can be JSON-serialized and sent to the UI:

<snippet-viewer source="tutorials-public" snippet="thermostat-browser-state@ThermostatState.java"></snippet-viewer>

At this point, it may be tempting to model the current temperature and mode as configuration values so the UI can simply read them. This is an anti-pattern. Configs represent stable values, not ephemeral states. The thermostat state is transient. Mixing these concepts leads to confusion and bugs.

One approach the UI could take is to periodically request the thermostat state from the backend. This can be done using a controller, which exposes RESTful HTTP endpoints.

Controllers are a common concept in KOS applications. They define HTTP APIs that can be used for testing, UI, or even other applications.

A controller, `ThermostatServiceController`, for the thermostat service can be defined as follows. The class is marked as a controller using the `@ApiController` annotation. Each endpoint is defined using the `@ApiEndpoint` annotation, which specifies the `HTTP` operation type, `URL` path, and description. The supported operations are `GET`, `PUT`, `POST`, and `DELETE`.

<snippet-viewer source="tutorials-public" snippet="thermostat-browser-controller@ThermostatController.java"></snippet-viewer>

To make the controller active and visible in the "API Browser" tool on KOS Studio, it must be added to the application context:

<snippet-viewer source="tutorials-public" snippet="thermostat-browser-controller@ThermostatApp.java"></snippet-viewer>

KOS automatically prefixes endpoint paths with the parent application's ID. As a result, the endpoint can be tested using Postman at:

```
GET http://{URL}:{PORT}/api/system/thermostat/service/state
```

This approach works, but it has several drawbacks. The UI must continuously poll for updates. What if the backend does not yet have data? What if the board is not linked, or becomes temporarily unlinked?

Polling introduces unnecessary complexity and inefficiency. A better approach is for the backend to send updates when state changes occur.

### Message Broker Topics

Because thermostat state is transient, it should be communicated as events rather than polled values.

KOS provides the `MessageBroker`, which enables decoupled, topic-based communication using the publish-subscribe pattern. Publishers send messages to topics, and subscribers receive those messages without having to directly connect to the publisher.

This mechanism is ideal for sending real-time updates to the UI. In the `ThermostatService`, sending state events involves autowiring the `MessageBroker`, and sending events as follows:

<snippet-viewer source="tutorials-public" snippet="thermostat-browser-broker@ThermostatService.java"></snippet-viewer>

Each time the temperature or mode changes, a message is published. Any UI subscribed to this topic receives the update immediately.

Topics are hierarchical, domain-oriented paths. For example, `thermostat/state` represents the thermostat feature and its state updates. Topics are defined relative to the application, and the application ID is automatically prefixed when a message is sent. The full topic for the thermostat state is:

```
Topic: /app/system/thermostat/state
```

### Viewing Messages

The simulator runs locally on the same machine as the development tools and uses `localhost`. However, external tools such as "Postman" or the "WebSocket Viewer" need a known port to connect to the simulator's HTTP/WebSocket endpoint. You can specify such a port by changing port mappings in the "Hardware Settings" of an image. These port mappings determine how external tools connect to the simulator. For tools to receive websocket traffic you will need to go to "Port Mappings" -> "HTTP" → "Team Port" (for example, 8081). This mapping will apply to everyone working with the image.

<image title="" name="image-ports.png" caption="" />

Once the port is configured, topics can be viewed using the "WebSocket Viewer" tool in KOS Studio.

Topics can also be viewed using "Postman". To do this, while the image is running, open a WebSocket connection:

```
ws://localhost:{PORT}/
```

Then subscribe to the topic by sending the following message:

```
{
  "type": "kos.broker.subscribe",
  "topics": ["/app/system/thermostat/state"]
}
```

Any published thermostat state updates should now appear in real time.

### Loading the UI KAB

On the Java side, the system app must locate the UI KAB during the initialization phase, within the `load()` method. Since this tutorial only requires a single UI KAB, it can simply be retrieved by its KAB type.

<snippet-viewer source="tutorials-public" snippet="thermostat-browser-kab@ThermostatApp.java"></snippet-viewer>

To access the contents of the KAB, the application must mount it to the Virtual File System (VFS). The VFS functions as a unified, read-only filesystem that merges multiple data sources into a single directory tree.

This mechanism reinforces the KOS principle of immutability. By mounting the KAB as a read-only source, the system ensures that the browser executes only the exact, verified assets provided in the KAB, preventing runtime modification of the UI source code.

<snippet-viewer source="tutorials-public" snippet="thermostat-browser-vfs@ThermostatApp.java"></snippet-viewer>

Once the backend application is fully initialized, in the `started()` callback, the `BrowserService` is used to launch the browser. The system resolves the destination URL using the full VFS path to the `index.html` file where the UI is rendered.

<snippet-viewer source="tutorials-public" snippet="thermostat-browser-url@ThermostatApp.java"></snippet-viewer>

## Conclusion

At this point, the backend system is fully functional and ready to connect to a browser-based UI. The UI can receive real-time updates and react immediately to changes in device state.

One major advantage of using a simulator in the backend is that it enables easy collaboration. UI developers, regardless of location, can build and test interfaces without access to physical hardware.

Everything is now in place for the UI team to build a rich, responsive browser-based interface. From the backend perspective, the next section focuses on replacing the simulator with real hardware. See the UI section to learn how the interface is built and connected.