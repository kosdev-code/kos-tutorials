package com.example;

import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.Board;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class OurBoard extends Board {

    private final String instanceId;
    @Getter
    private final OurPump plainWaterPump;
    @Getter
    private final OurPump carbonatedWaterPump;
    @Getter
    private final List<OurPump> syrupPumps;
    @Getter
    private List<OurPump> pumps;

    /**
     * Creates our pumps at the given positions:
     * - 1) pw: plain water
     * - 2) cw: carbonated water
     * - 3) s1: syrup #1 (don't care what the ingredients are at this time)
     * - 4) s2: syrup #2
     * - 5) s3: syrup #3
     * - 6) s4: syrup #4
     */
    public OurBoard(Assembly assembly, String name, String instanceId) {
        super(assembly, name);
        this.instanceId = instanceId;

        // Create the plain water pump (#1):
        plainWaterPump = new OurPump(this, 1, "P1", "water");

        // Create the carbonated water pump (#2):
        carbonatedWaterPump = new OurPump(this, 2, "P2", "water");

        // Create the syrup pumps (#3 - #6):
        syrupPumps = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            syrupPumps.add(new OurPump(this, i + 2, "P" + i, null));
        }

        // Create list of all pumps:
        pumps = new ArrayList<>();
        pumps.add(plainWaterPump);
        pumps.add(carbonatedWaterPump);
        pumps.addAll(syrupPumps);
    }

    @Override
    public String getType() {
        return "our.boardType";
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }
}
