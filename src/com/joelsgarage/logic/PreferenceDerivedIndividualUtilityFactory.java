/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.List;
import java.util.Map;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.IndividualPreference;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.solver.Eigensolver;
import com.joelsgarage.util.FatalException;

/**
 * To derive IndividualUtility from a set of IndividualPreference , we first fetch all the relevant
 * IndividualPreference , then run some big aggregate function (i.e. the eigensolver), and then we
 * have a set of IndividualUtility that we return on demand.
 * 
 * I'd like to use this class for both IndividualPreference and IndividualPropertyPreference, but
 * for now don't worry about it; it's just for IndividualPreference.
 * 
 * The domain of preferences is a single stakeholder, i.e. all the individuals of the decision
 * class, that are annotated with an individual preference.
 * 
 * @author joel
 * 
 */
public class PreferenceDerivedIndividualUtilityFactory {
    private static final String N_NAMESPACE = "namespace"; //$NON-NLS-1$
    private AlternativeStore store;
    private ExternalKey stakeholderKey;
    private Eigensolver<ExternalKey> solver;
    private List<IndividualPreference> inputPreferences;
    private Map<ExternalKey, Double> outputUtilities;

    /**
     * Fetch all the preferences for stakeholder, run the eigensolver, and populate the
     * outputUtilities map.
     */
    public PreferenceDerivedIndividualUtilityFactory(AlternativeStore store,
        ExternalKey stakeholderKey, int pageSize) {
        setStore(store);
        setStakeholderKey(stakeholderKey);
        setSolver(new Eigensolver<ExternalKey>());
        setInputPreferences(getStore().getAllIndividualPreferences(stakeholderKey, pageSize));
        if (getInputPreferences().size() == 0)
            return;
        for (IndividualPreference individualPreference : getInputPreferences()) {
            getSolver().addPreference(individualPreference.getPrimaryIndividualKey(),
                individualPreference.getSecondaryIndividualKey(), individualPreference.getValue());
        }
        setOutputUtilities(getSolver().getFirstEigenvector());
    }

    /**
     * Because the utilities are derived from *all* the preferences for a Stakeholder there is no
     * extra data to carry along, and we return the basic entity type.
     * 
     * @return
     * @throws FatalException
     *             if key population fails
     */
    public IndividualUtility newInstance(ExternalKey individualKey) throws FatalException {
        if (getOutputUtilities() == null)
            return null;
        if (getOutputUtilities().get(individualKey) == null)
            return null;
        IndividualUtility result =
            new IndividualUtility(getStakeholderKey(), individualKey, getOutputUtilities().get(
                individualKey), N_NAMESPACE);
        return result;
    }

    //

    protected Eigensolver<ExternalKey> getSolver() {
        return this.solver;
    }

    protected void setSolver(Eigensolver<ExternalKey> solver) {
        this.solver = solver;
    }

    protected AlternativeStore getStore() {
        return this.store;
    }

    protected void setStore(AlternativeStore store) {
        this.store = store;
    }

    protected ExternalKey getStakeholderKey() {
        return this.stakeholderKey;
    }

    protected void setStakeholderKey(ExternalKey stakeholderKey) {
        this.stakeholderKey = stakeholderKey;
    }

    protected List<IndividualPreference> getInputPreferences() {
        return this.inputPreferences;
    }

    protected void setInputPreferences(List<IndividualPreference> inputPreferences) {
        this.inputPreferences = inputPreferences;
    }

    protected Map<ExternalKey, Double> getOutputUtilities() {
        return this.outputUtilities;
    }

    protected void setOutputUtilities(Map<ExternalKey, Double> outputUtilities) {
        this.outputUtilities = outputUtilities;
    }
}
