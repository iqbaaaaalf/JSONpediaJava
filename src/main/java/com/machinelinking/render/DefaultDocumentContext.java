package com.machinelinking.render;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultDocumentContext implements DocumentContext {

    private final String scope;
    private final URL documentURL;
    private final Map<String,String> vars;

    public DefaultDocumentContext(String scope, URL documentURL, Map<String, String> vars) {
        this.scope = scope;
        this.documentURL = documentURL;
        this.vars = vars;
    }

    public DefaultDocumentContext(String scope, URL documentURL) {
        this(scope, documentURL, Collections.<String, String>emptyMap());
    }

    @Override
    public String getScope() {
        return scope;
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
