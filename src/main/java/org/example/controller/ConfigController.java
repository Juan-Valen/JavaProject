package org.example.controller;

import org.example.model.Config;

import java.io.*;
import java.util.HashMap;


public class ConfigController {
    private final static String FILENAME = "config.csv";

    static Config config = null;

    public static Config getConfig() {
        if (config == null) {
            config = new Config(getConfigValues());
        }
        if (config.getConfigValues().isEmpty()) {
            return null;
        }
        return config;
    }

    public static void main(String[] args) {

        HashMap<String, Long> newConfigValues = new HashMap<>();
        newConfigValues.put("simulationDuration", 1000L);
        newConfigValues.put("avgCarArrivalInterval", 120L);
        newConfigValues.put("carGroupAvgSize", 4L);
        newConfigValues.put("betweenIntersectionTime", 80L);
        newConfigValues.put("intersectionPassThroughTime", 15L);
        setConfigValues(newConfigValues);

        System.out.println(getConfigValues());
    }
    public static HashMap<String, Long> getConfigValues() {
        FileReader file;
        BufferedReader bufferedstream = null;
        String line = null;
        HashMap<String, Long> configValues = new HashMap<>();

        try {
            file = new FileReader(FILENAME);
            bufferedstream = new BufferedReader(file);

            do {
                line = bufferedstream.readLine();
                if (line != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String key = parts[0].trim();
                        Long value = Long.parseLong(parts[1].trim());
                        configValues.put(key, value);
                    }
                }
            } while (line != null);
        } catch (IOException e) {
            // Error output, will print to console even in case of output redirection
            System.err.println(e);
        } finally {
            try {
                // we will close the stream only if we were able to open it
                if (bufferedstream != null)
                    bufferedstream.close();
            } catch (Exception e) {
                System.out.println("Error while closing the file " + FILENAME);
            }
        }
        return configValues;
    }

    public static void setConfigValues(HashMap<String, Long> configValues) {
        FileWriter file;
        BufferedWriter bufferedstream = null;
        try {
            file = new FileWriter(FILENAME);
            bufferedstream = new BufferedWriter(file);

            for (String key : configValues.keySet()) {
                String line = key + "," + configValues.get(key) + "\n";
                bufferedstream.write(line);
            }
        } catch (IOException e) {
            // Error output, will print to console even in case of output redirection
            System.err.println(e);
        } finally {
            try {
                // we will close the stream only if we were able to open it
                if (bufferedstream != null)
                    bufferedstream.close();
            } catch (Exception e) {
                System.out.println("Error while closing the file " + FILENAME);
            }
        }
    }
}
