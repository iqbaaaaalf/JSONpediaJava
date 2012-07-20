package com.machinelinking.service;

import com.machinelinking.WikiEnricher;
import com.machinelinking.WikiEnricherFactory;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.render.DefaultHTMLRenderFactory;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    public enum OutputFormat {
        json,
        html
    }

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

    @Path("/resource/{outFormat}/{resource}")
    @GET
    @Produces({
            MediaType.APPLICATION_JSON + ";charset=UTF-8",
            MediaType.TEXT_HTML + ";charset=UTF-8"
    })
    @Override
    public Response annotate(
            @PathParam("resource") String resource,
            @PathParam("outFormat")String outFormat,
            @QueryParam("flags")   String flags
    ) {
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
            final String json = baos.toString();
            return toOutputFormat(json, outFormat);
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing resource", e);
        }
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

    private Response toOutputFormat(String json, String outFormat) throws IOException {
        final OutputFormat format;
        try {
            format = OutputFormat.valueOf(outFormat);
        } catch (Exception e) {
            throw new IllegalArgumentException( String.format("Unsupported output format: [%s]", outFormat) );
        }
        switch(format) {
            case json:
                return Response.ok(json, MediaType.APPLICATION_JSON + ";charset=UTF-8").build();
            case html:
                final JsonNode rootNode = JSONUtils.parseJSON(json); // TODO: avoid this!
                return Response.ok(
                        DefaultHTMLRenderFactory.getInstance().renderToHTML(rootNode),
                        MediaType.TEXT_HTML + ";charset=UTF-8"
                ).build();
            default:
                throw new IllegalArgumentException("Unsupported conversion to " + format);
        }
    }

}
