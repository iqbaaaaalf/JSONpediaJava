package com.machinelinking.splitter;

import com.machinelinking.parser.WikiPediaUtils;

/**
 * {@link Splitter} implementation for <i>infobox</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InfoboxSplitter extends Splitter {

    public InfoboxSplitter() {
        super("infobox-splitter");
    }

    @Override
    public void beginTemplate(TemplateName name) {
        if(WikiPediaUtils.getInfoBoxName(name.plain) != null) {
            super.split();
        }
    }

}
