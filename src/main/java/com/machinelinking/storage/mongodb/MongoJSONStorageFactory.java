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

package com.machinelinking.storage.mongodb;

import com.machinelinking.storage.AbstractJSONStorageFactory;
import com.machinelinking.storage.DocumentConverter;

import java.net.UnknownHostException;

/**
 * Mongo implementation of {@link com.machinelinking.storage.JSONStorageFactory}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MongoJSONStorageFactory extends AbstractJSONStorageFactory<MongoJSONStorageConfiguration, MongoJSONStorage, MongoDocument> {

    @Override
    public MongoJSONStorageConfiguration createConfiguration(String configURI) {
        final Configuration c = parseConfigurationURI(configURI);
        return new MongoJSONStorageConfiguration(c.host, c.port, c.db, c.collection);
    }

    @Override
    public MongoJSONStorage createStorage(
            MongoJSONStorageConfiguration config, DocumentConverter<MongoDocument> converter
    ) {
        try {
            return new MongoJSONStorage(config, converter);
        } catch (UnknownHostException uhe) {
            throw new IllegalArgumentException("Error while instantiating storage with configuration: " + config);
        }
    }

    @Override
    public MongoJSONStorage createStorage(MongoJSONStorageConfiguration config) {
        return createStorage(config, null);
    }

}
