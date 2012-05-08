/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util.dowser;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;

/**
 * This class has some annotations that the DowserFactory should find.
 * 
 * It doesn't actually *do* anything. :-)
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.INDIVIDUAL_TYPE)
public final class JoinClass extends ModelEntity {
    private ExternalKey joinField;

    @VisibleJoin(value = ScanInput.class, name = "foo")
    public ExternalKey getJoinField() {
        return this.joinField;
    }

    public void setJoinField(ExternalKey joinField) {
        this.joinField = joinField;
    }

    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

}
