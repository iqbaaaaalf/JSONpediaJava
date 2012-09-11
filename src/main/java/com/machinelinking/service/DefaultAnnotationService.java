package com.machinelinking.service;

import com.machinelinking.enricher.Flag;
import com.machinelinking.enricher.FlagSet;
import com.machinelinking.enricher.WikiEnricher;
import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.filter.DefaultJSONFilterEngine;
import com.machinelinking.filter.JSONFilter;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.render.DefaultHTMLRenderFactory;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Path("/annotate")
public class DefaultAnnotationService implements AnnotationService {

    public enum OutputFormat {
        json,
        html
    }

    public static final Flag[] DEFAULT_FLAGS = new Flag[] {
         WikiEnricherFactory.Structure
    };

    private final Pattern resourcePattern = Pattern.compile("^([a-z\\-]+):([^/]+)$");

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Path("/flags/")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public FlagSet flags() {
        return FlagSetWrapper.getInstance();
    }

    @Path("/resource/{outFormat}/{resource}")
    @GET
    @Produces({
            MediaType.APPLICATION_JSON + ";charset=UTF-8",
            MediaType.TEXT_HTML + ";charset=UTF-8"
    })
    @Override
    public Response annotateResource(
            @PathParam("resource") String resource,
            @PathParam("outFormat")String outFormat,
            @QueryParam("flags")   String flags,
            @QueryParam("filter")  String filter
    ) {
        try {
            final DocumentSource documentSource = new DocumentSource(toResourceURL(resource));
            return annotateDocumentSource(documentSource, flags, outFormat, filter);
        } catch (IllegalArgumentException iae) {
            throw new InvalidEntityException(iae);
        }
    }

    @Path("/resource/{outFormat}/{resource}")
    @POST
    @Produces({
            MediaType.APPLICATION_JSON + ";charset=UTF-8",
            MediaType.TEXT_HTML + ";charset=UTF-8"
    })
    @Override
    public Response annotateResource(
            @PathParam("resource") String resource,
            @PathParam("outFormat")String outFormat,
            @FormParam("flags")    String flags,
            @FormParam("wikitext") String wikitext,
            @FormParam("filter")   String filter
    ) {
        try {
        final DocumentSource documentSource = new DocumentSource(toResourceURL(resource), wikitext);
        return annotateDocumentSource(documentSource, flags, outFormat, filter);
        } catch (IllegalArgumentException iae) {
            throw new InvalidEntityException(iae);
        }
    }

    private URL toResourceURL(String resource) {
        final Matcher resourceMatcher = resourcePattern.matcher(resource);
        final String resourceURL;
        if(resourceMatcher.matches()) {
            resourceURL = String.format(
                    "http://%s.wikipedia.org/%s", resourceMatcher.group(1), resourceMatcher.group(2)
            );
        } else {
            resourceURL = resource;
        }
        try {
            return new URL(resourceURL);
        } catch (MalformedURLException murle) {
            throw new IllegalArgumentException(
                    String.format("Invalid resource [%s], must be a valid URL.", resource),
                    murle
            );
        }
    }

    private Response annotateDocumentSource(
            DocumentSource documentSource,
            String flags,
            String outFormat,
            String filterExp
    ) {
        final OutputFormat format = checkOutFormat(outFormat);
        final WikiEnricher wikiEnricher = WikiEnricherFactory
                .getInstance()
                .createFullyConfiguredInstance(flags, DEFAULT_FLAGS);
        final JSONSerializer jsonSerializer;
        final JSONFilter filter;
        try {
            filter = filterExp == null ? null : DefaultJSONFilterEngine.parseFilter(filterExp);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing filter.", e);
        }
        try {
            baos.reset();
            jsonSerializer = new JSONSerializer( JSONUtils.createJSONGenerator(baos, true) );
        } catch (IOException ioe) {
            throw new RuntimeException("Error while initializing serializer.", ioe);
        }
        try {
            wikiEnricher.enrichEntity(documentSource, jsonSerializer);
            final String json = baos.toString();
            return toOutputFormat(json, format, filter);
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing resource", e);
        }
    }

    private OutputFormat checkOutFormat(String outFormat) {
        try {
            return OutputFormat.valueOf(outFormat);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unsupported output format: [%s]", outFormat));
        }
    }

    private String printOutFilterResult(JsonNode json, JSONFilter filter) throws IOException {
        final JsonNode[] filteredNodes = DefaultJSONFilterEngine.applyFilter(
                json,
                filter
        );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final JsonGenerator generator = JSONUtils.createJSONGenerator(baos, true);
        generator.writeStartObject();
        generator.writeObjectField("filter", filter.print());
        generator.writeFieldName("result");
        generator.writeStartArray();
        final ObjectMapper mapper = new ObjectMapper();
        for(JsonNode filteredNode : filteredNodes) {
            mapper.writeTree(generator, filteredNode);
        }
        generator.writeEndArray();
        generator.writeEndObject();
        generator.close();
        return baos.toString();
    }

    private String printOutFilterResult(String json, JSONFilter filter) throws IOException {
        return printOutFilterResult(
                JSONUtils.parseJSON(json), // TODO: avoid this!
                filter
        );
    }

    private Response toOutputFormat(String json, OutputFormat format, JSONFilter filter)
    throws IOException {
        switch(format) {
            case json:
                return Response.ok(
                        filter == null ? json : printOutFilterResult(json, filter),
                        MediaType.APPLICATION_JSON + ";charset=UTF-8"
                ).build();
            case html:
                final JsonNode rootNode = JSONUtils.parseJSON(json); // TODO: avoid this!
                final JsonNode target   =
                        filter == null ? rootNode : JSONUtils.parseJSON( printOutFilterResult(rootNode, filter) ); // TODO: avoid this!
                return Response.ok(
                        DefaultHTMLRenderFactory.getInstance().renderToHTML(target),
                        MediaType.TEXT_HTML + ";charset=UTF-8"
                ).build();
            default:
                throw new IllegalArgumentException("Unsupported conversion to " + format);
        }
    }

}
