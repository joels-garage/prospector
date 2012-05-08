/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import java.util.ArrayList;
import java.util.List;

import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * Writer implementation that writes T records to a list. It's for testing.
 * 
 * @author joel
 * 
 */
public class ListRecordWriter<T> implements RecordWriter<T> {
	private List<T> list;

	public ListRecordWriter() {
		setList(new ArrayList<T>());
	}

	/**
	 * @see com.joelsgarage.extractor.ExtractorWriter#write(java.lang.String)
	 */
	@Override
	public void write(T payload) {
		getList().add(payload);
	}

	public List<T> getList() {
		return this.list;
	}

	public void setList(List<T> list) {
		this.list = list;
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
