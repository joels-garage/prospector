/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents a unit of measurement, e.g. length is measured in feet, meters, angstroms, etc.
 * 
 * Measurement fact values expressed in a subclass unit may be converted to another unit, using
 * conversions contained in the subclasses. See LinearMeasurementUnit and AffineMeasurementUnit.
 * 
 * TODO: more complicated unit types, e.g. wire gauges, color-space, degree-minute-second version of
 * angle.
 * 
 * A single unit may be expressed in various ways (e.g. "m", "meter"); these are UnitAbbreviations.
 * 
 * Instances of this class are canonical, i.e. there are no conversion functions.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.STANDARD_MEASUREMENT_UNIT_TYPE)
public final class StandardMeasurementUnit extends MeasurementUnit {
    /** The MeasurementQuantity that this unit measures, e.g. length, mass */
    private ExternalKey measurementQuantityKey = new ExternalKey();

    /** This is not persisted; it's just included in the hash key */
    private String name;

    protected StandardMeasurementUnit() {
        super();
    }

    /**
     * @param measurementTypeKey
     * @param name
     *            used for key only
     * @param namespace
     * @return
     * @throws FatalException
     */
    public StandardMeasurementUnit(ExternalKey measurementQuantityKey, String name, String namespace)
        throws FatalException {
        super(namespace);
        setName(name);
        setMeasurementQuantityKey(measurementQuantityKey);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getMeasurementQuantityKey());
        u.update(getName());
    }

    /** Since this *is* the standard unit, the conversion is identity */
    @Override
    public double convertFromStandard(double valueInCanonicalUnit) {
        return valueInCanonicalUnit;
    }

    /** Since this *is* the standard unit, the conversion is identity */
    @Override
    public double convertToStandard(double valueInThisUnit) {
        return valueInThisUnit;
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " measurement quantity : " + String.valueOf(getMeasurementQuantityKey());
    // return result;
    //    }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    //

    @VisibleJoin(value = MeasurementQuantity.class, name = "measurement_quantity")
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getMeasurementQuantityKey() {
        return this.measurementQuantityKey;
    }

    public void setMeasurementQuantityKey(ExternalKey measurementTypeKey) {
        this.measurementQuantityKey = measurementTypeKey;
    }

    /** Not persisted */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
