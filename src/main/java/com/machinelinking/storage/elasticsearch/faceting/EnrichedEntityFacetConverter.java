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

package com.machinelinking.storage.elasticsearch.faceting;

import com.machinelinking.pagestruct.Ontology;
import com.machinelinking.storage.elasticsearch.ElasticDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines a {@link com.machinelinking.storage.elasticsearch.faceting.FacetConverter} which convert Page documents
 * to Section documents ready for faceting.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class EnrichedEntityFacetConverter implements FacetConverter {

    @Override
    public Iterable<ElasticDocument> convert(ElasticDocument in) {
        final List<ElasticDocument> result = new ArrayList<>();
        final Map<String, Object> content = in.getContent();
        final List sections = (List) content.get(Ontology.SECTIONS_FIELD);
        Map<String, Object> sectionMap;
        Map<String, Object> newDocument;
        int sectionIndex = 0;
        for (Object section : sections) {
            sectionMap = (Map<String, Object>) section;
            final String name = String.format("%s # %s", in.getName(), sectionMap.get("title"));
            newDocument = new HashMap<>();
            newDocument.put("page", in.getName());
            newDocument.put("section", toPath(sectionIndex, sections));
            newDocument.put("section_stem", sectionMap.get("title"));
            newDocument.put("content", content.get("abstract"));
            newDocument.put("content_stem", content.get("abstract"));
            newDocument.put("links", toDescriptionList(sectionIndex, (List) content.get("links")));
            newDocument.put("references", toDescriptionList(sectionIndex, (List) content.get("references")));
            newDocument.put("categories", ((Map) content.get("categories")).get("content"));
            result.add(new ElasticDocument(sectionIndex, 1, name, newDocument));
            sectionIndex++;
        }
        return result;
    }

    private Map<String, Object> getAsMap(Map<String, Object> container, String field) {
        return (Map<String, Object>) container.get(field);
    }

    private String toPath(int sectionIndex, List sections) {
        final List ancestors = (List) ((Map<String, Object>) sections.get(sectionIndex)).get("ancestors");
        if (ancestors == null) return null;

        final StringBuilder sb = new StringBuilder();
        for (Object ancestor : ancestors) {
            sb.append(((Map) sections.get((int) ancestor)).get("title"));
            sb.append(" > ");
        }
        return sb.toString();
    }

    private String[] toDescriptionList(int sectionIndex, List links) {
        if (links == null) return null;
        List<String> result = new ArrayList<String>();
        Map<String, Object> linkMap;
        String description;
        for (Object link : links) {
            linkMap = (Map<String, Object>) link;
            if (sectionIndex == linkMap.get("section_idx")) {
                description = linkMap.get("description").toString();
                if (description != null && description.length() > 0)
                    result.add(description);
            }
        }
        return result.toArray(new String[0]);
    }

}
