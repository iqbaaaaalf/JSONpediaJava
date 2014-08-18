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

package com.machinelinking.storage.elasticsearch;

import com.machinelinking.storage.AbstractJSONStorageFactory;
import com.machinelinking.storage.DocumentConverter;

/**
 * {@link com.machinelinking.storage.JSONStorageFactory} implementation for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticJSONStorageFactory extends AbstractJSONStorageFactory<ElasticJSONStorageConfiguration, ElasticJSONStorage, ElasticDocument> {

    @Override
    public ElasticJSONStorageConfiguration createConfiguration(String configURI) {
        final Configuration c = parseConfigurationURI(configURI);
        return new ElasticJSONStorageConfiguration(c.host, c.port, c.db, c.collection);
    }

    @Override
    public ElasticJSONStorage createStorage(ElasticJSONStorageConfiguration config, DocumentConverter<ElasticDocument> converter) {
        return new ElasticJSONStorage(config, converter);
    }

    @Override
    public ElasticJSONStorage createStorage(ElasticJSONStorageConfiguration config) {
        return createStorage(config, new ElasticDocumentConverter());
    }

}
