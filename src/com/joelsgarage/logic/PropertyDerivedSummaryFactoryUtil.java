/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.writers.SingleRecordWriter;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.MeasurementUnit;
import com.joelsgarage.model.PropertyPreference;
import com.joelsgarage.model.PropertyWeight;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.QuantityFactUtil;

/**
 * Extracted common stuff from the two factories.
 * 
 * TODO: remove extra code here.
 * 
 * @author joel
 */
public class PropertyDerivedSummaryFactoryUtil {
	/** Our interface to the data */
	final private AlternativeStore store;
	/** All the measurement units that exist. Used for unit conversions. */
	final private List<MeasurementUnit> measurementUnitList;
	/** Does the conversions */
	final private QuantityFactUtil quantityFactUtil;
	/**
	 * Property range cache. It's static; maybe would be better to expand the lifecycle of this
	 * factory object. It's not like that at the moment in order to get pageSize in there.
	 */
	private static final Map<ExternalKey, RangeBounds> propertyRanges = new HashMap<ExternalKey, RangeBounds>();
	private int pageSize;

	public PropertyDerivedSummaryFactoryUtil(final AlternativeStore store, int pageSize) {
		this.store = store;
		setPageSize(pageSize);
		this.measurementUnitList = getStore().getMeasurementUnitList(getPageSize());
		this.quantityFactUtil = new QuantityFactUtil(getMeasurementUnitList());
	}

	/** Caching producer for RangeBounds. */
	protected RangeBounds getRangeBoundsForProperty(ExternalKey propertyKey) {
		Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class)
			.info("getting range bounds"); //$NON-NLS-1$
		RangeBounds bounds = getPropertyRanges().get(propertyKey);
		if (bounds != null)
			return bounds;

		// make a new one
		SingleRecordWriter<RangeBounds> writer = new SingleRecordWriter<RangeBounds>();
		ProcessNode<QuantityFact, RangeBounds> processNode = new RangeBoundsProcessNode(null,
			writer, 0, 0, getQuantityFactUtil());
		// resets the reader depending on the store.
		getStore().visitQuantityFactsForProperty(propertyKey, processNode);
		getPropertyRanges().put(propertyKey, writer.getRecord());
		Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
			"done getting range bounds: min: " + writer.getRecord().getMin() + //$NON-NLS-1$
				" max: " + writer.getRecord().getMax()); //$NON-NLS-1$
		return writer.getRecord();
	}

	/** Find any weights that stakeholder e has for property p */
	protected double getWeight(final Stakeholder e, final PropertyPreference rPP) {
		// fetch any weights that may exist.
		// hopefully the store caches this list. ;-)
		final List<PropertyWeight> wList = getStore().getPropertyWeightsForProperty(e.makeKey(),
			rPP.getPropertyKey(), getPageSize());
		for (PropertyWeight w : wList) {
			// there shouldn't really be multiple values for the same property
			// but if there are, i guess we take the first one.
			return w.getValue().doubleValue();
		}
		return 0.5;
	}

	//

	public Map<ExternalKey, RangeBounds> getPropertyRanges() {
		return propertyRanges;
	}

	public AlternativeStore getStore() {
		return this.store;
	}

	public List<MeasurementUnit> getMeasurementUnitList() {
		return this.measurementUnitList;
	}

	public QuantityFactUtil getQuantityFactUtil() {
		return this.quantityFactUtil;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
