package com.machinelinking.template;

import com.machinelinking.pagestruct.WikiTextSerializerHandlerFactory;
import com.machinelinking.parser.WikiTextParser;
import com.machinelinking.parser.WikiTextParserException;
import com.machinelinking.render.DefaultDocumentContext;
import com.machinelinking.render.DefaultHTMLRenderFactory;
import com.machinelinking.render.DocumentContext;
import com.machinelinking.serializer.JSONSerializer;
import com.machinelinking.util.JSONUtils;
import junit.framework.Assert;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultTemplateProcessorTest {

    @Test
    public void testProcessVariable() throws Exception {
        for(String var : DefaultTemplateProcessor.VARIABLES) {
            Assert.assertEquals(
                    String.format("(variable %s)", var),
                    processWikitext(String.format("{{%s}}", var))
            );
        }
    }

    @Test
    public void testProcessMetadata() throws Exception {
        for(String var : DefaultTemplateProcessor.METADATA) {
            Assert.assertEquals(
                    String.format("(metadata %s)", var),
                    processWikitext(String.format("{{%s}}", var))
            );
        }
    }

    @Test
    public void testProcessFormatting() throws Exception {
        checkProcess(
                "{{lc:HELLO}}",
                "hello"
        );
        checkProcess(
                "{{lcfirst:HELLO}}",
                "hELLO"
        );
        checkProcess(
                "{{uc:hello}}",
                "HELLO"
        );
        checkProcess(
                "{{ucfirst:hello}}",
                "Hello"
        );
        checkProcess(
                "{{formatnum:12459686}}",
                "12459686"
        );
        checkProcess(
                "{{#formatdate:03-05-2014}}",
                "03-05-2014"
        );
        checkProcess(
                "{{padleft:123|10}}",
                "1230000000"
        );
        checkProcess(
                "{{padright:123|10}}",
                "0000000123"
        );
        checkProcess(
                "{{plural:1|single|many}}",
                "single"
        );
        checkProcess(
                "{{plural:2|single|many}}",
                "many"
        );
        checkProcess(
                "{{#time:dd mm yyyy|1979-09-23}}",
                "1979-09-23"
        );
        checkProcess(
                "{{gender:micmos|lui|lei|esso}}",
                "lui/lei/esso"
        );
        checkProcess(
                "{{#tag:t|content|k1=v1|k2=v2|k3}}",
                "<t k3 k1=\"v1\" k2=\"v2\">content</t>"
        );
    }

    @Test
    public void testProcessPath() throws WikiTextParserException, TemplateProcessorException, IOException {
        checkProcess(
                "{{localurl:Test}}",
                "/w/index.php?title=Test&"
        );
        checkProcess(
                "{{localurl:Test|appo}}",
                "/w/index.php?title=Test&appo"
        );
        checkProcess(
                "{{fullurl:Test}}",
                "///w/index.php?title=Test&"
        );
        checkProcess(
                "{{fullurl:Test|appo}}",
                "///w/index.php?title=Test&appo"
        );
        checkProcess(
                "{{canonicalurl:Test}}",
                "http://en.wikipedia.org/w/index.php?title=Test&"
        );
        checkProcess(
                "{{canonicalurl:Test|appo}}",
                "http://en.wikipedia.org/w/index.php?title=Test&appo"
        );
        checkProcess(
                "{{filepath:File}}",
                "File"
        );
        checkProcess(
                "{{urlencode:appo pappo|PATH}}",
                "appo_pappo"
        );
        checkProcess(
                "{{urlencode:appo pappo|WIKI}}",
                "appo+pappo"
        );
        checkProcess(
                "{{anchorencode:appo pappo}}",
                "appo+pappo"
        );
        for(int i = 1; i <= DefaultTemplateProcessor.NAMESPACES.size(); i++) {
            checkProcess(
                    String.format("{{ns:%d}}", i),
                    DefaultTemplateProcessor.NAMESPACES.get(i - 1)
            );
        }
        checkProcess(
                "{{#reltoabs:a/b}}",
                "a/b"
        );
        checkProcess(
                "{{#titleparts:Title|1|1}}",
                "Title"
        );
    }

    @Test
    public void testConditionalExpressions() throws WikiTextParserException, TemplateProcessorException, IOException {
        checkProcess(
                "{{#expr:1+2+3}}",
                "1+2+3"
        );

        checkProcess(
                "{{#if:|POS|NEG}}",
                "NEG"
        );
        checkProcess(
                "{{#if:{{{unexisting}}}|POS|NEG}}",
                "NEG"
        );
        checkProcess(
                "{{#if:x|POS|NEG}}",
                "POS"
        );
        checkProcess(
                "{{#if:{{{x}}}|POS|NEG}}",
                "POS",
                "x", "vx"
        );

        checkProcess(
                "{{#ifeq:v1|v1|POS|NEG}}",
                "POS"
        );
        checkProcess(
                "{{#ifeq:v1|v2|POS|NEG}}",
                "NEG"
        );
        checkProcess(
                "{{#ifeq:|v2|POS|NEG}}",
                "NEG"
        );
        checkProcess(
                "{{#ifeq:{{{v1}}}|{{{v2}}}|POS|NEG}}",
                "POS",

                "v1", "value1",
                "v2", "value1"
        );
        checkProcess(
                "{{#ifeq:{{{v1}}}|{{{v2}}}|POS|NEG}}",
                "NEG",

                "v1", "value1",
                "v2", "value2"
        );

        checkProcess(
                "{{#switch:K1|K1=V1|K2=V2|K3=V3|V4}}",
                "V1"
        );
        checkProcess(
                "{{#switch:K3|K1=V1|K2=V2|K3=V3|V4}}",
                "V3"
        );
        checkProcess(
                "{{#switch:KX|K1=V1|K2=V2|K3=V3|V4}}",
                "V4"
        );
        checkProcess(
                "{{#switch:{{{v1}}}|K1=V1|K2=V2|K3=V3|V4}}",
                "V3",

                "v1", "K3"
        );
    }

    @Test
    public void testConditionalWithTemplateNesting()
    throws WikiTextParserException, TemplateProcessorException, IOException {
        checkProcess(
                 "{{lc:F{{{out}}}G}}",
                 "foutputg",

                 "out", "OUTPUT"
        );
        checkProcess(
                 "{{#ifeq:x|x|{{uc:{{{out1}}}}}|{{{out2}}}}}",
                 "OUTPUT 1",

                 "v1", "value",
                 "v2", "valueX",
                 "out1", "output 1",
                 "out2", "output 2"
        );
        checkProcess(
                 "{{#ifeq:{{{v1}}}|{{{v2}}}|{{uc:{{{out1}}}}}|{{lc:{{{out2}}}}}}}",
                 "output 2",

                 "v1", "value",
                 "v2", "valueX",
                 "out1", "output 1",
                 "out2", "OUTPUT 2"
        );
        checkProcess(
                 "{{#ifeq:{{{v1}}}|{{{v2}}}|A{{uc:B{{{out1}}}C}}D|E{{lc:F{{{out2}}}G}}H}}",
                 "Efoutput 2gH",

                 "v1", "value",
                 "v2", "valueX",
                 "out1", "output 1",
                 "out2", "OUTPUT 2"
         );
    }

    private String processWikitext(String wikiText, Map<String,String> contextMap)
    throws IOException, WikiTextParserException, TemplateProcessorException {
        final URL documentURL = new URL("http://en.wikipedia.org/page/Test");
        final ByteArrayOutputStream jsonBuffer = new ByteArrayOutputStream();
        final JSONSerializer serializer = new JSONSerializer(jsonBuffer);
        final WikiTextParser parser = new WikiTextParser(
                WikiTextSerializerHandlerFactory.getInstance().createSerializerHandler(serializer)
        );
        parser.parse(documentURL, new StringReader(wikiText));
        final JsonNode root = JSONUtils.parseJSON(jsonBuffer.toString()); // TODO: optimize
        final JsonNode fragment = root.get("structure").get(0);

        final DocumentContext context = new DefaultDocumentContext(documentURL, contextMap);
        return DefaultHTMLRenderFactory.getInstance().createRender(false).renderFragment(context, fragment);
    }

    private String processWikitext(String wikiText)
    throws WikiTextParserException, TemplateProcessorException, IOException {
        return processWikitext(wikiText, Collections.<String,String>emptyMap());
    }

    private void checkProcess(String wikitext, String expected, String... args)
    throws WikiTextParserException, TemplateProcessorException, IOException {
        final Map<String,String> context = new HashMap<>();
        for(int i = 0; i < args.length; i+=2) {
            context.put(args[i], args[i + 1]);
        }
        Assert.assertEquals(expected, processWikitext(wikitext, context));
    }

}