package com.kos.tutorial;

import com.tccc.kos.commons.core.service.blink.binarymsg.BinaryMsgSession;
import com.tccc.kos.commons.core.service.blink.binarymsg.IfaceClient;
import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.hardware.Board;
import com.tccc.kos.core.service.hardware.IfaceAwareBoard;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NavigationBoard extends Board implements IfaceAwareBoard {
    private static final String BOARD_TYPE = "navigationModule";

    private IfaceClient<NavigationIface> ifaceClient;

    public NavigationBoard(Assembly assembly) {
        super(assembly, BOARD_TYPE);
        ifaceClient = new IfaceClient<>();
    }

    public double[] getCurrentCoordinates() {
        return ifaceClient.from(iface -> iface.getCurrentCoordinates());
    }

    public boolean setDestination(double x, double y, double z, String name) {
        return ifaceClient.from(iface -> iface.setDestination(x, y, z, name));
    }

    public void resetModule(boolean isHardReset) {
        ifaceClient.withCatch(iface -> iface.resetModule(isHardReset));
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
    public void onLinkSession(BinaryMsgSession session) {
        new NavigationIface(session, ifaceClient);
    }
}
