/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.QuantityPropertyUtility;

/**
 * This class exists only because it exists in the model.
 * 
 * @author joel
 * 
 */
public class DisplayQuantityPropertyUtility extends DisplayPropertyPreference {
	public DisplayQuantityPropertyUtility(boolean dereference) {
		super(dereference);
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return QuantityPropertyUtility.class;
	}

	@Override
	public QuantityPropertyUtility getInstance() {
		return (QuantityPropertyUtility) super.getInstance();
	}

}
