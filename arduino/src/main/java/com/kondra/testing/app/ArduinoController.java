package com.kondra.testing.app;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.core.dispatcher.annotations.*;
import com.tccc.kos.core.service.assembly.Assembly;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApiController(base = "/arduino", title = "arduino controller", desc = "Endpoints for interacting with arduino")
public class ArduinoController {
    @ApiEndpoint(POST = "/{path}/part-1.1", desc = "part 1.1")
    public void part1step1(@HandleVariable("path") ArduinoBoard arduino) {
        arduino.hitHandler0();
    }

    @ApiEndpoint(POST = "/{path}/part-1.2", desc = "part 1.2")
    public void part1step2(@HandleVariable("path") ArduinoBoard arduino) {
        arduino.hitHandler1();
    }

    @ApiEndpoint(POST = "/{path}/part-2.1", desc = "part 2.1")
    public void part2step1(@HandleVariable("path") ArduinoBoard arduino) {
        arduino.hitHandler2();
    }

    @ApiEndpoint(POST = "/{path}/part-2.2", desc = "part 2.2")
    public void part2step2(@HandleVariable("path") ArduinoBoard arduino) {
        arduino.hitHandler3();
    }

    @ApiEndpoint(POST = "/{path}/part-3.1", desc = "part 3.1")
    public void part3step1(@HandleVariable("path") ArduinoBoard arduino) {
        arduino.hitHandler4();
    }

    @ApiEndpoint(POST = "/{path}/part-4.1", desc = "part 4.1")
    public void part4step1(@HandleVariable("path") ArduinoBoard arduino) {
        arduino.hitHandler5();
    }
}
