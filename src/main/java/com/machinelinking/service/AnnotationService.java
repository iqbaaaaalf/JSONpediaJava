package com.machinelinking.service;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface AnnotationService {

    FlagSet flags();

    String annotate(String resource, String flags);

}
