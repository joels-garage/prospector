/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.UnitSynonym;

/**
 * @author joel
 * 
 */
public class DisplayUnitSynonym extends DisplayModelEntity {
	private DisplayMeasurementUnit measurementUnit;

	public DisplayUnitSynonym(boolean dereference) {
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
		return UnitSynonym.class;
	}

	@Override
	public UnitSynonym getInstance() {
		return (UnitSynonym) super.getInstance();
	}

	//

	public DisplayMeasurementUnit getMeasurementUnit() {
		return this.measurementUnit;
	}

	public void setMeasurementUnit(DisplayMeasurementUnit measurementUnit) {
		this.measurementUnit = measurementUnit;
	}

}
