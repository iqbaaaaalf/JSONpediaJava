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

