/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.VisibleType;

/**
 * Represents a property whose value is a naked string value, put whatever you want in there.
 * 
 * Example: TODO ... maybe i don't really want this anyway?
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.STRING_PROPERTY_TYPE)
public final class StringProperty extends Property {
    // protected StringProperty() {
    // super();
    // }

    public StringProperty(ExternalKey domainClassKey, String name, String namespace)
        throws FatalException {
        super(domainClassKey, name, namespace);
        populateKey();
    }

//    @Override
//    protected void populateKey(KeyUtil u) {
//        super.populateKey(u);
//    }
}
