package com.machinelinking.dbpedia;

import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * In memory hashmap implementation of {@link OntologyManager}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InMemoryOntologyManager implements OntologyManager {

    public static final String DBPEDIA_ONTOLOGY_PREFIX = "http://dbpedia.org/ontology/";

    public static final String XSD_PREFIX = "http://www.w3.org/2001/XMLSchema#";

    private static final String SERVICE = "http://dbpedia.org/sparql?query=%s&format=json";

    private static final String QUERY =
            "SELECT * WHERE {\n" +
            "{?p a <http://www.w3.org/2002/07/owl#ObjectProperty>     . " +
                    "OPTIONAL {?p rdfs:domain ?domain; rdfs:range ?range} . ?p rdfs:label ?label}\n" +
            "UNION\n" +
            "{?p a <http://www.w3.org/2002/07/owl#FunctionalProperty> . " +
                    "OPTIONAL {?p rdfs:domain ?domain; rdfs:range ?range} . ?p rdfs:label ?label}\n" +
            "FILTER langMatches( lang(?label), \"EN\" )\n" +
            "}";

    private final Map<String, Property> mapping;

    public static Map<String, Property> initOntologyIndex(boolean force) throws OntologyManagerException {
        final File serializationFile = getSerializationFile();
        if(!force && serializationFile.exists()) return loadOntologyIndex(serializationFile);


        final JsonNode result;
        try {
            final URL url = new URL(String.format(SERVICE, URLEncoder.encode(QUERY, "UTF-8")));
            final InputStream is = new BufferedInputStream( url.openStream() );
            result = JSONUtils.parseJSON(is);
        } catch (IOException ioe) {
            throw new OntologyManagerException(ioe);
        }
        Iterator<JsonNode> bindings = result.get("results").get("bindings").getElements();
        JsonNode current;
        String property;
        String enLabel;
        String domain;
        String range;
        Map<String, Property> ontology = new HashMap<>();
        while(bindings.hasNext()) {
            current = bindings.next();
            property = normalizePrefix(current.get("p").get("value").asText());
            enLabel  = normalizePrefix(current.get("label").get("value").asText());
            domain   = normalizePrefix(JSONUtils.asPrimitiveString(optionallyGetFieldValue(current, "domain")));
            range    = normalizePrefix(JSONUtils.asPrimitiveString(optionallyGetFieldValue(current, "range")));
            ontology.put(property, new DefaultProperty(property, enLabel, domain, range));
        }
        saveOntologyIndex(ontology, serializationFile);
        return ontology;
    }

    public static Map<String, Property> initOntologyIndex() throws OntologyManagerException {
        return initOntologyIndex(false);
    }

    private static JsonNode optionallyGetFieldValue(JsonNode n, String field) {
        JsonNode f = n.get(field);
        return f == null ? null : f.get("value");
    }

    private static String normalizePrefix(String url) {
        if(url == null) return null;
        if(url.startsWith(DBPEDIA_ONTOLOGY_PREFIX)) {
            return url.substring(DBPEDIA_ONTOLOGY_PREFIX.length());
        } else if(url.startsWith(XSD_PREFIX)) {
            return String.format("xsd:%s", url.substring(XSD_PREFIX.length()));
        }
        return url;
    }

    private static void saveOntologyIndex(Map<String, ? extends Property> ontology, File serializationFile)
    throws OntologyManagerException {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream( new BufferedOutputStream( new FileOutputStream(serializationFile)));
            oos.writeObject(ontology);
            oos.close();
        } catch (IOException ioe) {
            throw new OntologyManagerException(ioe);
        }
    }

    private static Map<String, Property> loadOntologyIndex(File serializationFile) throws OntologyManagerException {
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(serializationFile)));
            final Map<String, Property> mapping =
                    (Map<String, Property>) ois.readObject();
            ois.close();
            return mapping;
        } catch (IOException | ClassNotFoundException ioe) {
            throw new OntologyManagerException(ioe);
        }
    }

    private static File getSerializationFile() {
        return new File("ontology.ser");
    }

    public InMemoryOntologyManager(Map<String, Property> mapping) throws OntologyManagerException {
        this.mapping = mapping;
    }

    public InMemoryOntologyManager() throws OntologyManagerException {
        this( initOntologyIndex() );
    }

    @Override
    public Property getProperty(String property) {
        return mapping.get(property);
    }

}
