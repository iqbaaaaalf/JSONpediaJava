package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.AbstractJSONStorageLoaderTest;
import com.machinelinking.storage.DocumentConverter;
import com.machinelinking.storage.JSONStorage;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link com.machinelinking.storage.elasticsearch.ElasticJSONStorage} test.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultJSONStorageLoaderTest extends AbstractJSONStorageLoaderTest {

    @Override
    protected JSONStorage getJSONStorage() throws UnknownHostException {
        return new ElasticJSONStorage(
                new ElasticJSONStorageConfiguration("localhost", 9300, "jsonpedia-test", "en"),
                new ElasticDocumentConverter()
        );
    }

    class ElasticDocumentConverter implements DocumentConverter<ElasticDocument> {

        final Set<String> blocked = new HashSet<>();

        ElasticDocumentConverter() {
            blocked.add("wikitext-json");
            blocked.add("infobox-splitter");
            blocked.add("table-splitter");
        }
        @Override
        public ElasticDocument convert(ElasticDocument in) {
            final Map<String,Object> content = (Map<String,Object>) in.getContent().get("content");
            final Map<String, Object> out = new HashMap<>();
            for (Map.Entry<String, ?> entry : content.entrySet()) {
                if (blocked.contains(entry.getKey())) continue;
                out.put(entry.getKey(), entry.getValue());
            }
            return new ElasticDocument(in.getId(), in.getVersion(), in.getName(), out);
        }
    }

}
