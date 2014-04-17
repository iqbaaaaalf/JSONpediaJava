package com.machinelinking.service;

import com.machinelinking.filter.DefaultJSONFilterEngine;
import com.machinelinking.filter.JSONFilter;
import com.machinelinking.storage.mongodb.MongoDocument;
import com.machinelinking.storage.mongodb.MongoJSONStorage;
import com.machinelinking.storage.mongodb.MongoJSONStorageConfiguration;
import com.machinelinking.storage.mongodb.MongoJSONStorageConnection;
import com.machinelinking.storage.mongodb.MongoJSONStorageFactory;
import com.machinelinking.storage.mongodb.MongoResultSet;
import com.machinelinking.storage.mongodb.MongoSelector;
import com.machinelinking.storage.mongodb.MongoSelectorParser;
import com.machinelinking.storage.mongodb.MongoUtils;
import com.machinelinking.util.JSONUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Default implementation of {@link com.machinelinking.service.StorageService}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Path("/storage")
public class DefaultStorageService implements StorageService {

    public static final String STORAGE_SERVICE_CONNECTION_PROP = "storage.service.connection";
    public static final String STORAGE_SERVICE_QUERY_LIMIT_PROP = "storage.service.query.limit";

    private static final Logger logger = Logger.getLogger(DefaultStorageService.class);

    private final MongoJSONStorageConnection connection;
    private final int QUERY_LIMIT;

    public DefaultStorageService() {
        final ConfigurationManager manager = ConfigurationManager.getInstance();
        final String connString = manager.getProperty(STORAGE_SERVICE_CONNECTION_PROP);
        QUERY_LIMIT = Integer.parseInt(manager.getProperty(STORAGE_SERVICE_QUERY_LIMIT_PROP));
        final MongoJSONStorageFactory factory = new MongoJSONStorageFactory();
        final MongoJSONStorageConfiguration configuration = factory.createConfiguration(connString);
        final MongoJSONStorage storage = factory.createStorage(configuration);
        connection = storage.openConnection(configuration.getCollection());
    }

    @Path("/select")
    @GET
    @Produces({
            MediaType.APPLICATION_JSON + ";charset=UTF-8",
    })
    @Override
    public Response queryStorage(
            @QueryParam("q") String selector,
            @QueryParam("filter") String filter,
            @QueryParam("limit") String limit)
    {
        try {
            final JSONFilter jsonFilter = DefaultJSONFilterEngine.parseFilter(filter);
            final MongoSelector mongoSelector = MongoSelectorParser.getInstance().parse(selector);
            try (final MongoResultSet rs = connection.query(mongoSelector, toMaxLimit(limit))) {
                return Response.ok(
                        toJSONOutput(mongoSelector, jsonFilter, rs),
                        MediaType.APPLICATION_JSON + ";charset=UTF-8"
                ).build();
            }
        } catch (IllegalArgumentException iae) {
            throw new InvalidRequestException(iae);
        } catch (Exception e) {
            logger.error("Error while performing request.", e);
            throw new InternalErrorException(e);
        }
    }

    private int toMaxLimit(String limitStr) {
        if(limitStr == null) return QUERY_LIMIT;
        try {
            final int limit = Integer.parseInt(limitStr);
            return limit <= QUERY_LIMIT ? limit : QUERY_LIMIT;
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(nfe);
        }
    }

    private JsonNode applyFilter(JsonNode target, JSONFilter filter) {
        if(filter == null) return target;
        final ArrayNode result = JsonNodeFactory.instance.arrayNode();
        for(JsonNode node : DefaultJSONFilterEngine.applyFilter(target, filter)) {
            result.add(node);
        }
        return result;
    }

    private String toJSONOutput(MongoSelector selector, JSONFilter filter, MongoResultSet rs) {
        final ObjectNode output = JsonNodeFactory.instance.objectNode();
        final ArrayNode result = JsonNodeFactory.instance.arrayNode();
        output.put("query", selector.toString());
        output.put("count", rs.getCount());
        output.put("result", result);
        MongoDocument nextMongo;
        JsonNode nextJack;
        while((nextMongo = rs.next()) != null) {
            nextJack = MongoUtils.convertToJsonNode(nextMongo.getContent());
            result.add(applyFilter(nextJack, filter));
        }
        return JSONUtils.serializeToJSON(output, false);
    }

}
