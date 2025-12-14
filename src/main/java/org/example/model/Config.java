package org.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Configuration class to hold key-value pairs of configuration settings.
 */
public class Config {

    /**
     * A HashMap to store configuration values with String keys and Long values.
     */
    private HashMap<String, Long> configValues = new HashMap<>();

    /**
     * Constructor to initialize the configuration with given values.
     * @param newConfigValues A HashMap containing configuration key-value pairs.
     */
    public Config(HashMap<String, Long> newConfigValues) {
        configValues = newConfigValues;
    }

    /**
     * Retrieves the configuration values.
     * @return A HashMap containing the configuration key-value pairs.
     */
    public HashMap<String, Long> getConfigValues() {
        return this.configValues;
    }
}
