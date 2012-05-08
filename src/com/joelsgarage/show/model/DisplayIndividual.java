/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.Individual;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * Individual has no dereferencing.
 * 
 * @author joel
 * 
 */
public class DisplayIndividual extends DisplayModelEntity {
	// private List<Fact> subjectfact;
	// private List<Fact> objectfact;

	// this seems crazy, duplicative of what's in the annotations.
	// try it differently, not a zillion DisplayX classes, but one, with
	// a map-of-maps structure.

	public DisplayIndividual(boolean dereference) {
		super(dereference);
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return Individual.class;
	}

	@Override
	public Individual getInstance() {
		return (Individual) super.getInstance();
	}
}
