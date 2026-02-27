package com.kos.tutorial;

import java.io.File;

import com.kosdev.kos.core.service.spawn.Adapter;
import com.kosdev.kos.core.service.udev.serial.SerialAdapterFactory;
import com.kosdev.kos.core.service.udev.serial.SerialDevice;

public class ArduinoSerialAdapterFactory extends SerialAdapterFactory {

    private static final int VENDOR_ID = 0x10c4;
    private static final int PRODUCT_ID = 0xea60;
    private static final String ADAPTER_NAME = "ArduinoAdapter";

    // base dir of the adapter binary
    private File baseDir;

    public ArduinoSerialAdapterFactory(File baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public Adapter matchDevice(SerialDevice device) {
        if ((device.getVendorId() == VENDOR_ID) && (device.getProductId() == PRODUCT_ID)) {
            // build the adapter
            Adapter adapter = new Adapter(ADAPTER_NAME);
            adapter.setBasePath(baseDir);

            adapter.addArg("-v");
            adapter.addArg("-d");
            adapter.addArg(device.getDevname());

            return adapter;
        }
        return null;
    }

}
