package com.machinelinking.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface WikiTextParserHandler extends NodeHandler {

    void beginDocument(URL document);

    void section(String title, int level);

    void parseWarning(String msg, int row, int col);

    void parseError(Exception e, int row, int col);

    void reference(String label, String description);

    void link(String url, String description);

    @Push(node="list")
    void beginList();

    void listItem();

    @Pop(node="list")
    void endList();

    @Push(node="template", id=0)
    void beginTemplate(String name);

    void templateParameterName(String param);

    @Pop(node="template", id=0)
    void endTemplate(String name);

    @Push(node="table")
    void beginTable();

    void headCell(int row, int col);

    void bodyCell(int row, int col);

    @Pop(node="table")
    void endTable();

    void text(String content);

    @ValidateStack
    void endDocument();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Push {
        String node();
        int id() default -1;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Pop  {
        String node();
        int id() default -1;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface ValidateStack {}

}


