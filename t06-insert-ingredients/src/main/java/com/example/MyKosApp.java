package com.example;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.core.service.app.AppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import com.tccc.kos.ext.dispense.service.ingredient.IngredientService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyKosApp extends SystemApplication<AppConfig> {

    @Autowired
    private IngredientService ingredientService;

    @Getter
    private OurAssembly assembly;

    @Override
    public void load() {
        log.info("MyKosApp.load()");
        // Create the controller and add it to the kOS context:
        getCtx().add(new MyController());
    }

    @Override
    public void start() {
        log.info("MyKosApp.start()");
        ingredientService.setSource(new OurBrandSet());  // install ingredients
        assembly = new OurAssembly();
        installAssembly(assembly);
        log.info("Assembly installed.");
    }
}
