package com.kosdev.samples.dispenser.part1.hardware;

import com.tccc.kos.commons.util.concurrent.future.FutureWork;
import com.tccc.kos.ext.dispense.Pump;
import lombok.Getter;

/**
 * kOS models pumps and valves using the {@link Pump} class. In this
 * example we model a common valve that is manually calibrated to
 * a fixed rate that is defined in the {@link ValveConfig} object.
 *
 * @since 1.0
 * @version 2024-09-23
 */
@Getter
public class Valve extends Pump<ValveConfig> {

    private final int pos;

    public Valve(ControlBoard controlBoard, String name, int pos) {
        super(controlBoard, name, null);
        this.pos = pos;
    }

    /**
     * Starts a time-based pour operation for this valve.
     */
    // @kdoc-valve-tpour@
    @Override
    public FutureWork tpour(int duration, double rate) {
        return ((ControlBoard) getBoard()).tpour(this, duration, rate);
    }
    // @kdoc-valve-tpour@

    /**
     * Starts a volume-based pour operation for this valve.
     */
    @Override
    public FutureWork vpour(int volume, double rate) {
        // convert the volume pour to a timed pour
        return tpour((int) (volume / rate), rate);
    }

    @Override
    public String getType() {
        return "valve";
    }
}
