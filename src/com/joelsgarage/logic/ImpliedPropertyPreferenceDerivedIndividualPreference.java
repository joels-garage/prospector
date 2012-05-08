/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.List;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.PropertyPreference;

/**
 * Represents an individualpreference derived from a chain of property preferences (on some property
 * of i and j).
 * 
 * Note that some of the source propertypreferences may, themselves, be derived.
 * 
 * TODO: actually this may represent the average of several implied values. So I should show that
 * too, if I need it.
 * 
 * @author joel
 * 
 */
public class ImpliedPropertyPreferenceDerivedIndividualPreference<T extends PropertyPreference>
    extends PropertyDerivedIndividualPreference {
    private List<T> propertyPreferenceList;

    protected ImpliedPropertyPreferenceDerivedIndividualPreference() {
        setPropertyPreferenceList(new ArrayList<T>());
    }

    protected ImpliedPropertyPreferenceDerivedIndividualPreference(ExternalKey stakeholderKey,
        ExternalKey propertyKey, Double value, List<T> propertyPreferenceList, String namespace) {
        super(stakeholderKey, propertyKey, value, namespace);
        setPropertyPreferenceList(propertyPreferenceList);
    }

    public void addPropertyPreference(T pp) {
        getPropertyPreferenceList().add(pp);
    }

    public List<T> getPropertyPreferenceList() {
        return this.propertyPreferenceList;
    }

    protected void setPropertyPreferenceList(List<T> propertyPreferenceList) {
        this.propertyPreferenceList = propertyPreferenceList;
    }
}
