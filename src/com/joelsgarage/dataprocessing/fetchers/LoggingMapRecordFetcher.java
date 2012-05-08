/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A MapRecordFetcher that logs accesses, for testing the cache.
 * 
 * @author joel
 * 
 */
public abstract class LoggingMapRecordFetcher<K, V> extends MapRecordFetcher<K, V> {
    public class Log {
        public K key;
        public String fieldName;

        public Log(K key, String fieldName) {
            this.key = key;
            this.fieldName = fieldName;
        }
    }

    private List<Log> accessLog;

    public LoggingMapRecordFetcher(Map<K, V> data) {
        super(data);
        setAccessLog(new ArrayList<Log>());
    }

    @Override
    public V get(K key) {
        getAccessLog().add(new Log(key, null));
        return super.get(key);
    }

    @Override
    public V get(String fieldName, K key) {
        getAccessLog().add(new Log(key, fieldName));
        return super.get(fieldName, key);
    }

    /** Does not log */
    @Override
    public V getCompound(Map<String, Object> queryTerms) {
        return super.getCompound(queryTerms);
    }

    public Iterator<Log> getAccessLogIterator() {
        return getAccessLog().iterator();
    }

    //

    protected List<Log> getAccessLog() {
        return this.accessLog;
    }

    protected void setAccessLog(List<Log> accessLog) {
        this.accessLog = accessLog;
    }

}
