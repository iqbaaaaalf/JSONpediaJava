/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

package com.machinelinking.extractor;

import com.machinelinking.pagestruct.Ontology;
import com.machinelinking.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Extractor for text of page sections.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SectionTextExtractor extends TextExtractor {

    private final List<String> sectionsTitle = new ArrayList<>();
    private final List<String> sectionsText = new ArrayList<>();

    private String lastSectionTitle;

    public SectionTextExtractor() {
        super(Ontology.SECTIONS_TEXT_FIELD, AbstractFilteredHandlerCriteria.NOT_ABSTRACT_INSTANCE);
    }

    @Override
    public void flushContent(Serializer serializer) {
        serializer.openList();
        for(int i = 0; i < sectionsText.size(); i++) {
            serializer.openObject();
            serializer.fieldValue(Ontology.TITLE_FIELD ,sectionsTitle.get(i));
            serializer.fieldValue(Ontology.CONTENT_FIELD ,sectionsText.get(i));
            serializer.closeObject();
        }
        serializer.closeList();
        sectionsTitle.clear();
        sectionsText.clear();
    }

    @Override
    public void section(String title, int level) {
        flushSection(title);
        super.section(title, level);
    }

    @Override
    public void endDocument() {
        flushSection(null);
        super.endDocument();
    }

    private void flushSection(String title) {
        sectionsTitle.add(lastSectionTitle);
        sectionsText.add(super.flushText());
        lastSectionTitle = title;
    }

}
