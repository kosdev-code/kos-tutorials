package com.example;

import com.tccc.kos.ext.dispense.service.ingredient.BaseIngredient;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OurIngredient extends BaseIngredient {

    // IDs for well-known ingredients:
    public static final String WATER_ID = "water-plain";
    public static final String CARB_ID = "water-carbonated";

    public boolean carbonated;  // true if the ingredient requires carbonated water to make a beverage
    public String icon;         // URL to the ingredient's icon
    public Type type;           // type of ingredient

    public enum Type {
        water,
        carb,
        syrup,
        flavor,
    }
}
