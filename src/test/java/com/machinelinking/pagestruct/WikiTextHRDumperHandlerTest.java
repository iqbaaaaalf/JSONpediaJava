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

package com.machinelinking.pagestruct;

import com.machinelinking.parser.WikiTextParserHandler;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Test case for {@link WikiTextHRDumperHandler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextHRDumperHandlerTest {

    @Test(expected = IllegalStateException.class)
    public void testValidation() throws MalformedURLException {
        final WikiTextHRDumperHandler handler = new WikiTextHRDumperHandler();
        handler.beginDocument( new URL("http://path/to/resource") );
        handler.beginTable();
        handler.beginList();
        handler.endList();
        handler.endTemplate(new WikiTextParserHandler.TemplateName("t1"));
        handler.endDocument();
    }

}
