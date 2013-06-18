package com.machinelinking.parser;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
// TODO HIGH: error detection is not working, see BrokenTemplate1 wikitext
public class WikiTextParser implements ParserReader {

    protected static final String MATH_NODE   = "math";
    protected static final String NOWIKI_NODE = "nowiki";
    protected static final String[] UNPARSED_NODES = new String[] {MATH_NODE, NOWIKI_NODE};

    private static final String[] TEMPLATE_CLOSURE = new String[]{"}}", "|"};

    private static final String[] TEMPLATE_LIST_DELIMITER = new String[]{"\n", "}}", "|"};

    private static final String[] TABLE_DELIMITERS = new String[]{"|}", "|-", "!!" , "!", "||", "|"};

    private static final String[] REFERENCE_DELIMITERS = new String[] {"]]", "]", "\n", "|"};

    private static final String[] LINK_DELIMITERS  = new String[] {"]",  "<"};

    private static final int AHEAD = 500;

    private Reader r;

    private int row, markrow, col, markcol;

    private char lastRead;

    private final TagReader tagReader;

    private WikiTextParserHandler handler;

    public WikiTextParser(WikiTextParserHandler h) {
        if(h == null) throw new NullPointerException("Handler must be not null.");
        handler = h;
        tagReader = new TagReader(handler);
    }

    public void parse(URL url, Reader r) throws IOException, WikiTextParserException {
        BufferedReader br;
        if(!(r instanceof BufferedReader)) {
            br = new BufferedReader(r);
        } else {
            br = (BufferedReader) r;
        }
        this.r = br;
        row = markrow = 1; col = markcol = 1;

        handler.beginDocument(url);

        try {
            while(true) {
                consumeChars();
                mark();
                final String couple = readCouple();
                if('<' == couple.charAt(0)) {
                    reset();
                    tagReader.readNode(this);
                    mark();
                    if (tagReader.isInsideNode(UNPARSED_NODES)) {
                        tagReader.readUntilNextTag(this);
                    }
                } else if ("{{".equals(couple)) {
                    readTemplate();
                } else if ("[[".equals(couple)) {
                    readReference();
                } else if('[' == couple.charAt(0)) {
                    reset(); read(); mark(); // TODO improve it.
                    readLink();
                } else if("{|".equals(couple)) {
                    readTable();
                } else if(col == 2 && "==".equals(couple)) {
                    readSection();
                } else if('=' == couple.charAt(0)) {
                    reset(); read(); mark(); // TODO improve it.
                }
            }
        } catch (EOFException eofe) {
            // Parse ended.
        } catch (Exception e) {
            e.printStackTrace();
            final WikiTextParserException wtpe = new WikiTextParserException(
                    row, col,
                    "Error while parsing document.", e
            );
            handler.parseError(wtpe, new DefaultParserLocation(this.row, this.col) );
            throw wtpe;
        } finally {
            handler.endDocument();
        }
    }

    public void parse(URL url, InputStream is) throws IOException, WikiTextParserException {
        parse(url, new InputStreamReader(is));
    }

    public void parse(DocumentSource source) throws IOException, WikiTextParserException {
        parse(
                source.getDocumentURL(),
                source.getInputStream()
        );
    }

    public char read() throws IOException {
        int intc = r.read();
        if(intc == -1) throw new EOFException();
        char c = (char) intc;
        lastRead = c;
        if(c == '\n') {
            col = 0;
            row++;
        } else {
            col++;
        }
        return c;
    }

    public void mark() throws IOException {
        r.mark(AHEAD);
        markrow = row;
        markcol = col;
    }

    public void reset() throws IOException {
        r.reset();
        row = markrow;
        col = markcol;
    }

    @Override
    public ParserLocation getLocation() {
        return new DefaultParserLocation(this.row, this.col);
    }

    private boolean assertChar(char c) throws IOException {
        return c == read();
    }

    final char[] couple = new char[2];
    private String readCouple() throws IOException {
        couple[0] = read();
        couple[1] = read();
        return new String(couple);
    }

    private void flushText(StringBuilder sb) {
        if (sb.length() > 0) {
            handler.text( sb.toString() );
            sb.delete(0, sb.length());
        }
    }

    private void clear(StringBuilder sb) {
        sb.delete(0, sb.length());
    }

    private void consumeSpaces() throws IOException {
        mark();
        char c;
        while(true) {
            c = read();
            if(c == '\n' || c == '\t' || c == ' ') {
                mark();
            } else {
                reset();
                break;
            }
        }
    }

