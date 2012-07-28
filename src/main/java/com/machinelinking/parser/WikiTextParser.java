package com.machinelinking.parser;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
// TODO HIGH: error detection is not working, see BrokenTemplate1 wikitext
public class WikiTextParser {

    private static final String[] TEMPLATE_CLOSURE = new String[]{"}}"};

    private static final String[] TEMPLATE_LIST_DELIMITER = new String[]{"\n", "}}"};

    private static final String[] TABLE_DELIMITERS = new String[]{"|}", "|-", "!!" , "!", "||", "|"};

    private static final String[] LINK_DELIMITERS  = new String[]{"|", "]", "}}", "|}"};

    private static final int AHEAD = 500;

    private static final Pattern URL_PATTERN = Pattern.compile("^https?://.*");

    private Reader r;

    // TODO: fix row / col management.
    private int row = 1, col = 0;

    private char lastRead;

    private WikiTextParserHandler handler;

    public WikiTextParser(WikiTextParserHandler h) {
        if(h == null) throw new NullPointerException("Handler must be not null.");
        handler = h;
    }

    public void parse(URL url, Reader r) throws IOException, WikiTextParserException {
        if(!(r instanceof BufferedReader)) {
            r = new BufferedReader(r);
        }
        this.r = r;
        //TODO: the tag reader integration must be completed.
        //this.r = new TagStripReader(r, handler);

        handler.beginDocument(url);

        try {
            while(true) {
                consumeChars();
                mark();
                final String couple = readCouple();
                // System.out.println("COUPLE " + couple);
                if ("{{".equals(couple)) {
                    readTemplate();
                } else if ("[[".equals(couple)) {
                    readReference();
                } else if('[' == couple.charAt(0)) {
                    reset(); read(); mark(); // TODO improve it.
                    readLink();
                } else if("{|".equals(couple)) {
                    readTable();
                } else if(col == 3 && "==".equals(couple)) {
                    //System.out.println("ROW " + row);
                    readSection();
                } else if('=' == couple.charAt(0)) {
                    reset(); read(); mark(); // TODO improve it.
                } else {
                    if(couple.charAt(0) == '{') { // A single { like "<math> {G} </math>" to be consumed.
                        read();
                    } else {
                        reset();
                    }
                }
            }
        } catch (EOFException eofe) {
            // Parse ended.
        } catch (Error e) {
            final WikiTextParserException wtpe = new WikiTextParserException(
                    row, col,
                    "Error while parsing document.", e
            );
            handler.parseError(wtpe, row, col);
            throw wtpe;
        } finally {
            handler.endDocument();
        }
    }

    public void parse(URL url, InputStream is) throws IOException, WikiTextParserException {
        parse(url, new BufferedReader(new InputStreamReader(is)));
    }

    public void parse(DocumentSource source) throws IOException, WikiTextParserException {
        parse(
                source.getDocumentURL(),
                source.getInputStream()
        );
    }

