/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.lookup;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.nodes.OneToManyLookupProcessNode;

/**
 * Uses SomeDataType as the lookup table, and scans SomeJoinType. Concatenates the "data" strings of
 * each, and writes them to the output.
 * 
 * @author joel
 * 
 */
public class SomeJoinNode extends OneToManyLookupProcessNode<String, SomeRecordType, String> {

	public SomeJoinNode(RecordReaderFactory<SomeRecordType> readerFactory,
		RecordWriter<String> writer, int inLimit, int outLimit) {
		super(readerFactory, writer, inLimit, outLimit);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean handleRecord(SomeRecordType mainRecord, SomeRecordType lookupRecord) {
		if (!(mainRecord instanceof SomeJoinType))
			return false;
		if (!(lookupRecord instanceof SomeDataType))
			return false;
		output(mainRecord.getData() + lookupRecord.getData());
		return true;
	}

	@Override
	protected String extractForeignKey(SomeRecordType record) {
		if (!(record instanceof SomeJoinType))
			return null;
		return ((SomeJoinType) record).getForeignKey();
	}

	@Override
	protected String extractLookupKey(SomeRecordType record) {
		if (!(record instanceof SomeDataType))
			return null;
		return record.getPrimaryKey();
	}

	/** Ignored in the test */
	@Override
	public ReaderConstraint getLookupConstraint() {
		return new ReaderConstraint(SomeDataType.class);
	}

	/** Ignored in the test */
	@Override
	public ReaderConstraint getMainConstraint() {
		return new ReaderConstraint(SomeJoinType.class);
	}

	@Override
	protected void done() {
		// TODO Auto-generated method stub
	}

}
