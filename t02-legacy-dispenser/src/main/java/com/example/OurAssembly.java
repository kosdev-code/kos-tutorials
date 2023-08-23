package com.example;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.core.service.assembly.CoreAssembly;
import com.tccc.kos.ext.dispense.Holder;
import com.tccc.kos.ext.dispense.pipeline.pour.PourNozzlePipeline;
import com.tccc.kos.ext.dispense.service.insertion.InsertionService;
import com.tccc.kos.ext.dispense.service.nozzle.Nozzle;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OurAssembly extends CoreAssembly {

    @Getter
    private OurBoard ourBoard;
    @Autowired
    private InsertionService insertionService;

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
        nozzle1.add(ourBoard.getAllPumps());

        // Create the water holders PW and CW:
        add(new Holder(this, "PW", ourBoard.getPlainWaterPump()));
        add(new Holder(this, "CW", ourBoard.getCarbonatedWaterPump()));

        // Create the syrup holders S1 - S4:
        int i = 0;
        for (OurPump pump : ourBoard.getSyrupPumps()) {
            i++;
            String holderName = "S" + i;
            add(new Holder(this, holderName, pump));
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
        // Insert plain and carbonated water as intrinsic ingredients:
        insertionService.insertIntrinsic(OurIngredient.ID.PLAIN_WATER, ourBoard.getPlainWaterPump().getHolder());
        insertionService.insertIntrinsic(OurIngredient.ID.CARB_WATER, ourBoard.getCarbonatedWaterPump().getHolder());

        // We are hard-coding this list of ingredients to make this example
        // easier to understand, as we know that the syrup pumps are in this order:
        List<String> ingredientIds = List.of(
                OurIngredient.ID.COCA_COLA, OurIngredient.ID.DIET_COKE,
                OurIngredient.ID.ORANGE_SODA, OurIngredient.ID.GRAPE_SODA);

        // Insert the syrup ingredients:
        List<OurPump> syrupPumps = ourBoard.getSyrupPumps();
        for (int i = 0; i < syrupPumps.size(); i++) {
            OurContainer ourContainer = new OurContainer(ingredientIds.get(i));
            String holderPath = syrupPumps.get(i).getHolder().getPath();
            insertionService.insert(false, ourContainer, holderPath);
        }
    }
}
