package com.example;

import com.tccc.kos.commons.core.vfs.VFSSource;
import com.tccc.kos.commons.kab.KabFile;
import com.tccc.kos.commons.util.KosUtil;
import com.tccc.kos.ext.dispense.service.ingredient.BaseIngredient;
import com.tccc.kos.ext.dispense.service.ingredient.IngredientSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;

public class OurIngredientSource implements IngredientSource<OurIngredient> {

    // KAB type for brandset files:
    public static final String KAB_TYPE = "our-brandset";

    // Mount point for brandset in VFS:
    public static final String MOUNT_POINT = "/brandset";

    // Name of the JSON file in the KAB file:
    private static final String BRANDSET_FILE_NAME = "brandset.json";

    // Map of loaded ingredient data:
    public Map<String, OurIngredient> ingredientMap;

    /**
     * Creates a new {code OurIngredientSource} from a KAB file containing the JSON
     * descriptor file and related icons. This also takes the VFSSource of where the
     * brandset was mounted into the VFS so that we can build URLs for the icons in
     * the KAB file.
     *
     * @param brandSetKabFile the KAB containing the brandset data (JSON + icons)
     * @param source          where the KAB is mounted in VFS (URL space)
     */
    public OurIngredientSource(KabFile brandSetKabFile, VFSSource source) throws IOException {

        // Deserialize JSON into Java objects:
        InputStream inputStream = brandSetKabFile.getEntry(BRANDSET_FILE_NAME).getInputStream();
        Schema schema = KosUtil.getMapper().readValue(inputStream, Schema.class);

        // Set URLs for all the ingredients based on the base path of assets in the KAB:
        String brandsetUrlPrefix = "http://localhost:8081" + source.getBasePath();
        schema.getIngredients().forEach(ingredient -> ingredient.icon = brandsetUrlPrefix + ingredient.getIcon());

        // Convert ingredients list to sorted Tree map for use as IngredientSource:
        ingredientMap = schema.getIngredients().stream().collect(Collectors.
                toMap(BaseIngredient::getId, Function.identity(), (o1, o2) -> o1, TreeMap::new));
    }

    /**
     * Returns the ingredient associated with the given ID.
     */
    @Override
    public OurIngredient getIngredient(String id) {
        return ingredientMap.get(id);
    }

    /**
     * Returns the full collection of all possible ingredients.
     */
    @Override
    public Collection<OurIngredient> getIngredients() {
        return ingredientMap.values();
    }

    @Data
    public static class Schema {
        private List<OurIngredient> ingredients;
    }
}
