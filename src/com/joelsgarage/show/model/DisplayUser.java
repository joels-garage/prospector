/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.User;

/**
 * @author joel
 */
public class DisplayUser extends DisplayModelEntity {
	public DisplayUser(boolean dereference) {
		super(dereference);
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return User.class;
	}

	@Override
	public User getInstance() {
		return (User) super.getInstance();
	}
}
