package com.example;

import com.tccc.kos.ext.dispense.service.ingredient.BaseIngredient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class OurIngredient extends BaseIngredient {

    //@formatter:off
    public static final String TYPE_WATER  = "water";
    public static final String TYPE_CARB   = "carb";
    public static final String TYPE_SYRUP  = "syrup";
    public static final String TYPE_FLAVOR = "flavor";
    //@formatter:on

    public boolean carbonated;  // true if the ingredient requires carbonated water to make a beverage (otherwise plain water)
    public String icon;         // URL to the ingredient's icon

    /**
     * IDs for the ingredients in our demo dispenser.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ID {
        //@formatter:off
        public static final String PLAIN_WATER = "water-plain";
        public static final String CARB_WATER  = "water-carbonated";
        public static final String COCA_COLA   = "coca-cola";
        public static final String DIET_COKE   = "diet-coke";
        public static final String ORANGE_SODA = "fanta-orange";
        public static final String GRAPE_SODA  = "fanta-grape";
        //@formatter:on
    }
}
