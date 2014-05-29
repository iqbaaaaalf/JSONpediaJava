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
import java.net.URLDecoder;

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

    private static final MongoSelector EMPTY_SELECTOR = new MongoSelector();

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

    @Path("/mongo/select")
    @GET
    @Produces({
            MediaType.APPLICATION_JSON + ";charset=UTF-8",
    })
    @Override
    public Response queryMongoStorage(
            @QueryParam("q") String selector,
            @QueryParam("filter") String filter,
            @QueryParam("limit") String limit
    ) {
        try {
            selector = trimIfNotNull(selector);
            filter = trimIfNotNull(filter);
            assertParam(selector, "selector parameter must be specified");

            final MongoSelector mongoSelector = MongoSelectorParser.getInstance().parse(selector);
            final JSONFilter jsonFilter = DefaultJSONFilterEngine.parseFilter(filter);
            try (final MongoResultSet rs = connection.query(mongoSelector, toMaxLimit(limit))) {
                return Response.ok(
                        toMongoSelectJSONOutput(mongoSelector, jsonFilter, rs),
                        MediaType.APPLICATION_JSON + ";charset=UTF-8"
                ).build();
            }
        } catch (IllegalArgumentException iae) {
            throw new InvalidRequestException(iae);
        } catch (Exception e) {
            logger.error("Error while processing request.", e);
            throw new InternalErrorException(e);
        }
    }

    @Path("/mongo/mapred")
    @GET
    @Produces({
            MediaType.APPLICATION_JSON + ";charset=UTF-8",
    })
    @Override
    public Response mapRedMongoStorage(
            @QueryParam("criteria") String criteria,
            @QueryParam("map")String map,
            @QueryParam("reduce")String reduce,
            @QueryParam("limit") String limit
    ) {
        try {
            criteria = trimIfNotNull(criteria);
            map = trimIfNotNull(URLDecoder.decode(map, "utf8"));
            reduce = trimIfNotNull(URLDecoder.decode(reduce, "utf8"));

            assertParam(map, "map param must be specified");
            assertParam(reduce, "reduce param must be specified");

            final MongoSelector mongoSelector;
            if(criteria == null || criteria.trim().length() == 0) {
                mongoSelector = EMPTY_SELECTOR;
            } else {
                mongoSelector = MongoSelectorParser.getInstance().parse(criteria);
            }

            final JsonNode result = connection.processMapReduce(
                    mongoSelector.toDBObjectSelection(), map, reduce, toMaxLimit(limit)
            );
            return Response.ok(
                    toMongoMapRedJSONOutput(mongoSelector, result),
                    MediaType.APPLICATION_JSON + ";charset=UTF-8"
            ).build();
        } catch (IllegalArgumentException iae) {
            throw new InvalidRequestException(iae);
        } catch (Exception e) {
            logger.error("Error while processing request.", e);
            throw new InternalErrorException(e);
        }
    }

    private String trimIfNotNull(String in) {
        return in == null ? null : in.trim();
    }

    private void assertParam(Object val, String errMsg) {
        if(val == null) throw new IllegalArgumentException(errMsg);
    }

    private int toMaxLimit(String limit) {
        if(limit == null || limit.length() == 0) return QUERY_LIMIT;
        try {
            final int l = Integer.parseInt(limit);
            return l <= QUERY_LIMIT ? l : QUERY_LIMIT;
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

    private String toMongoSelectJSONOutput(MongoSelector selector, JSONFilter filter, MongoResultSet rs) {
        final ObjectNode output = JsonNodeFactory.instance.objectNode();
        final ArrayNode result = JsonNodeFactory.instance.arrayNode();
        output.put("query-explain", selector.toString());
        output.put("mongo-selection", selector.toDBObjectSelection().toString());
        output.put("mongo-projection", selector.toDBObjectProjection().toString());
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

    private String toMongoMapRedJSONOutput(MongoSelector selector, JsonNode result) {
        final ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("query-explain", selector.toString());
        output.put("mongo-selection", selector.toDBObjectSelection().toString());
        output.put("count", result.size());
        output.put("result", result);
        return JSONUtils.serializeToJSON(output, false);
    }

}
