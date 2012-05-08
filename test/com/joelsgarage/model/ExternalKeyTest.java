/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import junit.framework.TestCase;

/**
 * key equality tests
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ExternalKeyTest extends TestCase {
    public void testEquality() {
        ExternalKey foo1 = new ExternalKey("type/fookey");
        ExternalKey foo2 = new ExternalKey("type/fookey");
        ExternalKey bar = new ExternalKey("type/barkey");
        ExternalKey baz = new ExternalKey("othertype/fookey");
        assertEquals(foo1, foo2);
        assertTrue(foo1.equals(foo2));
        assertFalse(foo1.equals(bar));
        assertFalse(baz.equals(foo1));
    }

    public void testEquality2() {
        ExternalKey foo1 = new ExternalKey("type", "fookey".getBytes());
        ExternalKey foo2 = new ExternalKey("type", "fookey".getBytes());
        ExternalKey bar = new ExternalKey("type", "barkey".getBytes());
        ExternalKey baz = new ExternalKey("othertype", "fookey".getBytes());
        assertEquals(foo1, foo2);
        assertTrue(foo1.equals(foo2));
        assertFalse(foo1.equals(bar));
        assertFalse(baz.equals(foo1));
    }

    public void testHashCode() {
        ExternalKey foo1 = new ExternalKey("type", "fookey".getBytes());
        ExternalKey foo2 = new ExternalKey("type", "fookey".getBytes());
        ExternalKey bar = new ExternalKey("type", "barkey".getBytes());
        ExternalKey baz = new ExternalKey("othertype", "fookey".getBytes());
        assertEquals(foo1.hashCode(), foo2.hashCode());
        assertTrue(foo1.hashCode() == foo2.hashCode());
        assertFalse(foo1.hashCode() == bar.hashCode());
        assertFalse(baz.hashCode() == foo1.hashCode());
    }
}
