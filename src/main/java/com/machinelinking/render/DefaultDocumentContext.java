package com.machinelinking.render;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultDocumentContext implements DocumentContext {

    private final URL documentURL;
    private final Map<String,String> vars;

    public DefaultDocumentContext(URL documentURL, Map<String, String> vars) {
        this.documentURL = documentURL;
        this.vars = vars;
    }

    public DefaultDocumentContext(URL documentURL) {
        this(documentURL, Collections.<String, String>emptyMap());
    }

    @Override
    public URL getDocumentURL() {
        return documentURL;
    }

    @Override
    public String[] getVarNames() {
        return vars.keySet().toArray(new String[vars.keySet().size()]);
    }

    @Override
    public String getVar(String varName) {
        return vars.get(varName);
    }
}
