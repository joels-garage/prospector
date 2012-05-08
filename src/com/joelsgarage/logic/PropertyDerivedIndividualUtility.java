/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.PropertyPreference;

/**
 * Represents a single individualutility derived from a single set of property preferences. This
 * single preference is combined with others in the PropertyDerivedPreferenceSummary.
 * 
 * So, for example, i has IndividualProperty ip and QuantityProperty qp.
 * 
 * Stakeholder e has a (perhaps derived) IndividualPropertyUtility that is relevant for ip, so that
 * would be one PropertyDerivedIndividualUtility.
 * 
 * Stakeholder e also has a QuantityPropertyUtility relevant to i, so that would be another
 * PropertyDerivedIndividualUtility.
 * 
 * @author joel
 * 
 */
public class PropertyDerivedIndividualUtility extends PropertyPreference {
    private Double value;

    protected PropertyDerivedIndividualUtility() {
        super();
    }

    protected PropertyDerivedIndividualUtility(ExternalKey stakeholderKey, ExternalKey propertyKey,
        Double value, String namespace) {
        super(stakeholderKey, propertyKey, namespace);
        setValue(value);
    }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    public Double getValue() {
        return this.value;
    }

    private void setValue(Double value) {
        this.value = value;
    }
}
