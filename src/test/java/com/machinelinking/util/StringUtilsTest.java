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

package com.machinelinking.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Test case for {@link com.machinelinking.util.StringUtils}
 *
 * @author Michele Mostarda (me@michelemostarda.it)
 */
public class StringUtilsTest {

    @Test
    public void testStripTags() {
        Assert.assertEquals("a b c", StringUtils.stripTags("a b c"));
        Assert.assertEquals("a  c", StringUtils.stripTags("a <b> c"));
        Assert.assertEquals("a ", StringUtils.stripTags("a <b"));
        Assert.assertEquals("a  c  e", StringUtils.stripTags("a <b x x x> c <d y y y> e"));
        Assert.assertEquals("a  c ", StringUtils.stripTags("a <b x x<> x> c <d y y< y> e"));
    }

}