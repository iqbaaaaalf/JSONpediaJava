package com.machinelinking.service;

import javax.ws.rs.core.Response;

/**
 * Defines a service to access a data storage.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface StorageService extends Service {

    Response queryStorage(String selector, String filter, String limit);

}
