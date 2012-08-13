package com.machinelinking.service;

import com.machinelinking.enricher.WikiEnricherFactory;
import com.machinelinking.enricher.Flag;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@XmlRootElement
public class DefaultFlag implements Flag {

    private final WikiEnricherFactory.Flag flag;

    public DefaultFlag(WikiEnricherFactory.Flag flag) {
        this.flag = flag;
    }

    private DefaultFlag() {
        this(null);
    }

    @XmlElement
    @Override
    public String getId() {
        return flag.name();
    }

    @XmlElement
    @Override
    public String getDescription() {
        return flag.description();
    }

}
