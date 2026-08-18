/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import lombok.Getter;
import lombok.Setter;

/**
 * This is the thermostat object which stores information related to the state of the thermostat
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
@Getter @Setter
public class Thermostat {
    private double temp;
    private int mode;

    public Thermostat(double temp, int mode) {
        this.temp = temp;
        this.mode = mode;
    }
}
