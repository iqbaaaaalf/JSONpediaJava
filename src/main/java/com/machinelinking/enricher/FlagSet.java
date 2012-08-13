package com.machinelinking.enricher;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */

public interface FlagSet {

    Flag[] getDefinedFlags();

    Flag[] getDefaultFlags();

}
