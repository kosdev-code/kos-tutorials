package com.kosdev.samples.dispenser.part1;

import com.kosdev.samples.dispenser.part1.brandset.Brandset;
import com.kosdev.samples.dispenser.part1.pour.VolumeDelegate;
import com.tccc.kos.commons.core.context.annotations.Autowired;
import com.tccc.kos.commons.kab.KabFile;
import com.tccc.kos.commons.util.KosUtil;
import com.tccc.kos.core.service.app.BaseAppConfig;
import com.tccc.kos.core.service.app.SystemApplication;
import com.tccc.kos.ext.dispense.service.ingredient.IngredientService;
import lombok.Getter;

/**
 * System application for a simple flavored water dispenser.
 *
 * A System App is the primary entry point for your code when the system boots.
 * Every device must have exactly one {@link SystemApplication}.
 *
 * @since 1.0
 * @version 2024-09-24
 */
public class DispenserApp extends SystemApplication<BaseAppConfig> {
    @Autowired
    private IngredientService ingredientService;
    @Getter
    private Brandset brandset;

    @Override
    public void load() throws Exception {
        // Add a delegate to provide information about pour volumes
        addToCtx(new VolumeDelegate());
    }

    @Override
    public void start() throws Exception {
        // Look for the brandset KAB in the same section as this system app
        KabFile kab = getSection().getKabByType("dispenser.brandset");
        if (kab != null) {
            // Load the brandset JSON into a brandset object
            brandset = KosUtil.getMapper().readValue(kab.getInputStream("brandset.json"), Brandset.class);

            // Tell kOS about all the ingredients using the {@code IngredientSource} interface
            ingredientService.setDefaultSource(brandset);
        }

        // Create and install the assembly for the device
        installAssembly(new DispenserAssembly(getDescriptor()));
    }

    @Override
    public void started() throws Exception {
    }

    /**
     * Intellij requires a main() for the debugger to work.
     */
    public static void main(String[] args) {
    }
}
