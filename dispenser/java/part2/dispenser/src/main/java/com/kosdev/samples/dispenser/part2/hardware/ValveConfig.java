package com.kosdev.samples.dispenser.part2.hardware;

import com.tccc.kos.ext.dispense.PumpConfig;

/**
 * Configuration class for a valve. Every {@link Pump} in kOS has
 * a nominal flow rate (the rate used when no other rate is specified)
 * and we'll use this as the target flow rate for the valves. As this
 * is a configuration bean, kOS can automatically manage the values
 * of these beans using the configuration service, allowing the flow
 * rates to be changed as needed without code changes.
 *
 * @since 1.0
 * @version 2024-09-23
 */
public class ValveConfig extends PumpConfig {

    public ValveConfig() {
        setNominalRate(75);
    }
}
