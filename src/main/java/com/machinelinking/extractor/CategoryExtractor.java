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

import com.machinelinking.pagestruct.PageStructConsts;
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
        super(PageStructConsts.CATEGORIES_FIELD);
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
        // serializer.fieldValue("__type", "categories");
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
