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

import java.util.HashMap;
import java.util.Map;

/**
 * Specific {@link Extractor} collecting occurrences of <i>Wikipedia Template</i>s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateOccurrencesExtractor extends Extractor {

    private Map<String,Integer> templateOccurrences = new HashMap<>();

    public TemplateOccurrencesExtractor() {
        super(Ontology.TEMPLATES_FIELD);
    }

    @Override
    public void beginTemplate(TemplateName name) {
        final String n = name.plain.trim();
        Integer count = templateOccurrences.get(n);
        count = count == null ? 1 : count + 1;
        templateOccurrences.put(n, count);
    }

    @Override
    public void flushContent(Serializer serializer) {
        serializer.openObject();
        // serializer.fieldValue("__type", "templates");
        serializer.field("occurrences");
        serializer.openObject();
        for(Map.Entry<String,Integer> entry : templateOccurrences.entrySet()) {
            serializer.fieldValue(entry.getKey(), entry.getValue());
        }
        serializer.closeObject();
        serializer.closeObject();
    }

    @Override
    public void reset() {
        templateOccurrences.clear();
    }

}
