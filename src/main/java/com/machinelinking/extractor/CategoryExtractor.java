package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link Extractor} for categories.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class CategoryExtractor extends Extractor {

    public static final String CATEGORY_PREFIX = "Category:";

    private final Set<String> categories = new HashSet<>();

    public CategoryExtractor() {
        super("categories");
    }

    @Override
    public void beginReference(String label) {
        if(label != null && label.startsWith(CATEGORY_PREFIX)) {
            categories.add(label.substring(CATEGORY_PREFIX.length()));
        }
    }

    @Override
    public void flushContent(Serializer serializer) {
        final String[] sortedCategories = categories.toArray( new String[categories.size()] );
        Arrays.sort(sortedCategories);
        serializer.openObject();
        serializer.fieldValue("__type", "category");
        serializer.field("content");
        serializer.openList();
        for(String category : sortedCategories) {
            serializer.value(category);
        }
        serializer.closeList();
        serializer.closeObject();
    }

    @Override
    public void reset() {
        categories.clear();
    }

}
