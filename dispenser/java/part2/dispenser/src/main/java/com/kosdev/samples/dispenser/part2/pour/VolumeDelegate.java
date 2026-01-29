package com.kosdev.samples.dispenser.part2.pour;

import com.kosdev.samples.dispenser.part2.DispenserApp;
import com.kosdev.samples.dispenser.part2.DispenserAppConfig.Cup;
import com.tccc.kos.commons.core.context.annotations.Autowired;
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
    @Autowired
    private DispenserApp app;   // access to config which contains cup sizes

    @Override
    public int getMaxPourVolume() {
        return 946;  // 32 oz... kOS uses SI units
    }

    @Override
    public int getFixedVolumeByName(String name) {
        // get the cup with the specified name from the application
        // config bean, but only if it's enabled
        Cup cup = app.getConfig().getCups().stream()
                .filter(c -> c.isEnabled() && c.getName().equals(name))
                .findFirst().orElse(null);

        // return the cup size or zero if no match
        return (cup != null) ? cup.getSize() : 0;
    }
}
