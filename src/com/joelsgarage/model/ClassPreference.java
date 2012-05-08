/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents preference about a class of individuals. Really only useful for nontrivial classes,
 * i.e. classes whose description is not just a single property restriction. Maybe that's not very
 * useful anyway -- in the context of a decision, all the alternatives are members of some class, so
 * the ClassPreference idea is just choosing a subclass, which is probably done using some property
 * of the base class.
 * <p>
 * So, maybe this isn't useful.
 * <p>
 * What are some compound property preferences that would be made easier if this actually worked? I
 * can't think of any.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.CLASS_PREFERENCE_TYPE)
public final class ClassPreference extends Preference {
    /** The class about which this preference is being stated. */
    private ExternalKey primaryClassKey = new ExternalKey();
    /** The secondary class, for comparative preferences. Null otherwise. */
    private ExternalKey secondaryClassKey = new ExternalKey();
    /** The value, in the range [0,1]. */
    private Double value = Double.valueOf(0.0);

    protected ClassPreference() {
        super();
    }
    
    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
    }

    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayClassPreference d = new DisplayClassPreference(true);
    // d.setInstance(this);
    // return d;
    // }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " primary class: " + String.valueOf(getPrimaryClassKey()) + //
    // " secondary class: " + String.valueOf(getSecondaryClassKey()) + //
    // " value: " + String.valueOf(getValue());
    //        return result;
    //    }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    @VisibleJoin(value = Class.class, name = "primary_class")
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getPrimaryClassKey() {
        return this.primaryClassKey;
    }

    public void setPrimaryClassKey(ExternalKey primaryClassKey) {
        this.primaryClassKey = primaryClassKey;
    }

    @VisibleJoin(value = Class.class, name = "secondary_class")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getSecondaryClassKey() {
        return this.secondaryClassKey;
    }

    public void setSecondaryClassKey(ExternalKey secondaryClassKey) {
        this.secondaryClassKey = secondaryClassKey;
    }

    @VisibleField("value")
    @WizardField(type = WizardField.Type.REQUIRED, position = 4)
    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
