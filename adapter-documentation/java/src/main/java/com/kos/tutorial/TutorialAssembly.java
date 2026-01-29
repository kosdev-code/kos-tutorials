package com.kos.tutorial;

import com.tccc.kos.core.service.assembly.Assembly;
import com.tccc.kos.core.service.assembly.CoreAssembly;

import lombok.Getter;

public class TutorialAssembly extends Assembly implements CoreAssembly {

    @Getter
    private NavigationBoard navigationBoard;

    public TutorialAssembly() {
        super("TutorialAssembly");
    }

    @Override
    public void load() throws Exception {
        navigationBoard = new NavigationBoard(this);
    }

    @Override
    public void start() throws Exception {

    }

}
