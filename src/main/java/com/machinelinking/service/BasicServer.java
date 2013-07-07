package com.machinelinking.service;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
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
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

// import org.glassfish.grizzly.http.server.CLStaticHttpHandler;

/**
 * Basic Service implementation based on <i>Grizzly</i>.
 *
 * See: http://mytecc.wordpress.com/2013/06/06/grizzly-2-3-3-serving-static-http-resources-from-jar-files/
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class BasicServer {

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int   DEFAULT_PORT  = 9998;

    public static final String RESOURCES_ROOT = "./static_resources";

    private static final String PACKAGE_ROOT = BasicServer.class.getPackage().getName().replace(".", "/");

    private static final String SRC_RESOURCE_ROOT = String.format("src/main/resources/%s/frontend", PACKAGE_ROOT);
    private static final String JAR_RESOURCE_ROOT = String.format("%s/%s/frontend", RESOURCES_ROOT, PACKAGE_ROOT);

    private static final String CONTENT_TYPE = "Content-Type";

    public final URI baseURI;

    private HttpServer httpServer;

    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length != 2) {
            System.err.println("Usage $0 <host> <port>");
            System.exit(1);
        }

        final BasicServer basicServer = new BasicServer(args[0], Integer.parseInt(args[1]));
        basicServer.setUp();
        System.out.println(
            String.format(
                    "Jersey app started with WADL available at %sapplication.wadl\n" +
                            "Hit C^ to stop ...",
                    basicServer.getBaseURI()
            )
        );
        synchronized (basicServer) {
            basicServer.wait();
        }
        basicServer.tearDown();
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
        System.out.println("Starting Grizzly Server...");
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

    }

    public void tearDown() {
        httpServer.stop();
    }

    private String initFrontendResources() throws IOException {
        final URL container = this.getClass().getClassLoader().getResource(PACKAGE_ROOT);
        final String containerProtocol = container.getProtocol();
        if ("file".equals(containerProtocol)) {
            return SRC_RESOURCE_ROOT;
        } else if("jar".equals(containerProtocol)) {
            decompress(container.getFile().substring("file:".length()).split("!")[0], PACKAGE_ROOT, RESOURCES_ROOT);
            return JAR_RESOURCE_ROOT;
        } else {
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
            InputStream is  = new BufferedInputStream(jar.getInputStream(entry));
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[1024 * 4];
            int readBytes;
            try {
                while ((readBytes = is.read(buffer)) != -1) {
                    os.write(buffer, 0, readBytes);
                }
            } finally {
                os.close();
                is.close();
            }
        }
    }

}
