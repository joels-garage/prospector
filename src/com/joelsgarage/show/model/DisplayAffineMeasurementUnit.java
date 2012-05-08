/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.AffineMeasurementUnit;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * @author joel
 * 
 */
public class DisplayAffineMeasurementUnit extends DisplayMeasurementUnit {
	private DisplayStandardMeasurementUnit standardMeasurementUnit;

	public DisplayAffineMeasurementUnit(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setStandardMeasurementUnit(new DisplayStandardMeasurementUnit(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getStandardMeasurementUnit() != null) {
			getStandardMeasurementUnit().fetch(getInstance().getStandardMeasurementUnitKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return AffineMeasurementUnit.class;
	}

	@Override
	public AffineMeasurementUnit getInstance() {
		return (AffineMeasurementUnit) super.getInstance();
	}

	public DisplayStandardMeasurementUnit getStandardMeasurementUnit() {
		return this.standardMeasurementUnit;
	}

	public void setStandardMeasurementUnit(DisplayStandardMeasurementUnit standardMeasurementUnit) {
		this.standardMeasurementUnit = standardMeasurementUnit;
	}

}
