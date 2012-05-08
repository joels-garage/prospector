/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.IndividualPropertyUtility;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * @author joel
 * 
 */
public class DisplayIndividualPropertyUtility extends DisplayPropertyPreference {
	private DisplayIndividual individual;

	public DisplayIndividualPropertyUtility(boolean dereference) {
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
		return IndividualPropertyUtility.class;
	}

	@Override
	public IndividualPropertyUtility getInstance() {
		return (IndividualPropertyUtility) super.getInstance();
	}

	//

	public DisplayIndividual getIndividual() {
		return this.individual;
	}

	public void setIndividual(DisplayIndividual individual) {
		this.individual = individual;
	}
}
