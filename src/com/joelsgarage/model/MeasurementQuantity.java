/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleType;

/**
 * Represents something quantifiable, i.e. a "quantity." Could have a unit (7 inches) or not (7
 * dwarves).
 * 
 * In the statement, "the earth is 8000 miles across," the quantity is "length".
 * 
 * For each quantity, there are several units (meters, feet, yards), each of which contains
 * conversion factors. Each unit references a single "canonical" unit.
 * 
 * For some unit conversions (e.g. wire gauge), it's not a simple multiplication. Leave this for
 * later.
 * 
 * Something to consider:
 * 
 * Approximation
 * 
 * All measurements are approximate. Often, measurements are presented in "round numbers" in some
 * particular measurement scale, and so when converted to canonical units, they will have lots of
 * digits. Rounding the digits seems weird (i.e. it changes the measurement). Keeping them seems
 * weird too (makes it seem very precise). So instead, record the uncertainty in the measurement.
 * Example: 100 inches. We interpret that as 100 +/- 0.5 inches. Translate to canonical units: 2.54
 * +/- 0.0127 meters.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.MEASUREMENT_QUANTITY_TYPE)
public final class MeasurementQuantity extends ModelEntity {
    /** for the key */
    private String name;

    protected MeasurementQuantity() {
        super();
    }

    /**
     * @param name
     *            used for the key only
     * @param namespace
     * @return
     * @throws FatalException
     */
    public MeasurementQuantity(String name, String namespace) throws FatalException {
        super(namespace);
        setName(name);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getName());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }
}
