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

package com.machinelinking.service;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for {@link com.machinelinking.service.InMemoryThrottler}
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InMemoryThrottlerTest {

    @Test
    public void testThrottle() throws InterruptedException {
        final long limit = 500;
        final InMemoryThrottler throttler = new InMemoryThrottler(limit, 100000);
        int ELEMS = 50000;
        for(int i = 0; i < ELEMS; i++) {
            Assert.assertEquals(throttler.checkAllowed(Integer.toString(i)), 0);
        }
        Assert.assertTrue(throttler.isRunning());

        for (int i = 0; i < ELEMS; i++) {
            final long time = throttler.checkAllowed(Integer.toString(i));
            Assert.assertTrue(time > 0 && time <= limit);
        }
        Assert.assertTrue(throttler.isRunning());

        synchronized (this) {
            this.wait(limit * 3);
        }
        Assert.assertFalse(throttler.isRunning());

        for (int i = 0; i < ELEMS; i++) {
            Assert.assertEquals(throttler.checkAllowed(Integer.toString(i)), 0);
        }
        Assert.assertTrue(throttler.isRunning());

        synchronized (this) {
            this.wait(limit * 3);
        }
        Assert.assertFalse(throttler.isRunning());
    }

}
