/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.Property;

/**
 * @author joel
 * 
 */
public class DisplayProperty extends DisplayModelEntity {
	private DisplayClass domainClass;

	public DisplayProperty(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setDomainClass(new DisplayClass(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getDomainClass() != null) {
			getDomainClass().fetch(getInstance().getDomainClassKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return Property.class;
	}

	@Override
	public Property getInstance() {
		return (Property) super.getInstance();
	}

	public DisplayClass getDomainClass() {
		return this.domainClass;
	}

	public void setDomainClass(DisplayClass domainClass) {
		this.domainClass = domainClass;
	}
}
