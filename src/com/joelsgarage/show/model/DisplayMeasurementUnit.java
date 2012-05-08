/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.MeasurementUnit;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * This class exists only to mirror the model hierarchy.
 * 
 * @author joel
 * 
 */
public class DisplayMeasurementUnit extends DisplayModelEntity {
	public DisplayMeasurementUnit(boolean dereference) {
		super(dereference);
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return MeasurementUnit.class;
	}

	@Override
	public MeasurementUnit getInstance() {
		return (MeasurementUnit) super.getInstance();
	}
}
