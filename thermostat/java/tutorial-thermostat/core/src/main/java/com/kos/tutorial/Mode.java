/**
 * (C) Copyright 2025 Kondra. All rights reserved.
 */
package com.kos.tutorial;

import com.tccc.kos.sdk.annotations.Sdk;

/**
 * The mode of the thermostat: off, heating, cooling
 *
 * @author Sneh Gupta (sneh@kondra.com)
 * @version 2025-12
 */
@Sdk
public enum Mode {
    OFF {
        @Override public String getColor() { return "#AFB2B4"; }
    },
    HEATING {
        @Override public String getColor() { return "#FFA69E"; }
    },
    COOLING {
        @Override public String getColor() { return "#83C9F4"; }
    };

    public abstract String getColor();
}
