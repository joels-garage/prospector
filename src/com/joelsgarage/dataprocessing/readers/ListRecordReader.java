/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

/**
 * Reader implementation that reads T records from a list. Provide the list in the construtor, read
 * through it once, and you're done. It's for testing.
 * 
 * If you're clever and mutate the list after you construct the reader, fun stuff will happen.
 * 
 * @author joel
 */
public class ListRecordReader<T> extends RecordReaderBase<T> {
    private List<T> list;
    private ListIterator<T> iterator;

    public ListRecordReader(List<T> list) {
        if (list == null)
            return;
        setList(list);
        setIterator(getList().listIterator());
    }

    /**
     * @see com.joelsgarage.extractor.ExtractorReader#read()
     */
    @Override
    public T read() {
        Logger.getLogger(ListRecordReader.class).info("read()"); //$NON-NLS-1$

        if (getIterator() == null) {
            Logger.getLogger(ListRecordReader.class).info("null iterator"); //$NON-NLS-1$
            return null;
        }
        if (!getIterator().hasNext()) {
            Logger.getLogger(ListRecordReader.class).info("end of iterator"); //$NON-NLS-1$

            return null;
        }
        return getIterator().next();
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public void open() {
        // do nothing
    }

    //
    //

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public ListIterator<T> getIterator() {
        return this.iterator;
    }

    public void setIterator(ListIterator<T> iterator) {
        this.iterator = iterator;
    }
}
