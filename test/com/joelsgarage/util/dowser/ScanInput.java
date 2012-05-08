/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util.dowser;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.VisibleType;

/**
 * This class has some annotations that the DowserFactory should find.
 * 
 * It doesn't actually *do* anything. :-)
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.DECISION_TYPE)
public class ScanInput extends ModelEntity {
    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    //    }

}
