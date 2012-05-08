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
 * Represents a preference about IndividualFacts, i.e. favoring individuals described by other
 * individuals, in relative terms (e.g. blue items are preferred over green ones by 0.2, or "i like
 * democrats better than republicans").
 * 
 * This is identical to IndividualPropertyPreference except for the inclusion, through the
 * superclass, PropertyPreference, of a Property. The idea is that individual preferences may depend
 * on their role, i.e. in a single decision, an individual may be a value for more than one
 * property. Maybe this is overly complex, and global preferences are good enough?
 * 
 * TODO: resolve this question after thinking about it for awhile. The only real conflicts I see are
 * 
 * 1. when the decision class is also a property range class. can't think of any examples of that.
 * 
 * 2. when the same range class is used in multiple properties. other than the obvious dimension
 * example (i.e. range = distance, for width, height, length), and color (i.e. range = color, for
 * things like trim-color and body-color), i dunno. maybe the color example is motivation enough?
 * saying i like white, in general, isn't useful. NEW: another example of this is broadly-useful
 * individuals like "low, medium, high", a scale which should not be repeated over and over, once
 * for each property, but whose meaning (and thus corresponding preferencing) varies depending on
 * the property context.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.INDIVIDUAL_PROPERTY_PREFERENCE_TYPE)
public final class IndividualPropertyPreference extends PropertyPreference {
    /**
     * The individual functioning as the primary value in a comparison (i.e. value > 0.5 means this
     * value is preferred)
     */
    private ExternalKey primaryIndividualKey = new ExternalKey();
    /**
     * The individual functioning as the secondary value.
     */
    private ExternalKey secondaryIndividualKey = new ExternalKey();
    /**
     * The value, in the range [0,1].
     */
    private Double value = Double.valueOf(0.0);

    protected IndividualPropertyPreference() {
        super();
    }

    /** Key is stakeholder, property, primary, and secondary */
    public IndividualPropertyPreference(ExternalKey stakeholderKey, ExternalKey propertyKey,
        ExternalKey primaryIndividualKey, ExternalKey secondaryIndividualKey, Double value,
        String namespace) throws FatalException {
        super(stakeholderKey, propertyKey, namespace);
        setPrimaryIndividualKey(primaryIndividualKey);
        setSecondaryIndividualKey(secondaryIndividualKey);
        setValue(value);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getPrimaryIndividualKey());
        u.update(getSecondaryIndividualKey());
    }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    /** Covariant */
    @VisibleJoin(value = IndividualProperty.class, name = "property")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    @Override
    public ExternalKey getPropertyKey() {
        // TODO Auto-generated method stub
        return super.getPropertyKey();
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result +=
    // " primary individual : " + String.valueOf(getPrimaryIndividualKey())
    // + " secondary individual: " + String.valueOf(getSecondaryIndividualKey()) + //
    // " value: " + String.valueOf(getValue());
    //        return result;
    //    }

    @VisibleJoin(value = Individual.class, name = "primary_individual")
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public ExternalKey getPrimaryIndividualKey() {
        return this.primaryIndividualKey;
    }

    public void setPrimaryIndividualKey(ExternalKey primaryIndividualKey) {
        this.primaryIndividualKey = primaryIndividualKey;
    }

    @VisibleJoin(value = Individual.class, name = "secondary_individual")
    @WizardField(type = WizardField.Type.REQUIRED, position = 5)
    public ExternalKey getSecondaryIndividualKey() {
        return this.secondaryIndividualKey;
    }

    public void setSecondaryIndividualKey(ExternalKey secondaryIndividualKey) {
        this.secondaryIndividualKey = secondaryIndividualKey;
    }

    @VisibleField("value")
    @WizardField(type = WizardField.Type.REQUIRED, position = 6)
    public Double getValue() {
        return this.value;
    }

    public void setValue(final Double value) {
        this.value = value;
    }
}
