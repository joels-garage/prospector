/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.NameUtil;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents the user role with respect to a particular decision.
 * 
 * Maybe this doesn't offer much value, and these fields should go in Preference instead? The reason
 * not to do that is that preference involves a property, and some stakeholders have no
 * property-specific preference, though they do care about the outcome in some unspecified way.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.STAKEHOLDER_TYPE)
public final class Stakeholder extends ModelEntity {
    /**
     * The decision to which this stakeholder role is relevant.
     */
    private ExternalKey decisionKey = new ExternalKey();
    /**
     * The user playing the role of stakeholder.
     */
    private ExternalKey userKey = new ExternalKey();

    protected Stakeholder() {
        super();
    }

    public Stakeholder(ExternalKey decisionKey, ExternalKey userKey, String namespace)
        throws FatalException {
        setNamespace(namespace);
        setDecisionKey(decisionKey);
        setUserKey(userKey);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getDecisionKey());
        u.update(getUserKey());

    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " decision: " + String.valueOf(getDecisionKey()) + //
    // " user: " + String.valueOf(getUserKey());
    // return result;
    //    }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.USER + NameUtil.EQUALS + getUserKey().escapedString() //
    // + NameUtil.AND //
    // + NameUtil.DECISION + NameUtil.EQUALS + getDecisionKey().escapedString();
    // }

    //
    //

    @VisibleJoin(value = Decision.class, name = NameUtil.DECISION)
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getDecisionKey() {
        return this.decisionKey;
    }

    public void setDecisionKey(ExternalKey decisionKey) {
        this.decisionKey = decisionKey;
    }

    @VisibleJoin(value = User.class, name = NameUtil.USER)
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getUserKey() {
        return this.userKey;
    }

    public void setUserKey(ExternalKey userKey) {
        this.userKey = userKey;
    }
}
