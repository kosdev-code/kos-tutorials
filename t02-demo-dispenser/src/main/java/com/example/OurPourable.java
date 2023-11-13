package com.example;

import com.tccc.kos.commons.util.misc.Str;
import com.tccc.kos.ext.dispense.pipeline.beverage.Pourable;
import lombok.Getter;
import lombok.SneakyThrows;

public class OurPourable extends Pourable {

    @Getter
    private final String beverageId;

    /**
     * Creates a new pourable from the specified definition string,
     * which is simply the beverage ID.
     */
    @SneakyThrows
    public OurPourable(String definitionStr) {
        beverageId = Str.trim(definitionStr);
    }

    @Override
    public Object getDefinition() {
        return beverageId;
    }
}
