/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.QuantityPropertyUtility;

/**
 * An individual utility derived from quantity property utility data.
 * 
 * @author joel
 * 
 */
public class QuantityPropertyUtilityDerivedIndividualUtility<T extends QuantityPropertyUtility>
    extends PropertyDerivedIndividualUtility {
    /** The derived instance */
    private T quantityPropertyUtility;

    protected QuantityPropertyUtilityDerivedIndividualUtility() {
        super();
    }

    protected QuantityPropertyUtilityDerivedIndividualUtility(ExternalKey stakeholderKey,
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
