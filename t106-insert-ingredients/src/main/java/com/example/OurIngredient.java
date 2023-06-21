package com.example;

import com.tccc.kos.ext.dispense.service.ingredient.Ingredient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class OurIngredient extends Ingredient {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ID {
        //@formatter:off
        public static final String PLAIN_WATER      = "101";
        public static final String CARBONATED_WATER = "102";
        public static final String COKE_SYRUP       = "201";
        public static final String DIET_COKE_SYRUP  = "202";
        public static final String ORANGE_SYRUP     = "203";
        public static final String GRAPE_SYRUP      = "204";
        //@formatter:on
    }

    public enum Type {
        PLAIN_WATER,
        CARBONATED_WATER,
        PLAIN_WATER_BEVERAGE,
        CARBONATED_BEVERAGE,
    }

    private Type type;
}
