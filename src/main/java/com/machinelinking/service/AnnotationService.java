package com.machinelinking.service;

import com.machinelinking.enricher.FlagSet;

import javax.ws.rs.core.Response;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface AnnotationService {

    FlagSet flags();

    Response annotateResource(String resource, String flags, String outFormat, String filter);

    Response annotateResource(String resource, String wikitext, String flags, String outFormat, String filter);

}
