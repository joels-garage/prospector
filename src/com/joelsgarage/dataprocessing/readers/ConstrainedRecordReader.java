/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import com.joelsgarage.dataprocessing.ReaderConstraint;

/**
 * A record reader that respects ReaderConstraints.
 * 
 * @author joel
 * 
 */
public abstract class ConstrainedRecordReader<T> extends RecordReaderBase<T> {
    private ReaderConstraint constraint;

    public ConstrainedRecordReader(ReaderConstraint constraint) {
        setConstraint(constraint);
    }
    
    //

    public ReaderConstraint getConstraint() {
        return this.constraint;
    }

    public void setConstraint(ReaderConstraint constraint) {
        this.constraint = constraint;
    }
}