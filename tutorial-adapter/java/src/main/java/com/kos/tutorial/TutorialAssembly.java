package com.kos.tutorial;

import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.assembly.CoreAssembly;

public class TutorialAssembly extends Assembly implements CoreAssembly {

    public TutorialAssembly() {
        super("TutorialAssembly");
    }

    @Override
    public void load() throws Exception {
        new ArduinoBoard(this);
    }

    @Override
    public void start() throws Exception {

    }

}
