/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

package com.machinelinking.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MultiJSONStorageFactory extends AbstractJSONStorageFactory<MultiJSONStorageConfiguration, MultiJSONStorage, MultiDocument> {

    private final Map<JSONStorageConfiguration, JSONStorageFactory> configurationToFactory = new HashMap<>();

    public static JSONStorageFactory loadJSONStorageFactory(String className) {
        try {
            return (JSONStorageFactory) DefaultJSONStorageLoader.class.getClassLoader()
                    .loadClass(className).newInstance();
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException( String.format("Invalid class name: %s .", className) );
        } catch (Exception e) {
            throw new IllegalArgumentException( String.format("Error while loading class: %s .", className), e);
        }
    }

    @Override
    public MultiJSONStorageConfiguration createConfiguration(String configURI) {
        final String[] storageConfigs = configURI.split(";");
        final List<JSONStorageConfiguration> configurations = new ArrayList<>();
        for(String storageConfig : storageConfigs) {
            String[] configParts = storageConfig.split("\\|");
            if(configParts.length != 2) throw new IllegalArgumentException();
            configurations.add(instantiateConfiguration(configParts[0], configParts[1]));
        }
        return new MultiJSONStorageConfiguration(
                configurations.toArray(new JSONStorageConfiguration[configurations.size()])
        );
    }

    @Override
    public MultiJSONStorage createStorage(
            MultiJSONStorageConfiguration multiConfig, DocumentConverter<MultiDocument> converter
    ) {
        final List<JSONStorage> storages = new ArrayList<>();
        for(JSONStorageConfiguration config : multiConfig) {
            final JSONStorageFactory factory = configurationToFactory.get(config);
            storages.add( factory.createStorage(config) );
        }
        return new MultiJSONStorage(multiConfig, converter, storages.toArray(new JSONStorage[storages.size()]));
    }

    @Override
    public MultiJSONStorage createStorage(MultiJSONStorageConfiguration config) {
        return createStorage(config, null);
    }

    private JSONStorageConfiguration instantiateConfiguration(String factoryClass, String configURI) {
        final JSONStorageFactory factory = loadJSONStorageFactory(factoryClass);
        final JSONStorageConfiguration configuration =  factory.createConfiguration(configURI);
        configurationToFactory.put(configuration, factory);
        return configuration;
    }

}
