/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.Decision;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * @author joel
 * 
 */
public class DisplayDecision extends DisplayModelEntity {
	private DisplayClass clas;

	public DisplayDecision(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setClas(new DisplayClass(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getClas() != null) {
			getClas().fetch(getInstance().getClassKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return Decision.class;
	}

	@Override
	public Decision getInstance() {
		return (Decision) super.getInstance();
	}

	//

	public DisplayClass getClas() {
		return this.clas;
	}

	public void setClas(DisplayClass clas) {
		this.clas = clas;
	}

}
