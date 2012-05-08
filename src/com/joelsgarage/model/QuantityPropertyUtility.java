/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents a utility expressed about QuantityFacts.
 * 
 * There are several ways such utility could be expressed:
 * <ul>
 * <li> "More is better" -- see MaximizerMeasurementPropertyUtility
 * <li> "Less is better" -- see MinimizerMeasurementPropertyUtility
 * <li> "Optimum point" -- see OptimizerMeasurementPropertyUtility
 * </ul>
 * 
 * @author joel
 */
@VisibleType(ExternalKey.QUANTITY_PROPERTY_UTILITY_TYPE)
public abstract class QuantityPropertyUtility extends PropertyPreference {
    /** The utility of the preferred value, in the range [0,1] */
    private Double preferredUtility = Double.valueOf(0.0);

    /** The utility of the extrema, in the range [0,1] */
    private Double minUtility = Double.valueOf(0.0);

    protected QuantityPropertyUtility() {
        super();
    }

    /** key includes stakeholder and property */
    protected QuantityPropertyUtility(ExternalKey stakeholderKey, ExternalKey propertyKey,
        Double preferredUtility, Double minUtility, String namespace) {
        super(stakeholderKey, propertyKey, namespace);
        setPreferredUtility(preferredUtility);
        setMinUtility(minUtility);
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
    }

    /** Covariant */
    @VisibleJoin(value = QuantityProperty.class, name = "property")
    @Override
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getPropertyKey() {
        // TODO Auto-generated method stub
        return super.getPropertyKey();
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " preferred utility: " + String.valueOf(getPreferredUtility()) + //
    // " min utility: " + String.valueOf(getMinUtility());
    // return result;
    //    }

    @VisibleField("preferred_utility")
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public Double getPreferredUtility() {
        return this.preferredUtility;
    }

    public void setPreferredUtility(Double preferredUtility) {
        this.preferredUtility = preferredUtility;
    }

    @VisibleField("min_utility")
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public Double getMinUtility() {
        return this.minUtility;
    }

    public void setMinUtility(Double minUtility) {
        this.minUtility = minUtility;
    }
}
