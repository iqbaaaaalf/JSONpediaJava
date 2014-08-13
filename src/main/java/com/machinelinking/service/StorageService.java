package com.machinelinking.service;

import javax.ws.rs.core.Response;

/**
 * Defines a service to access a data storage.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface StorageService extends Service {

    Response queryMongoStorage(String selector, String filter, String limit);

    Response mapRedMongoStorage(String criteria, String map, String reduce, String limit);

    Response queryElasticStorage(String selector, String filter, String limit);

    Response queryElasticFacets(String callback, String source);

}
