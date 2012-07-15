package com.machinelinking.service;

import com.machinelinking.WikiEnricher;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.serializer.JSONSerializer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Path("/annotate")
public class DefaultAnnotationService implements AnnotationService {

    @Path("/resource/{resource}")
    @GET
    @Produces({
            MediaType.APPLICATION_JSON
    })
    @Override
    public String annotate(@PathParam("resource")String resource) {
        final URL resourceURL;
        try {
            resourceURL = new URL(resource);
        } catch (MalformedURLException murle) {
            throw new IllegalArgumentException(
                    String.format("Invalid resource [%s], must be a valid URL.", resource),
                    murle
            );
        }
        // TODO: Optimize object usage.
        final DocumentSource documentSource = new DocumentSource(resourceURL);
        final WikiEnricher wikiEnricher = new WikiEnricher();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JSONSerializer jsonSerializer;
        try {
            jsonSerializer = new JSONSerializer(baos);
        } catch (IOException ioe) {
            throw new RuntimeException("Error while initializing serializer.", ioe);
        }
        try {
            wikiEnricher.enrichEntity(documentSource, jsonSerializer);
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing resource", e);
        }
        return baos.toString();
    }

}
