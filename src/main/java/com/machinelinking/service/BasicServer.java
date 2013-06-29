package com.machinelinking.service;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Basic Service implementation based on <i>Grizzly</i>.
 *
 * See: http://mytecc.wordpress.com/2013/06/06/grizzly-2-3-3-serving-static-http-resources-from-jar-files/
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class BasicServer {

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int   DEFAULT_PORT  = 9998;

    private static final String RESOURCE_ROOT =
            BasicServer.class.getPackage().getName().replace(".", "/") + "/frontend";

    private static final String CONTENT_TYPE = "Content-Type";

    public final URI baseURI;

    private HttpServer httpServer;

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
                //TODO: not working with JAR: new CLStaticHttpHandler( new ResourceRedirectionClassLoader(this.getClass().getClassLoader())){
                new CLStaticHttpHandler( this.getClass().getClassLoader() ){
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

    class ResourceRedirectionClassLoader extends ClassLoader {

        ResourceRedirectionClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public URL getResource(String name) {
            try {
                return new URL( String.format("%s%s/%s", super.getResource(""), RESOURCE_ROOT, name) );
            } catch (MalformedURLException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
