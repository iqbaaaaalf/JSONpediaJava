package com.machinelinking.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Used to define an invalid request: an error occurred while parsing parameter.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InvalidRequestException extends BaseServiceException {

    public InvalidRequestException(Exception e) {
        super(
                Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(new ExceptionWrapper(e))
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }

}

