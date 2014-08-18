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

package com.machinelinking.parser;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Test case for {@link com.machinelinking.parser.AttributeScanner}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class AttributeScannerTest {

    @Test
    public void testValue() {
        final StringBuilder value = new StringBuilder();

        value.delete(0, value.length());
        AttributeScanner.scanValue("v1 post", 0, value);
        Assert.assertEquals("v1", value.toString());

        value.delete(0, value.length());
        AttributeScanner.scanValue("\"v1 v2\" post", 0, value);
        Assert.assertEquals("v1 v2", value.toString());
    }

    @Test
    public void testValueWithAssign() {
        final Attribute[] attributes = AttributeScanner.scan("name=\"Arrian 1976 loc=I, 23\"");
        Assert.assertEquals("[name : 'Arrian 1976 loc=I, 23']", Arrays.asList(attributes).toString());
    }

    @Test
    public void testKeyValue() {
        Attribute[] attributes;

        attributes = AttributeScanner.scan("k1 = v1");
        Assert.assertEquals("[k1 : 'v1']", Arrays.asList(attributes).toString());

        attributes = AttributeScanner.scan("k1 = \"v1a v1b\"");
        Assert.assertEquals("[k1 : 'v1a v1b']", Arrays.asList(attributes).toString());

        attributes = AttributeScanner.scan("k1=v1 k2 = v2 k3 = \"v3\" k4 = \"v4a v4b\"");
        Assert.assertEquals("[k1 : 'v1', k2 : 'v2', k3 : 'v3', k4 : 'v4a v4b']", Arrays.asList(attributes).toString());
    }

}
