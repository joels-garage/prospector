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
 * Represents the question at hand. Each user may have hundreds of decisions to make over their life
 * span, so, this is a very large table (hundreds of billions of records), with a 64 bit primary
 * key.
 * 
 * TODO: some sort of "open question" flag, to keep from bugging users about old stuff.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.DECISION_TYPE)
public final class Decision extends ModelEntity {
    /** human-readable (string not null) "choosing a private school in palo alto" */
    private String description = new String();
    /** The type of candidate thing, e.g. PrivateSchool */
    private ExternalKey classKey = new ExternalKey();
    /**
     * not persisted; used in the key, creator of this record, so that different users can use the
     * same description.
     */
    private ExternalKey userKey = new ExternalKey();

    protected Decision() {
        super();
    }

    public Decision(ExternalKey classKey, String description, ExternalKey userKey, String namespace)
        throws FatalException {
        super(namespace);
        setClassKey(classKey);
        setDescription(description);
        setUserKey(userKey);
        populateKey();
    }

    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayDecision d = new DisplayDecision(true);
    // d.setInstance(this);
    // return d;
    // }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getDescription());
        u.update(getClassKey());
        u.update(getUserKey());
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " description : " + String.valueOf(getDescription()) + //
    // " class: " + String.valueOf(getClassKey());
    // return result;
    //    }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    @VisibleField("description")
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @VisibleJoin(value = Class.class, name = "class")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getClassKey() {
        return this.classKey;
    }

    /** The type of candidate thing, e.g. PrivateSchool */
    public void setClassKey(ExternalKey classKey) {
        this.classKey = classKey;
    }

    public ExternalKey getUserKey() {
        return this.userKey;
    }

    public void setUserKey(ExternalKey userKey) {
        this.userKey = userKey;
    }
}
