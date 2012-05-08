/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualFact;
import com.joelsgarage.model.IndividualPropertyUtility;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.MaximizerQuantityPropertyUtility;
import com.joelsgarage.model.MinimizerQuantityPropertyUtility;
import com.joelsgarage.model.OptimizerQuantityPropertyUtility;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.QuantityFactUtil;

/**
 * NEW!
 * 
 * Factory for PropertyDerivedUtilitySummary.
 * 
 * Derives the summary as follows:
 * 
 * <pre>
 * make an empty list of IndividualPropertyUtility
 * 
 * find facts with i as subject.
 * find facts with j as subject.
 * find propertypreferences for e (for now, just individualpropertypreferences)
 * for each preference
 *   for each ifact
 *     for each jfact
 *       if ifact.propertyid = jfact.propertyid, (then they're comparable)
 *       and ifact.objectid = jfact.objectid, (then indifferent, irrespective of preference.)
 *       and ifact.propertyid = preference.propertyid (then the preference is relevant)
 *       and ifact.objectId = primary and jfact.objectId = secondary, or the reverse,
 *       then add the preference to the list.
 * 
 * start with a final preference value of indifference.
 * 
 * for each preference in the accumulated set of preferences,
 *   find the corresponding weight, if it exists.
 *   if it does, weight the preference.
 *   combine the weighted preference with the final value using the compromise operator.
 * 
 * The concrete types of PropertyPreference we should handle:
 * <ul>
 * <li>
 * IndividualPropertyPreference -- use eigensolver.
 * <li>
 * IndividualPropertyUtility -- utility for individual object A
 * <li>
 * MaximizerQuantityPropertyUtility -- utility == function of fact value
 * <li>
 * MinimizerQuantityPropertyUtility -- same
 * <li>
 * OptimizerQuantityPropertyUtility -- same
 * </ul>
 * </pre>
 * 
 * Combination is no longer via compromise operator, but hm, instead, hm, something else. Weighted
 * average?
 * 
 * @author joel
 * 
 */
public class PropertyDerivedUtilitySummaryFactory {
    private static final String N_NAMESPACE = "n"; //$NON-NLS-1$
    final private AlternativeStore store;
    private PropertyDerivedSummaryFactoryUtil factoryUtil;
    private int pageSize;

    public PropertyDerivedUtilitySummaryFactory(final AlternativeStore store, int pageSize) {
        this.store = store;
        setFactoryUtil(new PropertyDerivedSummaryFactoryUtil(store, pageSize));
        setPageSize(pageSize);
    }

    /**
     * If it's possible to derive an individual preference for i and j, from the available
     * PropertyPreferences, return the PropertyDerivedUtilitySummary embodying this derivation.
     * otherwise null.
     * 
     * @param factoryMap
     *            maps propertyKey => factory, for (maybe) IndividualFacts of i
     * @param e
     * @param i
     * @return
     * @throws FatalException
     *             if key population fails
     */
    public PropertyDerivedUtilitySummary newIfValid(
        final Map<ExternalKey, PreferenceDerivedIndividualPropertyUtilityFactory> factoryMap,
        final Stakeholder e, final Individual i) throws FatalException {
        Logger.getLogger(PropertyDerivedUtilitySummaryFactory.class).info(
            "new if valid for stakeholder: " + e.makeKey().toString()); //$NON-NLS-1$

        // These are the individual derived utilities that go into the calculation of the
        // overall individual utility.
        List<PropertyDerivedIndividualUtility> rPList =
            new ArrayList<PropertyDerivedIndividualUtility>();

        // First derive the utility based on individual property preferences. We need a factory per
        // property, but we don't know the properties except by scanning the property preferences.
        // So, scan, and construct a new factory when we find a new property.

        // this is all the IndividualPropertyUtility records, for all the possible properties and
        // all the possible subject and object Individual records.

        final List<IndividualPropertyUtility> iPUList =
            getStore().getIndividualPropertyUtilityList(e, getPageSize());

        // Scan the IndividualFact records for this Individual.
        // We take IndividualPropertyPreference and produce IndividualPropertyUtility with
        // the eigenvector method.
        // We take IndividualPropertyUtility directly.
        final List<IndividualFact> iIFacts = getStore().getIndividualFacts(i, getPageSize());
        List<String> reasons = new ArrayList<String>();
        for (final IndividualFact iIF : iIFacts) {
            Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                "ifact " + iIF.makeKey().toString()); //$NON-NLS-1$

            ExternalKey propertyKey = iIF.getPropertyKey();
            if (propertyKey == null) {
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "null property key."); //$NON-NLS-1$
                continue; // odd
            }

