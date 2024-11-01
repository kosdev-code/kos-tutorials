package com.kosdev.samples.dispenser.part1.pour;

import com.kosdev.samples.dispenser.part1.DispenserApp;
import com.kosdev.samples.dispenser.part1.brandset.Beverage;
import com.kosdev.samples.dispenser.part1.brandset.Brandset;
import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.util.concurrent.future.FailedFuture;
import com.tccc.kos.commons.util.concurrent.future.FutureWork;
import com.tccc.kos.commons.util.concurrent.future.ParallelFuture;
import com.tccc.kos.ext.dispense.Pump;
import com.tccc.kos.ext.dispense.pipeline.beverage.BeveragePourEngine;
import com.tccc.kos.ext.dispense.pipeline.beverage.BeveragePourEngineConfig;
import com.tccc.kos.ext.dispense.pipeline.beverage.Pourable;
import com.tccc.kos.ext.dispense.pipeline.beverage.RecipeExtractor;
import com.tccc.kos.ext.dispense.pipeline.beverage.graph.BevGraphBuilder;
import com.tccc.kos.ext.dispense.pipeline.beverage.graph.BeverageNode;

/**
 * This is used abstract a way to pour a beverage
 *
 * @since 1.0
 * @version 2024-09-09
 */
public class DispenserPourEngine extends BeveragePourEngine<BeveragePourEngineConfig> {

    @Autowired
    private DispenserApp app; // Access to the brandset

    @Override
    public void start() {
        // Nothing to do when the engine starts
    }

    @Override
    public void stop() {
        // Nothing to do when the engine stops
    }

    // @kdoc-graph@
    /**
     * Builds graph of beverage ingredients that can be used to determine if a beverage is available or not
     */
    @Override
    public void rebuildGraph(BevGraphBuilder builder) {
        // Add ingredient nodes for all the pumps
        builder.addIngredientNodes();

        // Get the brandset from the app
        Brandset brandset = app.getBrandset();

        // Process beverages from the brandset
        for (Beverage bev : brandset.getBeverages()) {
            // add the beverage to the graph
            builder.addBeverage(new BeverageNode(bev.getId()).setNote(bev.getName()));

            // beverage depends on associated ingredients
            for (String ingredientId : bev.getIngredientIds()) {
                builder.addOptionalDependency(bev.getId(), ingredientId);
            }
        }
    }
    // @kdoc-graph@

    // @kdoc-getpourable@
    @Override
    public Pourable getPourable(String bevId) throws Exception {
        return new BevPourable(bevId);
    }
    // @kdoc-getpourable@

    // @kdoc-ispourable@
    @Override
    public boolean isPourable(Pourable pourable) throws Exception {
        // Can be poured if the beverage is available
        return isAvailable(((BevPourable) pourable).getBevId());
    }
    // @kdoc-ispourable@

    // @kdoc-build-future@
    @Override
    protected FutureWork buildFuture(Pourable pourable, Object lock) {

        String bevId = ((BevPourable) pourable).getBevId();

        // Create recipe extractor to extract the pumps to use
        RecipeExtractor extractor = new RecipeExtractor(this).addIngredients(bevId);

        // If the extractor didn't find a way to pour, return a failed future
        if (!extractor.isValid()) {
            return new FailedFuture("pour", "errNotPourable");
        }

        // Sum the flow rates of all the pumps in the recipe to get the overall flow rate
        double totalRate = extractor.getPumps().stream()
                .mapToDouble(Pump::getNominalRate)
                .sum();

        // Compute duration of the pour based on effective volume and total flow rate
        int durationMs = (totalRate > 0) ? (int) (pourable.getEffectiveVolume() * 1000 / totalRate) : 0;

        // Tpour all the valves in the recipe for the specified duration
        ParallelFuture future = new ParallelFuture("pour");
        for (Pump<?> pump : extractor.getPumps()) {
            future.add(pump.tpour(durationMs, 0));
        }

        return future;
    }
    // @kdoc-build-future@
}
