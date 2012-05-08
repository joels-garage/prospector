/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.joelsgarage.model.Decision;

/**
 * @author joel
 */
@SuppressWarnings("nls")
public class ReaderConstraintTest extends TestCase {
    public void testEquals() {
        Map<String, Object> mapA = new HashMap<String, Object>();
        mapA.put("foo", "bar");
        ReaderConstraint constraintA = new ReaderConstraint(Decision.class, mapA);
        Map<String, Object> mapB = new HashMap<String, Object>();
        mapB.put("foo", "bar");
        ReaderConstraint constraintB = new ReaderConstraint(Decision.class, mapB);
        assertEquals(constraintA, constraintB);
    }

    public void testNullEqual() {
        ReaderConstraint constraintA = new ReaderConstraint(Decision.class, null);
        ReaderConstraint constraintB = new ReaderConstraint(Decision.class, null);
        assertEquals(constraintA, constraintB);
    }

    public void testDoubleNullEqual() {
        ReaderConstraint constraintA = new ReaderConstraint(null, null);
        ReaderConstraint constraintB = new ReaderConstraint(null, null);
        assertEquals(constraintA, constraintB);
    }

}
