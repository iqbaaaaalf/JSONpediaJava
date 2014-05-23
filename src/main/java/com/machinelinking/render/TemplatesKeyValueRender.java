package com.machinelinking.render;

import com.machinelinking.wikimedia.WikimediaUtils;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplatesKeyValueRender implements KeyValueRender {

    @Override
    public void render(JsonContext context, RootRender rootRender, String key, JsonNode value, HTMLWriter writer)
    throws IOException {
        final List<TemplateOccurrence> templateOccurrences = new ArrayList<>();
        final Iterator<Map.Entry<String,JsonNode>> templates = value.get("occurrences").getFields();
        Map.Entry<String,JsonNode> current;
        while(templates.hasNext()) {
            current = templates.next();
            templateOccurrences.add(new TemplateOccurrence(current.getKey(), current.getValue().getIntValue()));
        }
        Collections.sort(templateOccurrences, new Comparator<TemplateOccurrence>() {
            @Override
            public int compare(TemplateOccurrence t1, TemplateOccurrence t2) {
                return t2.occurrences - t1.occurrences;
            }
        });

        writer.openTag("div");
        writer.heading(1, "Templates");
        final String lang = WikimediaUtils.urlToParts(context.getDocumentContext().getDocumentURL()).lang;
        for(TemplateOccurrence templateOccurrence : templateOccurrences) {
            writer.templateReference(
                    String.format("%s (%d)", templateOccurrence.templateName, templateOccurrence.occurrences),
                    lang,
                    templateOccurrence.templateName
            );
        }
        writer.closeTag();
    }

    class TemplateOccurrence {
        final String templateName;
        final int occurrences;

        TemplateOccurrence(String templateName, int occurrences) {
            this.templateName = templateName;
            this.occurrences = occurrences;
        }
    }

}
