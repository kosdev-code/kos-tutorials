package com.example;

import com.tccc.kos.commons.core.vfs.VFSSource;
import com.tccc.kos.commons.kab.KabFile;
import com.tccc.kos.core.service.app.BaseAppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import com.tccc.kos.ext.dispense.service.ingredient.IngredientService;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyKosApp extends SystemApplication<BaseAppConfig> {

    @Getter
    private OurAssembly assembly;

    private IngredientService<OurIngredient> ingredientService;

    @Override
    public void load() {
        log.info("MyKosApp.load()");

        // Create global IngredientService:
        ingredientService = new IngredientService<>();
        getCtx().add(ingredientService);

        // Create the MyController object:
        getCtx().add(new MyController());
    }

    @Override
    public void start() {
        log.info("MyKosApp.start()");
        assembly = new OurAssembly();
        installAssembly(assembly);
        log.info("Assembly installed.");
        loadTheBrandSet();
    }

    @SneakyThrows
    private void loadTheBrandSet() {
        // Process the brandset
        KabFile kabFile = getSection().getKabByType(OurIngredientSource.KAB_TYPE);
        if (kabFile != null) {
            // Mount the brandset data into web server and grab the VFS source to generate URLs to content:
            VFSSource source = getVfs().mount(OurIngredientSource.MOUNT_POINT, kabFile);
            // Load the brandset using the VFS source to set icon URLs, and use brandset as ingredient source:
            ingredientService.setSource(new OurIngredientSource(kabFile, source));
        } else {
            log.error("No brandset found in the system section of manifest.");
        }
    }

    /**
     * Used as a fake entry point required when debugging.
     */
    public static void main(String[] args) {
    }
}
