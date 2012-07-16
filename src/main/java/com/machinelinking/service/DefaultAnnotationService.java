package com.machinelinking.service;

import com.machinelinking.WikiEnricher;
import com.machinelinking.WikiEnricherFactory;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.serializer.JSONSerializer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Path("/annotate")
public class DefaultAnnotationService implements AnnotationService {

    public static final WikiEnricherFactory.Flag[] DEFAULT_FLAGS = new WikiEnricherFactory.Flag[] {
         WikiEnricherFactory.Flag.Structure
    };

    public static final String FLAG_SEPARATOR = ",";

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Path("/flags/")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public FlagSet flags() {
        return DefaultFlagList.getInstance();
    }

    @Path("/resource/{resource}")
    @GET
    @Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
    @Override
    public String annotate(@PathParam("resource")String resource, @QueryParam("flags")String flags) {
        final URL resourceURL;
        try {
            resourceURL = new URL(resource);
        } catch (MalformedURLException murle) {
            throw new IllegalArgumentException(
                    String.format("Invalid resource [%s], must be a valid URL.", resource),
                    murle
            );
        }

        final WikiEnricher wikiEnricher = WikiEnricherFactory.getInstance()
            .createFullyConfiguredInstance( toFlag(flags) );
        final DocumentSource documentSource = new DocumentSource(resourceURL);
        final JSONSerializer jsonSerializer;
        try {
            baos.reset();
            jsonSerializer = new JSONSerializer(baos);
        } catch (IOException ioe) {
            throw new RuntimeException("Error while initializing serializer.", ioe);
        }
        try {
            wikiEnricher.enrichEntity(documentSource, jsonSerializer);
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing resource", e);
        }
        // System.out.println("BAOS: " + baos.toString());
        return baos.toString();
    }

    private WikiEnricherFactory.Flag[] toFlag(String flagsStr) {
        if(flagsStr == null || flagsStr.trim().length() == 0) return DEFAULT_FLAGS;
        final String[] flagNames = flagsStr.split(FLAG_SEPARATOR);
        final Set<WikiEnricherFactory.Flag> flags = new HashSet<>();
        for(String flagName : flagNames) {
            try {
                flags.add(WikiEnricherFactory.Flag.valueOf(flagName));
            } catch (Exception e) {
                throw new RuntimeException(String.format("Error while resolving flag [%s]", flagName));
            }
        }
        return flags.toArray( new WikiEnricherFactory.Flag[flags.size()] );
    }

}
