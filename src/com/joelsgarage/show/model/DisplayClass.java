/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * @author joel
 * 
 */
public class DisplayClass extends DisplayModelEntity {
	public DisplayClass(boolean dereference) {
		super(dereference);
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return com.joelsgarage.model.Class.class;
	}

	@Override
	public com.joelsgarage.model.Class getInstance() {
		return (com.joelsgarage.model.Class) super.getInstance();
	}
}
