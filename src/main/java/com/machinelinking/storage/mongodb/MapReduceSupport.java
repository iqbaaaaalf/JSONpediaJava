package com.machinelinking.storage.mongodb;

import com.mongodb.DBObject;
import org.codehaus.jackson.JsonNode;

/**
 * Introduces map/reduce support in MongoDB.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface MapReduceSupport {

    JsonNode processMapReduce(DBObject query, String map, String reduce, int limit);

}
