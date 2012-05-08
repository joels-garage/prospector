/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.ArrayList;
import java.util.List;

import com.joelsgarage.dataprocessing.nodes.StringPassThroughProcessNode;
import com.joelsgarage.dataprocessing.readers.ListRecordReader;
import com.joelsgarage.dataprocessing.writers.ListRecordWriter;

import junit.framework.TestCase;

/**
 * Basic dataprocessing test, on strings.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ProcessNodeTest extends TestCase {
	private RecordReader<String> reader;
	private ListRecordWriter<String> writer;
	private ProcessNode<String, String> processNode;
	private List<String> input = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("foo0");
			add("foo1");
			add("foo2");
			add("foo3");
			add("foo4");
			add("foo5");
			add("foo6");
			add("foo7");
			add("foo8");
			add("foo9");
		}
	};

	int recordCount = 0;

	@Override
	protected void setUp() {
		setReader(new ListRecordReader<String>(getInput()));
		setWriter(new ListRecordWriter<String>());
		setProcessNode(new StringPassThroughProcessNode(getReader(), getWriter(), 0, 0));
	}

	/**
	 * Verifies construction, i.e. doesn't do anything.
	 */
	public void testNothing() {
		assertNotNull(getProcessNode());
	}

	/**
	 * Verify duplication of the input on the output.
	 */
	public void testPassThrough() {
		this.getProcessNode().run();

		List<String> output = getWriter().getList();
		// we called output four times.
		assertEquals(10, output.size());
		// It's a pass-through; these lists should have identical contents.
		assertEquals(output, getInput());
	}

	/**
	 * Verify the function of the "recordLimit" argument.
	 */
	public void testRecordLimit() {
		int recordLimit = 7;
		setProcessNode(new StringPassThroughProcessNode(getReader(), getWriter(), recordLimit, 0));
		getProcessNode().run();
		assertEquals(recordLimit, getProcessNode().getInCount());
		assertEquals(recordLimit, getWriter().getList().size());
	}

	// Accessors

	public int getRecordCount() {
		return this.recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public RecordReader<String> getReader() {
		return this.reader;
	}

	public void setReader(RecordReader<String> reader) {
		this.reader = reader;
	}

	public ProcessNode<String, String> getProcessNode() {
		return this.processNode;
	}

	public void setProcessNode(ProcessNode<String, String> processNode) {
		this.processNode = processNode;
	}

	public ListRecordWriter<String> getWriter() {
		return this.writer;
	}

	public void setWriter(ListRecordWriter<String> writer) {
		this.writer = writer;
	}

	public List<String> getInput() {
		return this.input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}
}
