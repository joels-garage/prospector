/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.VisibleType;

/**
 * Represents a unit of measurement, e.g. length is measured in feet, meters, angstroms, etc.
 * 
 * Measurement fact values expressed in a subclass unit may be converted to another unit, using
 * conversions contained in the subclasses. See LinearMeasurementUnit and AffineMeasurementUnit.
 * 
 * TODO: more complicated unit types, e.g. wire gauges, color-space, degree-minute-second version of
 * angle.
 * 
 * A single unit may be expressed in various ways (e.g. "m", "meter"); these are UnitSynonyms.
 * 
 * This class may not be instantiated, because it lacks a reference to the quantity, found in
 * StandardMeasurementUnit.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.MEASUREMENT_UNIT_TYPE)
public abstract class MeasurementUnit extends ModelEntity {
    protected MeasurementUnit() {
        super();
    }

    public MeasurementUnit(String namespace) {
        super(namespace);
    }

    /** Convert a value expressed in the standard unit to this one */
    public abstract double convertFromStandard(double valueInCanonicalUnit);

    /** Convert a value expressed in this unit to the standard one */
    public abstract double convertToStandard(double valueInThisUnit);
}
