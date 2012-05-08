/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.QuantityProperty;

/**
 * This class exists only because it exists in the model.
 * 
 * @author joel
 * 
 */
public class DisplayQuantityProperty extends DisplayProperty {
	private DisplayMeasurementQuantity measurementQuantity;
	private DisplayMeasurementUnit measurementUnit;

	public DisplayQuantityProperty(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setMeasurementQuantity(new DisplayMeasurementQuantity(false));
			setMeasurementUnit(new DisplayMeasurementUnit(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getMeasurementQuantity() != null) {
			getMeasurementQuantity().fetch(getInstance().getMeasurementQuantityKey());
		}
		if (getMeasurementUnit() != null) {
			getMeasurementUnit().fetch(getInstance().getMeasurementUnitKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return QuantityProperty.class;
	}

	@Override
	public QuantityProperty getInstance() {
		return (QuantityProperty) super.getInstance();
	}

	public DisplayMeasurementQuantity getMeasurementQuantity() {
		return this.measurementQuantity;
	}

	public void setMeasurementQuantity(DisplayMeasurementQuantity measurementQuantity) {
		this.measurementQuantity = measurementQuantity;
	}

	public DisplayMeasurementUnit getMeasurementUnit() {
		return this.measurementUnit;
	}

	public void setMeasurementUnit(DisplayMeasurementUnit measurementUnit) {
		this.measurementUnit = measurementUnit;
	}
}
