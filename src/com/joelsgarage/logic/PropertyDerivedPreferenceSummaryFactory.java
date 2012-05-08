/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualFact;
import com.joelsgarage.model.IndividualPreference;
import com.joelsgarage.model.IndividualPropertyPreference;
import com.joelsgarage.model.IndividualPropertyUtility;
import com.joelsgarage.model.MaximizerQuantityPropertyUtility;
import com.joelsgarage.model.MinimizerQuantityPropertyUtility;
import com.joelsgarage.model.OptimizerQuantityPropertyUtility;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.Membership;
import com.joelsgarage.util.QuantityFactUtil;

/**
 * Factory for PropertyDerivedPreferenceSummary.
 * 
 * Sits in between the AlternativeStore (which does data access and nothing else) and the
 * constructor, and serves to cache often-used data, e.g. property ranges.
 * 
 * Derives the summary as follows:
 * 
 * <pre>
 * make an empty list of individualpropertypreferences
 * 
 * find facts with i as subject.
 * find facts with j as subject.
 * find propertypreferences for e (for now, just individualpropertypreferences)
 * for each preference
 * for each ifact 
 * for each jfact 
 * if ifact.propertyid = jfact.propertyid, (then they're comparable)
 * if ifact.objectid = jfact.objectid, (then indifferent, irrespective of preference.)
 * if ifact.propertyid = preference.propertyid (then the preference is relevant)
 * if ifact.objectId = primary and jfact.objectId = secondary, or the reverse,
 *  then add the preference to the list.
 * 
 * start with a final preference value of indifference.
 * 
 * for each preference in the accumulated set of preferences,
 * find the corresponding weight, if it exists.  if it does, weight the preference.
 * combine the weighted preference with the final value using the compromise operator.
 * 
 * TODO: do something with property utilities.  for now, ignore them.
 * 
 * The concrete types of PropertyPreference we should handle:
 * <ul>
 * <li>
 * IndividualPropertyPreference -- prefer individual object A over B
 * <li>
 * IndividualPropertyUtility -- utility for individual object A
 * <li>
 * MaximizerQuantityPropertyUtility -- prefer greater quantity
 * <li>
 * MinimizerQuantityPropertyUtility -- prefer lesser quantity
 * <li>
 * OptimizerMeasurementPropertyUtility -- prefer an optimum point, with a unit
 * <li>
 * OptimizerQuantityPropertyUtility -- prefer an optimum point, for &quot;counts&quot;
 * </ul>
 * </pre>
 * 
 * @author joel
 * 
 */
public class PropertyDerivedPreferenceSummaryFactory {
    private static final String N_NAMESPACE = "namespace"; //$NON-NLS-1$
    final private AlternativeStore store;
    private PropertyDerivedSummaryFactoryUtil factoryUtil;
    private int pageSize;

    public PropertyDerivedPreferenceSummaryFactory(final AlternativeStore store, int pageSize) {
        this.store = store;
        setFactoryUtil(new PropertyDerivedSummaryFactoryUtil(store, pageSize));
        setPageSize(pageSize);
    }

    /**
     * If it's possible to derive an individual preference for i and j, from the available
     * PropertyPreferences, return the PropertyDerivedPreferenceSummary embodying this derivation.
     * otherwise null.
     * 
     * @param store
     * @param e
     * @param iIndividual
     * @param jIndividual
     * @return
     * @throws FatalException
     *             if key population fails
     */
    @SuppressWarnings("nls")
    public PropertyDerivedPreferenceSummary newIfValid(final Stakeholder e,
        final Individual iIndividual, final Individual jIndividual) throws FatalException {
        Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
            "new if valid for stakeholder: " + e.makeKey().toString());

        // These are the individual derived preferences that go into the calculation of the
        // individual preference.
        List<PropertyDerivedIndividualPreference> rPList =
            new ArrayList<PropertyDerivedIndividualPreference>();

        double preferenceScore = 0.5;

