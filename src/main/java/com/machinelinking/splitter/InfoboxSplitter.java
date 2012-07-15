package com.machinelinking.splitter;

import com.machinelinking.parser.WikiPediaUtils;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InfoboxSplitter extends Splitter {

    public InfoboxSplitter() {
        super("infobox-splitter");
    }

    @Override
    public void beginTemplate(String name) {
        if(WikiPediaUtils.getInfoBoxName(name) != null) {
            super.split();
        }
    }

}
