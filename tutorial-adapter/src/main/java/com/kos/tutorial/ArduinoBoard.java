package com.kos.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.Board;
import com.tccc.kos.core.service.hardware.IfaceAwareBoard;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArduinoBoard extends Board implements IfaceAwareBoard<ArduinoIface> {

    public ArduinoBoard(Assembly assembly) {
        super(assembly, "arduino");

    }

    @Override
    public String getType() {
        return "arduino";
    }

    @Override
    public String getInstanceId() {
        return null;
    }

    @Override
    public ArduinoIface createIface(BinaryMsgSession session) {
        log.info("createIface()");
        return new ArduinoIface(session);
    }

    @Override
    public void onIfaceConnect() throws Exception {
        log.info("onIfaceConnect()");
    }

    @Override
    public void onIfaceDisconnect() throws Exception {
        log.info("onIfaceDisconnect()");
    }
}
