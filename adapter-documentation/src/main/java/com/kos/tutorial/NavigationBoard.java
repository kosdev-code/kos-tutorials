package com.kos.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.Board;
import com.tccc.kos.core.service.hardware.IfaceAwareBoard;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NavigationBoard extends Board implements IfaceAwareBoard<NavigationIface> {
    private static final String BOARD_TYPE = "navigationModule";

    public NavigationBoard(Assembly assembly) {
        super(assembly, BOARD_TYPE);

    }

    @Override
    public String getType() {
        return BOARD_TYPE;
    }

    @Override
    public String getInstanceId() {
        return null;
    }

    @Override
    public NavigationIface createIface(BinaryMsgSession session) {
        log.info("createIface()");
        return new NavigationIface(session, this);
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
