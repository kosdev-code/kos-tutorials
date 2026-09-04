package com.kos.tutorial;

import com.kosdev.kos.core.service.assembly.Assembly;
import com.kosdev.kos.core.service.assembly.CoreAssembly;

import lombok.Getter;

// extract-code adapter-s3
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
