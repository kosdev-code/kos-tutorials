package com.kosdev.samples.dispenser.part2.brandset;

import com.tccc.kos.ext.dispense.service.ingredient.BaseIngredient;

/**
 * Represents an ingredient that can be assigned
 * to a valve. The {@link BaseIngredient} class
 * provided by kOS has all the key fields required
 * for this simple example, but a more complex
 * example would add additional properties.
 *
 * @since 1.0
 * @version 2024-09-23
 */
public class Ingredient extends BaseIngredient {
    // Ingredient id for water so we can assign it manually
    public static final String WATER = "water";
}
