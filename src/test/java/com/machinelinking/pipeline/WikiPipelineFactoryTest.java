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

import org.junit.Assert;
import org.junit.Test;

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
                Arrays.asList(DEFAULTS),
                Arrays.asList(WikiPipelineFactory.getInstance().toFlags("", DEFAULTS))
        );
    }

    @Test
    public void testToFlagsOptionalAdded() {
        final Flag[] DEFAULTS = new Flag[]{ WikiPipelineFactory.Structure, WikiPipelineFactory.Validate };
        Assert.assertEquals(
                Arrays.asList( new Flag[]{
                        WikiPipelineFactory.Structure, WikiPipelineFactory.Validate, WikiPipelineFactory.Linkers
                }),
                Arrays.asList(WikiPipelineFactory.getInstance().toFlags("Linkers", DEFAULTS))
        );
    }

    @Test
    public void testToFlagsDefaultRemoved() {
        final Flag[] DEFAULTS = new Flag[]{ WikiPipelineFactory.Structure, WikiPipelineFactory.Validate };
        Assert.assertEquals(
                Arrays.asList( new Flag[]{ WikiPipelineFactory.Structure }),
                Arrays.asList(WikiPipelineFactory.getInstance().toFlags("-Validate", DEFAULTS))
        );
    }

    @Test
    public void testToFlagsOptionalAddedDefaultRemoved() {
        final Flag[] DEFAULTS = new Flag[]{ WikiPipelineFactory.Structure, WikiPipelineFactory.Validate };
        Assert.assertEquals(
                Arrays.asList( new Flag[]{ WikiPipelineFactory.Validate, WikiPipelineFactory.Linkers } ),
                Arrays.asList(WikiPipelineFactory.getInstance().toFlags("Linkers,-Structure", DEFAULTS))
        );
    }

}
