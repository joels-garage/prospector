/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.StringProperty;

/**
 * @author joel
 */
public class DisplayStringProperty extends DisplayProperty {
	public DisplayStringProperty(boolean dereference) {
		super(dereference);
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return StringProperty.class;
	}

	@Override
	public StringProperty getInstance() {
		return (StringProperty) super.getInstance();
	}
}
