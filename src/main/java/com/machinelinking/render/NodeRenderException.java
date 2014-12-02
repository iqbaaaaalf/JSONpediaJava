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

package com.machinelinking.render;
/**
 * Defines any error raised by a {@link com.machinelinking.render.NodeRender}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NodeRenderException extends Exception {

    public NodeRenderException(Throwable cause) {
        super(cause);
    }

    public NodeRenderException(String message, Throwable cause) {
        super(message, cause);
    }

}