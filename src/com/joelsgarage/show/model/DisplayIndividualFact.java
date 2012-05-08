/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.IndividualFact;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * This idea is to write behaviorless beans to represent what is actually displayed, since the
 * domain model involves joins, and I don't want the display logic to know about them.
 * 
 * So this idea means things like the property associated with a fact, or a page of facts associated
 * with an individual. So pagination somehow is handled here, externally I guess.
 * 
 * @author joel
 * 
 */
public class DisplayIndividualFact extends DisplayFact {
	private DisplayIndividual object;

	public DisplayIndividualFact(boolean dereference) {
		super(dereference);

	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getObject() != null) {
			getObject().fetch(getInstance().getObjectKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return IndividualFact.class;
	}

	@Override
	public IndividualFact getInstance() {
		return (IndividualFact) super.getInstance();
	}

	public DisplayIndividual getObject() {
		return this.object;
	}

	public void setObject(DisplayIndividual object) {
		this.object = object;
	}

}
