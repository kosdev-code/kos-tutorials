package com.example;

import com.tccc.kos.commons.core.service.config.annotations.ConfigDesc;
import com.tccc.kos.core.service.app.BaseAppConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class OurAppConfig extends BaseAppConfig {

    //@formatter:off
    public final static double DEFAULT_WATER_FLOW_RATE =  71.0;  //  71 ml/sec (~2.5 oz)
    public final static int    DEFAULT_MAX_POUR_VOLUME = 946;    // 946 ml (~33 oz)
    //@formatter:on

    @ConfigDesc("Flow rate for both plain and carbonated waters (ml/sec)")
    private double waterFlowRate = DEFAULT_WATER_FLOW_RATE;

    @ConfigDesc("Maximum amount of liquid that can be poured for any one press of the button (ml)")
    private int maxPourVolume = DEFAULT_MAX_POUR_VOLUME;
}
