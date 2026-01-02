package com.kos.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.tccc.kos.commons.core.service.blink.binarymsg.IfaceClient;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.Board;
import com.tccc.kos.core.service.hardware.IfaceAwareBoard;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArduinoBoard extends Board implements IfaceAwareBoard {

    IfaceClient<ArduinoIface> arduinoIfaceClient;

    public ArduinoBoard(Assembly assembly) {
        super(assembly, "arduino");
        arduinoIfaceClient = new IfaceClient<>();


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
    public void onLinkSession(BinaryMsgSession session) {
        session.bind(new ArduinoIface(session, arduinoIfaceClient));
    }


}
