package com.machinelinking.enricher;

/**
 * Defines a {@link WikiEnricherFactory} build {@link Flag} set.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */

public interface FlagSet {

    /**
     * @return list of flags within this set.
     */
    Flag[] getDefinedFlags();

    /**
     * @return list of flags which are default in this set.
     */
    Flag[] getDefaultFlags();

}
