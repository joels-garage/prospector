/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

/**
 * Writes records of type T.
 * 
 * @author joel
 * 
 */
public interface RecordWriter<T> {
	/**
	 * Call prior to writing.
	 */
	public void open();

	/**
	 * Write the payload.
	 * 
	 * TODO: error handling.
	 * 
	 * @param payload
	 *            data to be written.
	 */
	public void write(T payload);

	/**
	 * Call after done writing.
	 */
	public void close();

}
