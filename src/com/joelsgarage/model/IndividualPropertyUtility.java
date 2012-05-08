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
 * Represents a utility expressed about IndividualFacts (e.g. the utility of blue items is 0.8, or
 * "i like democrats"). Only useful if utility is expressed for more than one individual in the
 * range of a property, so the differential utility (preference) can be derived.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.INDIVIDUAL_PROPERTY_UTILITY_TYPE)
public final class IndividualPropertyUtility extends PropertyPreference {
    /** The individual described by this preference (e.g. "blue"). */
    private ExternalKey individualKey = new ExternalKey();

    /** The value, in the range [0,1]. */
    private Double value = Double.valueOf(0.0);

    protected IndividualPropertyUtility() {
        super();
    }

    /**
     * key includes stakeholder, property, and individual
     * 
     * @throws FatalException
     */
    public IndividualPropertyUtility(ExternalKey stakeholderKey, ExternalKey propertyKey,
        ExternalKey individualKey, Double value, String namespace) throws FatalException {
        super(stakeholderKey, propertyKey, namespace);
        setIndividualKey(individualKey);
        setValue(value);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getIndividualKey());
    }

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
    // result += " individual : " + String.valueOf(getIndividualKey()) + //
    // " value: " + getValue().toString();
    // return result;
    //    }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.STAKEHOLDER + NameUtil.EQUALS + getStakeholderKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.PROPERTY + NameUtil.EQUALS + getPropertyKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.INDIVIDUAL + NameUtil.EQUALS + getIndividualKey().escapedString();
    // }

    @VisibleJoin(value = Individual.class, name = "individual")
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public ExternalKey getIndividualKey() {
        return this.individualKey;
    }

    public void setIndividualKey(ExternalKey individualKey) {
        this.individualKey = individualKey;
    }

    @VisibleField("value")
    @WizardField(type = WizardField.Type.REQUIRED, position = 5)
    public Double getValue() {
        return this.value;
    }

    public void setValue(final Double value) {
        this.value = value;
    }
}
