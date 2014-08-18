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

import com.machinelinking.parser.DefaultWikiTextParserHandler;
import com.machinelinking.serializer.Serializer;

/**
 * Defines an extractor for a <i>Wikipedia</i> specific feature.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class Extractor extends DefaultWikiTextParserHandler {

    private final String name;

    protected Extractor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void flushContent(Serializer serializer);

    public abstract void reset();

}
