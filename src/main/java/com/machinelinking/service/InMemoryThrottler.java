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

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In memory implementation for {@link com.machinelinking.service.Throttler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class InMemoryThrottler implements Throttler {

    private static final Logger logger = Logger.getLogger(InMemoryThrottler.class);

    private static final int DEFAULT_SIZE_LIMIT_WARNING = 500; // 500 * (((3*4) + 3) * 2 + 4) bytes

    private final Map<String,Long> lastAccess = Collections.synchronizedMap(new HashMap<String, Long>());

    private final Object waitObj = new Object();

    private final long limit;

    private final int sizeLimitWarning;

    private final Disposer disposer;

    private Thread disposerThread;

    public InMemoryThrottler(long limit, int sizeLimitWarning) {
        this.limit = limit;
        this.sizeLimitWarning = sizeLimitWarning;
        disposer = new Disposer();
    }

    public InMemoryThrottler(long limit) {
        this(limit, DEFAULT_SIZE_LIMIT_WARNING);
    }

    /**
     * Verifies that the disposer thread is not running.
     *
     * @return <code>true</code> if disposer thread is running.
     */
    public boolean isRunning() {
        synchronized (this) {
            return disposerThread != null;
        }
    }

    @Override
    public long checkAllowed(String ip) {
        final Long last = lastAccess.get(ip);
        if(last == null) {
            lastAccess.put(ip, System.currentTimeMillis());
            checkRunning();
            if(lastAccess.size() >= sizeLimitWarning) {
                logger.warn(String.format(
                        "Number of entries in cache exceeded warning limit: %d [%d]",
                        lastAccess.size(), sizeLimitWarning
                ));
            }
            return 0;
        } else {
            final long elapsed = System.currentTimeMillis() - last;
            return elapsed >= limit ? 0 : limit - elapsed;
        }
    }

    private void checkRunning() {
        synchronized (this) {
            if (disposerThread == null) {
                disposerThread = new Thread(disposer, InMemoryThrottler.class.getName() + "-disposer");
                disposerThread.start();
            }
        }
    }

    private class Disposer implements Runnable {
        @Override
        public void run() {
            while(true) {
                synchronized (waitObj) {
                    try {
                        waitObj.wait(limit);
                    } catch (InterruptedException ie) {
                        throw new RuntimeException("Interrupted.", ie);
                    }
                }
                final long curr = System.currentTimeMillis();
                final List<String> removing = new ArrayList<>();
                for (Map.Entry<String, Long> entry : lastAccess.entrySet()) {
                    if (curr - entry.getValue() > limit) {
                        removing.add(entry.getKey());
                    }
                }
                for (String k : removing) {
                    lastAccess.remove(k);
                }
                synchronized (this) {
                    if (lastAccess.isEmpty()) {
                        disposerThread = null;
                        break;
                    }
                }
            }
        }
    }

}
