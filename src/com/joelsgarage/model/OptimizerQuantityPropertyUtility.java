/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents a utility expressed about QuantityFacts, wherein some particular value is most
 * preferred, and both higher and lower values have proportionally lower utility.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.OPTIMIZER_QUANTITY_PROPERTY_UTILITY)
public final class OptimizerQuantityPropertyUtility extends QuantityPropertyUtility {
    /** The preferred value of the quantity. */
    private Double factValue = Double.valueOf(0.0);

    /**
     * Optional. The unit in which the optimum value is expressed. This may, or may not, be the same
     * as the canonical unit for the Property.
     */
    private ExternalKey measurementUnitKey = new ExternalKey();

    protected OptimizerQuantityPropertyUtility() {
        super();
    }

    public OptimizerQuantityPropertyUtility(ExternalKey stakeholderKey, ExternalKey propertyKey,
        Double preferredUtility, Double minUtility, Double factValue,
        ExternalKey measurementUnitKey, String namespace) throws FatalException {
        super(stakeholderKey, propertyKey, preferredUtility, minUtility, namespace);
        setFactValue(factValue);
        setMeasurementUnitKey(measurementUnitKey);
        populateKey();
    }
    
    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
    }

    /** Return true if we have a unit, i.e. we think we're a measurement. */
    public boolean isMeasurement() {
        if (getMeasurementUnitKey().equals(new ExternalKey()))
            return false;
        return true;
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " fact value : " + String.valueOf(getFactValue());
    // result += " measurement unit : " + String.valueOf(getMeasurementUnitKey());
    // return result;
    //    }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    //

    /** The preferred value of the quantity */
    @VisibleField("fact_value")
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public Double getFactValue() {
        return this.factValue;
    }

    public void setFactValue(Double factValue) {
        this.factValue = factValue;
    }

    @VisibleJoin(value = MeasurementUnit.class, name = "measurement_unit")
    @WizardField(type = WizardField.Type.OPTIONAL, position = 5)
    public ExternalKey getMeasurementUnitKey() {
        return this.measurementUnitKey;
    }

    public void setMeasurementUnitKey(ExternalKey measurementUnitKey) {
        this.measurementUnitKey = measurementUnitKey;
    }
}
