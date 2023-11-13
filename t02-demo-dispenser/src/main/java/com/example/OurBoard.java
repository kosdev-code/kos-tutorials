package com.example;

import com.tccc.kos.commons.util.KosUtil;
import com.tccc.kos.commons.util.concurrent.future.FutureEvent;
import com.tccc.kos.commons.util.concurrent.future.FutureWork;
import com.tccc.kos.ext.dispense.PumpBoard;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j @Getter
public class OurBoard extends PumpBoard {

    private final String instanceId;
    private final OurPump plainWaterPump;
    private final OurPump carbonatedWaterPump;
    private final List<OurPump> syrupPumps;
    private final List<OurPump> allPumps = new ArrayList<>();
    private final WaterRateProvider waterRateProvider;  // access to configured water flow rate

    /**
     * Creates our pumps at the given positions:
     * - P1: plain water
     * - P2: carbonated water
     * - P3: syrup #1 (don't care what the ingredients are at this time)
     * - P4: syrup #2
     * - P5: syrup #3
     * - P6: syrup #4
     */
    public OurBoard(OurAssembly assembly, String name, String instanceId, WaterRateProvider waterRateProvider) {
        super(name);
        this.instanceId = instanceId;
        this.waterRateProvider = waterRateProvider;

        // Create the plain water pump (P1):
        int position = 1;
        plainWaterPump = new OurPump(this, position, "P" + position, "water");
        plainWaterPump.getConfig().setWaterToIngredientRatio(1);

        // Create the carbonated water pump (P2):
        position++;
        carbonatedWaterPump = new OurPump(this, position, "P" + position, "water");
        carbonatedWaterPump.getConfig().setWaterToIngredientRatio(1);

        // Create the syrup pumps (P3 - P6):
        syrupPumps = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            position++;
            syrupPumps.add(new OurPump(this, position, "P" + position, "syrup"));
        }

        // Create list of all pumps:
        allPumps.add(plainWaterPump);
        allPumps.add(carbonatedWaterPump);
        allPumps.addAll(syrupPumps);
    }

    @Override
    public String getType() {
        return "ourBoardType";
    }

    /**
     * Sends a timed pour operation to the actual hardware.
     */
    public FutureWork doTimedPour(OurPump pump, int durationInMsec) {

        // Create a future that will turn the pump on:
        FutureWork future = new FutureWork("timed-pour:ON", f -> {
            log.info("Turning pump [{}] with ingredient [{}] ON", pump.getName(), pump.getHolder().getSlice().getIngredientId());
            pump.setPouring(true);  // let pump know it's pouring
            sendBitsToHardware();   // physically turn the pump on
            KosUtil.scheduleCallback(f::success, durationInMsec);  // mark this pour successful when duration expires
        }, durationInMsec);

        // When complete, turn the pump off; this is called for both success and cancel:
        future.append("timed-pour:OFF", FutureEvent.COMPLETE, f -> {
            log.info("Turning pump [{}] with ingredient [{}] OFF", pump.getName(), pump.getHolder().getSlice().getIngredientId());
            pump.setPouring(false);
            sendBitsToHardware();
        });

        return future;
    }

    /**
     * Simulates sending the pump bits to the physical pumps/valves.
     */
    private void sendBitsToHardware() {
        int onBits = 0;
        for (OurPump pump : allPumps) {
            if (pump.isPouring()) {
                onBits |= 1 << pump.getPosition();
            }
        }
        log.info("Hardware pump bits: [{}]", String.format("0x%08x", onBits));
    }
}
