/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.IndividualPropertyUtility;

/**
 * An individual utility derived from individual property utility data.
 * 
 * @author joel
 * 
 */
public class IndividualPropertyUtilityDerivedIndividualUtility extends
    PropertyDerivedIndividualUtility {
    /** The derived instance */
    private IndividualPropertyUtility utility;

    protected IndividualPropertyUtilityDerivedIndividualUtility() {
        super();
    }

    protected IndividualPropertyUtilityDerivedIndividualUtility(ExternalKey stakeholderKey,
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
