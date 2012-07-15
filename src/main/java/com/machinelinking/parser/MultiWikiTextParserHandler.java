package com.machinelinking.parser;

import com.machinelinking.parser.WikiTextParserHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MultiWikiTextParserHandler implements WikiTextParserHandler {

    private List<WikiTextParserHandler> handlers = new ArrayList<WikiTextParserHandler>();

    public MultiWikiTextParserHandler() {}

    public void add(WikiTextParserHandler h) {
        handlers.add(h);
    }

    public void remove(WikiTextParserHandler h) {
        handlers.remove(h);
    }

    @Override
    public void beginDocument(URL document) {
        for(WikiTextParserHandler handler : handlers) {
            handler.beginDocument(document);
        }
    }

    @Override
    public void parseWarning(String msg, int row, int col) {
        for(WikiTextParserHandler handler : handlers) {
            handler.parseWarning(msg, row, col);
        }

    }

    @Override
    public void parseError(Exception e, int row, int col) {
        for(WikiTextParserHandler handler : handlers) {
            handler.parseError(e, row, col);
        }
    }

    @Override
    public void section(String title, int level) {
        for(WikiTextParserHandler handler : handlers) {
            handler.section(title, level);
        }

    }

    @Override
    public void reference(String label, String description) {
        for(WikiTextParserHandler handler : handlers) {
            handler.reference(label, description);
        }

    }

    @Override
    public void link(String url, String description) {
        for(WikiTextParserHandler handler : handlers) {
            handler.link(url, description);
        }

    }

    @Override
    public void beginList() {
        for(WikiTextParserHandler handler : handlers) {
            handler.beginList();
        }

    }

    @Override
    public void listItem() {
        for(WikiTextParserHandler handler : handlers) {
            handler.listItem();
        }

    }

    @Override
    public void endList() {
        for(WikiTextParserHandler handler : handlers) {
            handler.endList();
        }
    }

    @Override
    public void beginTemplate(String name) {
        for(WikiTextParserHandler handler : handlers) {
            handler.beginTemplate(name);
        }
    }

    @Override
    public void templateParameterName(String param) {
        for(WikiTextParserHandler handler : handlers) {
            handler.templateParameterName(param);
        }
    }

    @Override
    public void endTemplate(String name) {
        for(WikiTextParserHandler handler : handlers) {
            handler.endTemplate(name);
        }
    }

    @Override
    public void beginTable() {
        for(WikiTextParserHandler handler : handlers) {
            handler.beginTable();
        }
    }

    @Override
    public void headCell(int row, int col) {
        for(WikiTextParserHandler handler : handlers) {
            handler.headCell(row, col);
        }
    }

    @Override
    public void bodyCell(int row, int col) {
        for(WikiTextParserHandler handler : handlers) {
            handler.bodyCell(row, col);
        }
    }

    @Override
    public void endTable() {
        for(WikiTextParserHandler handler : handlers) {
            handler.endTable();
        }
    }

    @Override
    public void text(String content) {
        for(WikiTextParserHandler handler : handlers) {
            handler.text(content);
        }
    }

    @Override
    public void endDocument() {
        for(WikiTextParserHandler handler : handlers) {
            handler.endDocument();
        }
    }
}
