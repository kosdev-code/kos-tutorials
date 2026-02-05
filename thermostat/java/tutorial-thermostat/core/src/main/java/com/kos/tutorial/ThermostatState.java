/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

/**
 * Immutable representation of the state of the thermostat device.
 *
 * @param temperature The current temperature reading from the thermostat
 * @param mode        The current operational mode of the thermostat
 */
public record ThermostatState(int temperature, String mode) { }
