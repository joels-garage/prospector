/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.IndividualPropertyUtility;
import com.joelsgarage.solver.Eigensolver;
import com.joelsgarage.util.FatalException;

/**
 * To derive an IndividualPropertyUtility from a set of IndividualPropertyPreference, we first fetch
 * all relevant IndividualPropertyPreference, then aggregate it with the eigensolver, and then store
 * the resulting set of IndividualPropertyUtility.
 * 
 * The domain of preferences is a single Stakeholder, for a single IndividualProperty, i.e. all the
 * preferences relevant to that property, that are annotated with an IndividualPreference.
 * 
 * The caller is expected to use this class as follows:
 * 
 * <pre>
 * factory = new PreferenceDerivedIndividualPropertyUtilityFactory(stakeholderKey);
 * factory.addPreference(i, j, v);
 * factory.addPreference(k, l, v);
 * factory.compute();
 * IndividualPropertyUtility utility = factory.newInstance(i);
 * </pre>
 * 
 * Thus, the association with any particular property is up to the caller.
 * 
 * @author joel
 */
public class PreferenceDerivedIndividualPropertyUtilityFactory {
    private static final String N_NAMESPACE = "n"; //$NON-NLS-1$
    private ExternalKey stakeholderKey;
    private Eigensolver<ExternalKey> solver;
    private Map<ExternalKey, Double> outputUtilities;

    /**
     * Minimal constructor.
     */
    public PreferenceDerivedIndividualPropertyUtilityFactory(ExternalKey stakeholderKey) {
        setStakeholderKey(stakeholderKey);
        setSolver(new Eigensolver<ExternalKey>());
    }

    /** Add the specified preference. Call this many times between constructor and compute() */
    public void addPreference(ExternalKey iKey, ExternalKey jKey, Double value) {
        getSolver().addPreference(iKey, jKey, value);
    }

    /** Run the eigensolver, and populate the outputUtilities map. */
    public void compute() {
        setOutputUtilities(getSolver().getFirstEigenvector());
    }

    /**
     * Because the utilities are derived from *all* the preferences for a Stakeholder there is no
     * extra data to carry along, and we return the basic entity type.
     * 
     * @return a minimally-populated IndividualPropertyUtility instance, or null if one can't be
     *         found
     * @throws FatalException
     *             if key population fails
     */
    public IndividualPropertyUtility newInstance(ExternalKey individualKey) throws FatalException {
        Double value = getOutputUtilities().get(individualKey);
        if (value == null) {
            Logger.getLogger(PreferenceDerivedIndividualPropertyUtilityFactory.class).info(
                "no value"); //$NON-NLS-1$
            return null;
        }
        IndividualPropertyUtility result =
            new IndividualPropertyUtility(getStakeholderKey(), null, individualKey, value,
                N_NAMESPACE);
        return result;
    }

    //

    protected Eigensolver<ExternalKey> getSolver() {
        return this.solver;
    }

    protected void setSolver(Eigensolver<ExternalKey> solver) {
        this.solver = solver;
    }

    protected ExternalKey getStakeholderKey() {
        return this.stakeholderKey;
    }

    protected void setStakeholderKey(ExternalKey stakeholderKey) {
        this.stakeholderKey = stakeholderKey;
    }

    protected Map<ExternalKey, Double> getOutputUtilities() {
        return this.outputUtilities;
    }

    protected void setOutputUtilities(Map<ExternalKey, Double> outputUtilities) {
        this.outputUtilities = outputUtilities;
    }
}
