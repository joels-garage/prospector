/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.Map;

/**
 * A RecordFetcher provides random access, for key lookups on primary (default) or foreign keys.
 * 
 * @author joel
 * 
 */
public interface RecordFetcher<K, V> {
    /**
     * Return a single records associated with the specified primary key. If multiple records
     * qualify, this arbitrarily selects the first one.
     * 
     * @param key
     * @return null if no corresponding record can be found.
     */
    public V get(K key);

    /**
     * Return the value assocated with the specified foreign key, using the specified fieldName to
     * select the foreign key field.
     * 
     * @param fieldName
     * @param key
     * @return null if no corresponding record can be found.
     */
    public V get(String fieldName, K key);

    /**
     * Multiple key constraints. Value can be anything; need not be key type.
     * 
     * @param queryTerms
     * @return
     */
    public V getCompound(Map</* field name */String, /* field value */Object> queryTerms);

    /** Fetch the specified subclass */
    public <S extends V> S getCompound(Class<S> entityClass, Map<String, Object> queryTerms);
}
