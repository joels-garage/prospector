/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.QuantityPropertyUtility;

/**
 * Represents an individualpreference derived from a pair of utilities, which are derived from a
 * single utilitypropertypreference, and two facts.
 * 
 * @author joel
 * 
 */
public class QuantityPropertyUtilityDerivedIndividualPreference<T extends QuantityPropertyUtility>
    extends PropertyDerivedIndividualPreference {
    private T quantityPropertyUtility;

    protected QuantityPropertyUtilityDerivedIndividualPreference() {
        super();
    }

    protected QuantityPropertyUtilityDerivedIndividualPreference(ExternalKey stakeholderKey,
        ExternalKey propertyKey, Double value, T quantityPropertyUtility, String namespace) {
        super(stakeholderKey, propertyKey, value, namespace);
        setQuantityPropertyUtility(quantityPropertyUtility);
    }

    public T getQuantityPropertyUtility() {
        return this.quantityPropertyUtility;
    }

    private void setQuantityPropertyUtility(T quantityPropertyUtility) {
        this.quantityPropertyUtility = quantityPropertyUtility;
    }
}
