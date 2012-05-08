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
 * A unit whose conversion function is an affine transformation.
 * 
 * It is implemented as a linear transformation followed by a translation. So a linear
 * transformation (e.g. feet <=> meters) is a special case.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.AFFINE_MEASUREMENT_UNIT_TYPE)
public final class AffineMeasurementUnit extends MeasurementUnit {
    public static final String SCALE = "scale"; //$NON-NLS-1$
    public static final String STANDARD_MEASUREMENT_UNIT = "standard_measurement_unit"; //$NON-NLS-1$
    public static final String TRANSLATION = "translation"; //$NON-NLS-1$
    /**
     * Measurement of the standard unit's size, expressed in the THIS UNIT.
     * <ul>
     * <li> e.g. a meter (standard unit) is 3.2808 feet (this unit)
     * <li> e.g. a celcius (standard unit) is 1.8 fahrenheit (this unit)
     * </ul>
     */
    private Double scale = Double.valueOf(0.0);
    /**
     * Reference a standardmeasurementunit that is the canonical unit, for the purposes of
     * conversion. Should not be null. This is the reference that links this unit to a quantity
     * (e.g. length).
     * 
     * This could go in the MeasurementType, but then the conversion would depend on it. Putting
     * this reference here keeps the conversion dependencies internal, though consensus on the
     * canonical unit is not enforced anywhere.
     */
    private ExternalKey standardMeasurementUnitKey = new ExternalKey();
    /**
     * Measurement of the canonical unit's zero, expressed in THIS UNIT.
     * 
     * e.g. the zero point of celsius is offset 32 FAHRENHEIT degrees
     * 
     * fahrenheit = 9/5 * celsius + 32 celsius = 5/9 * (fahnrenheit - 32)
     */
    private Double translation = Double.valueOf(0.0);

    protected AffineMeasurementUnit() {
        super();
    }

    public AffineMeasurementUnit(Double scale, ExternalKey standardMeasurementUnitKey,
        Double translation, String namespace) throws FatalException {
        setNamespace(namespace);
        setScale(scale);
        setStandardMeasurementUnitKey(standardMeasurementUnitKey);
        setTranslation(translation);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getScale());
        u.update(getStandardMeasurementUnitKey());
        u.update(getTranslation());
    }

//    @Override
//    @SuppressWarnings("nls")
//    public String toString() {
//        String result = super.toString();
//        result += " scale : " + String.valueOf(getScale()) + //
//            " standard measurement unit: " + String.valueOf(getStandardMeasurementUnitKey());
//        result += " translation: " + String.valueOf(getTranslation());
//        return result;
//    }

    /** Convert a value expressed in the canonical unit to this one */
    @Override
    public double convertFromStandard(double valueInCanonicalUnit) {
        double value =
            valueInCanonicalUnit * getScale().doubleValue() + getTranslation().doubleValue();
        return value;
    }

    /** Convert a value expressed in this unit to the canonical one */
    @Override
    public double convertToStandard(double valueInThisUnit) {
        double value =
            (valueInThisUnit - getTranslation().doubleValue()) / getScale().doubleValue();
        return value;
    }

    // /** multiple units may have identical scales, so, the key includes the name */
    // @Override
    // public String compositeKeyKey() {
    // return NAME + NameUtil.EQUALS
    // + NameUtil.encode(getName()) //
    // + NameUtil.AND //
    // + STANDARD_MEASUREMENT_UNIT + NameUtil.EQUALS
    // + getStandardMeasurementUnitKey().escapedString() //
    // + NameUtil.AND //
    // + SCALE + NameUtil.EQUALS + NameUtil.encode(String.valueOf(getScale())) //
    // + NameUtil.AND //
    // + TRANSLATION + NameUtil.EQUALS + NameUtil.encode(String.valueOf(getTranslation()));
    // }

    @VisibleJoin(value = StandardMeasurementUnit.class, name = STANDARD_MEASUREMENT_UNIT)
    @WizardField(type = WizardField.Type.OPTIONAL, position = 3)
    public ExternalKey getStandardMeasurementUnitKey() {
        return this.standardMeasurementUnitKey;
    }

    public void setStandardMeasurementUnitKey(ExternalKey standardMeasurementUnitKey) {
        this.standardMeasurementUnitKey = standardMeasurementUnitKey;
    }

    @VisibleField(SCALE)
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public Double getScale() {
        return this.scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    @VisibleField("translation")
    @WizardField(type = WizardField.Type.OPTIONAL, position = 2)
    public Double getTranslation() {
        return this.translation;
    }

    public void setTranslation(Double translation) {
        this.translation = translation;
    }
}
