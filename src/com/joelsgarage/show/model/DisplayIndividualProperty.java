/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.IndividualProperty;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * 
 * @author joel
 * 
 */
public class DisplayIndividualProperty extends DisplayProperty {
	private DisplayClass rangeClass;

	public DisplayIndividualProperty(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setRangeClass(new DisplayClass(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getRangeClass() != null) {
			getRangeClass().fetch(getInstance().getRangeClassKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return IndividualProperty.class;
	}

	@Override
	public IndividualProperty getInstance() {
		return (IndividualProperty) super.getInstance();
	}

	public DisplayClass getRangeClass() {
		return this.rangeClass;
	}

	public void setRangeClass(DisplayClass rangeClass) {
		this.rangeClass = rangeClass;
	}
}
