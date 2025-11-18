package com.example;

import com.tccc.kos.commons.util.concurrent.future.FailedFuture;
import com.tccc.kos.commons.util.concurrent.future.FutureWork;
import com.tccc.kos.commons.util.concurrent.future.ParallelFuture;
import com.tccc.kos.commons.util.convert.Convert;
import com.tccc.kos.ext.dispense.Pump;
import com.tccc.kos.ext.dispense.pipeline.beverage.BeveragePourEngine;
import com.tccc.kos.ext.dispense.pipeline.beverage.BeveragePourEngineConfig;
import com.tccc.kos.ext.dispense.pipeline.beverage.Pourable;
import com.tccc.kos.ext.dispense.pipeline.beverage.RecipeExtractor;
import com.tccc.kos.ext.dispense.pipeline.beverage.graph.BevGraphBuilder;
import com.tccc.kos.ext.dispense.pipeline.beverage.graph.BeverageNode;
import com.tccc.kos.ext.dispense.service.ingredient.BaseIngredient;

public class OurPourEngine extends BeveragePourEngine<BeveragePourEngineConfig> {

    private static final String REASON_errUnavailable = "errBeverageIsUnavailable";

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    /**
     * Converts the given definition string to a pourable object.
     */
    @Override
    public Pourable getPourable(String definitionStr) {
        return new OurPourable(definitionStr);
    }

    @Override
    public boolean isPourable(Pourable pourable) throws Exception {
        // Grab the beverage definition from the pourable:
        String beverageId = ((OurPourable)pourable).getBeverageId();

        // Drink is pourable if the beverage is available:
        return isAvailable(beverageId);
    }

    @Override
    protected FutureWork buildFuture(Pourable pourable) {
        // Grab the beverage definition from the pourable:
        String beverageId = ((OurPourable)pourable).getBeverageId();

        // Create recipe extrator for the beverage:
        RecipeExtractor recipe = new RecipeExtractor(this, beverageId);

        // If this drink can't be poured, then return an error:
        if (!recipe.isValid()) {
            return new FailedFuture("bev-pour", REASON_errUnavailable);
        }

        // Compute the overall flow rate of the pour:
        double rate = 0;
        for (Pump<?> pump : recipe.getPumps()) {
            rate += pump.getNominalRate();
        }

        // Convert volume to time (in milliseconds):
        int duration = Convert.toInt(pourable.getEffectiveVolume() * 1000 / rate);

        // Create a future to turn on all the pumps:
        ParallelFuture future = new ParallelFuture("beverage-pour");
        for (Pump<?> pump : recipe.getPumps()) {
            future.add(pump.tpour(duration, 0));
        }

        return future;
    }

    @Override
    public void rebuildGraph(BevGraphBuilder builder) {
        // Add ingredient nodes for every pump:
        builder.addIngredientNodes();

        // Process each ingredient available in the builder:
        for (BaseIngredient ing : builder.getIngredients()) {
            OurIngredient ingredient = (OurIngredient)ing;

            // We don't have a beverage that consists only of carbonated water:
            if (ingredient.getId().equals(OurIngredient.ID.CARB_WATER)) {
                continue;
            }

            // Build a beverage for this ingredient. In BiB-based systems, a beverage
            // is just a combination of an ingredient with plain or carbonated water.
            // This allows us to create a synthetic beverage ID for each ingredient,
            // which represents the combination of the syrup and a water to form a beverage.
            String beverageId = "bev:" + ingredient.getId();

            // Build a beverage for the ingredient using the synthetic beverage ID, but
            // store the ingredient ID in the alt ID field so that we can access it later.
            builder.addBeverage(new BeverageNode(beverageId, ingredient.getId()));

            // The new beverage node depends on the associated BiB ingredient, so link them:
            builder.addDependency(beverageId, ingredient.getId());

            // If the ingredient is a syrup then it needs to be poured with water or carb,
            // so add a dependency for the associated water ingredient:
            if (ingredient.getType().equals(OurIngredient.TYPE_SYRUP)) {
                String childId = ingredient.isCarbonated() ? OurIngredient.ID.CARB_WATER : OurIngredient.ID.PLAIN_WATER;
                builder.addDependency(beverageId, childId);
            }
        }
    }
}
