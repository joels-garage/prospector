/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.model.ClassMember;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * 
 * @author joel
 * 
 */
public class DisplayClassMember extends DisplayModelEntity {
	private DisplayIndividual individual;
	private DisplayClass clas;

	public DisplayClassMember(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setIndividual(new DisplayIndividual(false));
			setClas(new DisplayClass(false));
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
		if (getClas() != null) {
			getClas().fetch(getInstance().getClassKey());
		}
	}

	/**
	 * @see ModelEntityFetcher#getPersistentClass()
	 */
	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return ClassMember.class;
	}

	@Override
	public ClassMember getInstance() {
		return (ClassMember) super.getInstance();
	}

	public DisplayIndividual getIndividual() {
		return this.individual;
	}

	public void setIndividual(DisplayIndividual individual) {
		this.individual = individual;
	}

	public DisplayClass getClas() {
		return this.clas;
	}

	public void setClas(DisplayClass clas) {
		this.clas = clas;
	}
}
