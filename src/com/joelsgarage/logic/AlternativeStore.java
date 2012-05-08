/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.List;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualFact;
import com.joelsgarage.model.IndividualPreference;
import com.joelsgarage.model.IndividualPropertyPreference;
import com.joelsgarage.model.IndividualPropertyUtility;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.MaximizerQuantityPropertyUtility;
import com.joelsgarage.model.MeasurementQuantity;
import com.joelsgarage.model.MeasurementUnit;
import com.joelsgarage.model.MinimizerQuantityPropertyUtility;
import com.joelsgarage.model.OptimizerQuantityPropertyUtility;
import com.joelsgarage.model.PropertyWeight;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.Stakeholder;

/**
 * Supplies all the data needed for ranking.
 * 
 * @author joel
 * 
 */
public interface AlternativeStore {
	/**
	 * Fetch all the decisions.
	 * 
	 * TODO: restrict this list somehow.
	 * 
	 * @return
	 */
	public List<Decision> getDecisions(int pageSize);

	/**
	 * For the specified decision, fetch all the Individual records that qualify as alternatives,
	 * i.e. are members of the decision class.
	 * 
	 * In general, this is the whole database, so, paginate it.
	 * 
	 * In general
	 * 
	 * @param d
	 * @param pageSize
	 *            for now, just limit the result set size. this is the WRONG thing to do in general.
	 *            in general, it should be a single scoring scan through a set of individuals.
	 * @return
	 */
	public List<Individual> getAlternatives(Decision d, int pageSize);

	public List<Individual> getAlternatives(ExternalKey decisionKey, int pageSize);

	public Individual getIndividual(ExternalKey individualKey);

	/**
	 * Fetch the IndividualPreference records that mention, as primary or secondary, the specified
	 * Individual, and are due to a Stakeholder of the specified Decision.
	 * 
	 * @param i
	 * @return
	 */
	public List<IndividualPreference> getIndividualPreferences(Decision d, Individual i,
		int pageSize);

	/**
	 * Fetch the Stakeholders for this decision.
	 * 
	 * @param d
	 * @return
	 */
	public List<Stakeholder> getStakeholders(Decision d, int pageSize);

	/**
	 * Fetch the preferences stated by e that reference both i and j. This returns relative
	 * preferences only, not utilities.
	 * 
	 * The overloading of "preference" to include both types is annoying.
	 * 
	 * @param e
	 * @param i
	 * @param j
	 * @return
	 */
	public List<IndividualPreference> getIndividualPreferences(final Stakeholder e,
		final Individual i, final Individual j, int pageSize);

	public List<IndividualPreference> getIndividualPreferences(ExternalKey eKey, ExternalKey iKey,
		ExternalKey jKey, int pageSize);

	/** Get all the IndividualPreferences involving this Stakeholder */
	public List<IndividualPreference> getAllIndividualPreferences(ExternalKey stakeholderKey,
		int pageSize);

	/**
	 * Fetch stated utilities.
	 * 
	 * @param e
	 * @param i
	 * @param j
	 * @return
	 */
	public List<IndividualUtility> getIndividualUtilities(final Stakeholder e, final Individual i,
		int pageSize);

	/**
	 * Fetch all facts about the specified subject.
	 * 
	 * TODO: recast this somehow?
	 * 
	 * @param subject
	 * @return
	 */
	public List<IndividualFact> getIndividualFacts(Individual subject, int pageSize);

	public List<IndividualFact> getIndividualFacts(ExternalKey subjectKey, int pageSize);

	public List<QuantityFact> getQuantityFacts(Individual subject, int pageSize);

	public List<QuantityFact> getQuantityFacts(ExternalKey subjectKey, int pageSize);

	/**
	 * Select the quantityFacts for the specified property, and process them using the supplied
	 * processnode. This method resets the processnode's reader.
	 * 
	 * @param propertyKey
	 *            the property whose range is desired
	 * @param processNode
	 *            the visitor
	 */
	public void visitQuantityFactsForProperty(ExternalKey propertyKey,
		ProcessNode<QuantityFact, ?> processNode);

	/**
	 * Fetch ALL the individual property preferences for this stakeholder.
	 * 
	 * @param e
	 * @return
	 */
	public List<IndividualPropertyPreference> getIndividualPropertyPreferences(Stakeholder e,
		int pageSize);

	public List<IndividualPropertyPreference> getIndividualPropertyPreferences(
		ExternalKey stakeholderKey, int pageSize);

	/** Get the individual property preferences for the specified stakeholder and property */
	public List<IndividualPropertyPreference> getIndividualPropertyPreferencesForProperty(
		ExternalKey stakeholderKey, ExternalKey propertyKey, int pageSize);

	public List<PropertyWeight> getPropertyWeights(Stakeholder e, int pageSize);

	public List<PropertyWeight> getPropertyWeights(ExternalKey stakeholderKey, int pageSize);

	public List<PropertyWeight> getPropertyWeightsForProperty(ExternalKey stakeholderKey,
		ExternalKey propertyKey, int pageSize);

	// PREFRENCE LIST FETCHERS

	public List<OptimizerQuantityPropertyUtility> getOptimizerQuantityPropertyUtilityList(
		Stakeholder e, int pageSize);

	public List<OptimizerQuantityPropertyUtility> getOptimizerQuantityPropertyUtilityList(
		ExternalKey stakeholderKey, int pageSize);

	public List<MinimizerQuantityPropertyUtility> getMinimizerQuantityPropertyUtilityList(
		Stakeholder e, int pageSize);

	public List<MinimizerQuantityPropertyUtility> getMinimizerQuantityPropertyUtilityList(
		ExternalKey stakeholderKey, int pageSize);

	public List<MaximizerQuantityPropertyUtility> getMaximizerQuantityPropertyUtilityList(
		Stakeholder e, int pageSize);

	public List<MaximizerQuantityPropertyUtility> getMaximizerQuantityPropertyUtilityList(
		ExternalKey stakeholderKey, int pageSize);

	public List<IndividualPropertyUtility> getIndividualPropertyUtilityList(Stakeholder e,
		int pageSize);

	public List<IndividualPropertyUtility> getIndividualPropertyUtilityList(
		ExternalKey stakeholderKey, int pageSize);

	public MeasurementUnit getMeasurementUnit(ExternalKey measurementUnitKey);

	public List<MeasurementUnit> getMeasurementUnitList(int pageSize);

	public MeasurementQuantity getMeasurementQuantity(ExternalKey measurementQuantityKey);
}
