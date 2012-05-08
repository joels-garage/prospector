/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.Stakeholder;

/**
 * @author joel
 * 
 */
public class DisplayStakeholder extends DisplayModelEntity {
	private DisplayDecision decision;
	private DisplayUser user;

	public DisplayStakeholder(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setDecision(new DisplayDecision(false));
			setUser(new DisplayUser(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getDecision() != null) {
			getDecision().fetch(getInstance().getDecisionKey());
		}
		if (getUser() != null) {
			getUser().fetch(getInstance().getUserKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return Stakeholder.class;
	}

	@Override
	public Stakeholder getInstance() {
		return (Stakeholder) super.getInstance();
	}

	//

	public DisplayDecision getDecision() {
		return this.decision;
	}

	public void setDecision(DisplayDecision decision) {
		this.decision = decision;
	}

	public DisplayUser getUser() {
		return this.user;
	}

	public void setUser(DisplayUser user) {
		this.user = user;
	}

}
