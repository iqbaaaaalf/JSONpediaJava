package com.machinelinking.extractor;

import java.net.URL;

/**
 * Base {@link Extractor} to manage <i>Wikimedia Section</i>s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class SectionAwareExtractor extends Extractor {

    private short sectionIndex;
    private String sectionTitle;

    protected SectionAwareExtractor(String name) {
        super(name);
    }

    public short getSectionIndex() {
        return sectionIndex;
    }

    public String getSectionTitle() { return sectionTitle; }

    public boolean insideHeader() {
        return getSectionIndex() == -1;
    }

    @Override
    public void beginDocument(URL document) {
        sectionIndex = -1; // -1 == page header.
    }

    @Override
    public void section(String title, int level) {
        sectionTitle = title;
        sectionIndex++;
    }

}
