/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.PropertyPreference;

/**
 * Represents a single individualpreference derived from a single set of property preferences. This
 * single preference is combined with others in the PropertyDerivedPreferenceSummary.
 * 
 * So, for example, i and j have individualproperty ip and measurement property mp. Stakeholder e
 * has an individualpreference that is relevant for ip, so that would be one
 * PropertyDerivedIndividualPreference. Stakeholder e also has a measurementutility relevant to i
 * and another relevant to j, so those would be combined into another
 * PropertyDerivedIndividualPreference.
 * 
 * @author joel
 * 
 */
public abstract class PropertyDerivedIndividualPreference extends PropertyPreference {
    private Double value;

    protected PropertyDerivedIndividualPreference() {
        super();
    }

    protected PropertyDerivedIndividualPreference(ExternalKey stakeholderKey,
        ExternalKey propertyKey, Double value, String namespace) {
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

    protected void setValue(Double value) {
        this.value = value;
    }
}
