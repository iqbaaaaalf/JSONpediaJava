package com.machinelinking.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Used to define a unresolvable entities: the entity cannot be found or the service is unreachable.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InternalErrorException extends BaseServiceException {

    public InternalErrorException(Exception e) {
        super(
                Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ExceptionWrapper(e))
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }

}

