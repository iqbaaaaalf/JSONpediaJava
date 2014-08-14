package com.machinelinking.service;

import com.machinelinking.enricher.Flag;
import com.machinelinking.enricher.FlagSet;
import com.machinelinking.enricher.WikiEnricherFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Annotation wrapper for {@link FlagWrapper}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@XmlRootElement
public class FlagSetWrapper implements FlagSet {

    private static FlagSetWrapper instance;

    public static FlagSetWrapper getInstance() {
        if(instance == null)
            instance = new FlagSetWrapper();
        return instance;
    }

    private final FlagWrapper[] definedFlagSet;
    private final FlagWrapper[] defaultFlagSet;

    private FlagSetWrapper() {
        Flag[] flags = WikiEnricherFactory.getInstance().getDefinedFlags();
        definedFlagSet = new FlagWrapper[flags.length];
        for(int i = 0; i < flags.length; i++) {
            definedFlagSet[i] = new FlagWrapper(flags[i]);
        }
        defaultFlagSet = new FlagWrapper[WikiEnricherFactory.DEFAULT_FLAGS.length];
        for(int i = 0; i < WikiEnricherFactory.DEFAULT_FLAGS.length; i++) {
            defaultFlagSet[i] = new FlagWrapper(WikiEnricherFactory.DEFAULT_FLAGS[i]);
        }
    }

    @XmlElement
    @Override
    public FlagWrapper[] getDefinedFlags() {
        return definedFlagSet;
    }

    @XmlElement
    @Override
    public FlagWrapper[] getDefaultFlags() {
        return defaultFlagSet;
    }

}
