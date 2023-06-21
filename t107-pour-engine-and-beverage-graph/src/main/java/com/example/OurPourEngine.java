package com.example;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.util.concurrent.future.FutureWork;
import com.tccc.kos.ext.dispense.pipeline.pour.PourEngine;
import com.tccc.kos.ext.dispense.pipeline.pour.PourEngineConfig;
import com.tccc.kos.ext.dispense.pipeline.pour.Pourable;
import com.tccc.kos.ext.dispense.pipeline.pour.graph.BevGraphBuilder;
import com.tccc.kos.ext.dispense.pipeline.pour.graph.BeverageNode;
import com.tccc.kos.ext.dispense.service.ingredient.IngredientService;

public class OurPourEngine extends PourEngine<PourEngineConfig> {

    @Autowired
    private IngredientService ingredientService;

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void rebuildGraph(BevGraphBuilder builder) {

        // Add an ingredient node to each pump:
        builder.addIngredientNodes();

        // Process ingredients available in the builder:
        for (String ingredientId : builder.getIngredientIds()) {

            // Lookup the ingredient:
            OurIngredient ingredient = (OurIngredient)getIngredient(ingredientId);

            // We don't have a beverage that consists only of carbonated water:
            if (ingredient.getType() == OurIngredient.Type.CARBONATED_WATER) {
                continue;
            }

            // Build a beverage for the given ingredient:
            String beverageId = "Bev:" + ingredient.getName();
            builder.add(new BeverageNode(beverageId));
            builder.addChild(beverageId, ingredient.getId());
            builder.addBev(beverageId);

            // Construct a beverage from the ingredient:
            switch (ingredient.getType()) {
                // Combine syrup with plain water to form a beverage:
                case PLAIN_WATER_BEVERAGE -> {
                    builder.addChild(beverageId, OurIngredient.ID.PLAIN_WATER);
                }
                // Combine syrup with carbonated water to form a beverage:
                case CARBONATED_BEVERAGE -> {
                    builder.addChild(beverageId, OurIngredient.ID.CARBONATED_WATER);
                }
                // Plain water is not combined with any other ingredients:
                case PLAIN_WATER -> {
                    // do nothing
                }
                // Shouldn't get here:
                default -> {
                }
            }
        }
    }

    @Override
    public Pourable getPourable(String id) {
        return null;
    }

    @Override
    public FutureWork pour(Pourable pourable, double volume) {
        return null;
    }
}