            // Can we produce a utility value derived from the preference data?
            PreferenceDerivedIndividualPropertyUtilityFactory factory = factoryMap.get(propertyKey);
            if (factory != null) {
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class)
                    .info("got factory."); //$NON-NLS-1$
                IndividualPropertyUtility derivedUtility = factory.newInstance(iIF.getObjectKey());
                if (derivedUtility != null) {
                    Logger.getLogger(PropertyDerivedUtilitySummaryFactory.class).info(
                        "got derived utility " + derivedUtility.toString()); //$NON-NLS-1$
                    IndividualPropertyPreferenceDerivedIndividualUtility wrapper = //
                        new IndividualPropertyPreferenceDerivedIndividualUtility(null, null,
                            derivedUtility.getValue(), derivedUtility, N_NAMESPACE);
                    rPList.add(wrapper);
                    reasons.add("Factory Derived for individual: " + //$NON-NLS-1$
                        iIF.getObjectKey().toString() + " value: " //$NON-NLS-1$
                        + String.format("%.2f", derivedUtility.getValue())); //$NON-NLS-1$
                } else {
                    Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                        "no derived utility"); //$NON-NLS-1$
                }
            } else {
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info("no factory "); //$NON-NLS-1$
            }

            // Can we produce a utility value directly from the property utility? That is,
            // is there an IndividualPropertyUtility record corresponding to the object of this
            // IndividualFact?
            // TODO: reconsider this scan
            for (IndividualPropertyUtility iPU : iPUList) {
                if (iPU.getPropertyKey().equals(iIF.getPropertyKey())
                    && iPU.getIndividualKey().equals(iIF.getObjectKey())) {
                    IndividualPropertyUtilityDerivedIndividualUtility wrapper =
                        new IndividualPropertyUtilityDerivedIndividualUtility(null, null, iPU
                            .getValue(), iPU, N_NAMESPACE);
                    rPList.add(wrapper);
                    reasons.add("Direct for individual: " + //$NON-NLS-1$
                        iIF.getObjectKey().toString() + " value: " //$NON-NLS-1$
                        + String.format("%.2f", iPU.getValue())); //$NON-NLS-1$
                }
            }
        }

        // These lists are all probably pretty short.
        final List<OptimizerQuantityPropertyUtility> optQPUList =
            getStore().getOptimizerQuantityPropertyUtilityList(e, getPageSize());
        final List<MaximizerQuantityPropertyUtility> maxQPUList =
            getStore().getMaximizerQuantityPropertyUtilityList(e, getPageSize());
        final List<MinimizerQuantityPropertyUtility> minQPUList =
            getStore().getMinimizerQuantityPropertyUtilityList(e, getPageSize());

        // Scan the QuantityFact records.
        // The three kinds of QuantityPropertyUtility translate directly into utility values
        // for these QuantityFacts.
        final List<QuantityFact> iQFacts = getStore().getQuantityFacts(i, getPageSize());
        for (final QuantityFact iQF : iQFacts) {
            Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                "ifact " + iQF.makeKey().toString()); //$NON-NLS-1$
            ExternalKey propertyKey = iQF.getPropertyKey();
            if (propertyKey == null)
                continue;

            Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                "it's a quantity fact"); //$NON-NLS-1$
            RangeBounds bounds = getFactoryUtil().getRangeBoundsForProperty(iQF.getPropertyKey());
            if (bounds == null)
                continue;

            // These are in standard units
            Double minRange = Double.valueOf(bounds.getMin());
            Double maxRange = Double.valueOf(bounds.getMax());
            if (minRange == null || maxRange == null)
                continue;

            // The value of the QuantityFact.
            Double iStdValue = getFactoryUtil().getQuantityFactUtil().getValue(iQF);

            if (iStdValue == null)
                continue;

            for (final MaximizerQuantityPropertyUtility pU : maxQPUList) {
                if (propertyKey.equals(pU.getPropertyKey())) {
                    try {
                        double iUtility =
                            QuantityFactUtil.interpolateUtility(minRange.doubleValue(), maxRange
                                .doubleValue(), pU.getMinUtility().doubleValue(), pU
                                .getPreferredUtility().doubleValue(), iStdValue.doubleValue());

                        QuantityPropertyUtilityDerivedIndividualUtility<MaximizerQuantityPropertyUtility> pUDIP =
                            new QuantityPropertyUtilityDerivedIndividualUtility<MaximizerQuantityPropertyUtility>(
                                null, pU.getPropertyKey(), Double.valueOf(iUtility), pU,
                                N_NAMESPACE);
                        rPList.add(pUDIP);

                        reasons.add("Max Quant: " + pU.makeKey().toString() + " value: " //$NON-NLS-1$//$NON-NLS-2$
                            + String.format("%.2f", pUDIP.getValue())); //$NON-NLS-1$
                        Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                            "Found"); //$NON-NLS-1$
                    } catch (QuantityFactUtil.BoundsException ex) {
                        continue;
                    }
                }
            }
            for (final MinimizerQuantityPropertyUtility pU : minQPUList) {
                if (propertyKey.equals(pU.getPropertyKey())) {
                    try {
                        double iUtility =
                            QuantityFactUtil.interpolateUtility(minRange.doubleValue(), maxRange
                                .doubleValue(), pU.getPreferredUtility().doubleValue(), pU
                                .getMinUtility().doubleValue(), iStdValue.doubleValue());

                        QuantityPropertyUtilityDerivedIndividualUtility<MinimizerQuantityPropertyUtility> pUDIP =
                            new QuantityPropertyUtilityDerivedIndividualUtility<MinimizerQuantityPropertyUtility>(
                                null, pU.getPropertyKey(), Double.valueOf(iUtility), pU,
                                N_NAMESPACE);

                        rPList.add(pUDIP);

                        reasons.add("Max Quant: " + pU.makeKey().toString() + " value: " //$NON-NLS-1$ //$NON-NLS-2$
                            + String.format("%.2f", pUDIP.getValue())); //$NON-NLS-1$
                        Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                            "Found"); //$NON-NLS-1$
                    } catch (QuantityFactUtil.BoundsException ex) {
                        continue;
                    }
                }
            }
            for (final OptimizerQuantityPropertyUtility pU : optQPUList) {
                if (propertyKey.equals(pU.getPropertyKey())) {
                    Double preferredStdValue = getFactoryUtil().getQuantityFactUtil().getValue(pU);
                    if (preferredStdValue == null) {
                        continue;
                    }
                    try {
                        double iUtility =
                            QuantityFactUtil.interpolateUtilityForOptimum(iStdValue, minRange,
                                preferredStdValue, maxRange, pU.getMinUtility(), pU
                                    .getPreferredUtility());

                        QuantityPropertyUtilityDerivedIndividualUtility<OptimizerQuantityPropertyUtility> pUDIP//
                        =
                            new QuantityPropertyUtilityDerivedIndividualUtility<OptimizerQuantityPropertyUtility>(
                                null, pU.getPropertyKey(), Double.valueOf(iUtility), pU,
                                N_NAMESPACE);
                        rPList.add(pUDIP);

                        reasons.add("Max Quant: " + pU.makeKey().toString() + " value: " //$NON-NLS-1$//$NON-NLS-2$
                            + String.format("%.2f", pUDIP.getValue())); //$NON-NLS-1$
                        Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                            "Found"); //$NON-NLS-1$
                    } catch (QuantityFactUtil.BoundsException ex) {
                        continue;
                    }
                }
            }
        }

        if (rPList.size() == 0) {
            Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                "No property-derived preferences"); //$NON-NLS-1$
            return null;
        }

        // Now we have a full rPList.
        // Do aggregation.
        // For now, just use a weighted average.
        // And for now, set all the weights the same, i.e. it's just the mean.

        // WeightingOperator wOp = new WeightingOperator();
        // CompromiseOperator cOp = new CompromiseOperator();

        double resultUtility = 0;

        for (PropertyDerivedIndividualUtility rP : rPList) {
            // double weightValue = getFactoryUtil().getWeight(e, rP);
            double propertyUtilityScore = rP.getValue().doubleValue();
            // weighting
            // propertyPreferenceScore = wOp.f(weightValue, propertyPreferenceScore);
            // combine with preferences derived from other properties
            // preferenceScore = cOp.f(preferenceScore, propertyPreferenceScore);
            resultUtility += propertyUtilityScore;
        }
        resultUtility = resultUtility / rPList.size();

        IndividualUtility rIP =
            new IndividualUtility(e.makeKey(), i.makeKey(), Double.valueOf(resultUtility),
                N_NAMESPACE);

        // ok, so we have enough to make one
        return new PropertyDerivedUtilitySummary(e, i, rPList, rIP, reasons);
    }

    //

    public AlternativeStore getStore() {
        return this.store;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public PropertyDerivedSummaryFactoryUtil getFactoryUtil() {
        return this.factoryUtil;
    }

    public void setFactoryUtil(PropertyDerivedSummaryFactoryUtil factoryUtil) {
        this.factoryUtil = factoryUtil;
    }
}
