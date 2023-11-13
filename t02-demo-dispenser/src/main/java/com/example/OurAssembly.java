package com.example;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.ext.dispense.pipeline.beverage.BeverageNozzlePipeline;
import com.tccc.kos.ext.dispense.service.insertion.InsertionService;
import com.tccc.kos.ext.dispense.service.nozzle.Nozzle;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OurAssembly extends Assembly {

    @Getter
    private final OurBoard ourBoard;
    @Getter
    private final Nozzle ourNozzle;
    @Autowired
    private InsertionService insertionService;
    @Autowired
    private WaterRateProvider waterRateProvider;

    public OurAssembly() {

        super("name");

        // Create the circuit board and add it to the assembly:
        ourBoard = new OurBoard(this, "board1", "1", waterRateProvider);
        ///add(ourBoard);

        // Create the nozzle and add it to the assembly:
        ourNozzle = new Nozzle("nozzle1");
        ///add(ourNozzle);

        // Add the pour pipeline to the nozzle:
        OurPourEngine pourEngine = new OurPourEngine();
        BeverageNozzlePipeline pourPipeline = new BeverageNozzlePipeline(pourEngine);
        ourNozzle.add(pourPipeline);

        // Add all of the pumps from the board to the nozzle:
        ourNozzle.add(ourBoard.getAllPumps());

        // Create the water holders PW and CW:
        ///add(new Holder(this, "PW", ourBoard.getPlainWaterPump()));
        ///add(new Holder(this, "CW", ourBoard.getCarbonatedWaterPump()));

        // Create the syrup holders S1 - S4:
        ///for (int i = 0; i < ourBoard.getSyrupPumps().size(); i++) {
        ///add(new Holder(this, "S" + (i + 1), ourBoard.getSyrupPumps().get(i)));
        ///}
    }

    ///@Override
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

    @Override
    public void load() throws Exception {
    }

    @Override
    public void start() throws Exception {
    }

    @Override
    public void stop() throws Exception {
    }
}
