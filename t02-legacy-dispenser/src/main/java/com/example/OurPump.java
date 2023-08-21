package com.example;

import com.tccc.kos.commons.util.concurrent.future.FutureWork;
import com.tccc.kos.core.service.hardware.Board;
import com.tccc.kos.ext.dispense.Pump;
import com.tccc.kos.ext.dispense.PumpConfig;
import lombok.Getter;
import lombok.Setter;

public class OurPump extends Pump<PumpConfig> {

    @Getter @Setter
    private PumpConfig config = new PumpConfig();  // pump's configuration data
    @Getter
    private final int position;  // valve's position on the circuit board

    public OurPump(Board board, int position, String name, String category) {
        super(board, name, category);
        this.position = position;
        ///config.setNominalRate(15);  // default syrup rate of 15ml/sec
    }

    @Override
    public String getType() {
        return "ourPumpType";
    }

    /**
     * Compute the nominal rate for this valve based on the water rate
     * and the configured ratio for the valve.
     */
    /*
    @Override
    public double getNominalRate() {
        // Get the water flow rate from the board:
        ///double waterRate = ((MFVBoard)getBoard()).getWaterRateProvider().getWaterFlowRate();

        // Return the target flow rate from the water ratio:
        return 0; ///waterRate / config.getRatio();
    }
    */

    /**
     * Timed pour: pour for the specified amount of time (in millisecconds).
     */
    @Override
    public FutureWork tpour(int duration, double rate) {
        // Ignore rate as this is a fixed value for valves:
        return null; ///((MFVBoard) getBoard()).tpour(this, duration);
    }

    /**
     * Volume pour: pour the specified volume (in millilieters).
     */
    @Override
    public FutureWork vpour(int volume, double rate) {
        // Ignore the provided rate and use the nominal rate based on water and ratio:
        ///rate = getNominalRate();

        // Convert to a tpour based on nominal rate (multiply by 1000 to get time in msec):
        return null; ///tpour((int)((volume * 1000) / rate), rate);
    }
}
