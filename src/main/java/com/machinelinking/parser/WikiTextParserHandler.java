package com.machinelinking.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;

/**
 * Interface of events produced by {@link WikiTextParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface WikiTextParserHandler extends TagHandler {

    enum ListType {
        Unordered,
        Numbered
    }

    void beginDocument(URL document);

    void section(String title, int level);

    void parseWarning(String msg, ParserLocation location);

    void parseError(Exception e, ParserLocation location);

    @Push(node="reference", id=0)
    void beginReference(String label);

    @Pop(node="reference", id=0)
    void endReference(String label);

    @Push(node="link", id=0)
    void beginLink(URL url);

    @Pop(node="link", id=0)
    void endLink(URL url);

    @Push(node="list")
    void beginList();

    void listItem(ListType t, int level);

    @Pop(node="list")
    void endList();

    @Push(node="template", id=0)
    void beginTemplate(String name);

    @Pop(node="template", id=0)
    void endTemplate(String name);

    @Push(node="table")
    void beginTable();

    void headCell(int row, int col);

    void bodyCell(int row, int col);

    @Pop(node="table")
    void endTable();

    void parameter(String param);

    void text(String content);

    void italicBold(int level);

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


