package com.kondra.lessons;

import com.kosdev.kos.commons.core.service.config.ConfigBean;
import com.kosdev.kos.commons.core.service.config.annotations.ConfigDesc;
import com.kosdev.kos.commons.core.service.config.annotations.ConfigFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataGeneratorConfig extends ConfigBean {

    @ConfigDesc(value = "How often a trouble is generated in MS", format = ConfigFormat.MS)
    private int troublesInterval = 5000; 
    @ConfigDesc(value = "How often a analytic event is generated in MS", format = ConfigFormat.MS)
    private int analyticsInterval = 5000; 
     
}
