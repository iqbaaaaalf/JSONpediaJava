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


import com.machinelinking.util.Probe;
import org.glassfish.grizzly.nio.transport.TCPNIOConnection;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import java.net.InetSocketAddress;

/**
 * Base service class providing throttling functionalities.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class ServiceBase implements Service {

    public static final String SERVICE_THROTTLING_PROPERTY = "storage.service.query.throttling";

    private static final long throttling;

    private final Probe probe = Probe.getInstance();

    static {
        try {
            throttling = Long.parseLong(
                    ConfigurationManager.getInstance().getProperty(SERVICE_THROTTLING_PROPERTY, "0")
            );
        }catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(
                    String.format("Invalid value for %s, expected long.", SERVICE_THROTTLING_PROPERTY)
            );
        }
    }

    private final Throttler throttler = new InMemoryThrottler(throttling);

    @Context private Request req;

    //NOTE: this solution has been introduced to bypass issue
    //      http://stackoverflow.com/questions/11312578/how-to-determine-remote-ip-address-from-jax-rs-resource
    public void checkQuota() {
        final String ip;
        try {
            final TCPNIOConnection connection =  probe.probePath(req, "entity.inputBuffer.request.connection");
            ip = ((InetSocketAddress) connection.getPeerAddress()).getAddress().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(throttling == 0) return;
        final long wait = throttler.checkAllowed(ip);
        if(wait > 0) throw new QuotaOverflowException(wait);
    }

}
