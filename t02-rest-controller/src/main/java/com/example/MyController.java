package com.example;

import com.tccc.kos.commons.core.dispatcher.annotations.ApiController;
import com.tccc.kos.commons.core.dispatcher.annotations.ApiEndpoint;
import com.tccc.kos.commons.core.dispatcher.annotations.ApiVersion;
import com.tccc.kos.commons.core.dispatcher.annotations.PathVariable;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiController(base = "/mycontroller", title = "My first kOS REST controller")
public class MyController {

    /**
     * GET http://localhost:{port}/api/system/mycontroller/ping
     * returns: {"status":200,"version":{"major":1,"minor":0},"data":"pong"}
     */
    @ApiEndpoint(GET = "/ping", desc = "checks to see if controller is running")
    public String ping() {
        return "pong";
    }

    /**
     * GET http://localhost:{port}/api/system/mycontroller/dosomething
     * returns: {"status":200,"version":{"major":4,"minor":56}}
     */
    @ApiVersion("4.56")
    @ApiEndpoint(GET = "/dosomething", desc = "returns void")
    public void doSomething() {
    }

    /**
     * GET http://localhost:{port}/api/system/mycontroller/bean/17
     * returns: {"status":200,"version":{"major":1,"minor":0},"data":{"id":17,"name":"Fred","other":"Flintstone"}}
     */
    @ApiEndpoint(GET = "/bean/{id}", desc = "uses path variable and returns an object")
    public MyBean getBean(@PathVariable("id") Integer id) {
        MyBean myBean = new MyBean(id, "Fred", "Flintstone");
        return myBean;
    }

    @Data
    @AllArgsConstructor
    public static class MyBean {
        private int id;
        private String name;
        private String other;
    }
}
