package com.machinelinking.template;

import com.machinelinking.parser.WikiTextParserHandler;
import com.machinelinking.render.DefaultHTMLWriter;
import com.machinelinking.render.HTMLWriter;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class EvaluationContext {

    private final TemplateProcessor templateProcessor;
    private final String lang;
    private final String domain;
    private final Map<String,String> context;

    public EvaluationContext(TemplateProcessor templateProcessor, String lang, String domain, Map<String, String> context) {
        this.templateProcessor = templateProcessor;
        this.lang = lang;
        this.domain = domain;
        this.context = new HashMap<>(context);
    }

    public String getLanguage() {
        return lang;
    }

    public String getDomain() {
        return domain;
    }

    public String getValue(String varName) {
        return context.get(varName);
    }

    public String getValue(WikiTextParserHandler.Var var) {
        final String candidate = getValue(var.name);
        return candidate == null ? getValue(var.defaultValue) : candidate;
    }

    public String getValue(WikiTextParserHandler.Value v) {
        if(v == null) return "";
        if(v instanceof WikiTextParserHandler.Const) {
            return ((WikiTextParserHandler.Const) v).constValue;
        } else if(v instanceof WikiTextParserHandler.Var) {
            return getValue((WikiTextParserHandler.Var) v);
        } else
            throw new IllegalArgumentException();
    }

    public String getValue(TemplateCall templateCall) throws TemplateProcessorException {
        // TODO: optimize
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));
        final HTMLWriter htmlWriter = new DefaultHTMLWriter(writer);
        templateProcessor.process(this, templateCall, htmlWriter);
        try {
            htmlWriter.flush();
        } catch (IOException ioe) {
            throw new IllegalStateException();
        }
        return baos.toString();
    }

}
