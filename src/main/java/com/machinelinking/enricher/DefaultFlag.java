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

package com.machinelinking.enricher;

/**
 * Default {@link Flag} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultFlag implements Flag, Comparable {

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

    @Override
    public int compareTo(Object o) {
        final Flag other = (Flag) o;
        return id.compareTo(other.getId());
    }
}
