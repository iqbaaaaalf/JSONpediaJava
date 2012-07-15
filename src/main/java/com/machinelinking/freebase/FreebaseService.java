package com.machinelinking.freebase;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FreebaseService {

    private static final String API_SERVICE = "http://api.freebase.com/api/service/";

    private static final String SEARCH_SERVICE = API_SERVICE + "search?query=";

    private static final String RESPONSE_OK  = "200 OK";
    private static final String RESULT_FIELD = "result";

    private static FreebaseService instance;

    public static FreebaseService getInstance() {
        if(instance == null) instance = new FreebaseService();
        return instance;
    }

    private FreebaseService() {}

    public JsonNode getEntityData(String entityName) throws IOException {
        final URL query;
        try {
            query = new URL(SEARCH_SERVICE + URLEncoder.encode(entityName, "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("Error while preparing query.");
        }
        JsonNode response = JSONUtils.parseJSON(query.openStream());
        final String status = response.get("status").asText();
        if( ! RESPONSE_OK.equals(status) ) throw new RuntimeException("Invalid response status: " + status);
        return response.get(RESULT_FIELD).get(0);
    }

}
