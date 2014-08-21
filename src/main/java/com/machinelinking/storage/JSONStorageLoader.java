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

package com.machinelinking.storage;

import com.machinelinking.pipeline.WikiPipelineFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Defines a processor to massive load a <i>Wikitext dump</i> stream into a {@link JSONStorage}
 * the {@link com.machinelinking.pipeline.WikiPipeline} built by the specified {@link com.machinelinking.pipeline.WikiPipelineFactory}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONStorageLoader {

    WikiPipelineFactory getEnricherFactory();

    JSONStorage getStorage();

    StorageLoaderReport load(URL pagePrefix, InputStream is) throws IOException, SAXException;

}
