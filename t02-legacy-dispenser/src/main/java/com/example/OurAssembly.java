package com.example;

import com.tccc.kos.core.service.assembly.CoreAssembly;
import com.tccc.kos.ext.dispense.Holder;
import com.tccc.kos.ext.dispense.service.nozzle.Nozzle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OurAssembly extends CoreAssembly {

    @Getter
    private OurBoard ourBoard;

    public OurAssembly() {
        buildAssembly();
    }

    private void buildAssembly() {

        // Create the circuit board and add it to the assembly:
        ourBoard = new OurBoard(this, "board1", "1");
        add(ourBoard);

        // Create the nozzle and add it to the assembly:
        Nozzle nozzle1 = new Nozzle(this, "nozzle1");
        add(nozzle1);

        // Add all of the pumps from the board to the nozzle:
        nozzle1.add(ourBoard.getAllPumps());

        // Create the water holders PW and CW:
        add(new Holder(this, "PW", ourBoard.getPlainWaterPump()));
        add(new Holder(this, "CW", ourBoard.getCarbonatedWaterPump()));

        // Create the syrup holders S1 - S4:
        int i = 0;
        for (OurPump pump : ourBoard.getSyrupPumps()) {
            i++;
            add(new Holder(this, "S" + i, pump));
        }
    }

    @Override
    public void install() {
        log.info("Installing OurAssembly..");
    }

    @Override
    public void uninstall() {
        log.info("Uninstalling OurAssembly..");
    }
}