    private final StringBuilder externalCharsSB = new StringBuilder();
    private void consumeChars() throws IOException {
        mark();
        clear(externalCharsSB);
        char c;
        try {
            while (true) {
                c = read();
                if (c != '{' && c != '[' && c != '<' && (c != '=' || col > 3)) {
                    mark();
                    externalCharsSB.append(c);
                } else {
                    reset();
                    break;
                }
            }
        } finally {
            if (externalCharsSB.length() > 0) {
                handler.text(externalCharsSB.toString());
                clear(externalCharsSB);
            }
        }
    }

    private final StringBuilder templateHeaderSB = new StringBuilder();
    private String readTemplateHeader() throws IOException {
        clear(templateHeaderSB);
        char c;
        while(true) {
            mark();
            c = read();
            if(c != '|' && c != '}') {
                templateHeaderSB.append(c);
            } else {
                if(c == '}')
                    reset();
                break;
            }
        }
        return templateHeaderSB.toString();
    }

    private final StringBuilder tableHeaderSB = new StringBuilder();
    private String readTableHeader() throws IOException {
        clear(tableHeaderSB);
        char c;
        while(true) {
            mark();
            c = read();
            if(c != '|') {
                tableHeaderSB.append(c);
            } else {
                mark();
                c = read();
                if(c == '-')
                    mark();
                else
                    reset();
                break;
            }
        }
        return tableHeaderSB.toString();
    }

    private int readPropertyValue(String[] lookAhead, boolean resetSequence, boolean produceNullParamKey)
    throws IOException {
        final StringBuilder sb = new StringBuilder();
        char c;
        boolean foundAssignment = false;
        while(true) {
            mark();
            final int seq = lookAhead(lookAhead);
            if(seq != -1) {
                if(resetSequence) reset();
                if(produceNullParamKey && !foundAssignment && sb.length() > 0) {
                    handler.parameter(null);
                }
                flushText(sb);
                return seq;
            }

            c = read();

            if(!foundAssignment && c == '=') {
                foundAssignment = true;
                handler.parameter(sb.toString());
                clear(sb);
                mark();
                consumeSpaces();
                continue;
            }

            if(c == '<') {
                if (produceNullParamKey && !foundAssignment && sb.length() > 0) {
                    foundAssignment = true;
                    handler.parameter(null);
                }
                flushText(sb);
                reset();
                tagReader.readNode(this);
                mark();
                if (tagReader.isInsideNode(UNPARSED_NODES)) {
                    tagReader.readUntilNextTag(this);
                }
                continue;
            }

            // Nested element.
            if(c == '{') {
                mark();

                if(assertChar('{')) {
                    mark();
                    if (produceNullParamKey && !foundAssignment && sb.length() > 0) {
                        foundAssignment = true;
                        handler.parameter(null);
                    }
                    flushText(sb);
                    readTemplate();
                    continue;
                } else {
                    reset();
                }

                if (assertChar('|')) {
                    mark();
                    if (produceNullParamKey && !foundAssignment && sb.length() > 0) {
                        foundAssignment = true;
                        handler.parameter(null);
                    }
                    flushText(sb);
                    readTable();
                    continue;
                } else {
                    reset();
                }
            }

            if(c == '[') {
                mark();
                if(assertChar('[')) {
                    mark();
                    if (produceNullParamKey && !foundAssignment && sb.length() > 0) {
                        foundAssignment = true;
                        handler.parameter(null);
                    }
                    flushText(sb);
                    readReference();
                    continue;
                } else {
                    reset();
                    if (produceNullParamKey && !foundAssignment && sb.length() > 0) {
                        foundAssignment = true;
                        handler.parameter(null);
                    }
                    flushText(sb);
                    readLink();
                    continue;
                }
            }

            sb.append(c);
        }
    }

    private void readTemplateProperties() throws IOException {
        while(true) {
            consumeSpaces();
            final int seq = readPropertyValue(TEMPLATE_CLOSURE, false, true);
            mark();
            if(seq == 0) break;
        }
    }

    private void readTemplate() throws IOException {
        final String templateHeader = readTemplateHeader();
        if(templateHeader.length() == 0) return;
        handler.beginTemplate(templateHeader);

        consumeSpaces();
        mark();
        char c = read();
        if(c == '*')  {
            handler.beginList();
            while(true) {
                consumeSpaces();
                c = read();
                if(c == '*') {
                    mark();
                    handler.listItem();
                } else {
                   reset();
                }
                final int seq = readPropertyValue(TEMPLATE_LIST_DELIMITER, true, false);
                if(seq == 1) break;
            }
            handler.endList();
            // TODO: remove this block.
            if(assertChar('}')) {
                if(assertChar('}')) {
                    mark();
                    handler.endTemplate(templateHeader);
                } else {
                    handler.parseWarning("Unexpected '}' while parsing template.", new DefaultParserLocation(this.row, this.col));
                    reset();
                }
            } else {
                mark();
                handler.parseWarning("Expected template closure, found [" + lastRead + "]", new DefaultParserLocation(this.row, this.col));
            }
        } else {
            reset();
            readTemplateProperties();
            mark();
            handler.endTemplate(templateHeader);
        }
    }

