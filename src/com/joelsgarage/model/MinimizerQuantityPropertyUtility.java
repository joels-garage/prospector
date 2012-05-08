/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.VisibleType;

/**
 * Represents a utility expressed about QuantityFacts, wherein lower values are linearly preferred
 * over higher ones.
 * 
 * The unit doesn't matter in this context; just prefer lesser normalized values.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.MINIMIZER_QUANTITY_PROPERTY_UTILITY)
public final class MinimizerQuantityPropertyUtility extends QuantityPropertyUtility {
    protected MinimizerQuantityPropertyUtility() {
        super();
    }
    
    public MinimizerQuantityPropertyUtility(ExternalKey stakeholderKey, ExternalKey propertyKey,
        Double preferredUtility, Double minUtility, String namespace) throws FatalException {
        super(stakeholderKey, propertyKey, preferredUtility, minUtility, namespace);
        populateKey();
    }

    // /** TODO: finish this later */
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    //    }
}
