package com.kosdev.samples.dispenser.part1.pour;

import com.tccc.kos.ext.dispense.pipeline.beverage.BeveragePipelineDelegate;

/**
 * kOS provides rich beverage pouring infrastructure out of the box, including
 * hold-to-pour, fixed volume pours, split pours, layered pours and so on.
 * This delegate is used to define sizes to the pour engine. While this example
 * only uses simple hold-to-pour which requires {@code maxPourVolume}, this
 * delegate can also provide named volumes, such as cup sizes, for different
 * types of pours.
 *
 * @since 1.0
 * @version 2024-09-24
 */
public class VolumeDelegate implements BeveragePipelineDelegate {

    @Override
    public int getMaxPourVolume() {
        return 946;  // 32 oz... kOS uses SI units
    }
}
