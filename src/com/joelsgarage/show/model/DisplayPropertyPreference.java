/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.PropertyPreference;

/**
 * @author joel
 * 
 */
public class DisplayPropertyPreference extends DisplayPreference {
	private DisplayProperty property;

	public DisplayPropertyPreference(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setProperty(new DisplayProperty(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getProperty() != null) {
			getProperty().fetch(getInstance().getPropertyKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return PropertyPreference.class;
	}

	@Override
	public PropertyPreference getInstance() {
		return (PropertyPreference) super.getInstance();
	}

	//

	public DisplayProperty getProperty() {
		return this.property;
	}

	public void setProperty(DisplayProperty property) {
		this.property = property;
	}
}