    private final StringBuilder referenceLabelSB = new StringBuilder();
    private String readReferenceLabel() throws IOException {
        clear(referenceLabelSB);
        char c;
        while(true) {
            mark();
            c = read();
            if(c != '|' && c != ']') {
                referenceLabelSB.append(c);
            } else {
                if(c == ']') reset();
                break;
            }
        }
        return referenceLabelSB.toString();
    }

    private void readReference() throws IOException {
        final String referenceLabel = readReferenceLabel();
        handler.beginReference(referenceLabel);
        int ahead;
        try {
            while (true) {
                ahead = readPropertyValue(REFERENCE_DELIMITERS, false, true);
                if (ahead == 0)
                    break;
                if (ahead == 1 || ahead == 2) {
                    handler.parseWarning("Invalid closure for reference.", getLocation());
                    break;
                }
            }
        } finally {
            handler.endReference(referenceLabel);
        }
    }

    private final StringBuilder linkURLSB = new StringBuilder();
    private String readLinkURL() throws IOException {
        clear(linkURLSB);
        char c;
        while(true) {
            mark();
            c = read();
            if(c == LINK_DELIMITERS[0].charAt(0) || c == LINK_DELIMITERS[1].charAt(0)) {
                reset();
                break;
            }
            if(c != ' ' && c != '\t') {
                linkURLSB.append(c);
            } else {
                mark();
                break;
            }
        }
        return linkURLSB.toString();
    }

    private void readLink() throws IOException {
        final String urlString = readLinkURL();
        final URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException murle) {
            handler.text( String.format("[%s", urlString) );
            return;
        }

        handler.beginLink(url);
        int ahead;
        while(true) {
            ahead = readPropertyValue(LINK_DELIMITERS, true, true);
            if(ahead == 0) { // TODO: improve this solution.
                read();
                mark();
            }else if(ahead == 1) {
                handler.parseWarning("Invalid link closure.", getLocation());
            }
            break;
        }
        handler.endLink(url);
    }

    private void readTable() throws IOException {
        handler.beginTable();
        mark();
        int ahead;
        int tableRow, tableCol;
        tableRow = tableCol = 1;
        final String header = readTableHeader();
        handler.text(header);
        handler.headCell(tableRow, tableCol);
        tableCol++;
        consumeSpaces();
        while(true) {
            ahead = readPropertyValue(TABLE_DELIMITERS, false, false);
            if(ahead == 0) {
                mark();
                handler.endTable();
                break;
            } else if(ahead == 3) {
                // Do nothing.
            } else if(ahead == 2) {
                mark();
                handler.headCell(tableRow, tableCol);
                tableCol++;
            } else if (ahead == 1) {
                mark();
                tableCol = 1;
                tableRow++;
            } else if(ahead == 4 || ahead == 5) {
                mark();
                tableCol++;
                handler.bodyCell(tableRow, tableCol);
            }
            consumeSpaces();
        }
    }

    private final StringBuilder sectionSB = new StringBuilder();
    private void readSection() throws IOException {
        mark();
        char c;

        // Consumes section begin.
        int sectionLevel = 0;
        while(true) {
            c = read();
            if(c != '=') {
                reset();
                break;
            } else {
                mark();
                sectionLevel++;
            }
        }

        // Read section content.
        clear(sectionSB);
        while(true) {
            c = read();
            if(c == '=' && assertChar('=')) {
                mark();
                break;
            } else{
                mark();
                sectionSB.append(c);
            }
        }

        // Consumes section end.
        while(true) {
            c = read();
            if(c == '=') {
                mark();
                break;
            } else {
                reset();
                break;
            }
        }

        handler.section(sectionSB.toString(), sectionLevel);
    }

    private int lookAhead(String[] sequences) throws IOException {
        char c;
        boolean sequenceMatchComplete;
        for(int i = 0; i < sequences.length; i++) {
            sequenceMatchComplete = true;
            for(int k = 0; k < sequences[i].length(); k++) {
                c = read();
                if(c != sequences[i].charAt(k)) {
                    sequenceMatchComplete = false;
                    reset();
                    break;
                }
            }
            if(sequenceMatchComplete) {
                return i;
            }
        }
        return -1;
    }

}
