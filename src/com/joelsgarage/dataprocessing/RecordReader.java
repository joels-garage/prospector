/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.Collection;

/**
 * Reads records of type T.
 * 
 * @author joel
 * 
 */
/**
 * @author joel
 * 
 * @param <T>
 */
public interface RecordReader<T> {
    /**
     * Call prior to reading. Does initialization.
     */
    public void open() throws InitializationException;

    /**
     * Read a record.
     * 
     * @return the next record, or null if no more records are available.
     */
    public T read();

    /**
     * Fill the specified collection with all the records to be read.
     * 
     * @param <U>
     *            type the collection contains
     * @param collectionClass
     *            what the collection contains
     * @param collection
     *            to fill
     */
    public <U extends T> void readAll(Class<U> collectionClass, Collection<U> collection);

    /**
     * Call after done reading.
     */
    public void close();
}
