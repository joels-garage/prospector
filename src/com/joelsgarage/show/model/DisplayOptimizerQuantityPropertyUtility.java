/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.OptimizerQuantityPropertyUtility;

/**
 * @author joel
 * 
 */
public class DisplayOptimizerQuantityPropertyUtility extends DisplayQuantityPropertyUtility {
	// TODO: add unitsynonym?
	private DisplayMeasurementUnit measurementUnit;

	public DisplayOptimizerQuantityPropertyUtility(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setMeasurementUnit(new DisplayMeasurementUnit(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getMeasurementUnit() != null) {
			getMeasurementUnit().fetch(getInstance().getMeasurementUnitKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return OptimizerQuantityPropertyUtility.class;
	}

	@Override
	public OptimizerQuantityPropertyUtility getInstance() {
		return (OptimizerQuantityPropertyUtility) super.getInstance();
	}

	//

	public DisplayMeasurementUnit getMeasurementUnit() {
		return this.measurementUnit;
	}

	public void setMeasurementUnit(DisplayMeasurementUnit measurementUnit) {
		this.measurementUnit = measurementUnit;
	}
}
