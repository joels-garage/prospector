/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.RecordReader;

/**
 * @author joel
 */
public abstract class RecordReaderBase<T> implements RecordReader<T> {
    /**
     * Fill the specified collection with all the records from this reader.
     * 
     * @param <U>
     *            collection type, must be a subclass of the reader type
     * @param collectionClass
     *            class of above
     * @param collection
     *            target to fill
     */
    public <U extends T> void readAll(Class<U> collectionClass, Collection<U> collection) {
        T entity;
        while ((entity = read()) != null) {
            if (collectionClass.isInstance(entity)) {
                collection.add(collectionClass.cast(entity));
            } else {
                Logger.getLogger(RecordReaderBase.class).error(
                    "weird type: " + entity.getClass().getName()); //$NON-NLS-1$

            }
        }
    }
}
