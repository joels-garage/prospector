/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.model.ClassPreference;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * @author joel
 * 
 */
public class DisplayClassPreference extends DisplayPreference {
	private DisplayClass primaryClass;
	private DisplayClass secondaryClass;

	public DisplayClassPreference(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setPrimaryClass(new DisplayClass(false));
			setSecondaryClass(new DisplayClass(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getPrimaryClass() != null) {
			getPrimaryClass().fetch(getInstance().getPrimaryClassKey());
		}
		if (getSecondaryClass() != null) {
			getSecondaryClass().fetch(getInstance().getSecondaryClassKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return ClassPreference.class;
	}

	@Override
	public ClassPreference getInstance() {
		return (ClassPreference) super.getInstance();
	}

	public DisplayClass getPrimaryClass() {
		return this.primaryClass;
	}

	public void setPrimaryClass(DisplayClass primaryClass) {
		this.primaryClass = primaryClass;
	}

	public DisplayClass getSecondaryClass() {
		return this.secondaryClass;
	}

	public void setSecondaryClass(DisplayClass secondaryClass) {
		this.secondaryClass = secondaryClass;
	}
}
