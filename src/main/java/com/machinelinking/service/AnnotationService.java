package com.machinelinking.service;

import javax.ws.rs.core.Response;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface AnnotationService {

    FlagSet flags();

    Response annotate(String resource, String flags, String outFormat);

}
