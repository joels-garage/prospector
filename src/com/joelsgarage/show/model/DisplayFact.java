/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import com.joelsgarage.prospector.client.model.Fact;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * @author joel
 */
public class DisplayFact extends DisplayModelEntity {
	/** The reified subject */
	private DisplayIndividual subject;
	/** Note this value is never actually assigned */
	/** This crazy generic thing permits dynamic binding to this field */
	private DisplayProperty property;

	public DisplayFact(boolean dereference) {
		super(dereference);
		if (isDereference()) {
			setSubject(new DisplayIndividual(false));
			setProperty(new DisplayProperty(false));
		}
	}

	@Override
	public void dereference() {
		super.dereference();
		if (getInstance() == null)
			return;
		if (getSubject() != null) {
			getSubject().fetch(getInstance().getSubjectKey());
		}
		if (getProperty() != null) {
			getProperty().fetch(getInstance().getPropertyKey());
		}
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return Fact.class;
	}

	@Override
	public Fact getInstance() {
		return (Fact) super.getInstance();
	}

	public DisplayIndividual getSubject() {
		return this.subject;
	}

	public void setSubject(DisplayIndividual subject) {
		this.subject = subject;
	}

	public DisplayProperty getProperty() {
		return this.property;
	}

	public void setProperty(DisplayProperty property) {
		this.property = property;
	}
}
