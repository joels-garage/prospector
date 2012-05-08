/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

/**
 * @author joel
 */
public interface RecordReaderFactory<T> {
	/** Return a new reader configured with the specified constraint */
	public RecordReader<T> newInstance(ReaderConstraint constraint);
}
