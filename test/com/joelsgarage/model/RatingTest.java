/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import junit.framework.TestCase;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.TestData;

/**
 * @author joel
 */
@SuppressWarnings("nls")
public class RatingTest extends TestCase {
    public void testSimple() throws FatalException {
        TestData testData = new TestData();
        Rating rating = new Rating(testData.stakeholder.makeKey(), "aquery", "namespace");
        assertEquals(testData.stakeholder.makeKey(), rating.getStakeholderKey());
        assertEquals("aquery", rating.getQuery());
        assertEquals("namespace", rating.getNamespace());
    }

}
