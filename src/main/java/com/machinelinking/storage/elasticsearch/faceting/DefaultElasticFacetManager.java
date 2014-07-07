package com.machinelinking.storage.elasticsearch.faceting;

import com.machinelinking.storage.JSONStorageConnectionException;
import com.machinelinking.storage.elasticsearch.ElasticDocument;
import com.machinelinking.storage.elasticsearch.ElasticJSONStorage;
import com.machinelinking.storage.elasticsearch.ElasticJSONStorageConnection;
import com.machinelinking.storage.elasticsearch.ElasticResultSet;
import com.machinelinking.storage.elasticsearch.ElasticSelector;
import com.machinelinking.util.JSONUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.indices.IndexMissingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.machinelinking.storage.elasticsearch.faceting.ElasticFacetManagerConfiguration.Analyzer;
import static com.machinelinking.storage.elasticsearch.faceting.ElasticFacetManagerConfiguration.Property;

/**
 * Default implementation of {@link com.machinelinking.storage.elasticsearch.faceting.ElasticFacetManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultElasticFacetManager implements ElasticFacetManager {

    public static final String SOURCE_CONFIG_FILE = "source-config.json";

    private final ElasticFacetManagerConfiguration configuration;

    public DefaultElasticFacetManager(ElasticFacetManagerConfiguration configuration) {
        this.configuration = configuration;
        initStorage(configuration);
    }

    @Override
    public ElasticFacetManagerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public FacetLoadingReport loadFacets(ElasticSelector selector, FacetConverter converter)
    throws JSONStorageConnectionException {
        final ElasticJSONStorage inStorage = configuration.getSourceStorage();
        final ElasticJSONStorage outStorage = configuration.getDestinationStorage();
        ElasticDocument document;
        int processedDocs = 0;
        int generatedFacetDocs = 0;
        try(
            ElasticJSONStorageConnection readConnection = inStorage.openConnection();
            ElasticResultSet rs = readConnection.query(selector, configuration.getLimit());
            ElasticJSONStorageConnection writeConnection = outStorage.openConnection()
        ) {
            while ((document = rs.next()) != null) {
                for (ElasticDocument generatedDoc : converter.convert(document)) {
                    writeConnection.addDocument(generatedDoc);
                    generatedFacetDocs++;
                }
                processedDocs++;
            }
        }
        return new FacetLoadingReport(processedDocs, generatedFacetDocs);
    }

    private void initStorage(ElasticFacetManagerConfiguration configuration) {
        final ElasticJSONStorage storage = configuration.getDestinationStorage();
        try {
            storage.deleteCollection();
        } catch (IndexMissingException ime) {
            // Pass.
        }
        final ElasticJSONStorageConnection connection = storage.openConnection();
        final Client client = connection.getClient();

        final Map<String, Object> sourceTemplate;
        try {
            sourceTemplate = (Map<String, Object>) JSONUtils.parseJSONAsMap(
                    this.getClass().getResourceAsStream(SOURCE_CONFIG_FILE)
            );
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        setMappings(
                configuration.getProperties(),
                sourceTemplate
        );

        final CreateIndexResponse response =
                client.admin().indices().prepareCreate(storage.getConfiguration().getDB())
                .setSource(sourceTemplate).execute().actionGet();

        if(!response.isAcknowledged()) throw new IllegalStateException();
    }

    private Map<String,List<Property>> divideByType(Property[] properties) {
        final Map<String,List<Property>> result = new HashMap<>();
        for(Property property : properties) {
            List<Property> l = result.get(property.indexType);
            if(l == null) {
                l = new ArrayList<>();
                result.put(property.indexType, l);
            }
            l.add(property);
        }
        return result;
    }

    private Map<String,Object> toPropertyDefinition(final Property p) {
        return new HashMap<String,Object>(){{
            if(p.analyzer == Analyzer.not_analyzed) {
                put("index", "not_analyzed");
            } else {
                put("analyzer", p.analyzer.toValue());
            }
            put("type", p.type.toValue());
        }};
    }

    private void setMappings(Property[] properties, Map<String,?> source) {
        final Map<String,List<Property>> typeToProps = divideByType(properties);
        final Map<String,Object> mappings = (Map<String,Object>) source.get("mappings");
        for(Map.Entry<String,List<Property>> typeToPropsEntry : typeToProps.entrySet()) {
            final Map<String,Object> typeProperties = new HashMap<>();
            mappings.put(typeToPropsEntry.getKey(), typeProperties);
            Map typeMapping = new HashMap();
            typeProperties.put("properties", typeMapping);
            for (Property property : properties) {
                typeMapping.put(property.field, toPropertyDefinition(property));
            }
        }
    }
}
