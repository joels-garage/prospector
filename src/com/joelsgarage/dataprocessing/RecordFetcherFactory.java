/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

/**
 * The RecordFetcherFactory is the means to inject a datasource.
 * 
 * @author joel
 * 
 */
public interface RecordFetcherFactory<K, V> {
	public RecordFetcher<K, V> newInstance(ReaderConstraint constraint);
}
