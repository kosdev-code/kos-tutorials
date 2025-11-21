package com.kos.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.Board;
import com.tccc.kos.core.service.hardware.IfaceAwareBoard;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArduinoBoard extends Board implements IfaceAwareBoard<ArduinoIface> {

    public ArduinoBoard(Assembly assembly, String name) {
        super(assembly, name);
    }

    /**
     * PART 1.1
     */
    public void hitHandler0() {
        log.info("hitHandler0");
        withIfaceCatch(ArduinoIface::hitHandler0);
    }

    /**
     * PART 1.2
     */
    public void hitHandler1() {
        withIfaceCatch(ArduinoIface::hitHandler1);
    }

    /**
     * PART 2.1
     */
    public void hitHandler2() {
        withIfaceCatch(ArduinoIface::hitHandler2);
    }

    /**
     * PART 2.2
     */
    public void hitHandler3() {
        withIfaceCatch(ArduinoIface::hitHandler3);
    }

    /**
     * PART 3.1
     */
    public void hitHandler4() {
        withIfaceCatch(ArduinoIface::hitHandler4);
    }

    /**
     * PART 4.1
     */
    public void hitHandler5() {
        withIfaceCatch(ArduinoIface::hitHandler5);
    }

    /**
     * PART 5.1
     */
    public void hitHandler6() {
        withIfaceCatch(ArduinoIface::hitHandler6);
    }


    @Override
    public ArduinoIface createIface(BinaryMsgSession session) {
        log.info("creating arduino iface");

        return new ArduinoIface(session);
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
    public ArduinoIface getIface() {
        /*
        we will store the arduino iface in the iface field of the
        board class. Because the field is of type BinaryMsgIface
        we must cast are iface to the correct type.
         */

        return (ArduinoIface) iface;
    }

    @Override
    public void setIface(ArduinoIface iface) {
        log.info("setting arduino iface");

        this.iface = iface;
    }

    @Override
    public void onIfaceConnect() throws Exception {
        log.info("arduino iface connected");
    }

    @Override
    public void onIfaceDisconnect() throws Exception {
        log.info("arduino iface disconnected");
    }
}