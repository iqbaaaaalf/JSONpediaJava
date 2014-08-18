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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Base service exception definition.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class BaseServiceException extends WebApplicationException {

    BaseServiceException(Response r) {
        super(r);
    }

    @XmlRootElement
    public static class ExceptionWrapper {
        private final Exception e;

        public ExceptionWrapper(Exception e) {
            this.e = e;
        }

        private ExceptionWrapper() {
            this(null);
        }

        @XmlElement
        public boolean getSuccess() {
            return false;
        }

        @XmlElement
        public String getMessage() {
            return e.getMessage();
        }
    }

}
