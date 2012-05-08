package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents the importance of a property preference. Individual Preferences are derived from
 * Property Preferences using weights. A property preference relates to specific facts, whereas a
 * weight relates only to the property.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.PROPERTY_WEIGHT_TYPE)
public final class PropertyWeight extends ModelEntity {
    /**
     * The user-decision combination, which implies Class.
     */
    private ExternalKey stakeholderKey = new ExternalKey();
    /**
     * The property being evaluated, e.g. "color".
     * 
     * This is a polymorphic reference.
     */
    protected ExternalKey propertyKey = new ExternalKey();
    /**
     * The value, in the range [0,1].
     */
    private Double value = Double.valueOf(0.0);

    protected PropertyWeight() {
        super();
    }

    /** key is stakeholder/property */
    public PropertyWeight(ExternalKey stakeholderKey, ExternalKey propertyKey, Double value,
        String namespace) throws FatalException {
        super(namespace);
        setStakeholderKey(stakeholderKey);
        setPropertyKey(propertyKey);
        setValue(value);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getPropertyKey());
        u.update(getStakeholderKey());
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " stakeholder : " + String.valueOf(getStakeholderKey()) + //
    // " property: " + String.valueOf(getPropertyKey()) + //
    // " value: " + String.valueOf(getValue());
    // return result;
    // }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    // Could be any subclass of property
    @VisibleJoin(value = Property.class, name = "property")
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getPropertyKey() {
        return this.propertyKey;
    }

    public void setPropertyKey(final ExternalKey propertyKey) {
        this.propertyKey = propertyKey;
    }

    @VisibleJoin(value = Stakeholder.class, name = "stakeholder")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getStakeholderKey() {
        return this.stakeholderKey;
    }

    public void setStakeholderKey(final ExternalKey stakeholderKey) {
        this.stakeholderKey = stakeholderKey;
    }

    @VisibleField("value")
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public Double getValue() {
        return this.value;
    }

    public void setValue(final Double value) {
        this.value = value;
    }

}