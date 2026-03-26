package com.kos.tutorial;


import com.kosdev.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.kosdev.kos.commons.core.service.blink.binarymsg.IfaceClient;
import com.kosdev.kos.core.service.assembly.Assembly;
import com.kosdev.kos.core.service.hardware.Board;
import com.kosdev.kos.core.service.hardware.IfaceAwareBoard;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArduinoBoard extends Board implements IfaceAwareBoard{

    private IfaceClient<ArduinoIface> ifaceClient = new IfaceClient<>();

    public ArduinoBoard(Assembly assembly, String name) {
        super(assembly, name);
        ifaceClient = new IfaceClient<>();
    }

    /**
     * PART 1.1
     */
    public void hitHandler0() {
        ifaceClient.with(ArduinoIface::hitHandler0);

    }

    /**
     * PART 1.2
     */
    public void hitHandler1() {
        ifaceClient.with(ArduinoIface::hitHandler1);
    }

    /**
     * PART 2.1
     */
    public void hitHandler2() {
        ifaceClient.with(ArduinoIface::hitHandler2);
    }

    /**
     * PART 2.2
     */
    public void hitHandler3() {
        ifaceClient.with(ArduinoIface::hitHandler3);
    }

    /**
     * PART 3.1
     */
    public void hitHandler4() {
        ifaceClient.with(ArduinoIface::hitHandler4);
    }

    /**
     * PART 4.1
     */
    public void hitHandler5() {
        ifaceClient.with(ArduinoIface::hitHandler5);
    }

    /**
     * PART 5.1
     */
    public void hitHandler6() {
        ifaceClient.with(ArduinoIface::hitHandler6);
    }

    @Override
    public String getType() {
        /*
         by convention, we are just going to have the board type be
         the arduino iface's name
         */
        return ArduinoIface.NAME;
    }

    @Override
    public String getInstanceId() {
        /*
         by convention, the instance ids are numbered, since we
         are only going to have the single arduino in this tutorial
         our instance id will be "1"
         */
        return "1";
    }

    @Override
    public void onLinkSession(BinaryMsgSession session) {
        session.bind(new ArduinoIface(session, ifaceClient));
    }
}