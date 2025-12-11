package com.kos.tutorial;

public interface ThermostatListener {

    public void onTemperatureChange(double temperature);
    public void onModeChange(Mode mode);
}
