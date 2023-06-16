package com.example;

import com.tccc.kos.ext.dispense.service.ingredient.Ingredient;
import com.tccc.kos.ext.dispense.service.ingredient.IngredientSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OurBrandSet implements IngredientSource {

    private final Map<String, OurIngredient> ingredients = new HashMap<>();

    public OurBrandSet() {
        //@formatter:off
        add(OurIngredient.ID.PLAIN_WATER,      "Water",       OurIngredient.Type.PLAIN_WATER);
        add(OurIngredient.ID.CARBONATED_WATER, "Carb",        OurIngredient.Type.CARBONATED_WATER);
        add(OurIngredient.ID.COKE_SYRUP,       "Coke",        OurIngredient.Type.CARBONATED_BEVERAGE);
        add(OurIngredient.ID.DIET_COKE_SYRUP,  "Diet Coke",   OurIngredient.Type.CARBONATED_BEVERAGE);
        add(OurIngredient.ID.ORANGE_SYRUP,     "Orange Soda", OurIngredient.Type.PLAIN_WATER_BEVERAGE);
        add(OurIngredient.ID.GRAPE_SYRUP,      "Grape Soda",  OurIngredient.Type.PLAIN_WATER_BEVERAGE);
        //@formatter:on
    }

    private void add(String id, String name, OurIngredient.Type type) {
        OurIngredient ingredient = new OurIngredient();
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setType(type);
        ingredients.put(id, ingredient);
    }

    @Override
    public Collection<? extends Ingredient> getIngredients() {
        return ingredients.values();
    }

    @Override
    public Ingredient getIngredient(String id) {
        return ingredients.get(id);
    }
}
