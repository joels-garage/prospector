/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.List;

import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.Stakeholder;

/**
 * NEW!
 * 
 * Represents an IndividualUtility derived from properties of the individuals.
 * 
 * It's just a container; see the PropertyDerivedUtilitySummaryFactory for details.
 * 
 * @author joel
 */
public class PropertyDerivedUtilitySummary {
	/** The relevant stakeholder. */
	private Stakeholder e;
	/** The individual at hand */
	private Individual i;
	/** The sources for this summary */
	private List<PropertyDerivedIndividualUtility> rPList;
	/** The summary preference itself */
	private IndividualUtility r;
	/** Some debugging info */
	private List<String> reasons;

	public PropertyDerivedUtilitySummary(final Stakeholder e, final Individual i,
		List<PropertyDerivedIndividualUtility> rPList, IndividualUtility r, List<String> reasons) {
		setE(e);
		setI(i);
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

	public List<PropertyDerivedIndividualUtility> getRPList() {
		return this.rPList;
	}

	public void setRP(final List<PropertyDerivedIndividualUtility> rPList) {
		this.rPList = rPList;
	}

	public IndividualUtility getR() {
		return this.r;
	}

	public void setR(final IndividualUtility r) {
		this.r = r;
	}

	public List<String> getReasons() {
		return this.reasons;
	}

	protected void setReasons(List<String> reasons) {
		this.reasons = reasons;
	}

	protected void setRPList(List<PropertyDerivedIndividualUtility> list) {
		this.rPList = list;
	}

}
