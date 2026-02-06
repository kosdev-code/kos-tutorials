package com.kos.tutorial;

import java.io.File;

import com.kosdev.kos.core.service.spawn.Adapter;

public class ExampleAdapter  extends Adapter {
    private static final String ADAPTER_NAME = "exampleAdapter";

    public ExampleAdapter(File baseDir) {
        super(ADAPTER_NAME);
        setBasePath(baseDir);
    } 

}
