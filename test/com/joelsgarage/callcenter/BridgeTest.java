/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import junit.framework.TestCase;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.InitializationException;

/**
 * @author joel
 * 
 */
public class BridgeTest extends TestCase {
    public void testSimple() throws FatalException {
        Bridge bridge;
        try {
            bridge = new Bridge();
            assertNotNull(bridge);
            bridge.run(null, false, null);
        } catch (InitializationException e) {
            e.printStackTrace();
            fail();
        }
    }
}
