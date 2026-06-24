package com.kos.tutorial;

import com.kosdev.kos.core.service.assembly.Assembly;
import com.kosdev.kos.core.service.assembly.CoreAssembly;

import lombok.Getter;

public class TutorialAssembly extends Assembly implements CoreAssembly {

    @Getter
    private ExampleBoard navigationBoard;

    public TutorialAssembly() {
        super("TutorialAssembly");
    }

    @Override
    public void load() throws Exception {
        navigationBoard = new ExampleBoard(this);
    }

    @Override
    public void start() throws Exception {

    }

}
