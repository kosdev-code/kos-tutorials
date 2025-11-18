package com.kondra.testing.app;

import com.tccc.kos.commons.core.dispatcher.annotations.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApiController(base = "/arduino", title = "arduino controller", desc = "Endpoints for interacting with arduino")
public class ArduinoController {
    @ApiEndpoint(POST = "/trigger/log/{path}", desc = "Triggers a demo log")
    public void triggerLog(@HandleVariable("path") ArduinoBoard arduino) {
        arduino.triggerLog();
    }
}
