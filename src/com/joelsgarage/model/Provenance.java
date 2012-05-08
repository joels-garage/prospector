/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.WizardField;

/**
 * Provenance. (Latin: prōvenīre, to come forth). The records authenticating an object or the
 * history of its ownership.
 * 
 * This entity describes the provenance of another entity.
 * 
 * For example, when a gardener derives a new entity from an old one, it should also produce a
 * provenance record reflecting the relationship. In this case, the provenance is "derived from X"
 * where X is another entity. There may be other kinds of provenance -- maybe there should be a
 * provenance type like "crawled from X" for site X.
 * 
 * A Provenance record is analogous to an IndividualFact, except that the subject and object are any
 * entity, not just individuals, and there's an additional String field describing the relationship.
 * 
 * Maybe this should be a controlled vocabulary instead, like an enum or a reference to a third
 * individual. For i18n, it shouldn't be a raw string, it should be a key to SOMETHING. It shouldn't
 * be an individual -- to do it that way would be putting metadata into the base model. So then an
 * enum? For now just a string.
 * 
 * @author joel
 * 
 */
public abstract class Provenance extends ModelEntity {
    /** The entity (of any type) whose provenance is herein described */
    private ExternalKey subjectKey = new ExternalKey();

    protected Provenance() {
        super();
    }

    protected Provenance(ExternalKey subjectKey, String namespace) {
        super(namespace);
        setSubjectKey(subjectKey);
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getSubjectKey());
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " subject : " + String.valueOf(getSubjectKey());
    // return result;
    // }

    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getSubjectKey() {
        return this.subjectKey;
    }

    public void setSubjectKey(ExternalKey subjectKey) {
        this.subjectKey = subjectKey;
    }
}
