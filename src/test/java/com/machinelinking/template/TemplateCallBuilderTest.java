package com.machinelinking.template;

import com.machinelinking.parser.DocumentSource;
import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.parser.WikiTextParserHandler;
import junit.framework.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Test case for {@link com.machinelinking.template.TemplateCallBuilder}
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateCallBuilderTest {

    @Test
    public void testCreateHandlerWithListener() throws IOException, WikiTextParserException {
        final int[] handlerCount = new int[] {0};
        final WikiTextParserHandler handler = TemplateCallBuilder.createHandlerWithListener(
                new TemplateCallListener() {
                    @Override
                    public void handle(TemplateCall call) {
                        Assert.assertEquals("['t1'] [{null=null:['v2'], p1=p1:['v1']}]", call.toString());
                        handlerCount[0]++;
                    }
                });

        final WikiTextParser parser = new WikiTextParser(handler);
        parser.parse( new DocumentSource(
                new URL("http://fake.org"),
                new ByteArrayInputStream("{{t1|p1=v1|v2}}".getBytes()))
        );

        Assert.assertEquals(1, handlerCount[0]);
    }

}