        // this is all the preferences, for all the possible properties and all the possible
        // subject and object individuals
        final List<IndividualPropertyPreference> iPPList =
            getStore().getIndividualPropertyPreferences(e, getPageSize());
        Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
            "got " + String.valueOf(iPPList.size()) + " IndividualPropertyPreferences");
        final List<IndividualPropertyUtility> iPUList =
            getStore().getIndividualPropertyUtilityList(e, getPageSize());
        Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
            "got " + String.valueOf(iPUList.size()) + " IndividualPropertyUtilities");

        // crap this contains both q and m.

        final List<OptimizerQuantityPropertyUtility> optQPUList =
            getStore().getOptimizerQuantityPropertyUtilityList(e, getPageSize());
        Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
            "got " + String.valueOf(optQPUList.size()) + " OQPUs");

        // final List<OptimizerMeasurementPropertyUtility> optMPUList = getStore()
        // .getOptimizerMeasurementPropertyUtilityList(e);

        // TODO: redo this as a merge of sorted recordreaders.

        final List<IndividualFact> iIFacts =
            getStore().getIndividualFacts(iIndividual, getPageSize());
        final List<IndividualFact> jIFacts =
            getStore().getIndividualFacts(jIndividual, getPageSize());

        List<String> reasons = new ArrayList<String>();

        for (final IndividualFact iIF : iIFacts) {
            for (final IndividualFact jIF : jIFacts) {
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "ifact " + iIF.makeKey().toString()); //$NON-NLS-1$
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "jfact " + jIF.makeKey().toString()); //$NON-NLS-1$

                if (!iIF.getPropertyKey().equals(jIF.getPropertyKey()))
                    continue;

                ExternalKey propertyKey = iIF.getPropertyKey();
                if (propertyKey == null)
                    continue;

                // they're comparable
                if (iIF.getObjectKey().equals(jIF.getObjectKey()))
                    continue;
                boolean temp;
                temp = findDirectIPP(iPPList, iIF, jIF, rPList, reasons);
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "DirectIPP: " + String.valueOf(temp));

                findImpliedIPP(iPPList, iIF, jIF, rPList, reasons);
                temp = findDirectIPU(iPUList, iIF, jIF, rPList, reasons);
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "DirectIPU: " + String.valueOf(temp));

            }
        }

        // Now work on quantities

        final List<MaximizerQuantityPropertyUtility> maxQPUList =
            getStore().getMaximizerQuantityPropertyUtilityList(e, getPageSize());
        final List<MinimizerQuantityPropertyUtility> minQPUList =
            getStore().getMinimizerQuantityPropertyUtilityList(e, getPageSize());

        final List<QuantityFact> iQFacts = getStore().getQuantityFacts(iIndividual, getPageSize());
        final List<QuantityFact> jQFacts = getStore().getQuantityFacts(jIndividual, getPageSize());

        for (final QuantityFact iQF : iQFacts) {
            for (final QuantityFact jQF : jQFacts) {
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "ifact " + iQF.makeKey().toString()); //$NON-NLS-1$
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "jfact " + jQF.makeKey().toString()); //$NON-NLS-1$

                if (!iQF.getPropertyKey().equals(jQF.getPropertyKey()))
                    continue;

                ExternalKey propertyKey = iQF.getPropertyKey();
                if (propertyKey == null)
                    continue;

                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "it's a quantity fact");
                RangeBounds bounds =
                    getFactoryUtil().getRangeBoundsForProperty(iQF.getPropertyKey());
                if (bounds == null)
                    continue;

                // These are in standard units
                Double minRange = Double.valueOf(bounds.getMin());
                Double maxRange = Double.valueOf(bounds.getMax());
                if (minRange == null || maxRange == null)
                    continue;

                if (!QuantityFactUtil.areCompatibleType(iQF, jQF))
                    continue;

                // The values of these quantityFacts.
                Double iStdValue = getFactoryUtil().getQuantityFactUtil().getValue(iQF);
                Double jStdValue = getFactoryUtil().getQuantityFactUtil().getValue(jQF);

                if (iStdValue == null || jStdValue == null)
                    continue;

                // get quantity property utility based on max properties
                boolean temp =
                    findDirectMaxQPU(maxQPUList, minRange, maxRange, iStdValue, jStdValue,
                        propertyKey, rPList, reasons);
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "MaxQPU: " + String.valueOf(temp));

                temp =
                    findDirectMinQPU(minQPUList, minRange, maxRange, iStdValue, jStdValue,
                        propertyKey, rPList, reasons);
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "MinQPU: " + String.valueOf(temp));

                temp =
                    findDirectOptQPU(optQPUList, minRange, maxRange, iStdValue, jStdValue,
                        propertyKey, rPList, reasons);
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                    "OptQPU: " + String.valueOf(temp));
                // if ((iF instanceof MeasurementFact) && (jF instanceof MeasurementFact)) {
                // final MeasurementFact iMF = (MeasurementFact) iF;
                // final MeasurementFact jMF = (MeasurementFact) jF;
                // findDirectOptMPU(optMPUList, minRange, maxRange, iStdValue, jStdValue,
                // propertyKey, rPList, reasons);
                // }

            }
        }

        if (rPList.size() == 0) {
            Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                "No direct, reverse, implied, or property-derived preferences");
            // We didn't find any direct, reverse, or implied preferences.
            return null;
        }

        WeightingOperator wOp = new WeightingOperator();
        CompromiseOperator cOp = new CompromiseOperator();

        for (PropertyDerivedIndividualPreference rP : rPList) {
            double weightValue = getFactoryUtil().getWeight(e, rP);
            double propertyPreferenceScore = rP.getValue().doubleValue();
            // weighting
            propertyPreferenceScore = wOp.f(weightValue, propertyPreferenceScore);
            // combine with preferences derived from other properties
            preferenceScore =
                cOp.f(Membership.newInstance(preferenceScore),
                    Membership.newInstance(propertyPreferenceScore)).getM().doubleValue();

        }

        IndividualPreference rIP =
            new IndividualPreference(e.makeKey(), iIndividual.makeKey(), jIndividual.makeKey(),
                Double.valueOf(preferenceScore), N_NAMESPACE);

        // ok, so we have enough to make one
        return new PropertyDerivedPreferenceSummary(e, iIndividual, jIndividual, rPList, rIP,
            reasons);
    }

    /**
     * Populate rPList with any direct or reverse individual property preferences found in iPPList
     * that link the two facts.
     * 
     * @param iPPList
     *            input: all the individual property preferences
     * @param iIF
     *            input: an individual fact with individual subject i, and the same property as jIF.
     * @param jIF
     *            input: an individual fact with individual subject j, and the same property as iIF.
     * @param rPList
     *            output: individual property preferences relevant to the specified facts.
     * @param reasons
     *            output: diagnostic text
     * @return true if there are any direct preferences
     */
    @SuppressWarnings("nls")
    protected static boolean findDirectIPP(final List<IndividualPropertyPreference> iPPList,
        final IndividualFact iIF, final IndividualFact jIF,
        List<PropertyDerivedIndividualPreference> rPList, List<String> reasons) {
        Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info("findDirectIpp()");

        boolean foundOne = false;
        for (final IndividualPropertyPreference iPP : iPPList) {
            if (iIF.getPropertyKey().equals(iPP.getPropertyKey())) {
                // same property, now check the objects
                if (iIF.getObjectKey().equals(iPP.getPrimaryIndividualKey())
                    && jIF.getObjectKey().equals(iPP.getSecondaryIndividualKey())) {
                    // this preference is relevant
                    PropertyPreferenceDerivedIndividualPreference<IndividualPropertyPreference> pPDIP =
                        new PropertyPreferenceDerivedIndividualPreference<IndividualPropertyPreference>(
                            null, iPP.getPropertyKey(), iPP.getValue(), iPP, N_NAMESPACE);

                    rPList.add(pPDIP);
                    reasons.add("Direct id: " + iPP.makeKey().toString() + " value: "
                        + String.format("%.2f", iPP.getValue()));
                    foundOne = true;
                } else if (jIF.getObjectKey().equals(iPP.getPrimaryIndividualKey())
                    && iIF.getObjectKey().equals(iPP.getSecondaryIndividualKey())) {
                    // try the reverse if we don't have the direct

                    PropertyPreferenceDerivedIndividualPreference<IndividualPropertyPreference> pPDIP =
                        new PropertyPreferenceDerivedIndividualPreference<IndividualPropertyPreference>(
                            null, iPP.getPropertyKey(), iPP.getValue(), iPP, N_NAMESPACE);

                    // it's the reverse, so it's 1-value.
                    pPDIP.setValue(Double.valueOf(1 - iPP.getValue().doubleValue()));
                    rPList.add(pPDIP);
                    reasons.add("Reverse id: " + iPP.makeKey().toString() + " value: "
                        + String.format("%.2f", pPDIP.getValue()));
                    foundOne = true;
                }
            }
        }
        return foundOne;
    }

    /**
     * If there is an IndividualPropertyUtility relevant to iF, and another to jF, then add a new
     * PropertyUtilityDerivedIndividualPreference to rpList.
     * 
     * @param iPUList
     * @param iIF
     * @param jIF
     * @param rPList
     * @param reasons
     * @return
     */
    @SuppressWarnings("nls")
    protected static boolean findDirectIPU(final List<IndividualPropertyUtility> iPUList,
        final IndividualFact iIF, final IndividualFact jIF,
        List<PropertyDerivedIndividualPreference> rPList, List<String> reasons) {

        boolean foundOne = false;
        for (final IndividualPropertyUtility iPP : iPUList) {
            for (final IndividualPropertyUtility jPP : iPUList) {
                // Skip identical ones
                if (iPP.makeKey().equals(jPP.makeKey()))
                    continue;

                if (iIF.getPropertyKey().equals(iPP.getPropertyKey())
                    && iIF.getPropertyKey().equals(jPP.getPropertyKey())) {

                    // same property, now check the objects
                    if (iIF.getObjectKey().equals(iPP.getIndividualKey())
                        && jIF.getObjectKey().equals(jPP.getIndividualKey())) {

                        // this preference is relevant

                        IndividualPropertyUtilityDerivedIndividualPreference<IndividualPropertyUtility> pUDIP =
                            new IndividualPropertyUtilityDerivedIndividualPreference<IndividualPropertyUtility>(
                                null, iPP.getPropertyKey(), Double
                                    .valueOf(UtilityDerivedIndividualPreference
                                        .utilityDerivedPreferenceValue(
                                            iPP.getValue().doubleValue(), jPP.getValue()
                                                .doubleValue())), iPP, jPP, N_NAMESPACE);

                        rPList.add(pUDIP);

                        reasons.add("Direct i: " + iPP.makeKey().toString() + " value: "
                            + String.format("%.2f", iPP.getValue()) + " j: "
                            + jPP.makeKey().toString() + " value: "
                            + String.format("%.2f", jPP.getValue()));

                        foundOne = true;
                    }
                }
            }
        }
        return foundOne;
    }

    /**
     * Find properties specified by propertyKey in maxQPUList. If found, construct a new
     * QuantityPropertyUtilityDerivedIndividualPreference given the specified fact values for i and
     * j, and the specified min and max bounds.
     * 
     * If there is a MaximizerQuantityPropertyUtility relevant to iIF, and another to jIF, then add
     * a new PropertyUtilityDerivedIndividualPreference to rpList, with the value of the preference
     * derived from the values of the QuantityFacts iIF and jIF.
     * 
     * @param maxQPUList
     * @param minRange
     *            the minimum value of all facts of the same property as iQF and jQF
     * @param maxRange
     *            the maximum value
     * @param iStdValue
     *            value of the fact for individual i
     * @param jStdValue
     *            value of the fact for individual j
     * @param propertyKey
     *            the property of the above facts.
     * @param rPList
     *            OUTPUT
     * @param reasons
     *            OUTPUT
     * @return
     */
    @SuppressWarnings("nls")
    protected boolean findDirectMaxQPU(final List<MaximizerQuantityPropertyUtility> maxQPUList,
        Double minRange, Double maxRange, Double iStdValue, Double jStdValue,
        ExternalKey propertyKey, List<PropertyDerivedIndividualPreference> rPList,
        List<String> reasons) {
        Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
            "Looking for MaxQPU in list size: " + maxQPUList.size());

        boolean foundOne = false;
        for (final MaximizerQuantityPropertyUtility pU : maxQPUList) {
            Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                "Comparing desired property " + propertyKey.toString() + " with list property "
                    + pU.getPropertyKey().toString());

            if (propertyKey.equals(pU.getPropertyKey())) {
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info("Looking...");

                // same property, so this is relevant.

                try {
                    double iUtility =
                        QuantityFactUtil.interpolateUtility(minRange.doubleValue(), maxRange
                            .doubleValue(), pU.getMinUtility().doubleValue(), pU
                            .getPreferredUtility().doubleValue(), iStdValue.doubleValue());
                    double jUtility =
                        QuantityFactUtil.interpolateUtility(minRange.doubleValue(), maxRange
                            .doubleValue(), pU.getMinUtility().doubleValue(), pU
                            .getPreferredUtility().doubleValue(), jStdValue.doubleValue());

                    QuantityPropertyUtilityDerivedIndividualPreference<MaximizerQuantityPropertyUtility> pUDIP =
                        new QuantityPropertyUtilityDerivedIndividualPreference<MaximizerQuantityPropertyUtility>(
                            null, pU.getPropertyKey(), Double
                                .valueOf(UtilityDerivedIndividualPreference
                                    .utilityDerivedPreferenceValue(iUtility, jUtility)), pU,
                            N_NAMESPACE);

                    rPList.add(pUDIP);

                    reasons.add("Max Quant: " + pU.makeKey().toString() + " value: "
                        + String.format("%.2f", pUDIP.getValue()));
                    Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info("Found");

                    foundOne = true;

                } catch (QuantityFactUtil.BoundsException ex) {
                    continue;
                }
            }
        }
        return foundOne;
    }

    /**
     * Given the MQPU's in minQPUList, find ones with property propertyKey, and add a new
     * PropertyDerivedIndividualPreference to rPList derived from the iStdValue and jStdValue.
     * 
     * @param minQPUList
     * @param minRange
     * @param maxRange
     * @param iStdValue
     * @param jStdValue
     * @param propertyKey
     * @param rPList
     *            OUTPUT
     * @param reasons
     *            OUTPUT
     * @return
     */
    @SuppressWarnings("nls")
    protected boolean findDirectMinQPU(final List<MinimizerQuantityPropertyUtility> minQPUList,
        Double minRange, Double maxRange, Double iStdValue, Double jStdValue,
        ExternalKey propertyKey, List<PropertyDerivedIndividualPreference> rPList,
        List<String> reasons) {

        boolean foundOne = false;
        for (final MinimizerQuantityPropertyUtility pU : minQPUList) {

            if (propertyKey.equals(pU.getPropertyKey())) {

                // same property, so this is relevant.

                // TODO: the only difference between min and max is the arguments here, so
                // refactor these two somehow.

                try {
                    double iUtility =
                        QuantityFactUtil.interpolateUtility(minRange.doubleValue(), maxRange
                            .doubleValue(), pU.getPreferredUtility().doubleValue(), pU
                            .getMinUtility().doubleValue(), iStdValue.doubleValue());
                    double jUtility =
                        QuantityFactUtil.interpolateUtility(minRange.doubleValue(), maxRange
                            .doubleValue(), pU.getPreferredUtility().doubleValue(), pU
                            .getMinUtility().doubleValue(), jStdValue.doubleValue());

                    QuantityPropertyUtilityDerivedIndividualPreference<MinimizerQuantityPropertyUtility> pUDIP//
                    =
                        new QuantityPropertyUtilityDerivedIndividualPreference<MinimizerQuantityPropertyUtility>(
                            null, pU.getPropertyKey(), Double
                                .valueOf(UtilityDerivedIndividualPreference
                                    .utilityDerivedPreferenceValue(iUtility, jUtility)), pU,
                            N_NAMESPACE);

                    rPList.add(pUDIP);

                    reasons.add("Min Quant: " + pU.makeKey().toString() + " value: "
                        + String.format("%.2f", pUDIP.getValue()));

                    foundOne = true;

                } catch (QuantityFactUtil.BoundsException ex) {
                    continue;
                }
            }
        }
        return foundOne;
    }

    /**
     * Given the OQPU's in optQPUList, find ones with property propertyKey, and add a new
     * PropertyDerivedIndividualPreference to rPList derived from the iStdValue and jStdValue.
     * 
     * @param optQPUList
     * @param minRange
     *            in STANDARD UNITS
     * @param maxRange
     *            in STANDARD UNITS
     * @param iStdValue
     * @param jStdValue
     * @param propertyKey
     * @param rPList
     *            OUTPUT
     * @param reasons
     *            OUTPUT
     * @return
     */
    @SuppressWarnings("nls")
    protected boolean findDirectOptQPU(final List<OptimizerQuantityPropertyUtility> optQPUList,
        Double minRange, Double maxRange, Double iStdValue, Double jStdValue,
        ExternalKey propertyKey, List<PropertyDerivedIndividualPreference> rPList,
        List<String> reasons) {
        Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
            "looking for " + propertyKey.toString());

        boolean foundOne = false;
        for (final OptimizerQuantityPropertyUtility pU : optQPUList) {
            Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                "looking at " + pU.toString());

            if (propertyKey.equals(pU.getPropertyKey())) {
                System.out.println("FOOOOO2");

                Double preferredStdValue = getFactoryUtil().getQuantityFactUtil().getValue(pU);
                if (preferredStdValue == null) {
                    System.out.println("FOOOOO3");
                    continue;
                }
                try {
                    System.out.println("FOOOOO4");

                    double iUtility =
                        QuantityFactUtil.interpolateUtilityForOptimum(iStdValue, minRange,
                            preferredStdValue, maxRange, pU.getMinUtility(), pU
                                .getPreferredUtility());

                    double jUtility =
                        QuantityFactUtil.interpolateUtilityForOptimum(jStdValue, minRange,
                            preferredStdValue, maxRange, pU.getMinUtility(), pU
                                .getPreferredUtility());

                    System.out.println("FOOOOO6");

                    QuantityPropertyUtilityDerivedIndividualPreference<OptimizerQuantityPropertyUtility> pUDIP =
                        new QuantityPropertyUtilityDerivedIndividualPreference<OptimizerQuantityPropertyUtility>(
                            null, pU.getPropertyKey(), Double
                                .valueOf(UtilityDerivedIndividualPreference
                                    .utilityDerivedPreferenceValue(iUtility, jUtility)), pU,
                            N_NAMESPACE);
                    rPList.add(pUDIP);
                    reasons.add("Opt Quant: " + pU.makeKey().toString() + " value: "
                        + String.format("%.2f", pUDIP.getValue()));

                    foundOne = true;

                } catch (QuantityFactUtil.BoundsException ex) {
                    System.out.println("FOOOOO7");

                    continue;
                }
            }
        }
        return foundOne;
    }

    /**
     * Populate rPList with any available implied individual property preferences.
     * 
     * didn't find a direct or reverse, so now look for implied. we're looking for a "wheel"
     * individual that can be used with two individualpropertypreferences. The problem is that we
     * don't have an easy way to find the set of all wheels. But we don't actually need it.
     * 
     * Instead, look at all pairs of preferences, and find those that constitute pairs like:
     * <ul>
     * <li>(rIK, rKJ)
     * <li>(rIK, rJK)
     * <li>(rKI, rKJ)
     * <li>(rKI, rJK)
     * </ul>
     * Make a list of them, and then stuff them into the implication operator.
     * 
     * @param iPPList
     *            input: all the individualpropertypreferences.
     * @param iIF
     *            input: a fact with individual subject i, and the same property as jIF.
     * @param jIF
     *            input: a fact with individual subject j, and the same property as iIF.
     * @param rPList
     *            output: property preferences relevant to the specified facts.
     * @param reasons
     *            output: diagnostic text
     */
    @SuppressWarnings("nls")
    protected static void findImpliedIPP(final List<IndividualPropertyPreference> iPPList,
        final IndividualFact iIF, final IndividualFact jIF,
        List<PropertyDerivedIndividualPreference> rPList, List<String> reasons) {

        Map<ExternalKey, ImplicationOperator.Inputs> kMap =
            new HashMap<ExternalKey, ImplicationOperator.Inputs>();
        // the property id we're working on
        ExternalKey propertyKey = iIF.getPropertyKey();

        // I can't believe this will actually work

        ImpliedPropertyPreferenceDerivedIndividualPreference<IndividualPropertyPreference> pPDIP =
            new ImpliedPropertyPreferenceDerivedIndividualPreference<IndividualPropertyPreference>(
                null, propertyKey, null, new ArrayList<IndividualPropertyPreference>(), N_NAMESPACE);

        for (IndividualPropertyPreference pIPP : iPPList) {
            if (pIPP.getPropertyKey().equals(propertyKey)) {
                // only proceed if this preference has the same property as the facts
                for (IndividualPropertyPreference qIPP : iPPList) {
                    if (!(qIPP.makeKey().equals(pIPP.makeKey()))
                        && qIPP.getPropertyKey().equals(propertyKey)) {
                        /**
                         * proceed if has the same property and isn't identical to the other
                         * preference.
                         * 
                         * now we have two distinct propertypreferences with the same property as
                         * the pair of facts.
                         * 
                         * Now try to find the pairs described above, and record what we find.
                         * 
                         * If something strange happens and the combination we seek occurs more than
                         * once, we forget the early occurrances.
                         */
                        if (pIPP.getPrimaryIndividualKey().equals(iIF.getObjectKey())
                            && pIPP.getSecondaryIndividualKey().equals(
                                qIPP.getPrimaryIndividualKey())
                            && qIPP.getSecondaryIndividualKey().equals(jIF.getObjectKey())) {
                            // this means (p,q) == (rIK, rKJ)
                            ExternalKey kKey = pIPP.getSecondaryIndividualKey();
                            ImplicationOperator.Inputs inputs = kMap.get(kKey);
                            if (inputs == null) {
                                inputs = new ImplicationOperator.Inputs();
                                kMap.put(kKey, inputs);
                            }
                            inputs.setRIK(pIPP.getValue());
                            inputs.setRKJ(qIPP.getValue());
                            pPDIP.addPropertyPreference(pIPP);
                            pPDIP.addPropertyPreference(qIPP);
                            reasons.add("Implied using k "
                                + pIPP.getSecondaryIndividualKey().toString() + " rIK: "
                                + String.format("%.2f", pIPP.getValue()) + " rKJ: "
                                + String.format("%.2f", qIPP.getValue()));

                        } else if (pIPP.getPrimaryIndividualKey().equals(iIF.getObjectKey())
                            && pIPP.getSecondaryIndividualKey().equals(
                                qIPP.getSecondaryIndividualKey())
                            && qIPP.getPrimaryIndividualKey().equals(jIF.getObjectKey())) {
                            // this means (p,q) == (rIK, rJK)
                            ExternalKey kKey = pIPP.getSecondaryIndividualKey();
                            ImplicationOperator.Inputs inputs = kMap.get(kKey);
                            if (inputs == null) {
                                inputs = new ImplicationOperator.Inputs();
                                kMap.put(kKey, inputs);
                            }
                            inputs.setRIK(pIPP.getValue());
                            inputs.setRJK(qIPP.getValue());
                            pPDIP.addPropertyPreference(pIPP);
                            pPDIP.addPropertyPreference(qIPP);
                            reasons.add("Implied using k "
                                + pIPP.getSecondaryIndividualKey().toString() + " rIK: "
                                + String.format("%.2f", pIPP.getValue()) + " rJK: "
                                + String.format("%.2f", qIPP.getValue()));

                        } else if (pIPP.getSecondaryIndividualKey().equals(iIF.getObjectKey())
                            && pIPP.getPrimaryIndividualKey()
                                .equals(qIPP.getPrimaryIndividualKey())
                            && qIPP.getSecondaryIndividualKey().equals(jIF.getObjectKey())) {
                            // this means (p,q) == (rKI, rKJ)
                            ExternalKey kKey = pIPP.getPrimaryIndividualKey();
                            ImplicationOperator.Inputs inputs = kMap.get(kKey);
                            if (inputs == null) {
                                inputs = new ImplicationOperator.Inputs();
                                kMap.put(kKey, inputs);
                            }
                            inputs.setRKI(pIPP.getValue());
                            inputs.setRKJ(qIPP.getValue());
                            pPDIP.addPropertyPreference(pIPP);
                            pPDIP.addPropertyPreference(qIPP);
                            reasons.add("Implied using k "
                                + pIPP.getPrimaryIndividualKey().toString() + " rKI: "
                                + String.format("%.2f", pIPP.getValue()) + " rKJ: "
                                + String.format("%.2f", qIPP.getValue()));

                        } else if (pIPP.getSecondaryIndividualKey().equals(iIF.getObjectKey())
                            && pIPP.getPrimaryIndividualKey().equals(
                                qIPP.getSecondaryIndividualKey())
                            && qIPP.getPrimaryIndividualKey().equals(jIF.getObjectKey())) {
                            // this means (p,q) == (rKI, rJK)
                            ExternalKey kKey = pIPP.getPrimaryIndividualKey();
                            ImplicationOperator.Inputs inputs = kMap.get(kKey);
                            if (inputs == null) {
                                inputs = new ImplicationOperator.Inputs();
                                kMap.put(kKey, inputs);
                            }
                            inputs.setRKI(pIPP.getValue());
                            inputs.setRJK(qIPP.getValue());
                            pPDIP.addPropertyPreference(pIPP);
                            pPDIP.addPropertyPreference(qIPP);
                            reasons.add("Implied using k "
                                + pIPP.getPrimaryIndividualKey().toString() + " rKI: "
                                + String.format("%.2f", pIPP.getValue()) + " rJK: "
                                + String.format("%.2f", qIPP.getValue()));

                        } else {
                            // it's some other set of preferences that are not useful.
                        }

                    }
                }
            }
        }

        if (kMap.size() == 0) {
            // didn't find any triangle preferences.
            return;
        }
        // Now look at the inputs and do something about them.
        // It may be that we'll have multiple implied values, for multiple K's, so we should average
        // them to produce a new IndividualPropertyPreference.
        List<Double> inputValues = new ArrayList<Double>();
        for (Map.Entry<ExternalKey, ImplicationOperator.Inputs> entry : kMap.entrySet()) {
            ExternalKey key = entry.getKey();
            ImplicationOperator.Inputs inputs = entry.getValue();
            ImplicationOperator implicationOp = new ImplicationOperator(inputs);
            Double rIJValue = implicationOp.getValue();
            if (rIJValue != null) {
                inputValues.add(rIJValue);
                reasons.add("Implied using k " + key.toString() + " value: "
                    + String.format("%.2f", rIJValue));
            }
        }
        if (inputValues.size() == 0) {
            // shouldn't happen
            return;
        }

        OWAOperator averageOp = new AverageOWAOperator();
        Double averageValue = averageOp.F(inputValues);
        pPDIP.setValue(averageValue);

        reasons.add("setvalue: " + String.format("%.2f", averageValue)); //$NON-NLS-1$
        rPList.add(pPDIP);
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
