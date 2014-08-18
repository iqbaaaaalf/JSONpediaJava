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
