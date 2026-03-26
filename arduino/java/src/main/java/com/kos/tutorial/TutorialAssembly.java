package com.kos.tutorial;

import com.kosdev.kos.core.service.assembly.Assembly;
import com.kosdev.kos.core.service.assembly.CoreAssembly;
import com.kosdev.kos.core.service.udev.UsbId;
import com.kosdev.kos.core.service.udev.serial.SerialDevice;
import com.kosdev.kos.core.service.udev.serial.blink.SerialBlinkMatch;
import com.kosdev.kos.core.service.udev.serial.blink.SerialBlinkMatcher;

public class TutorialAssembly extends Assembly implements CoreAssembly, SerialBlinkMatcher {
    private static final int BAUD = 115200; // baud rate

    // vendorId / productId for arduino mega
    private static final UsbId ARDUINO_MEGA_BOARD_ID = new UsbId(0x2341, 0x0042);

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
    public SerialBlinkMatch matchSerialBlinkDevice(UsbId usbId, SerialDevice device) {
        // match the vendor and product id of the arduino (an arduino mega in this case)
        if(ARDUINO_MEGA_BOARD_ID.equals(usbId)){
            // create a match and set the baud rate that you will be using
            SerialBlinkMatch match = new SerialBlinkMatch(BAUD);

            // set the post-open delay to allow the arduino time to reboot
            match.setPostOpenDelayMs(2000);
            return match;
        }else{
            return null;
        }
    }
}
