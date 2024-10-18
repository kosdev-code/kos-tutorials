package com.kosdev.samples.dispenser.part2.brandset;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * Represents a beverage in the brand set. A beverage
 * has a unique id and a display name and is defined
 * by a list of ingredients that form the beverage.
 *
 * @since 1.0
 * @version 2024-09-23
 */
@Getter
@Setter
public class Beverage {
    private String id;
    private String name;
    private List<String> ingredientIds;
}