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
