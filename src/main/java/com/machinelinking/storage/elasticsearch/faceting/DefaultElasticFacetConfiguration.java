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

package com.machinelinking.storage.elasticsearch.faceting;

import com.machinelinking.storage.elasticsearch.ElasticJSONStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of {@link ElasticFacetManagerConfiguration}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultElasticFacetConfiguration implements ElasticFacetManagerConfiguration {

    public static final String MAPPING_PROPERTY_PREFIX = "mapping.";

    private int limit;
    private ElasticJSONStorage inStorage;
    private ElasticJSONStorage outStorage;

    private final List<Property> properties = new ArrayList<>();

    public DefaultElasticFacetConfiguration(
            Properties facetConfig, int limit, ElasticJSONStorage inStorage, ElasticJSONStorage outStorage
    ) {
        init(facetConfig, limit, inStorage, outStorage);
    }

    public DefaultElasticFacetConfiguration(
            File facetConfigFile, int limit, ElasticJSONStorage inStorage, ElasticJSONStorage outStorage
    ) {
        final Properties faceConfig = new Properties();
        try {
            try(FileInputStream fis = new FileInputStream(facetConfigFile)) {
                faceConfig.load(fis);
            }
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Cannot find or open file " + facetConfigFile, ioe);
        }
        init(faceConfig, limit, inStorage, outStorage);
    }

    private void init(Properties facetConfig, int limit, ElasticJSONStorage inStorage, ElasticJSONStorage outStorage) {
        this.limit = limit;
        this.inStorage = inStorage;
        this.outStorage = outStorage;
        loadProperties(facetConfig);
    }

    public void addProperty(String indexName, String field, PropertyType type, Analyzer analyzer) {
        properties.add( new Property(indexName, field, type, analyzer) );
    }

    public boolean removeProperty(String indexName, String field) {
        List<Property> targets = new ArrayList<>();
        for(Property property : properties) {
            if(property.indexName.equals(indexName)) {
                targets.add(property);
            }
        }
        if(!properties.isEmpty())
            return properties.removeAll(targets);
        return false;
    }

    @Override
    public Property[] getProperties() {
        return properties.toArray(new Property[0]);
    }

    @Override
    public ElasticJSONStorage getSourceStorage() {
        return inStorage;
    }

    @Override
    public ElasticJSONStorage getDestinationStorage() {
        return outStorage;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    private void loadPropertyValue(String name, String value) {
        String[] mapping = value.split(":");
        final boolean missingAnalyzer = mapping.length == 2;
        if (!missingAnalyzer && mapping.length != 3)
            throw new IllegalArgumentException(String.format("Invalid mapping definition for line [%s]", value));
        addProperty(
                name,
                mapping[0],
                PropertyType.valueOf(mapping[1]),
                missingAnalyzer ? Analyzer.not_analyzed : Analyzer.valueOf(mapping[2])
        );
    }

    private void loadProperties(Properties props) {
        String key, value, name;
        String[] declarations;
        for(Map.Entry<Object,Object> prop : props.entrySet()) {
            key = prop.getKey().toString();
            value = prop.getValue().toString();
            if(key.startsWith(MAPPING_PROPERTY_PREFIX)) {
                name = key.substring(MAPPING_PROPERTY_PREFIX.length());
                declarations = value.split("\\|");
                for(String declaration : declarations) {
                    loadPropertyValue(name, declaration);
                }
            }
        }
    }
}
