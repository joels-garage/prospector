/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.List;

import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualFact;
import com.joelsgarage.model.IndividualPreference;
import com.joelsgarage.model.IndividualPropertyPreference;
import com.joelsgarage.model.IndividualPropertyUtility;
import com.joelsgarage.model.MaximizerQuantityPropertyUtility;
import com.joelsgarage.model.MinimizerQuantityPropertyUtility;
import com.joelsgarage.model.OptimizerQuantityPropertyUtility;
import com.joelsgarage.model.PropertyWeight;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.Stakeholder;

/**
 * Base implementation of AlternativeStore interface.
 * 
 * @author joel
 * 
 */
public abstract class AlternativeStoreImpl implements AlternativeStore {
	@Override
	public final List<IndividualPreference> getIndividualPreferences(final Stakeholder e,
		final Individual i, final Individual j, int pageSize) {
		return getIndividualPreferences(e.makeKey(), i.makeKey(), j.makeKey(), pageSize);
	}

	@Override
	public final List<IndividualFact> getIndividualFacts(final Individual subject, int pageSize) {
		return getIndividualFacts(subject.makeKey(), pageSize);
	}

	@Override
	public final List<QuantityFact> getQuantityFacts(final Individual subject, int pageSize) {
		return getQuantityFacts(subject.makeKey(), pageSize);
	}

	@Override
	public final List<IndividualPropertyPreference> getIndividualPropertyPreferences(
		final Stakeholder e, int pageSize) {
		return getIndividualPropertyPreferences(e.makeKey(), pageSize);
	}

	@Override
	public final List<IndividualPropertyUtility> getIndividualPropertyUtilityList(
		final Stakeholder e, int pageSize) {
		return getIndividualPropertyUtilityList(e.makeKey(), pageSize);
	}

	@Override
	public final List<MaximizerQuantityPropertyUtility> getMaximizerQuantityPropertyUtilityList(
		final Stakeholder e, int pageSize) {
		return getMaximizerQuantityPropertyUtilityList(e.makeKey(), pageSize);
	}

	@Override
	public final List<MinimizerQuantityPropertyUtility> getMinimizerQuantityPropertyUtilityList(
		final Stakeholder e, int pageSize) {
		return getMinimizerQuantityPropertyUtilityList(e.makeKey(), pageSize);
	}

	@Override
	public final List<OptimizerQuantityPropertyUtility> getOptimizerQuantityPropertyUtilityList(
		final Stakeholder e, int pageSize) {
		return getOptimizerQuantityPropertyUtilityList(e.makeKey(), pageSize);
	}

	@Override
	public final List<PropertyWeight> getPropertyWeights(final Stakeholder e, int pageSize) {
		return getPropertyWeights(e.makeKey(), pageSize);
	}
}
