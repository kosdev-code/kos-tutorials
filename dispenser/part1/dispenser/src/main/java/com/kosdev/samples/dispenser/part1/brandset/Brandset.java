package com.kosdev.samples.dispenser.part1.brandset;

import com.tccc.kos.ext.dispense.service.ingredient.BaseIngredient;
import com.tccc.kos.ext.dispense.service.ingredient.IngredientSource;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a brand set that can be loaded from a JSON file.
 * This is a simple brandset that defines ingredients and
 * beverages but no content or other typical components.
 * <p>
 * This implements {@link IngredientSource} which allows the
 * kOS {@link IngredientService} to use the ingredients from
 * the brandset without any additional work.
 *
 * @since 1.0
 * @version 2024-09-23
 */
@Getter
@Setter
public class Brandset implements IngredientSource {
    static final String SOURCE_ID = "brandset";

    private List<Ingredient> ingredients;
    private List<Beverage> beverages;

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

    @Override
    public BaseIngredient getIngredient(String id) {
        return ingredients.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}