/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.nodes;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.util.FatalException;

/**
 * The lookup table contains one row per main row.
 * 
 * TODO: full inner join, i.e. multiple lookup table rows per main row, repeating the main row.
 * 
 * @author joel
 * 
 * @param <K>
 *            lookup key type
 * @param <T>
 *            input value type
 * @param <U>
 *            output value type
 * 
 */
public abstract class OneToManyLookupProcessNode<K, T, U> extends LookupProcessNode<K, T, U> {
	/** Log every N records */
	private int progressCount = 100;
	/** A convenience timer */
	private long time;

	public OneToManyLookupProcessNode(RecordReaderFactory<T> readerFactory, RecordWriter<U> writer,
		int inLimit, int outLimit) {
		super(readerFactory, writer, inLimit, outLimit);
	}

	/**
     * Read main records until handleRecord() has had enough, or we hit the input or output limit.
     * 
     * @throws FatalException
     * 
     * @see com.joelsgarage.dataprocessing.nodes.MultiReaderProcessNode#run()
     */
	@SuppressWarnings("nls")
	@Override
	public void run() throws FatalException {
		Logger.getLogger(OneToManyLookupProcessNode.class).info(
			"run with inlimit: " + getInLimit() + " outlimit: " + getOutLimit());
		// First populate the readers.
		start();

		RecordReader<T> mainReader = getReader(getMainConstraint());
		RecordWriter<U> writer = getWriter();
		if (mainReader == null) {
			Logger.getLogger(OneToManyLookupProcessNode.class).info("no main reader");
			return;
		}
		if (writer == null) {
			Logger.getLogger(OneToManyLookupProcessNode.class).info("bail");
			return;
		}
		try {
			mainReader.open();
		} catch (InitializationException ex) {
			ex.printStackTrace();
			return;
		}
		writer.open();

		T currentRecord = null;
		while (true) {
			// Logger.getLogger(OneToManyLookupProcessNode.class).info("in main loop");

			if (getInLimit() > 0 && getInCount() >= getInLimit()) {
				Logger.getLogger(OneToManyLookupProcessNode.class).info("input limit exceeded");

				break;
			}
			if (getOutLimit() > 0 && getOutCount() >= getOutLimit()) {
				Logger.getLogger(OneToManyLookupProcessNode.class).info("output limit exceeded");

				break;
			}
			currentRecord = input();
			if (currentRecord == null) {
				Logger.getLogger(OneToManyLookupProcessNode.class).info("input() returned null");
				break;
			}
			// lookup
			K foreignKey = extractForeignKey(currentRecord);
			T lookupRecord = get(foreignKey);
			// Logger.getLogger(OneToManyLookupProcessNode.class).info(
			// "working on: " + currentRecord.toString());

			if (!handleRecord(currentRecord, lookupRecord)) {
				Logger.getLogger(OneToManyLookupProcessNode.class).info("handleRecord() returned false");
				break;
			}
		}
		done();
		mainReader.close();
		writer.close();
	}

	/**
	 * Obtain a main table record from the reader. Updates the input record count.
	 * 
	 * @return the record, or null if there are no more records.
	 */
	protected T input() {
		RecordReader<T> inputReader = getReader(getMainConstraint());
		if (inputReader == null) {
			Logger.getLogger(OneToManyLookupProcessNode.class).error("no main reader!"); //$NON-NLS-1$
			return null;
		}
		T inputRecord = inputReader.read();
		if (inputRecord == null) {
			Logger.getLogger(OneToManyLookupProcessNode.class).info("end of input"); //$NON-NLS-1$
			return null;
		}
		// Logger.getLogger(OneToManyLookupProcessNode.class).info("got input"); //$NON-NLS-1$
		setInCount(getInCount() + 1);
		if (getProgressCount() > 0 && getInCount() % getProgressCount() == 0) {
			Logger.getLogger(ProcessNode.class).info("input records: " + getInCount()); //$NON-NLS-1$
			long newTime = System.currentTimeMillis();
			Logger.getLogger(ProcessNode.class).info(
				"duration: " + String.valueOf(((double) newTime - (double) getTime()) / 1000)); //$NON-NLS-1$
			setTime(newTime);
		}
		return inputRecord;
	}

	/**
	 * Called once for each record. return true if you want more records.
	 */
	protected abstract boolean handleRecord(T mainRecord, T lookupRecord);

	public int getProgressCount() {
		return this.progressCount;
	}

	public void setProgressCount(int progressCount) {
		this.progressCount = progressCount;
	}

	public long getTime() {
		return this.time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
