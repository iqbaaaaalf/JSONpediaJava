package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SectionExtractor extends Extractor {

    private List<Section> sections;

    public SectionExtractor() {
        super("sections");
    }

    @Override
    public void section(String title, int level) {
        if(sections == null) sections = new ArrayList<Section>();
        sections.add( new Section(title, level) );
    }

    @Override
    public void flushContent(Serializer serializer) {
        if( sections == null) {
            serializer.value(null);
            return;
        }
        serializer.openList();
        for(Section section : sections) {
            section.serialize(serializer);
        }
        serializer.closeList();
        sections.clear();
    }

    @Override
    public void reset() {
        if(sections != null) sections.clear();
    }
}
