package com.machinelinking.service;

import com.machinelinking.enricher.Flag;
import com.machinelinking.enricher.FlagSet;
import com.machinelinking.enricher.WikiEnricher;
import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.filter.DefaultJSONFilterEngine;
import com.machinelinking.filter.JSONFilter;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.render.DefaultDocumentContext;
import com.machinelinking.render.DefaultHTMLRenderFactory;
import com.machinelinking.render.DocumentContext;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.template.RenderScope;
import com.machinelinking.util.JSONUtils;
import com.machinelinking.wikimedia.WikiAPIParserException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.util.TokenBuffer;
import org.xml.sax.SAXException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation for {@link AnnotationService}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Path("/annotate")
public class DefaultAnnotationService implements AnnotationService {

    public static final String SPACE_REPLACER = "_";

    public static final boolean FORMAT_JSON = false;

    public enum OutputFormat {
        json,
        html
    }

    public static final Flag[] DEFAULT_FLAGS = new Flag[] {
         WikiEnricherFactory.Extractors
    };

    private final Pattern resourcePattern = Pattern.compile("^([a-z\\-]+):([^/]+)$");

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
            @QueryParam("procs")   String processors,
            @QueryParam("filter")  String filter
    ) {
        try {
            final DocumentSource documentSource = new DocumentSource(toResourceURL(resource));
            return annotateDocumentSource(documentSource, processors, outFormat, filter);
        } catch (IllegalArgumentException iae) {
            throw new InvalidRequestException(iae);
        } catch (UnknownHostException uhe) {
            throw new UnreachableWikipediaService(uhe);
        } catch (WikiAPIParserException wape) {
            throw new UnresolvableEntityException(wape);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalErrorException(e);
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
            @FormParam("procs")    String processors,
            @FormParam("wikitext") String wikitext,
            @FormParam("filter")   String filter
    ) {
        try {
            final DocumentSource documentSource = new DocumentSource(toResourceURL(resource), wikitext);
            return annotateDocumentSource(documentSource, processors, outFormat, filter);
        } catch (IllegalArgumentException iae) {
            throw new InvalidRequestException(iae);
        } catch (UnknownHostException uhe) {
            throw new UnreachableWikipediaService(uhe);
        } catch (WikiAPIParserException wape) {
            throw new UnresolvableEntityException(wape);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    private URL toResourceURL(String resource) {
        final Matcher resourceMatcher = resourcePattern.matcher(resource);
        final String resourceURL;
        if(resourceMatcher.matches()) {
            final String lang = resourceMatcher.group(1);
            final String document = resourceMatcher
                    .group(2)
                    .replaceAll(" ", SPACE_REPLACER).replaceAll("%20", SPACE_REPLACER);
            resourceURL = String.format("http://%s.wikipedia.org/wiki/%s", lang, document);
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
    ) throws InterruptedException, SAXException, WikiTextParserException, ExecutionException, IOException {
        final OutputFormat format = checkOutFormat(outFormat);
        final WikiEnricher wikiEnricher = WikiEnricherFactory
                .getInstance()
                .createFullyConfiguredInstance(flags, DEFAULT_FLAGS);
        final JSONSerializer jsonSerializer;
        final JSONFilter filter;
        try {
            filter = DefaultJSONFilterEngine.parseFilter(filterExp);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while parsing filter.", e);
        }

        final TokenBuffer buffer = JSONUtils.createJSONBuffer();
        try {
            jsonSerializer = new JSONSerializer(buffer);
        } catch (IOException ioe) {
            throw new IllegalStateException("Error while initializing serializer.", ioe);
        }

        wikiEnricher.enrichEntity(documentSource, jsonSerializer);
        return toOutputFormat(documentSource.getDocumentURL(), buffer, format, filter);
    }

    private OutputFormat checkOutFormat(String outFormat) {
        try {
            return OutputFormat.valueOf(outFormat);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unsupported output format: [%s]", outFormat));
        }
    }

    private TokenBuffer printOutFilterResult(JsonNode json, JSONFilter filter) throws IOException {
        final JsonNode[] filteredNodes = DefaultJSONFilterEngine.applyFilter(json, filter);
        final TokenBuffer buffer = JSONUtils.createJSONBuffer();
        buffer.writeStartObject();
        buffer.writeObjectField("filter", filter.humanReadable());
        buffer.writeFieldName("result");
        buffer.writeStartArray();
        final ObjectMapper mapper = new ObjectMapper();
        for(JsonNode filteredNode : filteredNodes) {
            mapper.writeTree(buffer, filteredNode);
        }
        buffer.writeEndArray();
        buffer.writeEndObject();
        buffer.close();
        return buffer;
    }

    private TokenBuffer printOutFilterResult(TokenBuffer buffer, JSONFilter filter) throws IOException {
        return printOutFilterResult(
                JSONUtils.bufferToJSONNode(buffer),
                filter
        );
    }

    private Response toOutputFormat(URL documentURL, TokenBuffer buffer, OutputFormat format, JSONFilter filter)
    throws IOException {
        switch(format) {
            case json:
                return Response.ok(
                        JSONUtils.bufferToJSONString(
                                filter.isEmpty() ?
                                        buffer
                                        :
                                        printOutFilterResult(buffer, filter),
                                FORMAT_JSON
                        ),
                        MediaType.APPLICATION_JSON + ";charset=UTF-8"
                ).build();
            case html:
                final JsonNode rootNode = JSONUtils.bufferToJSONNode(buffer);
                final JsonNode target   =
                        filter.isEmpty() ? rootNode : JSONUtils.bufferToJSONNode(printOutFilterResult(rootNode, filter));
                final DocumentContext context = new DefaultDocumentContext(
                        RenderScope.FULL_RENDERING,
                        documentURL
                );
                return Response.ok(
                        DefaultHTMLRenderFactory.getInstance().createRender().renderDocument(context, target),
                        MediaType.TEXT_HTML + ";charset=UTF-8"
                ).build();
            default:
                throw new IllegalArgumentException("Unsupported conversion to " + format);
        }
    }

}
