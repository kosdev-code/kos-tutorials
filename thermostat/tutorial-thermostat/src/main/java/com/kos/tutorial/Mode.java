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
    OFF("#808080"),
    HEAT("#FF0000"),
    COOL("#0000FF");

    private final String color;
}
