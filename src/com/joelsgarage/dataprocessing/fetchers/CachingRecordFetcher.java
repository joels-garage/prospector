/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.SimpleLRUCache;

/**
 * Wraps a record fetcher with an LRU cache. Because the fetcher may be called for any foreign key
 * field, we actually have a map of caches.
 * 
 * @author joel
 */
public class CachingRecordFetcher<K, V> implements RecordFetcher<K, V> {
    /** Delegate data access to the fetcher. */
    private RecordFetcher<K, V> fetcher;
    /** How big each cache is */
    private int capacity;
    /** Cache for primary-key lookups */
    private SimpleLRUCache<K, V> primaryKeyCache;
    /** One cache per foreign key, lazily (obviously) created */
    private Map<String, SimpleLRUCache<K, V>> foreignKeyCaches;

    public CachingRecordFetcher(int capacity, RecordFetcher<K, V> fetcher) {
        setFetcher(fetcher);
        setCapacity(capacity);
        setPrimaryKeyCache(new SimpleLRUCache<K, V>(capacity));
        setForeignKeyCaches(new HashMap<String, SimpleLRUCache<K, V>>(capacity));
    }

    @Override
    public V get(K key) {
        // Note that we *DO CACHE NULLS* so that if the underlying store doesn't have it, we don't
        // keep asking
        if (getPrimaryKeyCache().containsKey(key)) {
            Logger.getLogger(CachingRecordFetcher.class).info("Hit on " + key.toString()); //$NON-NLS-1$
            return getPrimaryKeyCache().get(key);
        }
        Logger.getLogger(CachingRecordFetcher.class).info("Miss on " + key.toString()); //$NON-NLS-1$
        V value = getFetcher().get(key); // may be null ...
        getPrimaryKeyCache().put(key, value); // we cache it anyway
        return value;
    }

    @Override
    public V get(String fieldName, K key) {
        SimpleLRUCache<K, V> cache = getForeignKeyCaches().get(fieldName);
        if (cache == null) {
            Logger.getLogger(CachingRecordFetcher.class).info("new cache for field " + fieldName); //$NON-NLS-1$
            cache = new SimpleLRUCache<K, V>(getCapacity());
            getForeignKeyCaches().put(fieldName, cache);
        }
        if (cache.containsKey(key)) {
            Logger.getLogger(CachingRecordFetcher.class).info("hit on " + key.toString()); //$NON-NLS-1$
            return cache.get(key);
        }
        Logger.getLogger(CachingRecordFetcher.class).info("Miss on " + key.toString()); //$NON-NLS-1$
        V value = getFetcher().get(fieldName, key);
        if (value == null) {
            Logger.getLogger(CachingRecordFetcher.class).info("Null for " + key.toString()); //$NON-NLS-1$
        }
        cache.put(key, value);
        return value;
    }

    /**
     * We don't cache compound gets.
     * 
     * TODO: do so.
     */
    @Override
    public V getCompound(Map<String, Object> queryTerms) {
        return getFetcher().getCompound(queryTerms);
    }

    @Override
    public <S extends V> S getCompound(Class<S> entityClass, Map<String, Object> queryTerms) {
        return getFetcher().getCompound(entityClass, queryTerms);
    }

    //
    //

    protected RecordFetcher<K, V> getFetcher() {
        return this.fetcher;
    }

    protected void setFetcher(RecordFetcher<K, V> fetcher) {
        this.fetcher = fetcher;
    }

    protected SimpleLRUCache<K, V> getPrimaryKeyCache() {
        return this.primaryKeyCache;
    }

    protected void setPrimaryKeyCache(SimpleLRUCache<K, V> primaryKeyCache) {
        this.primaryKeyCache = primaryKeyCache;
    }

    protected Map<String, SimpleLRUCache<K, V>> getForeignKeyCaches() {
        return this.foreignKeyCaches;
    }

    protected void setForeignKeyCaches(Map<String, SimpleLRUCache<K, V>> foreignKeyCaches) {
        this.foreignKeyCaches = foreignKeyCaches;
    }

    protected int getCapacity() {
        return this.capacity;
    }

    protected void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
