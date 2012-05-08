/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents a property whose value is a quantity, i.e. magnitude or multitude, which may have a
 * unit (e.g. "3 feet"), or not (e.g. "3 bedrooms").
 * 
 * In the statement, "the earth is 8000 miles across", the property is "diameter," with domain
 * "planet" (this distinguishes planetary diameter from, say, diameter of human hair) and the
 * canonical unit is probably meters.
 * 
 * Note, this entity represents a SINGLE measurement, i.e. 100 +/- 0.5.
 * 
 * Range measurements are complicated. Some are just very large errors, i.e. 75-125 == 100 +/- 25,
 * so the measurement is in fact a point, we just don't know exactly what point. A
 * functionally-identical version of this idea is the "canonical individual," whose measurements are
 * the union of all the corresponding concrete individuals. For example, the canonical human height
 * is something like 32-86 inches. Obviously some kind of distribution would be good here.
 * 
 * But some measurements indicate some kind of flexibility, i.e. a zoom lens can be adjusted, so
 * that it really can function as 75, or 100, or 125, if desired. These two kinds of "range" must be
 * treated differently with respect to preferences. In short, the first kind of range only partially
 * satisfies partially-overlapping constraints, whereas the second *fully* satisfies *partially*
 * overlapping constraints.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.QUANTITY_PROPERTY_TYPE)
public final class QuantityProperty extends Property {
    public static final String MEASUREMENT_QUANTITY = "measurement_quantity"; //$NON-NLS-1$
    public static final String MEASUREMENT_UNIT = "measurement_unit"; //$NON-NLS-1$
    /**
     * The MeasurementQuantity being measured, e.g. "length"
     */
    private ExternalKey measurementQuantityKey = new ExternalKey();
    /**
     * The canonical unit for this property, e.g. distance gained in a football game is measured in
     * yards. Note this unit has nothing to do with unit CONVERSION. It's not the standard unit for
     * the quantity, it's just the canonical unit used in display.
     * 
     * Maybe this is should be a display preference rather than a core thing.
     */
    private ExternalKey measurementUnitKey = new ExternalKey();

    protected QuantityProperty() {
        super();
    }

    // TODO: replace these key args with object args (to facilitate typechecking donchaknow
    /**
     * @param domainClassKey
     * @param name
     *            this is the "name" of the property (e.g. "height", Freebase calls this "key") --
     *            note this is not stored anywhere, it is only used as input to the key generator.
     *            the "real" name is externalized as a WordSense.
     * @param measurementQuantityKey
     * @param measurementUnitKey
     * @param namespace
     * @return
     * @throws FatalException
     */
    public QuantityProperty(ExternalKey domainClassKey, String name,
        ExternalKey measurementQuantityKey, ExternalKey measurementUnitKey, String namespace)
        throws FatalException {
        super(domainClassKey, name, namespace);
        setMeasurementQuantityKey(measurementQuantityKey);
        setMeasurementUnitKey(measurementUnitKey);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getMeasurementQuantityKey());
        u.update(getMeasurementUnitKey());
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setMeasurementQuantityKey(new ExternalKey(input.get(MEASUREMENT_QUANTITY)));
        setMeasurementUnitKey(new ExternalKey(input.get(MEASUREMENT_UNIT)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(MEASUREMENT_QUANTITY, String.valueOf(getMeasurementQuantityKey()));
        output.put(MEASUREMENT_UNIT, String.valueOf(getMeasurementUnitKey()));
    }

    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayQuantityProperty d = new DisplayQuantityProperty(true);
    // d.setInstance(this);
    // return d;
    // }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result +=
    // " measurement quantity : " + String.valueOf(getMeasurementQuantityKey())
    // + " measurement unit: " + String.valueOf(getMeasurementUnitKey());
    // return result;
    // }

    //

    @VisibleJoin(value = MeasurementQuantity.class, name = MEASUREMENT_QUANTITY)
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getMeasurementQuantityKey() {
        return this.measurementQuantityKey;
    }

    public void setMeasurementQuantityKey(ExternalKey measurementQuantityKey) {
        this.measurementQuantityKey = measurementQuantityKey;
    }

    @VisibleJoin(value = MeasurementUnit.class, name = MEASUREMENT_UNIT)
    @WizardField(type = WizardField.Type.OPTIONAL, position = 4)
    public ExternalKey getMeasurementUnitKey() {
        return this.measurementUnitKey;
    }

    public void setMeasurementUnitKey(ExternalKey measurementUnitKey) {
        this.measurementUnitKey = measurementUnitKey;
    }
}
