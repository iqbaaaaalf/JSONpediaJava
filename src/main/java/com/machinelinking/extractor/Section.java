package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Section implements Serializable {

    private String title;
    private int level;

    public Section(String title, int level) {
        this.title = title;
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.openObject();
        serializer.fieldValue("title", title);
        serializer.fieldValue("level", level);
        serializer.closeObject();
    }

}
