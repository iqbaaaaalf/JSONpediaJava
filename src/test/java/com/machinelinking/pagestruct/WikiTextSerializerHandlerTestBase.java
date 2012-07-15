package com.machinelinking.pagestruct;

import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextSerializerHandlerTestBase <T extends WikiTextSerializerHandler> {

    private final Class<T> handlerClass;
    private T handler;

    public WikiTextSerializerHandlerTestBase(Class<T> handlerClass) {
        this.handlerClass = handlerClass;
    }

    protected T getHandler() {
        return handler;
    }

    protected void verifySerialization(String wikiPage, String expectedJSON) throws IOException, WikiTextParserException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Serializer serializer = new JSONSerializer(baos);
        try {
            this.handler = this.handlerClass.getConstructor(Serializer.class).newInstance(serializer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        final WikiTextParser parser = new WikiTextParser(handler);

        final String actual;
        try {
            parser.parse(
                    new URL("http://test/" + wikiPage),
                    new BufferedReader(
                            new InputStreamReader(
                                    this.getClass().getResourceAsStream(String.format("/%s.wikitext", wikiPage))
                            )
                    )
            );
        } finally {
            actual = baos.toString();
            System.out.println("BAOS: " + actual);
        }

        final JsonNode actualJSONNode   = JSONUtils.parseJSON(actual);
        final JsonNode expectedJSONNode = JSONUtils.parseJSON(
            this.getClass().getResourceAsStream(String.format("/%s.json", expectedJSON))
        );

        Assert.assertEquals("Unexpected serialization.", expectedJSONNode, actualJSONNode);
    }

    protected void verifySerialization(String wikiPage) throws IOException, WikiTextParserException {
        verifySerialization(wikiPage, wikiPage);
    }

}
