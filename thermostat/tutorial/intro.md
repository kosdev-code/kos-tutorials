---
title: Thermostat Tutorial Introduction
excerpt: Build an end-to-end KOS application, covering backend logic, hardware integration, and user interface integration
categories: [backend-thermostat]
tags: [Tutorial]
status: publish
author: sneh
---

This tutorial series demonstrates how to build an end-to-end thermostat application, covering backend logic, hardware integration, and user interface integration. Before starting this tutorial, the reader should complete the “Your First KOS App” tutorial, which explains how to create a simple System Application in KOS and run it using KOS Studio. That tutorial establishes the core structure and tooling that are used in KOS.

The goal of this tutorial is not to create a production-ready thermostat, but to clearly illustrate how KOS concepts work together in practice—including connecting logical and physical Boards, building a hardware simulator, using configs, services, the message broker, and applications.

By the end of this tutorial, the reader will have a working system that:
* Reads the environment temperature
* Allows temperature set points to be configured
* Switches logically between heating, cooling, and off modes
* Supports both simulated and real hardware backends 
* Reflects all system components in a clean, browser-based UI

The tutorial mirrors how real systems are designed and evolved in practice. It begins with abstraction and logical structure, then progressively introduces simulation, user interfaces, and finally integration with physical hardware.
