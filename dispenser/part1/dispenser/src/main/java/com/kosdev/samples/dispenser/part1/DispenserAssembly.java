package com.kosdev.samples.dispenser.part1;

import com.kosdev.samples.dispenser.part1.brandset.Ingredient;
import com.kosdev.samples.dispenser.part1.hardware.ControlBoard;
import com.kosdev.samples.dispenser.part1.pour.VolumeDelegate;
import com.kosdev.samples.dispenser.part1.pour.DispenserPourEngine;
import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.util.json.JsonDescriptor;
import com.tccc.kos.core.service.assembly.CoreAssembly;
import com.tccc.kos.ext.dispense.DispenseAssembly;
import com.tccc.kos.ext.dispense.HolderBuilder;
import com.tccc.kos.ext.dispense.pipeline.beverage.BeverageNozzlePipeline;
import com.tccc.kos.ext.dispense.service.insertion.InsertionService;
import com.tccc.kos.ext.dispense.service.nozzle.Nozzle;

/**
 * An assembly is a logical representation of the hardware in your device.
 * kOS models the physical parts of your device (boards, valves, nozzle, etc..)
 * and provides functionality to make all the parts work together.
 *
 * @since 1.0
 * @version 2024-09-24
 */
public class DispenserAssembly extends DispenseAssembly implements CoreAssembly {
    @Autowired
    private InsertionService insertionService; // Used to insert ingredients
    private ControlBoard board;

    public DispenserAssembly(JsonDescriptor descriptor) throws Exception {
        super("core", descriptor);
    }

    @Override
    public void load() throws Exception {

        // Create a nozzle to pour from
        Nozzle nozzle = new Nozzle(this, "nozzle");
        addNozzle(nozzle);

        // Create an instance of a board that controls several valves
        board = new ControlBoard(this, "control");
        addBoard(board);

        // Add the valves from the board to the nozzle
        nozzle.add(board.getSyrupValves());

        // Build holders for the valves starting with the water valve
        HolderBuilder builder = new HolderBuilder(this, nozzle);
        builder.buildWater(board.getWaterValve());
        builder.buildCarb(board.getCarbValve());

        // Iterate over the remaining valves and name the holders with 'S' prefix
        builder.setPumpIterator(board.getSyrupValves());
        builder.setIncrementNameIterator("S", 1);
        builder.build(board.getSyrupValves().size());

        // Create a pipeline for beverage pouring and add to the nozzle
        DispenserPourEngine engine = new DispenserPourEngine();
        nozzle.add(new BeverageNozzlePipeline(engine));
    }

    @Override
    public void start() {
        // Nothing to do after the assembly is configured and autowired
    }

    @Override
    public void started() {
        // Assign water to the water valve so it's always connected
        insertionService.insertIntrinsic(Ingredient.WATER, board.getWaterValve().getHolder());
        // Assign carb to the carb valve so it's always connected
        insertionService.insertIntrinsic(Ingredient.CARB, board.getCarbValve().getHolder());
    }
}
