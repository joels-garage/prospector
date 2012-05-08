/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.model.ExternalKey;

/**
 * Represents an individualpreference derived from a pair of utilities (derived from facts of
 * individual i and individual j).
 * 
 * @author joel
 * 
 */
public class IndividualPropertyUtilityDerivedIndividualPreference<IndividualPropertyUtility>
    extends PropertyDerivedIndividualPreference {
    private IndividualPropertyUtility iIndividualPropertyUtility;
    private IndividualPropertyUtility jIndividualPropertyUtility;

    protected IndividualPropertyUtilityDerivedIndividualPreference() {
        super();
    }

    protected IndividualPropertyUtilityDerivedIndividualPreference(ExternalKey stakeholderKey,
        ExternalKey propertyKey, Double value, IndividualPropertyUtility i,
        IndividualPropertyUtility j, String namespace) {
        super(stakeholderKey, propertyKey, value, namespace);
        setIIndividualPropertyUtility(i);
        setJIndividualPropertyUtility(j);
    }

    public IndividualPropertyUtility getIIndividualPropertyUtility() {
        return this.iIndividualPropertyUtility;
    }

    private void setIIndividualPropertyUtility(IndividualPropertyUtility individualPropertyUtility) {
        this.iIndividualPropertyUtility = individualPropertyUtility;
    }

    public IndividualPropertyUtility getJIndividualPropertyUtility() {
        return this.jIndividualPropertyUtility;
    }

    private void setJIndividualPropertyUtility(IndividualPropertyUtility individualPropertyUtility) {
        this.jIndividualPropertyUtility = individualPropertyUtility;
    }
}
