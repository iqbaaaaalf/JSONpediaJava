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

import com.machinelinking.storage.JSONStorageConfiguration;

/**
 * {@link com.machinelinking.storage.JSONStorageConfiguration} implementation for <i>ElasticSearch</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ElasticJSONStorageConfiguration implements JSONStorageConfiguration {

    private final String host;
    private final int port;
    private final String db;
    private final String collection;

    public ElasticJSONStorageConfiguration(String host, int port, String db, String collection) {
        this.host = host;
        this.port = port;
        this.db = db;
        this.collection = collection;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String getDB() {
        return db;
    }

    @Override
    public String getCollection() {
        return collection;
    }

}
