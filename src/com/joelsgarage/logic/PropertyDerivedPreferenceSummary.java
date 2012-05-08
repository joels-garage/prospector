/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.List;

import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualPreference;
import com.joelsgarage.model.Stakeholder;

/**
 * Represents an IndividualPreference derived from properties of the individuals.
 * 
 * Construction involves some nontrivial state (e.g. stuff cached from the DB) and so is handled by
 * PropertyDerivedPreferenceSummaryFactory.
 * 
 * @author joel
 */
public class PropertyDerivedPreferenceSummary {
	/** The relevant stakeholder. */
	private Stakeholder e;
	/** One of the individuals at hand */
	private Individual i;
	/** The other individual */
	private Individual j;
	/** The sources for this summary */
	private List<PropertyDerivedIndividualPreference> rPList;
	/** The summary preference itself */
	private IndividualPreference r;
	/** Some debugging info */
	private List<String> reasons;

	public PropertyDerivedPreferenceSummary(final Stakeholder e, final Individual i,
		final Individual j, List<PropertyDerivedIndividualPreference> rPList,
		IndividualPreference r, List<String> reasons) {
		setE(e);
		setI(i);
		setJ(j);
		setRP(rPList);
		setR(r);
		setReasons(reasons);
	}

	// Accessors

	public Stakeholder getE() {
		return this.e;
	}

	public void setE(final Stakeholder e) {
		this.e = e;
	}

	public Individual getI() {
		return this.i;
	}

	public void setI(final Individual i) {
		this.i = i;
	}

	public Individual getJ() {
		return this.j;
	}

	public void setJ(final Individual j) {
		this.j = j;
	}

	public List<PropertyDerivedIndividualPreference> getRPList() {
		return this.rPList;
	}

	public void setRP(final List<PropertyDerivedIndividualPreference> rPList) {
		this.rPList = rPList;
	}

	public IndividualPreference getR() {
		return this.r;
	}

	public void setR(final IndividualPreference r) {
		this.r = r;
	}

	public List<String> getReasons() {
		return this.reasons;
	}

	protected void setReasons(List<String> reasons) {
		this.reasons = reasons;
	}

	protected void setRPList(List<PropertyDerivedIndividualPreference> list) {
		this.rPList = list;
	}

}
