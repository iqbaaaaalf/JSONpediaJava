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

import com.machinelinking.pipeline.Flag;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Annotation wrapper for {@link Flag}.
 *
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
