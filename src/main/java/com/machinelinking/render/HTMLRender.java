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

import org.codehaus.jackson.JsonNode;

/**
 * Defines a <i>JSON</i> to <i>HTML</i> renderer.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface HTMLRender extends RootRender {

    /**
     * Registers a {@link NodeRender} for a specific type.
     *
     * @param type
     * @param render
     */
    void addNodeRender(String type, NodeRender render);

    /**
     * Deregisters a {@link NodeRender} for a specific type.
     *
     * @param type
     * @return <code>true</code> if removal succeeded, <code>false</code> otherwise.
     */
    boolean removeNodeRender(String type);

    /**
     * Registers a {@link KeyValueRender} for a given key.
     *
     * @param key
     * @param render
     */
    void addKeyValueRender(String key, KeyValueRender render);

    /**
     * Deregisters a {@link KeyValueRender} for a given key.
     *
     * @param key
     * @return <code>true</code> if removal succeeded, <code>false</code> otherwise.
     */
    boolean removeKeyValueRender(String key);

    /**
     * Registers a {@link PrimitiveNodeRender}.
     *
     * @param render
     */
    void addPrimitiveRender(PrimitiveNodeRender render);

    /**
     * Deregisters a {@link PrimitiveNodeRender}.
     *
     * @param render
     */
    void removePrimitiveRender(PrimitiveNodeRender render);

    /**
     * Renders a JSON node as a document.
     *
     * @param context
     * @param rootNode
     * @return
     * @throws NodeRenderException
     */

    String renderDocument(DocumentContext context, JsonNode rootNode) throws NodeRenderException;

    /**
     * Renders a JSON node as a fragment (without header and footer).
     *
     * @param context
     * @param node
     * @return
     * @throws NodeRenderException
     */
    String renderFragment(DocumentContext context, JsonNode node) throws NodeRenderException;

}
