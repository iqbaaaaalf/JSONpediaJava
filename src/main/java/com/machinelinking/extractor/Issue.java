package com.machinelinking.extractor;

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
    private int    row, col;

    public Issue(Type type, String description, int row, int col) {
        this.type = type;
        this.description = description;
        this.row = row;
        this.col = col;
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void serialize(Serializer serializer) {
        serializer.openObject();
        serializer.fieldValue("type", type.toString());
        serializer.fieldValue("description", description);
        serializer.fieldValue("row", row);
        serializer.fieldValue("col", col);
        serializer.closeObject();
    }

}
