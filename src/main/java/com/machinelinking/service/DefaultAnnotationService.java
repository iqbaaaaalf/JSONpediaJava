/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

package com.machinelinking.service;

import com.machinelinking.pipeline.FlagSet;
import com.machinelinking.pipeline.WikiPipeline;
import com.machinelinking.pipeline.WikiPipelineFactory;
import com.machinelinking.filter.DefaultJSONFilterEngine;
import com.machinelinking.filter.JSONFilter;
import com.machinelinking.parser.DocumentSource;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.render.DefaultDocumentContext;
import com.machinelinking.render.DefaultHTMLRenderFactory;
import com.machinelinking.render.DocumentContext;
import com.machinelinking.render.NodeRenderException;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.template.RenderScope;
import com.machinelinking.util.JSONUtils;
import com.machinelinking.wikimedia.WikiAPIParserException;
import org.codehaus.jackson.JsonNode;
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
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 * Default implementation for {@link AnnotationService}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Path("/annotate")
public class DefaultAnnotationService implements AnnotationService {

    public static final boolean FORMAT_JSON = false;

    public enum OutputFormat {
        json,
        html
    }

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
            final DocumentSource documentSource = new DocumentSource(JSONUtils.toResourceURL(resource));
            return annotateDocumentSource(documentSource, processors, outFormat, filter);
        } catch (IllegalArgumentException iae) {
            throw new InvalidRequestException(iae);
        } catch (UnknownHostException uhe) {
            throw new UnreachableWikipediaServiceException(uhe);
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
            final DocumentSource documentSource = new DocumentSource(JSONUtils.toResourceURL(resource), wikitext);
            return annotateDocumentSource(documentSource, processors, outFormat, filter);
        } catch (IllegalArgumentException iae) {
            throw new InvalidRequestException(iae);
        } catch (UnknownHostException uhe) {
            throw new UnreachableWikipediaServiceException(uhe);
        } catch (WikiAPIParserException wape) {
            throw new UnresolvableEntityException(wape);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    private Response annotateDocumentSource(
            DocumentSource documentSource,
            String flags,
            String outFormat,
            String filterExp
    ) throws InterruptedException, SAXException, WikiTextParserException, ExecutionException, IOException, NodeRenderException {
        final OutputFormat format = checkOutFormat(outFormat);
        final WikiPipeline wikiEnricher = WikiPipelineFactory
                .getInstance()
                .createFullyConfiguredInstance(flags, WikiPipelineFactory.DEFAULT_FLAGS);
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

    private Response toOutputFormat(URL documentURL, TokenBuffer buffer, OutputFormat format, JSONFilter filter)
            throws IOException, NodeRenderException {
        switch(format) {
            case json:
                return Response.ok(
                        JSONUtils.bufferToJSONString(
                                JSONUtils.createResultFilteredObject(buffer, filter), FORMAT_JSON
                        ),
                        MediaType.APPLICATION_JSON + ";charset=UTF-8"
                ).build();
            case html:
                final JsonNode rootNode = JSONUtils.bufferToJSONNode(buffer);
                final JsonNode target   =
                        filter.isEmpty() ? rootNode : JSONUtils.bufferToJSONNode(
                                JSONUtils.createResultFilteredObject(rootNode, filter)
                        );
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
