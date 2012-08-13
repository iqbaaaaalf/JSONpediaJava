package com.machinelinking.service;

import com.machinelinking.enricher.Flag;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@XmlRootElement(name = "flag")
public class FlagWrapper implements Flag {

    private final Flag flag;

    public FlagWrapper(Flag flag) {
        this.flag = flag;
    }

    private FlagWrapper() {
        this(null);
    }

    @XmlElement
    @Override
    public String getId() {
        return flag.getId();
    }

    @XmlElement
    @Override
    public String getDescription() {
        return flag.getDescription();
    }

}
