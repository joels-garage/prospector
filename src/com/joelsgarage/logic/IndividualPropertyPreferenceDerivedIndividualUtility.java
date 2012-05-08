/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.IndividualPropertyUtility;

/**
 * An individual utility derived from individual property preference data, i.e. the eigenvector way.
 * 
 * @author joel
 * 
 */
public class IndividualPropertyPreferenceDerivedIndividualUtility extends
    PropertyDerivedIndividualUtility {
    /** The derived instance */
    private IndividualPropertyUtility utility;

    protected IndividualPropertyPreferenceDerivedIndividualUtility() {
        super();
    }

    protected IndividualPropertyPreferenceDerivedIndividualUtility(ExternalKey stakeholderKey,
        ExternalKey propertyKey, Double value, IndividualPropertyUtility utility, String namespace) {
        super(stakeholderKey, propertyKey, value, namespace);
        setUtility(utility);
    }

    public IndividualPropertyUtility getUtility() {
        return this.utility;
    }

    private void setUtility(IndividualPropertyUtility utility) {
        this.utility = utility;
    }
}
