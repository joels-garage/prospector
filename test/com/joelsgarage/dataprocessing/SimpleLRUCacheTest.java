/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class SimpleLRUCacheTest extends TestCase {
    /**
     * Verify the oldest entry is discarded. In this case, the oldest accessed (with "get") is also
     * the oldest added (with "put")
     */
    public void testSimple() {
        // a very small cache
        SimpleLRUCache<String, String> cache = new SimpleLRUCache<String, String>(2);
        cache.put("foo", "bar");
        assertEquals("bar", cache.get("foo")); // this is the oldest access
        cache.put("red", "rock");
        assertEquals("rock", cache.get("red"));

        cache.put("a bridge", "too far");
        assertEquals("too far", cache.get("a bridge"));

        assertNull(cache.get("foo")); // oldest is gone
        assertEquals(2, cache.size());
    }

    /**
     * Verify the oldest entry is discarded. In this case the oldest accessed (with "get") is not
     * the oldest added (with "put")
     */
    public void testOutOfOrder() {
        // a very small cache
        SimpleLRUCache<String, String> cache = new SimpleLRUCache<String, String>(2);
        cache.put("foo", "bar");
        cache.put("red", "rock");
        assertEquals("rock", cache.get("red")); // this is the oldest accessed
        assertEquals("bar", cache.get("foo"));

        cache.put("a bridge", "too far");
        assertEquals("too far", cache.get("a bridge"));

        assertNull(cache.get("red")); // oldest is gone
        assertEquals(2, cache.size());
    }

}
