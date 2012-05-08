/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents a quantity. A measurement unit is optional. If null, then instances of this class are
 * unitless counts, e.g. e.g. "2.5 bathrooms."
 * 
 * Maybe also useful for facts wherein we don't yet know the unit, e.g. for "length (furlongs) = 3"
 * we could say "property = length(furlongs)" and "value = 3", and then some gardener later
 * transforms this into the synonymous measurement.
 * 
 * Now has an optional unit, for the new "abstract or final" regime.
 * 
 * TODO: uncertainty, using a distribution.
 * 
 * TODO: measurement ranges.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.QUANTITY_FACT_TYPE)
public final class QuantityFact extends Fact {
    public static final String VALUE = "value"; //$NON-NLS-1$
    public static final String EXPRESSED_UNIT = "expressed_unit"; //$NON-NLS-1$

    /**
     * The value of the quantity, e.g. in "the house had 2.5 bathrooms," the value is 2.5.
     */
    private Double value = Double.valueOf(0.0);

    /**
     * The MeasurementUnit for the value of this fact, which may be different from the canonical
     * unit. Putting the unit here avoids dependence on the choice of canonical unit.
     */
    private ExternalKey expressedUnitKey = new ExternalKey();

    protected QuantityFact() {
        super();
    }

    public QuantityFact(ExternalKey subjectKey, ExternalKey propertyKey, Double value,
        ExternalKey expressedUnitKey, String namespace) throws FatalException {
        super(subjectKey, propertyKey, namespace);
        setValue(value);
        setExpressedUnitKey(expressedUnitKey);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getValue());
        u.update(getExpressedUnitKey());
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setValue(Double.valueOf(input.get(VALUE)));
        setExpressedUnitKey(new ExternalKey(input.get(EXPRESSED_UNIT)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(VALUE, String.valueOf(getValue()));
        output.put(EXPRESSED_UNIT, String.valueOf(getExpressedUnitKey()));
    }

    /**
     * Return true if we have a unit, i.e. we think we're a measurement. MOVE THIS SOMEWHERE THAT
     * HAS ACCESS TO THE PROPERTY; IT'S NOT A FACT THING
     */
    public boolean measurement() {
        // THIS IS WRONG; need to check the unit on the PROPERTY
        if (getExpressedUnitKey() == null)
            return false;
        Logger.getLogger(QuantityFact.class).info("unit: " + getExpressedUnitKey().toString()); //$NON-NLS-1$
        if (getExpressedUnitKey() == null || getExpressedUnitKey().equals(new ExternalKey())) {
            Logger.getLogger(QuantityFact.class).info("not measurement"); //$NON-NLS-1$

            return false;
        }
        Logger.getLogger(QuantityFact.class).info("measurement"); //$NON-NLS-1$

        return true;
    }

    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayQuantityFact d = new DisplayQuantityFact(true);
    // d.setInstance(this);
    // return d;
    // }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " value: " + String.valueOf(getValue());
    // result += " expressed unit : " + String.valueOf(getExpressedUnitKey());
    // return result;
    //    }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.SUBJECT + NameUtil.EQUALS + getSubjectKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.PROPERTY + NameUtil.EQUALS + getPropertyKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.VALUE + NameUtil.EQUALS + NameUtil.encode(String.valueOf(getValue()));
    // }

    @VisibleField("value")
    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @VisibleJoin(value = MeasurementUnit.class, name = "expressed_unit")
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public ExternalKey getExpressedUnitKey() {
        return this.expressedUnitKey;
    }

    public void setExpressedUnitKey(ExternalKey expressedUnitKey) {
        this.expressedUnitKey = expressedUnitKey;
    }

    /** Covariant */
    @VisibleJoin(value = QuantityProperty.class, name = "property")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    @Override
    public ExternalKey getPropertyKey() {
        return super.getPropertyKey();
    }
}
