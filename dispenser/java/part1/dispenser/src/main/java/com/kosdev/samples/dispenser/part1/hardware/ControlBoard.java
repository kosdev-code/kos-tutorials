package com.kosdev.samples.dispenser.part1.hardware;

import com.tccc.kos.commons.util.KosUtil;
import com.tccc.kos.commons.util.concurrent.future.FutureEvent;
import com.tccc.kos.commons.util.concurrent.future.FutureWork;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.ext.dispense.DispenseAssembly;
import com.tccc.kos.ext.dispense.Pump;
import com.tccc.kos.ext.dispense.PumpBoard;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a simple control board that contains one water valve and
 * four syrup valves. This is modeled after typical hardware where
 * each valve is assigned a position and turning valves on and off
 * involves sending the list of valve positions to the actual hardware
 * as part of a pour command.
 *
 * @since 1.0
 * @version 2024-09-23
 */
@Slf4j
@Getter
@Setter
public class ControlBoard extends PumpBoard {
    private final Valve waterValve;
    private final Valve carbValve;
    private final List<Pump<?>> syrupValves;

    public ControlBoard(Assembly assembly, String name) {
        super(assembly, name);

        // Board contains one water valve and four syrup valves
        waterValve = new Valve(this, "water", 0);
        carbValve = new Valve(this, "carb", 1);
        syrupValves = new ArrayList<>();
        for (int i = 2; i <= 5; i++) {
            syrupValves.add(new Valve(this, "syrup-" + i, i));
        }
    }

    /**
     * Valve.tpour() calls this, allowing a central location to manage
     * the list of valve positions that should be on or off. This sample
     * code simply logs the pump state using a timer.
     */
    // @kdoc-board-tpour@
    public FutureWork tpour(Valve pump, int duration, double rate) {
        // Create a future to turn the pump on for the requested duration
        FutureWork future = new FutureWork("tpour" + "-" + pump.getName(), f -> {
            log.info("start: {}", pump.getName());
            pump.recordRate(pump.getConfig().getNominalRate());
            KosUtil.scheduleCallback(() -> f.success(), duration);
            // TODO: turn on the valve
        });

        // Add a completion handler to turn off the valve, which is
        // called when the timer fires or if the pour is cancelled
        future.append("stop", FutureEvent.COMPLETE, f -> {
            log.info("stop: {}", pump.getName());
            pump.recordRate(0);
            // TODO: turn off the valve
        });

        return future;
    }
    // @kdoc-board-tpour@

    @Override
    public String getType() {
        return "controlBoard";
    }

    @Override
    public String getInstanceId() {
        return null;
    }
}
