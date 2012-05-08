/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.StandardMeasurementUnit;

/**
 * This idea is to write behaviorless beans to represent what is actually displayed, since the
 * domain model involves joins, and I don't want the display logic to know about them.
 * 
 * So this idea means things like the property associated with a fact, or a page of facts associated
 * with an individual. So pagination somehow is handled here, externally I guess.
 * 
 * @author joel
 * 
 */
public class DisplayStandardMeasurementUnit extends DisplayMeasurementUnit {
	private DisplayMeasurementQuantity measurementQuantity;

	public DisplayStandardMeasurementUnit(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setMeasurementQuantity(new DisplayMeasurementQuantity(false));
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
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return StandardMeasurementUnit.class;
	}

	@Override
	public StandardMeasurementUnit getInstance() {
		return (StandardMeasurementUnit) super.getInstance();
	}

	//

	public DisplayMeasurementQuantity getMeasurementQuantity() {
		return this.measurementQuantity;
	}

	public void setMeasurementQuantity(DisplayMeasurementQuantity measurementQuantity) {
		this.measurementQuantity = measurementQuantity;
	}

}
