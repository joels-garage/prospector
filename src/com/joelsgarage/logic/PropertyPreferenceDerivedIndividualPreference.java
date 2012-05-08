/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.PropertyPreference;

/**
 * Represents an individualpreference derived from a single property preference (on some property of
 * i and j).
 * 
 * @author joel
 * 
 */
public class PropertyPreferenceDerivedIndividualPreference<T extends PropertyPreference> extends
    PropertyDerivedIndividualPreference {
    private T propertyPreference;

    protected PropertyPreferenceDerivedIndividualPreference() {
        super();
    }

    protected PropertyPreferenceDerivedIndividualPreference(ExternalKey stakeholderKey,
        ExternalKey propertyKey, Double value, T propertyPreference, String namespace) {
        super(stakeholderKey, propertyKey, value, namespace);
        setPropertyPreference(propertyPreference);
    }

    public T getPropertyPreference() {
        return this.propertyPreference;
    }

    protected void setPropertyPreference(T propertyPreference) {
        this.propertyPreference = propertyPreference;
    }
}
