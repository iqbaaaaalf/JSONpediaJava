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

package com.machinelinking.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.machinelinking.service.BasicServer;
import com.machinelinking.service.ConfigurationManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Runs the JSONpedia WebApp based on {@link com.machinelinking.service.BasicServer}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class server {

    public static final String HOST_PARAM = "server.host";
    public static final String PORT_PARAM = "server.port";

    public static void main(String[] args) {
        final ParamsMapper mapper = new ParamsMapper();
        final JCommander commander = new JCommander(mapper);
        int exitCode = 0;
        try {
            commander.parse(args);

            final ConfigurationManager configurationManager = ConfigurationManager.getInstance();
            configurationManager.initProperties(new File(mapper.configFile));
            final String host = readHost(configurationManager.getProperty(HOST_PARAM, BasicServer.DEFAULT_HOST));
            final int port = readPort(configurationManager.getProperty(PORT_PARAM, BasicServer.DEFAULT_PORT.toString()));

            final BasicServer server = new BasicServer(host, port);
            server.setUp();
            System.out.println(
                    String.format(
                            "JSONpedia service started at port %d.\n" +
                                    "WADL descriptor at %sapplication.wadl\n" +
                                    "Hit C^ to stop ...",
                            port,
                            server.getBaseURI()
                    )
            );
            synchronized (server) {
                server.wait();
            }
            server.tearDown();
        } catch (ParameterException pe) {
            System.err.println(pe.getMessage());
            commander.usage();
            exitCode = 1;
        } catch (Exception e) {
            System.err.println("Error while running " + BasicServer.class.getName());
            e.printStackTrace();
            exitCode = 2;
        } finally {
            System.exit(exitCode);
        }
    }

    private static String readHost(String host) {
        try {
            new URL(String.format("http://%s", host));
        } catch (MalformedURLException murle) {
            throw new IllegalArgumentException("Invalid host", murle);
        }
        return host;
    }

    private static int readPort(String port) {
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid port", nfe);
        }
    }

    static class ParamsMapper {
        @Parameter(
                names = {"--conf", "-c"},
                description = "configuration file",
                required = true
        )
        String configFile;
    }

}
