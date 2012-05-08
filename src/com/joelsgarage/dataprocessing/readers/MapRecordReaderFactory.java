/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.util.Map;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;

/**
 * A record reader factory that's just a map, supplied in the ctor.
 * 
 * @author joel
 * 
 */
public class MapRecordReaderFactory<T> implements RecordReaderFactory<T> {
    private Map<ReaderConstraint, RecordReader<T>> readers;

    public MapRecordReaderFactory(Map<ReaderConstraint, RecordReader<T>> readers) {
        this.readers = readers;
    }

    @Override
    public RecordReader<T> newInstance(ReaderConstraint constraint) {
        return this.readers.get(constraint);
    }
}
