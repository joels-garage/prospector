/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.util.ArrayList;
import java.util.List;

import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * Writer implementation that writes to a list.
 * 
 * @author joel
 * 
 */
public class MockExtractorWriter implements RecordWriter<String> {
	private List<String> list;

	/** Writer implementation that writes to a list. */
	public MockExtractorWriter() {
		setList(new ArrayList<String>());
	}

	/**
	 * @see com.joelsgarage.extractor.ExtractorWriter#write(java.lang.String)
	 */
	@Override
	public void write(String payload) {
		getList().add(payload);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
	}

	// Accessors

	public List<String> getList() {
		return this.list;
	}

	protected void setList(List<String> list) {
		this.list = list;
	}
}
