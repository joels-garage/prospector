/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.VisibleType;

/**
 * Represents a utility expressed about QuantityFacts, wherein higher values are linearly preferred
 * over lower ones. Units don't matter: just prefer larger normalized values.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.MAXIMIZER_QUANTITY_PROPERTY_UTILITY)
public final class MaximizerQuantityPropertyUtility extends QuantityPropertyUtility {
    protected MaximizerQuantityPropertyUtility() {
        super();
    }

    /** Key includes stakeholder and property */
    public MaximizerQuantityPropertyUtility(ExternalKey stakeholderKey, ExternalKey propertyKey,
        Double preferredUtility, Double minUtility, String namespace) throws FatalException {
        super(stakeholderKey, propertyKey, preferredUtility, minUtility, namespace);
        populateKey();
    }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }
}
