package com.example;

import com.tccc.kos.commons.core.dispatcher.annotations.ApiController;
import com.tccc.kos.commons.core.dispatcher.annotations.ApiEndpoint;
import com.tccc.kos.commons.core.dispatcher.annotations.PathVariable;
import com.tccc.kos.commons.core.dispatcher.annotations.RequestParam;
import java.util.ArrayList;
import java.util.List;
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
    @ApiEndpoint(GET = "/dosomething", desc = "returns void", version = "4.56")
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

    /**
     * GET http://localhost:{port}/api/system/mycontroller/user/list?order=firstName&limit=10
     * passes in: the parameter "order" contains "firstName" and the parameter "limit" is "20"
     */
    @ApiEndpoint(GET = "/user/list", desc = "")
    public List<MyBean> listUsers(
            @RequestParam(value = "order", required = true) String order,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        List<MyBean> list = new ArrayList<>(limit);
        list.add(new MyBean(1, "Fred", "Foo"));
        list.add(new MyBean(2, "Barney", "Bar"));
        return list;
    }

    @Data
    @AllArgsConstructor
    public static class MyBean {
        private int id;
        private String name;
        private String other;
    }
}
