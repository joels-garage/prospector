/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.util.QuantityFactUtil;

/**
 * Calculate the RangeBounds that span the input set. Produces output upon done().
 * 
 * @author joel
 * 
 */
public class RangeBoundsProcessNode extends ProcessNode<QuantityFact, RangeBounds> {
	private QuantityFactUtil util;
	private RangeBounds rangeBounds;

	/**
	 * Constructor
	 * 
	 * @param reader
	 *            from which we get QuantityFacts
	 * @param writer
	 *            we write one RangeBound record to this writer, when done() is called
	 * @param inLimit
	 *            leave this zero to get valid bounds
	 * @param outLimit
	 *            leave this zero to get valid bounds
	 * @param util
	 *            handles unit conversion
	 */
	public RangeBoundsProcessNode(RecordReader<QuantityFact> reader,
		RecordWriter<RangeBounds> writer, int inLimit, int outLimit, final QuantityFactUtil util) {
		super(reader, writer, inLimit, outLimit);
		setUtil(util);
		setRangeBounds(new RangeBounds(getUtil()));
	}

	/**
	 * 
	 */
	@SuppressWarnings("nls")
	@Override
	protected boolean handleRecord(QuantityFact record) {
		Logger.getLogger(RangeBoundsProcessNode.class).info(
			"got record " + record.makeKey().toString() + " for property "
				+ record.getPropertyKey().toString());
		if (!getRangeBounds().isValid())
			return false;
		getRangeBounds().update(record);
		if (!getRangeBounds().isValid())
			return false;
		return true;
	}

	/**
	 * Writes the completed bounds to the writer as a single record.
	 */
	@Override
	protected void done() {
		output(getRangeBounds());
	}

	public RangeBounds getRangeBounds() {
		return this.rangeBounds;
	}

	public void setRangeBounds(RangeBounds rangeBounds) {
		this.rangeBounds = rangeBounds;
	}

	public QuantityFactUtil getUtil() {
		return this.util;
	}

	public void setUtil(QuantityFactUtil util) {
		this.util = util;
	}

}
