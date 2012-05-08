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
 * Represents preference directly about an alternative. This is a fuzzy preference. For an absolute
 * utility measurement, see IndividualUtility.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.INDIVIDUAL_PREFERENCE_TYPE)
public final class IndividualPreference extends Preference {
    /** The primary individual about which this preference is being stated. */
    private ExternalKey primaryIndividualKey = new ExternalKey();
    /** The secondary individual. */
    private ExternalKey secondaryIndividualKey = new ExternalKey();
    /** The value of the preference, a number in the range [0,1]. */
    private Double value = Double.valueOf(0.0);

    protected IndividualPreference() {
        super();
    }

    /**
     * Key is stakeholder, primary, and secondary
     * 
     * @throws FatalException
     */
    public IndividualPreference(final ExternalKey stakeholderKey,
        final ExternalKey primaryIndividualKey, final ExternalKey secondaryIndividualKey,
        final Double value, String namespace) throws FatalException {
        super(stakeholderKey, namespace);
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

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    @VisibleField("value")
    @WizardField(type = WizardField.Type.REQUIRED, position = 5)
    public Double getValue() {
        return this.value;
    }

    public void setValue(final Double value) {
        this.value = value;
    }

    @VisibleJoin(value = Individual.class, name = "primary_individual")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getPrimaryIndividualKey() {
        return this.primaryIndividualKey;
    }

    public void setPrimaryIndividualKey(ExternalKey primaryIndividualKey) {
        this.primaryIndividualKey = primaryIndividualKey;
    }

    @VisibleJoin(value = Individual.class, name = "secondary_individual")
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public ExternalKey getSecondaryIndividualKey() {
        return this.secondaryIndividualKey;
    }

    public void setSecondaryIndividualKey(ExternalKey secondaryIndividualKey) {
        this.secondaryIndividualKey = secondaryIndividualKey;
    }
}
