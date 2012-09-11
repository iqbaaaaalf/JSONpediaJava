package com.machinelinking.service;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InvalidEntityException extends WebApplicationException {

    public InvalidEntityException(Exception e) {
        super(
                Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(new ExceptionWrapper(e))
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }

    @XmlRootElement
    public static class ExceptionWrapper {
        private final Exception e;
        public ExceptionWrapper(Exception e) {
            this.e = e;
        }
        private ExceptionWrapper() { this(null); }

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

