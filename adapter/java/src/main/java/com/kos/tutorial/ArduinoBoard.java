package com.kos.tutorial;

import com.kosdev.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.kosdev.kos.commons.core.service.blink.binarymsg.IfaceClient;
import com.kosdev.kos.core.service.assembly.Assembly;
import com.kosdev.kos.core.service.hardware.Board;
import com.kosdev.kos.core.service.hardware.IfaceAwareBoard;

import lombok.extern.slf4j.Slf4j;

// extract-code adapter-s6
// extract-code adapter-s2
@Slf4j
public class ArduinoBoard extends Board implements IfaceAwareBoard {

    IfaceClient<ArduinoIface> arduinoIfaceClient;

    public ArduinoBoard(Assembly assembly) {
        super(assembly, "arduino");
        // extract-code ignore adapter-s2
        arduinoIfaceClient = new IfaceClient<>();
    }

    // extract-code adapter-s6
    @Override
    public String getType() {
        return "arduino";
    }

    // extract-code adapter-s6
    @Override
    public String getInstanceId() {
        // since there is only the one board, nothing needs to be returned
        return null;
    }

    // extract-code ignore adapter-s2
    @Override
    public void onLinkSession(BinaryMsgSession session) {
        session.bind(new ArduinoIface(session, arduinoIfaceClient));
    }


}
