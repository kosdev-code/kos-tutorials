package com.example;

import com.tccc.kos.commons.util.concurrent.future.FutureWork;
import com.tccc.kos.ext.dispense.pipeline.pour.PourEngine;
import com.tccc.kos.ext.dispense.pipeline.pour.PourEngineConfig;
import com.tccc.kos.ext.dispense.pipeline.pour.Pourable;
import com.tccc.kos.ext.dispense.pipeline.pour.graph.BevGraphBuilder;
import com.tccc.kos.ext.dispense.pipeline.pour.graph.BeverageNode;

public class OurPourEngine extends PourEngine<PourEngineConfig> {

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public Pourable getPourable(String definitionStr) throws Exception {
        return null;
    }

    @Override
    public boolean isPourable(Pourable pourable) throws Exception {
        return false;
    }

    @Override
    protected FutureWork buildFuture(Pourable pourable) {
        return null;
    }

    @Override
    public void rebuildGraph(BevGraphBuilder builder) {

        // Add ingredient nodes for every pump:
        builder.addIngredientNodes();

        // Process each ingredient available in the builder:
        for (String ingredientId : builder.getIngredientIds()) {

            // Look up the ingredient:
            OurIngredient ingredient = (OurIngredient)getIngredient(ingredientId);
            if (ingredient == null) {
                continue;
            }

            // We don't have a beverage that consists only of carbonated water:
            if (ingredient.getId().equals(OurIngredient.ID.CARB_WATER)) {
                continue;
            }

            // Build a beverage for this ingredient. In BiB-based systems, a beverage
            // is just a combination of an ingredient with plain or carbonated water.
            // This allows us to create a synthetic beverage ID for each ingredient,
            // which represents the combination of the syrup and a water to form a beverage.
            String beverageId = "bev:" + ingredientId;

            // Build a beverage for the ingredient using the synthetic beverage ID, but
            // store the ingredient ID in the alt ID field so that we can access it later.
            builder.addBeverage(new BeverageNode(beverageId, ingredientId));

            // The new beverage node depends on the associated BiB ingredient, so link them:
            builder.addDependency(beverageId, ingredientId);

            // If the ingredient is a syrup then it needs to be poured with water or carb,
            // so add a dependency for the associated water ingredient:
            if (ingredient.getType() == OurIngredient.Type.syrup) {
                String childId = ingredient.isCarbonated() ? OurIngredient.ID.CARB_WATER : OurIngredient.ID.PLAIN_WATER;
                builder.addDependency(beverageId, childId);
            }
        }
    }
}
