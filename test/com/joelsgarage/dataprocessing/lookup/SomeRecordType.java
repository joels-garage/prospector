/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.lookup;

import com.joelsgarage.model.ModelEntity;

/**
 * A record type to use to test the lookup process node.
 * 
 * @author joel
 * 
 */
public class SomeRecordType extends ModelEntity {
    private String primaryKey;
    private String data;

    /**
     * @param primaryKey
     * @param data
     */
    public SomeRecordType(String primaryKey, String data) {
        super();
        this.primaryKey = primaryKey;
        this.data = data;
    }

    // @Override
    // public String compositeKeyKey() {
    // Logger.getLogger(Annotation.class).error("don't call me"); //$NON-NLS-1$
    // return null;
    // }

    public String getPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
