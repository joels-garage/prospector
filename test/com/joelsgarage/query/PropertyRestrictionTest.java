/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.query;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class PropertyRestrictionTest extends TestCase {
    public void testEmpty() {
        try {
            @SuppressWarnings("unused")
            PropertyRestriction r = PropertyRestriction.newInstance("");
            fail();
        } catch (QueryException e) {
            assertTrue(true);
        }
    }

    public void testSimple() {
        try {
            PropertyRestriction r = PropertyRestriction.newInstance("foo");
            assertEquals(false, r.reverse);
            assertEquals(Operator.EQUALS, r.operator);
            assertEquals("foo", r.property);
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testOperator() {
        try {
            PropertyRestriction r = PropertyRestriction.newInstance("foo>>");
            assertEquals(false, r.reverse);
            assertEquals(Operator.MAXIMIZE, r.operator);
            assertEquals("foo", r.property);
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testReverse() {
        try {
            PropertyRestriction r = PropertyRestriction.newInstance("!foo");
            assertEquals(true, r.reverse);
            assertEquals(Operator.EQUALS, r.operator);
            assertEquals("foo", r.property);
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testReverseOperator() {
        try {
            PropertyRestriction r = PropertyRestriction.newInstance("!foo@@");
            assertEquals(true, r.reverse);
            assertEquals(Operator.OPTIMIZE, r.operator);
            assertEquals("foo", r.property);
        } catch (QueryException e) {
            e.printStackTrace();
            fail();
        }
    }
}
