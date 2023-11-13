package com.example;

/**
 * Interface to return the configured water flow rate.
 */
public interface WaterRateProvider {

    /**
     * Returns the rate at which water flows through its valve (in milliliters).
     */
    double getWaterFlowRate();
}
