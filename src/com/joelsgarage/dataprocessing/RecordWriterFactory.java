/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

/**
 * This is not necessary for operation in the same way the reader factory is -- all the extant
 * processes involve a single writer -- but it's convenient for testing.
 * 
 * @author joel
 * 
 */
public interface RecordWriterFactory<T> {
	/** Return a new reader configured with the specified constraint */
	public RecordWriter<T> newInstance(ReaderConstraint constraint);
}
