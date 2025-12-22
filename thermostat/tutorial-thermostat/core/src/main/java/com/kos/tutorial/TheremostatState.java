package com.kos.tutorial;

/**
 * Immutable representation of the state of a thermostat device.
 *
 * @param temperature The current temperature reading from the thermostat.
 * @param mode        The current operational mode of the thermostat,
 */
public record TheremostatState(int temperature, String mode, String color) { }
