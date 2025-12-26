/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.assembly.CoreAssembly;
import com.tccc.kos.core.service.udev.UsbId;
import com.tccc.kos.core.service.udev.serial.SerialDevice;
import com.tccc.kos.core.service.udev.serial.blink.SerialBlinkMatch;
import com.tccc.kos.core.service.udev.serial.blink.SerialBlinkMatcher;
import lombok.Getter;

/**
 * This is the assembly for the thermostat
 * An assembly is a collection of boards, in this case the assembly contains the
 * <code>ThermostatBoard</code>
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
public class ThermostatAssembly extends Assembly implements CoreAssembly, SerialBlinkMatcher {
    // Serial baud rate used by the Arduino Mega thermostat adapter.
    // Must match BLINK_BAUD in the Arduino firmware.
    private static final int BAUD_RATE = 115200;

    // USB vendor/product ID for the Arduino Mega running the thermostat adapter.
    // This is used to identify the correct serial device during Blink probing.
    private static final UsbId ARDUINO_ID = new UsbId(0x2341, 0x0042);

    @Getter
    private ThermostatBoard thermostat;

    // Create a constructor that follows the super Assembly
    public ThermostatAssembly(String name) {
        super(name);
    }

    @Override
    public void load() {
        // Load all boards in the assembly here.
        // In this tutorial we only have the thermostat board
        thermostat = new ThermostatBoard(this, "thermostat");
    }

    @Override
    public void start() {}

    @Override
    public SerialBlinkMatch matchSerialBlinkDevice(UsbId usbId, SerialDevice device) {
        if (ARDUINO_ID.equals(usbId)) {
            // Configure Blink serial parameters for the Arduino Mega.
            SerialBlinkMatch serialBlinkMatch = new SerialBlinkMatch(BAUD_RATE);

            // When a serial connection is opened, the Arduino Mega automatically resets.
            // These delays allow the board to reboot for probing
            serialBlinkMatch.setPostOpenDelayMs(1500);

            return serialBlinkMatch;
        }
        // Not a matching device
        return new SerialBlinkMatch(0);
    }
}
