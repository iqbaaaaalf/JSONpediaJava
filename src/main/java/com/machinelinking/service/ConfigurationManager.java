package com.machinelinking.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Manages the service configuration.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ConfigurationManager {

    private static ConfigurationManager instance;

    private Properties properties;

    public static ConfigurationManager getInstance() {
        if(instance == null) instance = new ConfigurationManager();
        return instance;
    }

    private ConfigurationManager() {}

    public void initProperties(Properties properties) {
        if(this.properties != null) throw new IllegalStateException("Properties already initialized.");
        this.properties = properties;
    }

    public void initProperties(File file) {
        final Properties p = new Properties();
        try(FileInputStream fis = new FileInputStream(file)) {
            p.load(fis);
        } catch(Exception e) {
            throw new IllegalStateException("Error while reading configuration file.", e);
        }
        initProperties(p);
    }

    public String getProperty(String name, String defaultValue) {
        checkInitProperties();
        final String value = this.properties.getProperty(name, defaultValue);
        if(value == null)
            throw new IllegalArgumentException(String.format("Cannot find value for property '%s'", name));
        return value;
    }

    public String getProperty(String name) {
        return getProperty(name, null);
    }

    public boolean isInitialized() {
        return this.properties != null;
    }

    private void checkInitProperties() {
        if(this.properties == null) throw new IllegalStateException("Properties have not yet initialized.");
    }

}
