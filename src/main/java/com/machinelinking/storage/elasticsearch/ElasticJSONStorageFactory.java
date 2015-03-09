/*
 * Copyright 2012-2015 Michele Mostarda (me@michelemostarda.it)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
