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
 * Represents utility stated directly about an alternative.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.INDIVIDUAL_UTILITY_TYPE)
public final class IndividualUtility extends Preference {
    /** The individual about which this utility is being stated. */
    private ExternalKey individualKey = new ExternalKey();
    /** The value of the utility, a number in the range [0,1]. */
    private Double value = Double.valueOf(0.0);

    protected IndividualUtility() {
        super();
    }

    /**
     * The individual utility key is made of the stakeholder and individual; not the value. It makes
     * no sense to have more than one conflicting stakeholder/individual combination.
     * 
     * @throws FatalException
     */
    public IndividualUtility(final ExternalKey stakeholderKey, final ExternalKey individualKey,
        final Double value, String namespace) throws FatalException {
        super(stakeholderKey, namespace);
        setIndividualKey(individualKey);
        setValue(value);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getIndividualKey());
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " individual : " + String.valueOf(getIndividualKey()) + //
    // " value: " + String.valueOf(getValue());
    // return result;
    // }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.STAKEHOLDER + NameUtil.EQUALS + getStakeholderKey().escapedString()
    // + NameUtil.AND //
    // + NameUtil.INDIVIDUAL + NameUtil.EQUALS + getIndividualKey().escapedString();
    // }

    @VisibleJoin(value = Individual.class, name = "individual")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getIndividualKey() {
        return this.individualKey;
    }

    public void setIndividualKey(ExternalKey individualKey) {
        this.individualKey = individualKey;
    }

    /** Not a key field */
    @VisibleField("value")
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public Double getValue() {
        return this.value;
    }

    public void setValue(final Double value) {
        this.value = value;
    }
}
