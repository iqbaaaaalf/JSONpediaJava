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

import java.net.URL;

/**
 * Base {@link Extractor} to manage <i>Wikimedia Section</i>s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class SectionAwareExtractor extends Extractor {

    private short sectionIndex;
    private String sectionTitle;

    protected SectionAwareExtractor(String name) {
        super(name);
    }

    public short getSectionIndex() {
        return sectionIndex;
    }

    public String getSectionTitle() { return sectionTitle; }

    public boolean insideHeader() {
        return getSectionIndex() == -1;
    }

    @Override
    public void beginDocument(URL document) {
        sectionIndex = -1; // -1 == page header.
    }

    @Override
    public void section(String title, int level) {
        sectionTitle = title;
        sectionIndex++;
    }

}
