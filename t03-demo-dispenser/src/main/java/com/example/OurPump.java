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
    private final int position;  // pump's position on the circuit board

    public OurPump(Board board, int position, String name, String category) {
        super(board, name, category);
        this.position = position;
        config.setNominalRate(15);  // default syrup rate of 15ml/sec
    }

    @Override
    public String getType() {
        return "our.pump";
    }

    @Override
    public FutureWork tpour(int i, double v) {
        return null;
    }

    @Override
    public FutureWork vpour(int i, double v) {
        return null;
    }
}
