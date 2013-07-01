package com.machinelinking.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class UnreachableWikipediaService extends BaseServiceException {

    UnreachableWikipediaService(Exception e) {
        super(
                Response
                        .status(Response.Status.FORBIDDEN)
                        .entity(new ExceptionWrapper(e))
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );

    }

}
