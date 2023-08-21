package com.example;

import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.Board;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class OurBoard extends Board {

    private final String instanceId;
    private final OurPump plainWaterPump;
    private final OurPump carbonatedWaterPump;
    private final List<OurPump> syrupPumps;
    private final List<OurPump> allPumps = new ArrayList<>();

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

        // Create the plain water pump (P1):
        int position = 1;
        plainWaterPump = new OurPump(this, position, "P" + position, "water");

        // Create the carbonated water pump (P2):
        position++;
        carbonatedWaterPump = new OurPump(this, position, "P" + position, "water");

        // Create the syrup pumps (P3 - P6):
        syrupPumps = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            position++;
            syrupPumps.add(new OurPump(this, position, "P" + position, null));
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
}
