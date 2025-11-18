package com.kondra.testing.app;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.Board;
import com.tccc.kos.core.service.hardware.IfaceAware;
import com.tccc.kos.core.service.hardware.IfaceAwareBoard;
import com.tccc.kos.core.service.log.BlinkLoggerIface;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArduinoBoard extends Board implements IfaceAwareBoard<ArduinoIface> {

    private ArduinoIface iface;

    public ArduinoBoard(Assembly assembly, String name) {
        super(assembly, name);
    }

    public void triggerLog(){
        iface.triggerLog();
    }

    @Override
    public ArduinoIface createIface(BinaryMsgSession session) {
        log.info("Creating arduino iface");
        this.setIface(new ArduinoIface(session));
        return this.iface;
    }

    @Override
    public String getType() {
        return ArduinoIface.NAME;
    }

    @Override
    public String getInstanceId() {
        return "1";
    }

    @Override
    public ArduinoIface getIface() {
        return iface;
    }

    @Override
    public void setIface(ArduinoIface iface) {
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
