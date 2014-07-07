package com.machinelinking.storage.elasticsearch.faceting;

import com.machinelinking.storage.elasticsearch.ElasticJSONStorage;

import java.util.Objects;

/**
 * Configuration of {@link com.machinelinking.storage.elasticsearch.faceting.ElasticFacetManager}
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface ElasticFacetManagerConfiguration {

    interface Attribute {
        String toValue();
    }

    enum PropertyType implements Attribute {
         string {
             @Override
             public String toValue() {
                 return "string";
             }
         }
    }

    enum Analyzer implements Attribute {
        not_analyzed {
            @Override
            public String toValue() {
                throw new IllegalStateException();
            }
        },
        keyword_analyzer {
            @Override
            public String toValue() {
                return "keyword_analyzer";
            }
        }
    }

    ElasticJSONStorage getSourceStorage();

    ElasticJSONStorage getDestinationStorage();

    int getLimit();

    Property[] getProperties();

    class Property {
        public final String indexType;
        public final String field;
        public final PropertyType type;
        public final Analyzer analyzer;

        public Property(String index_type, String field, PropertyType type, Analyzer analyzer) {
            Objects.requireNonNull(index_type);
            Objects.requireNonNull(field);
            Objects.requireNonNull(type);
            Objects.requireNonNull(analyzer);
            this.indexType = index_type;
            this.field = field;
            this.type = type;
            this.analyzer = analyzer;
        }
    }

}
