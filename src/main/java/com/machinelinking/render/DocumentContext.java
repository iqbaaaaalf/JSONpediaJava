package com.machinelinking.render;

import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface DocumentContext {

    URL getDocumentURL();

    String[] getVarNames();

    String getVar(String varName);

}
