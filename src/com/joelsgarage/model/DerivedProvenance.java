/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Provenance indicating that one entity was derived from another.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.DERIVED_PROVENANCE_TYPE)
public final class DerivedProvenance extends Provenance {
    /** The entity (of any type) that constituted the antecedent of the subject */
    private ExternalKey antecedentKey = new ExternalKey();
    /**
     * Text description of the process that transformed the antecedent into the subject. This is
     * probably not a user-visible thing, so the precise string doesn't matter much.
     */
    private String derivation = new String();

    protected DerivedProvenance() {
        super();
    }

    /** all fields in key, since it's kinda a vote :-) */
    public DerivedProvenance(ExternalKey subjectKey, ExternalKey antecedentKey, String derivation,
        String namespace) throws FatalException {
        super(subjectKey, namespace);
        setAntecedentKey(antecedentKey);
        setDerivation(derivation);
        populateKey();
    }
    
    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getAntecedentKey());
        u.update(getDerivation());
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " antecedent : " + String.valueOf(getAntecedentKey()) + //
    // " derivation: " + String.valueOf(getDerivation());
    // return result;
    //    }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    public ExternalKey getAntecedentKey() {
        return this.antecedentKey;
    }

    public void setAntecedentKey(ExternalKey antecedentKey) {
        this.antecedentKey = antecedentKey;
    }

    @VisibleField("derivation")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public String getDerivation() {
        return this.derivation;
    }

    public void setDerivation(String derivation) {
        this.derivation = derivation;
    }
}
