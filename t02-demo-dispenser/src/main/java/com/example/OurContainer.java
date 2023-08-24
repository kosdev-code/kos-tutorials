package com.example;

import com.tccc.kos.ext.dispense.Container;
import com.tccc.kos.ext.dispense.ContainerSlice;

/**
 * Simple container that contains a single slice.
 * Both the Container and its ContainerSlice use ingredientId as their IDs.
 */
public class OurContainer extends Container {

    /**
     * Creates a new container with the specified ingredient ID.
     */
    public OurContainer(String ingredientId) {
        super(ingredientId, new ContainerSlice(ingredientId));
    }
}
