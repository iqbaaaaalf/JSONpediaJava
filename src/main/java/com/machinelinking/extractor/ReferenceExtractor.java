package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ReferenceExtractor extends Extractor {

    private URL documentURL;

    private List<Reference> references;

    private StringBuilder referenceContent = new StringBuilder();
    private boolean foundParam;

    public ReferenceExtractor() {
        super("references");
    }

    @Override
    public void beginDocument(URL document) {
        documentURL = document;
    }

    @Override
    public void beginReference(String label) {
        referenceContent.delete(0, referenceContent.length());
        foundParam = false;
    }

    @Override
    public void parameter(String param) {
        if(foundParam) referenceContent.append("|");
        foundParam = true;
        if(param != null) {
            referenceContent.append(param).append("=");
        }
    }

    @Override
    public void text(String content) {
        referenceContent.append(content);
    }

    @Override
    public void endReference(String label) {
        if(references == null) references = new ArrayList<>();
        try {
            references.add(new Reference(documentURL, label, referenceContent.toString()));
        } catch (MalformedURLException murle) {
            throw new RuntimeException("Error while building reference.", murle);
        }
    }

    @Override
    public void flushContent(Serializer serializer) {
        if(references == null) {
            serializer.value(null);
            return;
        }
        serializer.openList();
        for(Reference reference : references) {
            reference.serialize(serializer);
        }
        serializer.closeList();
        references.clear();
    }

    @Override
    public void reset() {
        if(references != null) references.clear();
    }

}
