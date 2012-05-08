/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.util.QuantityFactUtil;

/**
 * Represents the bounds on a property range.
 * 
 * TODO: add some more statistics, e.g. ntiles.
 * 
 * @author joel
 * 
 */
public class RangeBounds {
    /**
     * Helper class that does the conversion.
     */
    private QuantityFactUtil quantityFactUtil;
    /**
     * The property this range bounds applies to. This is set to the property of the first update
     * fact. It is an error for facts to reference any other property.
     */
    private ExternalKey propertyKey;
    /**
     * This range bounds applies to a measurement property, and thus requires conversion for update
     * facts. It is an error to mix measurement and non-measurement facts. It's a Boolean object so
     * it can have three states: true, false, and null (i.e. don't know yet).
     */
    private Boolean isMeasurement;

    /**
     * This range bounds is valid, i.e. has not had any invalid input (e.g. mixed types, as
     * specified above).
     */
    private boolean isValid = true;

    private double min = Double.POSITIVE_INFINITY;
    private double max = Double.NEGATIVE_INFINITY;

    public RangeBounds(QuantityFactUtil util) {
        setQuantityFactUtil(util);
    }

    /** Extend the bounds, if required, to accomodate the specified fact */
    @SuppressWarnings("nls")
    public void update(QuantityFact fact) {
        if (fact == null)
            return;

        // Check same property
        final ExternalKey factPropertyKey = fact.getPropertyKey();
        if (getPropertyKey() == null) {
            setPropertyKey(factPropertyKey);
        } else if (!getPropertyKey().equals(factPropertyKey)) {
            setValid(false);
            return;
        }

        // Does it have a unit?
        final boolean factIsMeasurement = (!fact.getExpressedUnitKey().valid());
        if (getIsMeasurement() == null) {
            setIsMeasurement(Boolean.valueOf(factIsMeasurement));
        } else if (getIsMeasurement().booleanValue() != factIsMeasurement) {
            setValid(false);
            return;
        }

        double value = fact.getValue().doubleValue();
        if (factIsMeasurement) {
            // convert it
            Double stdValue = getQuantityFactUtil().getValue(fact);
            if (stdValue == null) {
                // this shouldn't happen
                return;
            }
            value = stdValue.doubleValue();
        }
        if (value < getMin()) {
            setMin(value);
        }
        if (value > getMax()) {
            setMax(value);
        }
    }

    /** The lower bound, in the STANDARD UNIT */
    public double getMin() {
        return this.min;
    }

    protected void setMin(double min) {
        this.min = min;
    }

    /** The upper bound, in the STANDARD UNIT */
    public double getMax() {
        return this.max;
    }

    protected void setMax(double max) {
        this.max = max;
    }

    public QuantityFactUtil getQuantityFactUtil() {
        return this.quantityFactUtil;
    }

    public void setQuantityFactUtil(QuantityFactUtil quantityFactUtil) {
        this.quantityFactUtil = quantityFactUtil;
    }

    public ExternalKey getPropertyKey() {
        return this.propertyKey;
    }

    public void setPropertyKey(ExternalKey propertyKey) {
        this.propertyKey = propertyKey;
    }

    public Boolean getIsMeasurement() {
        return this.isMeasurement;
    }

    public void setIsMeasurement(Boolean isMeasurement) {
        this.isMeasurement = isMeasurement;
    }

    public boolean isValid() {
        return this.isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }
}