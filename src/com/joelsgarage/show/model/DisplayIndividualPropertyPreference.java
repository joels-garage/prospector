/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.IndividualPropertyPreference;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * @author joel
 * 
 */
public class DisplayIndividualPropertyPreference extends DisplayPropertyPreference {
	private DisplayIndividual primaryIndividual;
	private DisplayIndividual secondaryIndividual;

	public DisplayIndividualPropertyPreference(boolean dereference) {
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
		return IndividualPropertyPreference.class;
	}

	@Override
	public IndividualPropertyPreference getInstance() {
		return (IndividualPropertyPreference) super.getInstance();
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
