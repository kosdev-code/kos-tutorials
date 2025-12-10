/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The mode of the thermostat: off, heating, cooling
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-10
 */
@Getter
@RequiredArgsConstructor
public enum Mode {
    OFF("#AFB2B4"),
    HEATING("#FFA69E"),
    COOLING("#83C9F4");

    private final String color;
}
