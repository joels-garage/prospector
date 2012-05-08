/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * Writer implementation that uses an OutputStream.
 * 
 * @author joel
 * 
 */
public class StreamExtractorWriter implements RecordWriter<String> {
	private OutputStream output;
	private OutputStreamWriter writer;

	public StreamExtractorWriter(OutputStream output) {
		setOutput(output);
		if (getOutput() != null) {
			setWriter(new OutputStreamWriter(getOutput()));
		}
	}

	@Override
	public void write(String payload) {
		try {
			getWriter().write(payload);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
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

	public OutputStream getOutput() {
		return this.output;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}

	public OutputStreamWriter getWriter() {
		return this.writer;
	}

	public void setWriter(OutputStreamWriter writer) {
		this.writer = writer;
	}

}