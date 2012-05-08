/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import java.util.HashMap;
import java.util.Map;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.RecordFetcherFactory;

/**
 * A record reader factory that's just a map.
 * 
 * @author joel
 * 
 */
public class MapRecordFetcherFactory<K, V> implements RecordFetcherFactory<K, V> {
	private Map<ReaderConstraint, RecordFetcher<K, V>> fetchers;

	public MapRecordFetcherFactory() {
		this.fetchers = new HashMap<ReaderConstraint, RecordFetcher<K, V>>();
	}

	@Override
	public RecordFetcher<K, V> newInstance(ReaderConstraint constraint) {
		return this.fetchers.get(constraint);
	}

	public void putReader(ReaderConstraint c, RecordFetcher<K, V> r) {
		this.fetchers.put(c, r);
	}
}
