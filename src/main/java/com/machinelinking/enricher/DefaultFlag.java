package com.machinelinking.enricher;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultFlag implements Flag {

    private final String id;
    private final String description;

    public DefaultFlag(String id, String description) {
        this.id          = id;
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(obj instanceof Flag) {
            final Flag other = (Flag) obj;
            return id.equals(other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }

}
