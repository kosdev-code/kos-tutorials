package com.example;

import com.tccc.kos.commons.core.service.config.annotations.ConfigDesc;
import com.tccc.kos.ext.dispense.PumpConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class OurPumpConfig extends PumpConfig {

    @ConfigDesc("Ratio of amount of water required for one part ingredient")
    private double waterToIngredientRatio;

    public OurPumpConfig() {
        waterToIngredientRatio = 4.5;  // default ratio of water to ingredient
    }
}
