/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.PropertyWeight;

/**
 * @author joel
 * 
 */
public class DisplayPropertyWeight extends DisplayModelEntity {
	private DisplayStakeholder stakeholder;
	private DisplayProperty property;

	public DisplayPropertyWeight(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setStakeholder(new DisplayStakeholder(false));
			setProperty(new DisplayProperty(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getStakeholder() != null) {
			getStakeholder().fetch(getInstance().getStakeholderKey());
		}
		if (getProperty() != null) {
			getProperty().fetch(getInstance().getPropertyKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return PropertyWeight.class;
	}

	@Override
	public PropertyWeight getInstance() {
		return (PropertyWeight) super.getInstance();
	}

	//

	public DisplayStakeholder getStakeholder() {
		return this.stakeholder;
	}

	public void setStakeholder(DisplayStakeholder stakeholder) {
		this.stakeholder = stakeholder;
	}

	public DisplayProperty getProperty() {
		return this.property;
	}

	public void setProperty(DisplayProperty property) {
		this.property = property;
	}
}
