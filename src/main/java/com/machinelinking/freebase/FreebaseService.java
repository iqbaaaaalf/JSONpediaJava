package com.machinelinking.freebase;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Provides enrichment on the <i>Google Freebase</i> service.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FreebaseService {

    private static final String API_SERVICE = "https://www.googleapis.com/freebase/v1/";

    private static final String SEARCH_SERVICE = API_SERVICE + "search?query=%s";

    private static final String RESPONSE_OK  = "200 OK";
    private static final String RESULT_FIELD = "result";

    private static FreebaseService instance;

    public static FreebaseService getInstance() {
        if(instance == null) instance = new FreebaseService();
        return instance;
    }

    private FreebaseService() {}

    /**
     * Returns data for a given entity name.
     *
     * @param entityName
     * @return
     * @throws IOException
     */
    public JsonNode getEntityData(String entityName) throws IOException {
        final URL query;
        try {
            query = new URL(
                    String.format(SEARCH_SERVICE, URLEncoder.encode(entityName, "UTF-8"))
            );
        } catch (Exception e) {
            throw new RuntimeException("Error while preparing query.");
        }
        JsonNode response = JSONUtils.parseJSON(query.openStream());
        final String status = response.get("status").asText();
        if( ! RESPONSE_OK.equals(status) ) throw new RuntimeException("Invalid response status: " + status);
        return response.get(RESULT_FIELD).get(0);
    }

}
