package org.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {

    private HashMap<String, Long> configValues = new HashMap<>();

    public Config(HashMap<String, Long> newConfigValues) {
        configValues = newConfigValues;
    }

    public void setConfigValues(HashMap<String, Long> newConfigValues) {
        this.configValues = newConfigValues;
    }

    public HashMap<String, Long> getConfigValues() {
        return this.configValues;
    }
}
