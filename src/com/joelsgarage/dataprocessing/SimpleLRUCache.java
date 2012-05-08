/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU Cache using LinkedHashMap's removeEldestEntry() feature.
 * 
 * @author joel
 * 
 */
public class SimpleLRUCache<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 1L;
    private final int maxEntries;

    /**
     * 
     * @param maxEntries
     *            maximum size of the cache, using LinkedHashMap LRU feature.
     */
    public SimpleLRUCache(int maxEntries) {
        super(maxEntries, 0.75f, true);
        this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > this.maxEntries;
    }
}
