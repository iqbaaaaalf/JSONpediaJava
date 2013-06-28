package com.machinelinking.pagestruct;

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
        handler.endTemplate("t1");
        handler.endDocument();
    }

}
