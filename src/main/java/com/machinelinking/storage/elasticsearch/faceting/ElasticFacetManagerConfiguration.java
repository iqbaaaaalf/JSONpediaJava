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
         },
        _long {
             @Override
             public String toValue() {
                 return "long";
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
        custom_lowercase {
            @Override
            public String toValue() {
                return "custom_lowercase";
            }
        },
        custom_kstem {
            @Override
            public String toValue() {
                return "custom_kstem";
            }
        }
    }

    ElasticJSONStorage getSourceStorage();

    ElasticJSONStorage getDestinationStorage();

    int getLimit();

    Property[] getProperties();

    class Property {
        public final String indexName;
        public final String field;
        public final PropertyType type;
        public final Analyzer analyzer;

        public Property(String indexName, String field, PropertyType type, Analyzer analyzer) {
            Objects.requireNonNull(indexName);
            Objects.requireNonNull(field);
            Objects.requireNonNull(type);
            Objects.requireNonNull(analyzer);
            this.indexName = indexName;
            this.field = field;
            this.type = type;
            this.analyzer = analyzer;
        }

        @Override
        public String toString() {
            return String.format("%s: [%s] %s %s", indexName, field, type, analyzer);
        }
    }

}
