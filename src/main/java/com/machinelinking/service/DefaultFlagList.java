package com.machinelinking.service;

import com.machinelinking.WikiEnricherFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@XmlRootElement
public class DefaultFlagList implements FlagSet {

    private static DefaultFlagList instance;

    public static DefaultFlagList getInstance() {
        if(instance == null) {
            instance = new DefaultFlagList();
        }
        return instance;
    }

    private final DefaultFlag[] flagSet;

    private DefaultFlagList() {
        WikiEnricherFactory.Flag[] flags = WikiEnricherFactory.Flag.values();
        flagSet = new DefaultFlag[flags.length];
        for(int i = 0; i < flags.length; i++) {
            flagSet[i] = new DefaultFlag(flags[i]);
        }
    }

    @XmlElement
    @Override
    public DefaultFlag[] getFlags() {
        return flagSet;
    }

}
