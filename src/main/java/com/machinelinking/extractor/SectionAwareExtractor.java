package com.machinelinking.extractor;

import java.net.URL;

/**
 * Base {@link Extractor} to manage <i>Wikimedia Section</i>s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class SectionAwareExtractor extends Extractor {

    private short sectionIndex;

    protected SectionAwareExtractor(String name) {
        super(name);
    }

    public short getSectionIndex() {
        return sectionIndex;
    }

    @Override
    public void beginDocument(URL document) {
        sectionIndex = -1; // -1 == page header.
    }

    @Override
    public void section(String title, int level) {
        sectionIndex++;
    }

}
