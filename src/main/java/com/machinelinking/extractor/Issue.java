package com.machinelinking.extractor;

import com.machinelinking.parser.ParserLocation;
import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Issue implements Serializable {

    public enum Type {
        Error,
        Warning;
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
