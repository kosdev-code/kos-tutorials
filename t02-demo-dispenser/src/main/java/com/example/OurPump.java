package com.example;

import com.tccc.kos.commons.util.concurrent.future.FutureWork;
import com.tccc.kos.commons.util.convert.Convert;
import com.tccc.kos.ext.dispense.Pump;
import lombok.Getter;
import lombok.Setter;

public class OurPump extends Pump<OurPumpConfig> {

    @Getter @Setter
    private OurPumpConfig config = new OurPumpConfig();  // valve's configuration data
    @Getter
    private final int position;  // valve's position on the circuit board
    @Getter @Setter
    private boolean pouring;     // true when this valve is on, false when off

    public OurPump(OurBoard board, int position, String name, String category) {
        super(board, name, category);
        this.position = position;
    }

    @Override
    public String getType() {
        return "ourPumpType";
    }

    /**
     * Computes the nominal flow rate for this valve based on the water flow rate
     * and the configured water-to-ingredient ratio. Returns result in milliliters/second.
     */
    @Override
    public double getNominalRate() {
        // Retrieve the water flow rate from the board (ml/sec):
        double waterRate = ((OurBoard)getBoard()).getWaterRateProvider().getWaterFlowRate();

        // Calculate and return the ingredient flow rate based on the water-to-ingredient ratio:
        return waterRate / config.getWaterToIngredientRatio();
    }

    @Override
    public FutureWork tpour(int duration, double rate) {
        // Ignore the rate parameter because we are interfacing to a valve that is simply on or off:
        return ((OurBoard)getBoard()).doTimedPour(this, duration);
    }

    @Override
    public FutureWork vpour(int volume, double rate) {
        // Convert to a tpour based on the nominal flow rate (multiply by 1000 to get time in msec):
        int duration = Convert.toInt((volume * 1000) / getNominalRate());
        return tpour(duration, 0);
    }
}
