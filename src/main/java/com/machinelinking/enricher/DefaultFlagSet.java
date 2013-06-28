package com.machinelinking.enricher;

/**
 * Default {@link FlagSet} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultFlagSet implements FlagSet {

    private final Flag[] definedFlags;
    private final Flag[] defaultFlags;

    public DefaultFlagSet(Flag[] definedFlags, Flag[] defaultFlags) {
        this.definedFlags = definedFlags;
        this.defaultFlags = defaultFlags;
    }

    @Override
    public Flag[] getDefinedFlags() {
        return definedFlags;
    }

    @Override
    public Flag[] getDefaultFlags() {
        return defaultFlags;
    }

}
