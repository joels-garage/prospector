/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import junit.framework.TestCase;

/**
 * Just verifies that asserts are being run in test.
 * 
 * @author joel
 * 
 */
public class AssertTest extends TestCase {
    public void testSimple() {
        try {
            assert (false);
            fail();
        } catch (AssertionError ex) {
            assertTrue(true);
        }
    }
}
