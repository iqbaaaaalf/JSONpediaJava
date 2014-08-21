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
import java.util.Stack;

/**
 * Specific {@link Extractor} for <i>Wikipedia section</i>s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SectionExtractor extends Extractor {

    private List<Section> sections;
    private Stack<Integer> s;

    public SectionExtractor() {
        super(Ontology.SECTIONS_FIELD);
    }

    @Override
    public void section(String title, int level) {
        if(sections == null) sections = new ArrayList<Section>();
        if(s == null) s = new Stack<Integer>();

        // use a stack to keep the parents
        // if we have the following structure
        // S1
        // -- S2
        // -- -- S3
        // S4
        // the stack will need to pop twice to jump from S3 to S4
        while(s.size() > level){
            s.pop();
        }

        sections.add(new Section(title, s, level));
        s.push(sections.size() -1); // for the next section, I'm the parent
    }

    @Override
    public void flushContent(Serializer serializer) {
        if(sections == null) {
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
        if(s != null) s.clear();
    }
}
