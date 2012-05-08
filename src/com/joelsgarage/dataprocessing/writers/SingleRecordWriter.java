/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * Writer implementation that expects a SINGLE call to write().
 * 
 * Multiple calls to write() simply OVERWRITE the record.
 * 
 * @author joel
 * 
 */
public class SingleRecordWriter<T> implements RecordWriter<T> {
	private T record;

	public SingleRecordWriter() {
		// foo
	}

	/**
	 * @see com.joelsgarage.extractor.ExtractorWriter#write(T)
	 */
	@SuppressWarnings("nls")
	@Override
	public void write(T payload) {
		Logger.getLogger(SingleRecordWriter.class).info("writing a record");
		setRecord(payload);
	}

	public T getRecord() {
		return this.record;
	}

	public void setRecord(T record) {
		this.record = record;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
	}
}
