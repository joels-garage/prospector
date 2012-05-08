/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.IndividualPreference;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * 
 * @author joel
 * 
 */
public  class DisplayIndividualPreference extends DisplayPreference {
	private DisplayIndividual primaryIndividual;
	private DisplayIndividual secondaryIndividual;

	public DisplayIndividualPreference(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setPrimaryIndividual(new DisplayIndividual(false));
			setSecondaryIndividual(new DisplayIndividual(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getPrimaryIndividual() != null) {
			getPrimaryIndividual().fetch(getInstance().getPrimaryIndividualKey());
		}
		if (getSecondaryIndividual() != null) {
			getSecondaryIndividual().fetch(getInstance().getSecondaryIndividualKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return IndividualPreference.class;
	}

	@Override
	public IndividualPreference getInstance() {
		return (IndividualPreference) super.getInstance();
	}

	//

	public DisplayIndividual getPrimaryIndividual() {
		return this.primaryIndividual;
	}

	public void setPrimaryIndividual(DisplayIndividual primaryIndividual) {
		this.primaryIndividual = primaryIndividual;
	}

	public DisplayIndividual getSecondaryIndividual() {
		return this.secondaryIndividual;
	}

	public void setSecondaryIndividual(DisplayIndividual secondaryIndividual) {
		this.secondaryIndividual = secondaryIndividual;
	}

}
