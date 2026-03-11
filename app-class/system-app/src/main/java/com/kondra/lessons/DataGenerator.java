package com.kondra.lessons;

import com.kosdev.kos.commons.core.context.annotations.Autowired;
import com.kosdev.kos.commons.core.service.AbstractConfigurableService;
import com.kosdev.kos.commons.core.service.config.BeanChanges;
import com.kosdev.kos.commons.core.service.trouble.Trouble;
import com.kosdev.kos.commons.core.service.trouble.TroubleService;
import com.kosdev.kos.commons.util.concurrent.AdjustableCallback;
import com.kosdev.kos.core.service.analytics.AnalyticsService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DataGenerator extends AbstractConfigurableService<DataGeneratorConfig> {

    private static final List<String> EVENT_TYPES = Arrays.asList(
        "temperature_reading",
        "humidity_reading",
        "motion_detected",
        "door_opened",
        "door_closed",
        "battery_low",
        "connectivity_lost",
        "connectivity_restored",
        "pressure_reading",
        "light_level_reading"
    );

    private static final List<String> EVENT_DATA = Arrays.asList(
        "{\"value\": 23.5, \"unit\": \"celsius\", \"sensor_id\": \"temp-01\"}",
        "{\"value\": 65.2, \"unit\": \"percent\", \"sensor_id\": \"hum-03\"}",
        "{\"zone\": \"entry\", \"confidence\": 0.97, \"duration_ms\": 1200}",
        "{\"door_id\": \"front\", \"user\": \"unknown\", \"timestamp_offset\": -300}",
        "{\"door_id\": \"garage\", \"locked\": true, \"trigger\": \"manual\"}",
        "{\"battery_id\": \"bat-07\", \"level\": 8, \"estimated_hours\": 4}",
        "{\"interface\": \"wifi\", \"last_seen_ms\": 30000, \"retry_count\": 3}",
        "{\"interface\": \"wifi\", \"signal_strength\": -62, \"ip\": \"192.168.1.42\"}",
        "{\"value\": 1013.25, \"unit\": \"hPa\", \"sensor_id\": \"pres-02\"}",
        "{\"value\": 340, \"unit\": \"lux\", \"sensor_id\": \"light-05\", \"auto_dim\": false}"
    );

    private static final Random RANDOM = new Random();

    @Autowired
    private TroubleService troubleService;
    @Autowired
    private AnalyticsService analyticsService;

    private AdjustableCallback troubleCallback;
    private AdjustableCallback analyticsCallback;
    private boolean dependenciesReady = false;

    // adjustable callback that is configurable

    /**
     * Start generating data with the service
     */
    public void start() {

        troubleCallback = new AdjustableCallback(true, getConfig().getAnalyticsInterval(), () -> {
            if (dependenciesReady) {
                troubleService.add(new Trouble());
            }
        });
        analyticsCallback = new AdjustableCallback(true, getConfig().getAnalyticsInterval(), () -> {
            if (dependenciesReady) {
                String eventType = EVENT_TYPES.get(RANDOM.nextInt(EVENT_TYPES.size()));
                String eventData = EVENT_DATA.get(RANDOM.nextInt(EVENT_DATA.size()));
                analyticsService.recordHigh(eventType, eventData);
            }

        });

    }


    public void stop() {
        troubleCallback.stop();
        troubleCallback = null;
        analyticsCallback.stop();
        analyticsCallback = null;
    }


    @Override
    public void onConfigChanged(BeanChanges changes) {
        troubleCallback.setAbsDelay(getConfig().getTroublesInterval());
        analyticsCallback.setAbsDelay(getConfig().getAnalyticsInterval());
    }


    @Override
    public boolean onBeanReady() {

        dependenciesReady = true;

        return super.onBeanReady();
    }
}
