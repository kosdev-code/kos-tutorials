/**
 * (C) Copyright 2024, TCCC, All rights reserved.
 */
package com.kosdev.samples.dispenser.part2.pour;

import com.tccc.kos.ext.dispense.pipeline.beverage.Pourable;
import lombok.Getter;

import java.io.IOException;

/**
 * kOS abstracts how beverages are poured into a {@link Pourable}.
 * This allows a beverage to be defined as a simple beverageId or
 * as complex as a dynamic recipe. In this example we'll simply
 * use a beverageId lookup from the brandset.
 *
 * @since 1.0
 * @version 2024-09-24
 */
@Getter
public class BevPourable extends Pourable {

    private final String bevId;

    public BevPourable(String bevId) throws IOException {
        this.bevId = bevId;
    }

    @Override
    public Object getDefinition() {
        return bevId;
    }
}
