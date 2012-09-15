package com.machinelinking.enricher;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiEnricherFactoryTest {

    @Test
    public void testToFlagsJustDefault() {
        final Flag[] DEFAULTS = new Flag[]{ WikiEnricherFactory.Structure, WikiEnricherFactory.Validate };
        Assert.assertEquals(
                Arrays.asList(DEFAULTS),
                Arrays.asList(WikiEnricherFactory.getInstance().toFlags("", DEFAULTS))
        );
    }

    @Test
    public void testToFlagsOptionalAdded() {
        final Flag[] DEFAULTS = new Flag[]{ WikiEnricherFactory.Structure, WikiEnricherFactory.Validate };
        Assert.assertEquals(
                Arrays.asList( new Flag[]{ WikiEnricherFactory.Structure, WikiEnricherFactory.Validate, WikiEnricherFactory.Online }),
                Arrays.asList(WikiEnricherFactory.getInstance().toFlags("OnlineExtractors", DEFAULTS))
        );
    }

    @Test
    public void testToFlagsDefaultRemoved() {
        final Flag[] DEFAULTS = new Flag[]{ WikiEnricherFactory.Structure, WikiEnricherFactory.Validate };
        Assert.assertEquals(
                Arrays.asList( new Flag[]{ WikiEnricherFactory.Structure }),
                Arrays.asList(WikiEnricherFactory.getInstance().toFlags("-Validate", DEFAULTS))
        );
    }

    @Test
    public void testToFlagsOptionalAddedDefaultRemoved() {
        final Flag[] DEFAULTS = new Flag[]{ WikiEnricherFactory.Structure, WikiEnricherFactory.Validate };
        Assert.assertEquals(
                Arrays.asList( new Flag[]{ WikiEnricherFactory.Validate, WikiEnricherFactory.Online } ),
                Arrays.asList(WikiEnricherFactory.getInstance().toFlags("OnlineExtractors,-Structure", DEFAULTS))
        );
    }

}
