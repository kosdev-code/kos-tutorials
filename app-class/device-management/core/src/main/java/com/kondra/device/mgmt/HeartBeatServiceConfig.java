package com.kondra.device.mgmt;

import com.kosdev.kos.commons.core.service.config.ConfigBean;
import com.kosdev.kos.commons.core.service.config.annotations.ConfigDesc;
import com.kosdev.kos.commons.core.service.config.annotations.ConfigFormat;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HeartBeatServiceConfig extends ConfigBean {
    @ConfigDesc(value = "Frequency of hearbeats", format = ConfigFormat.MS)
    private int hearBeatInterval = 5000;
}


