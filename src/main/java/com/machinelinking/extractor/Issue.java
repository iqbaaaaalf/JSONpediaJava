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

import com.machinelinking.parser.ParserLocation;
import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;

/**
 * Defines any issue raised while processing extraction.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Issue implements Serializable {

    public enum Type {
        Error,
        Warning
    }

    private Type   type;
    private String description;
    private ParserLocation location;

    public Issue(Type type, String description, ParserLocation location) {
        this.type = type;
        this.description = description;
        this.location = location;
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getRow() {
        return location.getRow();
    }

    public int getCol() {
        return location.getCol();
    }

    public void serialize(Serializer serializer) {
        serializer.openObject();
        serializer.fieldValue("type", type.toString());
        serializer.fieldValue("description", description);
        serializer.fieldValue("row", getRow());
        serializer.fieldValue("col", getCol());
        serializer.closeObject();
    }

}
