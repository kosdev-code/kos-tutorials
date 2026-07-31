---
title: Thermostat Tutorial Introduction
excerpt: Build an end-to-end KOS application, covering backend logic, hardware integration, and user interface integration
categories: [Thermostat Backend]
tags: [Tutorial]
date: 2026-07-10 00:00:00
status: publish
author: sneh
_yoast_wpseo_focuskw: Application
---

This tutorial series demonstrates how to build an end-to-end thermostat application, covering backend logic, hardware integration, and user interface integration.

[kondra_note type="admin-alert" title="Target Audience"]
This tutorial assumes you have completed the [Your First KOS App](https://staging23.kosdev.com/articles/rack_tutorial_tutorial_md/#kondra-heading-6) tutorial. The source code for the thermostat tutorials is available on [GitHub](https://github.com/kosdev-code/kos-tutorials/tree/main/thermostat).
[/kondra_note]

The goal of this tutorial is not to create a production-ready thermostat, but to clearly illustrate how KOS concepts work together in practice—including connecting logical and physical Boards, building a hardware simulator, using configs, services, the message broker, and applications.

By the end of this tutorial, the reader will have a working system that:
* Reads the environment temperature
* Allows temperature set points to be configured
* Switches logically between heating, cooling, and off modes
* Supports both simulated and real hardware backends 
* Reflects all system components in a clean, browser-based UI

The tutorial mirrors how real systems are designed and evolved in practice. It begins with abstraction and logical structure, then progressively introduces simulation, user interfaces, and finally integration with physical hardware.
