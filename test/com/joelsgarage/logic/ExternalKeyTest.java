/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import junit.framework.TestCase;

import com.joelsgarage.model.ExternalKey;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ExternalKeyTest extends TestCase {
    // public void testBlank() {
    // ExternalKey key = new ExternalKey();
    // assertEquals("", key.getType());
    // assertEquals("", key.getNamespace());
    // assertEquals("", key.getKey());
    // }

    public void testTwoArguments() {
        ExternalKey key = new ExternalKey("foo", new String("bar").getBytes());
        assertEquals("foo", key.getType());
        assertEquals("bar", new String(key.getKey()));
    }

    // public void testOneArgument() {
    // ExternalKey key = new ExternalKey("bar/foo/baz");
    // assertEquals("bar", key.getType());
    // assertEquals("foo", key.getNamespace());
    // assertEquals("baz", key.getKey());
    // }

    // public void testOneArgumentBlank() {
    // ExternalKey key = new ExternalKey("//");
    // assertEquals("", key.getType());
    // assertEquals("", key.getNamespace());
    // assertEquals("", key.getKey());
    // }
    //
    // public void testOneArgumentFail1() {
    // ExternalKey key = new ExternalKey("");
    // assertEquals("", key.getType());
    // assertEquals("", key.getNamespace());
    // assertEquals("", key.getKey());
    // }
    //
    // public void testOneArgumentFail2() {
    // ExternalKey key = new ExternalKey("/");
    // assertEquals("", key.getType());
    // assertEquals("", key.getNamespace());
    // assertEquals("", key.getKey());
    // }

    public void testTwoArgumentsWithSpaces() {
        ExternalKey key = new ExternalKey("bar bar", new String("abcabcabcabc").getBytes());
        assertEquals("bar bar", key.getType());
        assertEquals("abcabcabcabc", new String(key.getKey()));
        assertEquals("bar bar/YWJjYWJjYWJjYWJj", key.toString());
        assertEquals("bar+bar/YWJjYWJjYWJjYWJj", key.escapedString());
    }

    // public void testOneArgumentWithSpaces() {
    // ExternalKey key = new ExternalKey("bar bar/foo foo/baz baz");
    // assertEquals("bar bar", key.getType());
    // assertEquals("foo foo", key.getNamespace());
    // assertEquals("baz baz", key.getKey());
    // assertEquals("bar bar/foo foo/baz baz", key.toString());
    // assertEquals("bar+bar/foo+foo/baz+baz", key.escapedString());
    // }
}
