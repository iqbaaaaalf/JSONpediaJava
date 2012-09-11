package com.machinelinking.service;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.junit.After;
import org.junit.Before;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

//NOTE: TESTED WITH http://localhost:9998/annotate/resource/http%3A%2F%2Fen.wikipedia.org%2Fpage%2FLondon
/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ServiceTestBase {

    public static final String HOST = "localhost";
    public static final int PORT    = 9998;

    private static final String CONTENT_TYPE = "Content-Type";

    public static final URI BASE_URI = getBaseURI().build();

    private HttpServer httpServer;

    protected static UriBuilder getBaseURI() {
        return UriBuilder.fromUri( String.format("http://%s/", HOST)).port(PORT);
    }

    @Before
    public void setUp() throws IOException {
        System.out.println("Starting Grizzly Server...");
        ResourceConfig rc = new PackagesResourceConfig(ServiceTestBase.class.getPackage().getName());
        httpServer = GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
        httpServer.getServerConfiguration().addHttpHandler(
                new StaticHttpHandler("./src/main/resources/frontend/"){
                    @Override
                    protected boolean handle(String uri, Request req, Response res) throws Exception {
                        if(uri.endsWith(".css")) {
                            res.setHeader(CONTENT_TYPE, "text/css");
                        } else if(uri.endsWith(".js")) {
                            res.setHeader(CONTENT_TYPE, "test/javascript");
                        }
                        return super.handle(uri, req, res);
                    }
                },
                "/frontend/"
        );
        httpServer.start();

    }

    @After
    public void tearDown() {
        httpServer.stop();
    }

    public static void main(String[] args) throws IOException {
        final ServiceTestBase serviceTestBase = new ServiceTestBase();
        serviceTestBase.setUp();
        System.out.println(
            String.format(
                "Jersey app started with WADL available at %sapplication.wadl\n" +
                 "Hit enter to stop it...",
                BASE_URI
            )
        );
        System.in.read();
        serviceTestBase.tearDown();
    }

}
