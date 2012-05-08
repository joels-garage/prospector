/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.nodes;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * Copy input strings to output strings. For testing.
 * 
 * @author joel
 * 
 */
public class StringPassThroughProcessNode extends ProcessNode<String, String> {
	public StringPassThroughProcessNode(RecordReader<String> reader, RecordWriter<String> writer,
		int inLimit, int outLimit) {
		super(reader, writer, inLimit, outLimit);
	}

	/**
	 * @see ProcessNode#handleRecord(Object)
	 */
	@Override
	protected boolean handleRecord(String record) {
		output(record);
		return true;
	}
}
