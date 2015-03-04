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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Annotation wrapper for {@link com.machinelinking.pipeline.Flag}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@XmlRootElement(name = "quota-overflow")
public class QuotaOverflowMessage {

    private final long quota;

    public QuotaOverflowMessage(long quota) {
        this.quota = quota;
    }

    private QuotaOverflowMessage() {
        this(0);
    }

    @XmlElement
    public String getMessage() {
        return String.format("Quota reached, please retry in %d ms.", quota);
    }

    @XmlElement
    public long getWaitTime() {
        return quota;
    }

}
