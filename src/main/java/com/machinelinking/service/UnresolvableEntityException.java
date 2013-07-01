package com.machinelinking.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Used to define an unresolvable entity in <i>Wikipedia</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class UnresolvableEntityException extends BaseServiceException {

    public UnresolvableEntityException(Exception e) {
        super(
                Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(new ExceptionWrapper(e))
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }

}

