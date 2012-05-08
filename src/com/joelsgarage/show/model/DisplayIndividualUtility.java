/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.IndividualUtility;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * @author joel
 * 
 */
public  class DisplayIndividualUtility extends DisplayPreference {
	private DisplayIndividual individual;

	public DisplayIndividualUtility(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setIndividual(new DisplayIndividual(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getIndividual() != null) {
			getIndividual().fetch(getInstance().getIndividualKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return IndividualUtility.class;
	}

	@Override
	public IndividualUtility getInstance() {
		return (IndividualUtility) super.getInstance();
	}

	//

	public DisplayIndividual getIndividual() {
		return this.individual;
	}

	public void setIndividual(DisplayIndividual individual) {
		this.individual = individual;
	}
}
