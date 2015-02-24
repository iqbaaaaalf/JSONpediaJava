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

/**
 * Manages the throttling time for a generic {@link com.machinelinking.service.Service} access.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Throttler {

    /**
     * Verifies the waiting time (in milliseconds) for a given IP address.
     *
     * @param ip IP address to be verified.
     * @return <code>0</code> if no longer wait, <code>w > 0</code> milliseconds waiting time if still waiting.
     */
    long checkAllowed(String ip);

}
