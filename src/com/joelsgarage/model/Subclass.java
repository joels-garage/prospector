/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleType;

/**
 * Subclass indicates that the subject's extension is contained within the object's extension.
 * 
 * That is, that all the members of the subject(child) class are also members of the object(parent)
 * class.
 * 
 * The properties of the child class also include all the properties of the parent class.
 * 
 * Note this has no fields, it's just a marker.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.SUBCLASS_TYPE)
public final class Subclass extends ClassAxiom {
    protected Subclass() {
        // no
    }

    public Subclass(ExternalKey subjectKey, ExternalKey objectKey, String namespace)
        throws FatalException {
        super(subjectKey, objectKey, namespace);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
    }
}
