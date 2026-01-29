package com.kosdev.samples.dispenser.part2;

import java.util.ArrayList;
import java.util.List;

import com.tccc.kos.commons.core.service.config.annotations.ConfigDesc;
import com.tccc.kos.commons.core.service.config.annotations.ConfigFormat;
import com.tccc.kos.core.service.app.BaseAppConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class for the dispenser application. This allows
 * us to add configurable cup sizes for fixed size volume pours.
 *
 * @since 1.0
 * @version 2024-09-23
 */
@Getter @Setter
public class DispenserAppConfig extends BaseAppConfig {
    // the number of cup sizes we support
    private static final int MAX_CUPS = 5;

    private List<Cup> cups;    // list of cups we can pour

    {
        cups = new ArrayList<>();
        for (int i=0; i<MAX_CUPS; i++) {
            cups.add(new Cup());
        }
    }

    /**
     * Class that represents an available cup size
     */
    @Getter @Setter
    public static class Cup {
        @ConfigDesc("Name of the cup displayed to the user")
        private String name;
        @ConfigDesc(value = "Size of the cup in ml", format = ConfigFormat.ML)
        private int size;
        @ConfigDesc("When true, cup is visible to the user")
        private boolean enabled;
    }
}
