package com.kos.tutorial;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.kosdev.kos.commons.core.dispatcher.annotations.ApiController;
import com.kosdev.kos.commons.core.dispatcher.annotations.ApiEndpoint;
import com.kosdev.kos.commons.core.dispatcher.annotations.PathVariable;
import com.kosdev.kos.commons.core.dispatcher.annotations.RequestBody;
import com.kosdev.kos.commons.core.dispatcher.annotations.ApiEndpoint.Param;

import lombok.Getter;
import lombok.Setter;

@ApiController(base = "/test", title = "Testing endpoints")
public class TutorialController {

    private TutorialAssembly assembly;

    public TutorialController(TutorialAssembly assembly) {
        this.assembly = assembly;
    }

    //@formatter:off
    @ApiEndpoint(  POST =  "/reset/{isHardReset}",
                 params = @Param(name = "isHardReset", desc = "This will send a boolean down to the adapter"),
                   desc = "This is a test endpoint")
    public void sendModuleReset(@PathVariable("isHardReset") boolean isHardReset) {
    //@formatter:on

        if (assembly.getNavigationBoard() != null) {
            assembly.getNavigationBoard().resetModule(isHardReset);
        }
    }

    //@formatter:off
    @ApiEndpoint(  POST =  "/destination",
                   desc = "This endpoint will try to send information down to the adapter through the baord")
    public void setDestination(@RequestBody Destination d ) {
    //@formatter:on

        if (assembly.getNavigationBoard() != null) {
            assembly.getNavigationBoard().setDestination(d.getXCoordinate(), d.getYCoordinate(), d.getZCoordinate(),
                    d.getName());
        }
    }

    //@formatter:off
    @ApiEndpoint(  GET =  "/coordinates",
                   desc = "This endpoint will try to send information down to the adapter through the baord")
    public List<Double> getCurrentCoordinates() {
    //@formatter:on

        if (assembly.getNavigationBoard() != null) {
            double[] array = assembly.getNavigationBoard().getCurrentCoordinates();
            List<Double> list = Arrays.stream(array)
                    .boxed()
                    .collect(Collectors.toList());

            return list;
        }
        return null;

    }

    @Getter
    @Setter
    private static class Destination {
        private double xCoordinate;
        private double yCoordinate;
        private double zCoordinate;
        private String name;
    }
}
