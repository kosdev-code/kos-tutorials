package com.example;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.core.service.assembly.CoreAssembly;
import com.tccc.kos.ext.dispense.Holder;
import com.tccc.kos.ext.dispense.pipeline.pour.PourNozzlePipeline;
import com.tccc.kos.ext.dispense.service.insertion.InsertionService;
import com.tccc.kos.ext.dispense.service.nozzle.Nozzle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OurAssembly extends CoreAssembly {

    @Autowired
    private InsertionService insertionService;

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

        // Add the pour pipeline to the nozzle:
        OurPourEngine pourEngine = new OurPourEngine();
        PourNozzlePipeline pourPipeline = new PourNozzlePipeline(pourEngine);
        nozzle1.add(pourPipeline);

        // Add all of the pumps from the board to the nozzle:
        nozzle1.add(ourBoard.getPumps());

        // Create the water holders:
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

    @Override
    public void postInstall() {

        // Insert waters as intrinsic ingredients:
        insertionService.insertIntrinsic(OurIngredient.ID.PLAIN_WATER,
                ourBoard.getPlainWaterPump().getHolder());
        insertionService.insertIntrinsic(OurIngredient.ID.CARBONATED_WATER,
                ourBoard.getCarbonatedWaterPump().getHolder());

        // Insert beverage ingredients:
        insertionService.insertIntrinsic(OurIngredient.ID.COKE_SYRUP,
                ourBoard.getSyrupPumps().get(0).getHolder());
        insertionService.insertIntrinsic(OurIngredient.ID.DIET_COKE_SYRUP,
                ourBoard.getSyrupPumps().get(1).getHolder());
        insertionService.insertIntrinsic(OurIngredient.ID.ORANGE_SYRUP,
                ourBoard.getSyrupPumps().get(2).getHolder());
        insertionService.insertIntrinsic(OurIngredient.ID.GRAPE_SYRUP,
                ourBoard.getSyrupPumps().get(3).getHolder());
    }
}
