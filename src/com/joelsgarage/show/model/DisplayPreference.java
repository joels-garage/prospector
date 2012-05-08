/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.Preference;

/**
 * 
 * @author joel
 * 
 */
public class DisplayPreference extends DisplayModelEntity {
	private DisplayStakeholder stakeholder;

	public DisplayPreference(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setStakeholder(new DisplayStakeholder(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getStakeholder() != null) {
			getStakeholder().fetch(getInstance().getStakeholderKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return Preference.class;
	}

	@Override
	public Preference getInstance() {
		return (Preference) super.getInstance();
	}

	//

	public DisplayStakeholder getStakeholder() {
		return this.stakeholder;
	}

	public void setStakeholder(DisplayStakeholder stakeholder) {
		this.stakeholder = stakeholder;
	}
}
