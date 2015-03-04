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

package com.machinelinking.pipeline;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * Test case for {@link WikiPipelineFactory}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiPipelineFactoryTest {

    @Test
    public void testToFlagsJustDefault() {
        final Flag[] DEFAULTS = new Flag[]{ WikiPipelineFactory.Structure, WikiPipelineFactory.Validate };
        Assert.assertEquals(
                Arrays.asList(WikiPipelineFactory.getInstance().toFlags("", DEFAULTS)),
                Arrays.asList(DEFAULTS)
        );
    }

    @Test
    public void testToFlagsOptionalAdded() {
        final Flag[] DEFAULTS = new Flag[]{ WikiPipelineFactory.Structure, WikiPipelineFactory.Validate };
        Assert.assertEquals(
                Arrays.asList(WikiPipelineFactory.getInstance().toFlags("Linkers", DEFAULTS)),
                Arrays.asList(
                        WikiPipelineFactory.Structure, WikiPipelineFactory.Validate, WikiPipelineFactory.Linkers
                )
        );

        // Lowercase.
        Assert.assertEquals(
                Arrays.asList(WikiPipelineFactory.getInstance().toFlags("linkers", DEFAULTS)),
                Arrays.asList(
                        WikiPipelineFactory.Structure, WikiPipelineFactory.Validate, WikiPipelineFactory.Linkers
                )
        );
    }

    @Test
    public void testToFlagsDefaultRemoved() {
        final Flag[] DEFAULTS = new Flag[]{ WikiPipelineFactory.Structure, WikiPipelineFactory.Validate };
        Assert.assertEquals(
                Arrays.asList(WikiPipelineFactory.getInstance().toFlags("-Validate", DEFAULTS)),
                Arrays.asList(WikiPipelineFactory.Structure)
        );

        // Lowercase.
        Assert.assertEquals(
                Arrays.asList(WikiPipelineFactory.getInstance().toFlags("-validate", DEFAULTS)),
                Arrays.asList(WikiPipelineFactory.Structure)
        );
    }

    @Test
    public void testToFlagsOptionalAddedDefaultRemoved() {
        final Flag[] DEFAULTS = new Flag[]{ WikiPipelineFactory.Structure, WikiPipelineFactory.Validate };
        Assert.assertEquals(
                Arrays.asList(WikiPipelineFactory.getInstance().toFlags("Linkers,-Structure", DEFAULTS)),
                Arrays.asList(WikiPipelineFactory.Validate, WikiPipelineFactory.Linkers)
        );
    }

}
