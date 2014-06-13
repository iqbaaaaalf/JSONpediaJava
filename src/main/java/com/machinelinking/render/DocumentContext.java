package com.machinelinking.render;

import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface DocumentContext {

    String getScope();

    URL getDocumentURL();

    String[] getVarNames();

    String getVar(String varName);

}
