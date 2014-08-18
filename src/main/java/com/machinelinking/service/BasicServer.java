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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import javax.ws.rs.core.UriBuilder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Basic Service implementation based on <i>Grizzly</i>.
 *
 * See: http://mytecc.wordpress.com/2013/06/06/grizzly-2-3-3-serving-static-http-resources-from-jar-files/
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class BasicServer {

    public static final String HOST_PARAM = "server.host";
    public static final String PORT_PARAM = "server.port";

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final Integer DEFAULT_PORT  = 9998;

    public static final String RESOURCES_ROOT = "./static_resources";

    private static final Logger logger = Logger.getLogger(BasicServer.class);

    private static final String PACKAGE_ROOT = BasicServer.class.getPackage().getName().replace(".", "/");

    private static final String SRC_RESOURCE_ROOT = String.format("src/main/resources/%s/frontend", PACKAGE_ROOT);
    private static final String JAR_RESOURCE_ROOT = String.format("%s/%s/frontend", RESOURCES_ROOT, PACKAGE_ROOT);

    private static final String CONTENT_TYPE = "Content-Type";

    public final URI baseURI;

    private HttpServer httpServer;

    public static void main(String[] args) {
        final ParamsMapper mapper = new ParamsMapper();
        final JCommander commander = new JCommander(mapper);
        int exitCode = 0;
        try {
            commander.parse(args);

            final ConfigurationManager configurationManager = ConfigurationManager.getInstance();
            configurationManager.initProperties(new File(mapper.configFile));
            final String host = readHost(configurationManager.getProperty(HOST_PARAM, DEFAULT_HOST));
            final int port = readPort(configurationManager.getProperty(PORT_PARAM, DEFAULT_PORT.toString()));

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

    public BasicServer(String host, int port) {
        this.baseURI = UriBuilder.fromUri( String.format("http://%s/", host)).port(port).build();
    }

    public BasicServer() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public URI getBaseURI() {
        return baseURI;
    }

    public void setUp() throws IOException {
        ResourceConfig rc = new PackagesResourceConfig(BasicServer.class.getPackage().getName());
        httpServer = GrizzlyServerFactory.createHttpServer(getBaseURI(), rc);
        httpServer.getServerConfiguration().addHttpHandler(
                // Provided by Grizzly 2.3.3 that unfortunately doesn't manage correctly encoded URLs in GET requests.
                // new CLStaticHttpHandler( this.getClass().getClassLoader() ){
                new StaticHttpHandler( initFrontendResources() ) {
                    @Override
                    protected boolean handle(String uri, Request req, Response res) throws Exception {
                        if(uri.endsWith(".html")) {
                            res.setHeader(CONTENT_TYPE, "text/html; charset=utf-8");
                        } else if(uri.endsWith(".css")) {
                            res.setHeader(CONTENT_TYPE, "text/css");
                        } else if(uri.endsWith(".js")) {
                            res.setHeader(CONTENT_TYPE, "text/javascript");
                        } else if(uri.endsWith(".png")) {
                            res.setHeader(CONTENT_TYPE, "image/png");
                        } else if(uri.endsWith(".gif")) {
                            res.setHeader(CONTENT_TYPE, "image/gif");
                        }
                        return super.handle(uri, req, res);
                    }
                },
                "/frontend/"
        );
        httpServer.start();
        logger.info("Started Grizzly server: " + getBaseURI());
    }

    public void tearDown() {
        httpServer.stop();
    }

    private String initFrontendResources() throws IOException {
        final URL container = this.getClass().getClassLoader().getResource(PACKAGE_ROOT);
        final String containerProtocol = container.getProtocol();
        switch (containerProtocol) {
            case "file":
                return SRC_RESOURCE_ROOT;
            case "jar":
                decompress(container.getFile().substring("file:".length()).split("!")[0], PACKAGE_ROOT, RESOURCES_ROOT);
                return JAR_RESOURCE_ROOT;
            default:
                throw new IllegalStateException("Invalid protocol for container: " + containerProtocol);
        }
    }

    private void decompress(String jarFile, String filter, String destination) throws IOException {
        new File(destination).delete();
        JarFile jar = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jar.entries();
        String entryName;
        while (entries.hasMoreElements()){
            JarEntry entry = entries.nextElement();
            entryName = entry.getName();
            if(! entryName.startsWith(filter)) continue;
            if(entryName.endsWith(".class")) continue;
            File file = new java.io.File(destination, entryName);
            if (entry.isDirectory()) {
                file.mkdirs();
                continue;
            }
            byte[] buffer = new byte[1024 * 4];
            int readBytes;
            try (
                    final InputStream is = new BufferedInputStream(jar.getInputStream(entry));
                    final OutputStream os = new BufferedOutputStream(new FileOutputStream(file))
            ) {
                while ((readBytes = is.read(buffer)) != -1) {
                    os.write(buffer, 0, readBytes);
                }
            }
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
