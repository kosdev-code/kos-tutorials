package com.example;

import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.core.vfs.VFSSource;
import com.tccc.kos.commons.kab.KabFile;
import com.tccc.kos.core.service.app.SystemApplication;
import com.tccc.kos.ext.dispense.pipeline.beverage.BeveragePourServiceDelegate;
import com.tccc.kos.ext.dispense.service.ingredient.IngredientService;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyKosApp extends SystemApplication<OurAppConfig>
        implements BeveragePourServiceDelegate, WaterRateProvider {

    @Getter
    private OurAssembly ourAssembly;
    @Autowired
    private IngredientService ingredientService;

    @Override
    public void load() {
        log.info("MyKosApp.load()");
        getCtx().add(new MyController());
    }

    @Override
    public void start() {
        log.info("MyKosApp.start()");
        ourAssembly = new OurAssembly();
        installAssembly(ourAssembly);
        loadAllPossibleIngredients();
    }

    @SneakyThrows
    private void loadAllPossibleIngredients() {
        KabFile kabFile = getSection().getKabByType(OurIngredientSource.KAB_TYPE);
        if (kabFile != null) {
            // Mount the brandset data into web server and grab the VFS source to generate URLs to content:
            VFSSource source = getVfs().mount(OurIngredientSource.MOUNT_POINT, kabFile);
            // Load the ingredients using the VFS source to set icon URLs:
            OurIngredientSource ourIngredientSource = new OurIngredientSource(kabFile, source);
            // Inform the ingredient service about the source of all possible ingredients:
            ingredientService.setDefaultSource(ourIngredientSource);
        } else {
            log.error("No brandset found in the system section of manifest.");
        }
    }

    /**
     * Used as a fake entry point required when debugging.
     */
    public static void main(String[] args) {
    }

    /**
     * PourServiceDelegate interface.
     */
    @Override
    public int getMaxPourVolume() {
        return getConfig().getMaxPourVolume();
    }

    /**
     * WaterRateProvider interface.
     */
    @Override
    public double getWaterFlowRate() {
        return getConfig().getWaterFlowRate();
    }
}
