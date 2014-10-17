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

package com.machinelinking.extractor;

import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Michele Mostarda (me@michelemostarda.it)
 */
public class ReferenceTest {

    @Test
    public void testIsImage() {
        Assert.assertTrue(Reference.isImage("en:File:test.png"));
        Assert.assertTrue(Reference.isImage("en:File:test.jpg"));
        Assert.assertTrue(Reference.isImage("en:Image:1919_eclipse_positive.jpg"));
        Assert.assertFalse(Reference.isImage("en:File:test.txt"));
        Assert.assertFalse(Reference.isImage(":United_Kingdom"));
    }

    @Test
    public void testGetURLDeclaredLang() throws MalformedURLException {
        Assert.assertEquals("en", Reference.getURLDeclaredLang(new URL("http://en.wikipedia.org/")));
        Assert.assertEquals("it", Reference.getURLDeclaredLang(new URL("http://it.wikipedia.org/")));
    }

    @Test
    public void testIsLangPrefix() throws MalformedURLException {
        Assert.assertEquals(true, Reference.isLangPrefix("en"));
        Assert.assertEquals(true, Reference.isLangPrefix("lmo"));
        Assert.assertEquals(true, Reference.isLangPrefix("fiu-vro"));
        Assert.assertEquals(true, Reference.isLangPrefix("zh-min-nan"));
        Assert.assertEquals(false, Reference.isLangPrefix("Time"));
        Assert.assertEquals(false, Reference.isLangPrefix("Zed"));
        Assert.assertEquals(false, Reference.isLangPrefix("fiuvro"));
        Assert.assertEquals(false, Reference.isLangPrefix("zhminnan"));
    }


    @Test
    public void testImageResourceToURL() {
        Assert.assertEquals(
                "http://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/Albert_Einstein_at_the_age_of_three_%281882%29.jpg/110px-Albert_Einstein_at_the_age_of_three_%281882%29.jpg",
                Reference.imageResourceToURL("File:Albert Einstein at the age of three (1882).jpg")
        );
        Assert.assertEquals(
                "http://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/age%3A1919_eclipse_positive.jpg/110px-age%3A1919_eclipse_positive.jpg",
                Reference.imageResourceToURL("en:Image:1919_eclipse_positive.jpg")
        );
    }

    @Test
    public void testResourceToURL() throws MalformedURLException {
        Assert.assertEquals(
                "http://en.wikipedia.org/wiki/Time_100:_The_Most_Important_People_of_the_Century",
                Reference.labelToURL(new URL("http://en.wikipedia.org/"), "Time 100: The Most Important People of the Century").toString()
        );
        Assert.assertEquals(
                "http://lmo.wikipedia.org/wiki/Albert_Einstein",
                Reference.labelToURL(new URL("http://en.wikipedia.org/"), "lmo:Albert Einstein").toString()
        );
        Assert.assertEquals(
                "http://be-x-old.wikipedia.org/wiki/Лёндан",
                Reference.labelToURL(new URL("http://en.wikipedia.org/"), "be-x-old:Лёндан").toString()
        );
        Assert.assertEquals(
                "http://en.wikipedia.org/wiki/United_Kingdom",
                Reference.labelToURL(new URL("http://en.wikipedia.org/"), ":United_Kingdom").toString()
        );
    }

    @Test
    public void testURLtoLabel() {
        Assert.assertArrayEquals(new String[]{"en", "Page"}, Reference.urlToLabel("http://en.wikipedia.org/wiki/Page"));
        Assert.assertArrayEquals(new String[]{"it", "Page"}, Reference.urlToLabel("http://it.wikipedia.org/wiki/Page"));
    }

}
