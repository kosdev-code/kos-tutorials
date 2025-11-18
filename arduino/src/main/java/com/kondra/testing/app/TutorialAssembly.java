package com.kondra.testing.app;

import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.assembly.CoreAssembly;
import com.tccc.kos.core.service.udev.serial.SerialDevice;
import com.tccc.kos.core.service.udev.serial.blink.SerialBlinkMatch;
import com.tccc.kos.core.service.udev.serial.blink.SerialBlinkMatcher;

public class TutorialAssembly extends Assembly implements CoreAssembly, SerialBlinkMatcher {
    private static final int VENDOR_ID = 0x2341;    //vendor id of arduino mega
    private static final int PRODUCT_ID = 0x0042;   //product id of the arduino mega

    private static final int BAUD = 115200;         // baud rate for the lidar

    public TutorialAssembly(String name) {
        super(name);
    }

    @Override
    public void load() throws Exception {
        new ArduinoBoard(this, "kondra.arduino");
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public SerialBlinkMatch matchSerialBlinkDevice(int vendorId, int productId, SerialDevice device) {
        // match the vendor and product id of the arduino (an arduino mega in this case)
        if((vendorId == VENDOR_ID) && (productId == PRODUCT_ID)){
            // create a match and set the baud rate that you will be using
            SerialBlinkMatch match = new SerialBlinkMatch(BAUD);

            // set the post open delay to allow the arduino time to reboot
            match.setPostOpenDelayMs(2000);
            return match;
        }else{
            return null;
        }
    }
}
