/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.MeasurementQuantity;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * 
 * @author joel
 * 
 */
public class DisplayMeasurementQuantity extends DisplayModelEntity {
	public DisplayMeasurementQuantity(boolean dereference) {
		super(dereference);
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return MeasurementQuantity.class;
	}

	@Override
	public MeasurementQuantity getInstance() {
		return (MeasurementQuantity) super.getInstance();
	}

}