    private char read() throws IOException {
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

    private boolean assertChar(char c) throws IOException {
        return c == read();
    }

    final char[] couple = new char[2];
    private String readCouple() throws IOException {
        couple[0] = read();
        couple[1] = read();
        return new String(couple);
    }

    private void mark() throws IOException {
        r.mark(AHEAD);
    }

    private void reset() throws IOException {
        r.reset();
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
        while(true) {
            c = read();
            if(c != '{' && c != '[' && (c != '=' || col > 3) ) {
                mark();
                externalCharsSB.append(c);
            } else {
                reset();
                if(externalCharsSB.length() > 0) {
                    handler.text(externalCharsSB.toString());
                    clear(externalCharsSB);
                }
                break;
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

    private final StringBuilder propertyKeySB = new StringBuilder();
    private String readPropertyKey() throws IOException {
        clear(propertyKeySB);
        char c;
        while(true) {
            mark();
            c = read();
            if(c != '=' && c != '|' && c != '}' ) {
                propertyKeySB.append(c);
                mark();
            } else {
                if(c != '=')
                    reset();
                break;
            }
        }
        return propertyKeySB.toString();
    }

    private int readPropertyValue(String[] lookAhead, boolean resetSequence) throws IOException {
        final StringBuilder sb = new StringBuilder();
        char c;
        while(true) {
            mark();
            final int seq = lookAhead(lookAhead);
            if(seq != -1) {
                if(resetSequence) reset();
                if(sb.length() > 0) handler.text(sb.toString());
                return seq;
            }

            c = read();

            // Nested element.
            if(c == '{') {
                mark();

                if(assertChar('{')) {
                    mark();
                    flushText(sb);
                    readTemplate();
                    continue;
                } else {
                    reset();
                }

                if (assertChar('|')) {
                    mark();
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
                    flushText(sb);
                    readReference();
                    continue;
                } else {
                    reset();
                    flushText(sb);
                    readLink();
                    continue;
                }
            }

            // Simple element.
            if(c != '|') {
                sb.append(c);
            } else {
                break;
            }
        }
        flushText(sb);
        return -1;
    }

    private void readTemplateProperties() throws IOException {
        while(true) {
            consumeSpaces();
            final String propertyKey = readPropertyKey();
            if(propertyKey.length() != 0) {
                handler.templateParameterName(propertyKey);
            }
            consumeSpaces();
            final int seq = readPropertyValue(TEMPLATE_CLOSURE, true );
            if(seq != -1) break;
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
                final int seq = readPropertyValue(TEMPLATE_LIST_DELIMITER, true );
                if(seq == 1) break;
            }
            handler.endList();
        } else {
            reset();
            readTemplateProperties();
        }

        if(assertChar('}')) {
            if(assertChar('}')) {
                mark();
                handler.endTemplate(templateHeader);
            } else {
                handler.parseWarning("Unexpected '}' while parsing template.", row, col);
                reset();
            }
        } else {
            mark();
            handler.parseWarning("Expected template closure, found [" + lastRead + "]", row, col);
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

    private final StringBuilder referenceDescriptionSB = new StringBuilder();
    private String readReferenceDescription() throws IOException {
        clear(referenceDescriptionSB);
        char c;
        while(true) {
            mark();
            c = read();
            if(c != ']') {
                referenceDescriptionSB.append(c);
            } else {
                reset();
                break;
            }
        }
        return referenceDescriptionSB.toString();
    }

    private void readReference() throws IOException {
        final String referenceLabel       = readReferenceLabel();
        final String referenceDescription = readReferenceDescription();
        if( assertChar(']') && assertChar(']') ) mark();
        handler.reference(referenceLabel, referenceDescription);
    }

    private final StringBuilder linkURLSB = new StringBuilder();
    private String readLinkURL() throws IOException {
        clear(linkURLSB);
        char c;
        while(true) {
            mark();
            c = read();
            if(c == ']') {
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

    private final StringBuilder linkDescriptionSB = new StringBuilder();
    private String readLinkDescription() throws IOException {
        clear(linkDescriptionSB);
        char c;
        int ahead;
        while(true) {
            mark();
            ahead = lookAhead(LINK_DELIMITERS);
            if(ahead != -1) {
                reset();
                break;
            }
            c = read();
            linkDescriptionSB.append(c);
        }
        return linkDescriptionSB.toString();
    }

    private void readLink() throws IOException {
        final String url         = readLinkURL();
        final String description = readLinkDescription();
        if( assertChar(']') ) mark(); else reset();
        if( URL_PATTERN.matcher(url).matches() ) { //TODO: this can be improved?
            handler.link(url, description);
        } else {
            handler.text(url);
            if(description.length() > 0){
                handler.text(" ");
                handler.text(description);
            }
        }
    }

    private void readTable() throws IOException {
        handler.beginTable();
        mark();
        int ahead;
        int tableRow, tableCol;
        tableRow = tableCol = 0;
        while(true) {
            ahead = readPropertyValue(TABLE_DELIMITERS, false);
            if(ahead == 0) {
                mark();
                handler.endTable();
                break;
            } else if(ahead == 2 || ahead == 3) {
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
