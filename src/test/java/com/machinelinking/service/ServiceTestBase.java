package com.machinelinking.service;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ServiceTestBase {

    private final BasicServer server = new BasicServer();

    @Before
    public void setUp() throws IOException {
        server.setUp();
    }

    @After
    public void tearDown() {
        server.tearDown();
    }

}
