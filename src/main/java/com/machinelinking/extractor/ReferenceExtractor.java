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

    public ReferenceExtractor() {
        super("references");
    }

    @Override
    public void beginDocument(URL document) {
        documentURL = document;
    }

    @Override
    public void reference(String label, String description) {
        if(references == null) references = new ArrayList<Reference>();
        try {
            references.add( new Reference(documentURL, label, description) );
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
