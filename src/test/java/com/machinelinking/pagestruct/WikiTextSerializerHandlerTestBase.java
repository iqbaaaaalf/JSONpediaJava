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

package com.machinelinking.pagestruct;

import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.serializer.Serializer;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Base test case. for {@link WikiTextSerializerHandler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextSerializerHandlerTestBase <T extends WikiTextSerializerHandler> {

    private static final Logger logger = Logger.getLogger(WikiTextSerializerHandlerTestBase.class);

    private final Class<T> handlerClass;
    private T handler;

    public WikiTextSerializerHandlerTestBase(Class<T> handlerClass) {
        this.handlerClass = handlerClass;
    }

    protected T getHandler() {
        return handler;
    }

    protected void verifySerialization(String wikiPage, String expectedJSON)
    throws IOException, WikiTextParserException {
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
                                    this.getClass().getResourceAsStream(String.format("%s.wikitext", wikiPage))
                            )
                    )
            );
        } finally {
            actual = baos.toString();
            logger.debug("Serialization: " + actual);
        }

        final JsonNode actualJSONNode   = JSONUtils.parseJSON(actual);
        final JsonNode expectedJSONNode = JSONUtils.parseJSON(
            this.getClass().getResourceAsStream(String.format("%s.json", expectedJSON))
        );

        Assert.assertEquals("Unexpected serialization.", expectedJSONNode.toString(), actualJSONNode.toString());
    }

    protected void verifySerialization(String wikiPage) throws IOException, WikiTextParserException {
        verifySerialization(wikiPage, wikiPage);
    }

}
