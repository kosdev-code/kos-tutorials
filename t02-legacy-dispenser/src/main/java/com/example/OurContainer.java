package com.example;

import com.tccc.kos.ext.dispense.Container;
import com.tccc.kos.ext.dispense.ContainerSlice;

/**
 * Simple container that contains a single slice.
 * <p>
 * Both the {@code Container} and its {@code ContainerSlice} use {@code ingredientId} as their IDs, respectively.
 */
public class OurContainer extends Container {

    /**
     * Creates a new container with the specified ingredient ID.
     */
    public OurContainer(String ingredientId) {
        super(ingredientId, new ContainerSlice(ingredientId));
    }
}
