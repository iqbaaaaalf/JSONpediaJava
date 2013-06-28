package com.machinelinking.enricher;

/**
 * Defines an {@link WikiEnricherFactory} build flag.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Flag {

    /**
     * @return the id of the flag.
     */
    String getId();

    /**
     * @return the human readable description of the flag.
     */
    String getDescription();

}
