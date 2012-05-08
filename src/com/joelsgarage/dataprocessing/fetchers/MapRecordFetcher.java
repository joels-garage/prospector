/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.util.ClassUtil;
import com.joelsgarage.util.KeyUtil;

/**
 * A record fetcher whose datasource is a map.
 * 
 * @author joel
 * 
 */
public abstract class MapRecordFetcher<K, V> implements RecordFetcher<K, V> {
    private Map<K, V> data;

    public MapRecordFetcher(Map<K, V> data) {
        setData(data);
    }

    @Override
    public V get(K key) {
        return getData().get(key);
    }

    /** This is a simple class; a fk lookup is a scan. */
    @Override
    public V get(String fieldName, K key) {
        for (V value : getData().values()) {
            K fk = extractForeignKey(fieldName, value);
            if (fk.equals(key))
                return value;
        }
        return null;
    }

    /** Scan all the keys */
    public V getCompound(Map<String, Object> queryTerms) {
        values: for (V value : getData().values()) {
            for (Map.Entry<String, Object> entry : queryTerms.entrySet()) {
                K fk = extractForeignKey(entry.getKey(), value);
                if (!fk.equals(entry.getValue()))
                    continue values;
            }
            return value;
        }
        return null;
    }

    @Override
    public <S extends V> S getCompound(Class<S> entityClass, Map<String, Object> queryTerms) {
        values: for (V value : getData().values()) {
            Logger.getLogger(MapRecordFetcher.class).info(
                "considering " + ClassUtil.shortClassName(value.getClass()) + //$NON-NLS-1$
                    " : " + value.toString()); //$NON-NLS-1$
            if (entityClass.isInstance(value)) {
                S sValue = entityClass.cast(value);
                for (Map.Entry<String, Object> entry : queryTerms.entrySet()) {
                    K fk = extractForeignKey(entry.getKey(), sValue);
                    if (!fk.equals(entry.getValue()))
                        continue values;
                }
                Logger.getLogger(MapRecordFetcher.class).info("ok"); //$NON-NLS-1$
                return sValue;
            }
        }
        return null;
    }

    /**
     * Extracts the foreign key, for foreign-key lookups. For now, FK lookups are scans.
     */
    protected abstract K extractForeignKey(String fieldName, V instance);

    protected Map<K, V> getData() {
        return this.data;
    }

    protected void setData(Map<K, V> data) {
        this.data = data;
    }
}
