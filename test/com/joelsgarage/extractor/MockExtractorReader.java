/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.util.List;
import java.util.ListIterator;

import org.archive.io.arc.ARCRecord;

import com.joelsgarage.dataprocessing.readers.RecordReaderBase;

/**
 * Reader implementation that reads from a list. Provide the list in the construtor, read through it
 * once, and you're done.
 * 
 * @author joel
 * 
 */
public class MockExtractorReader extends RecordReaderBase<ARCRecord> {
    private List<ARCRecord> list;
    private ListIterator<ARCRecord> iterator;

    public MockExtractorReader(List<ARCRecord> list) {
        if (list == null)
            return;
        setList(list);
        setIterator(getList().listIterator());
    }

    /**
     * @see com.joelsgarage.extractor.ExtractorReader#read()
     */
    @Override
    public ARCRecord read() {
        if (getIterator() == null)
            return null;
        if (!getIterator().hasNext()) {
            return null;
        }
        return getIterator().next();
    }

    protected List<ARCRecord> getList() {
        return this.list;
    }

    protected void setList(List<ARCRecord> list) {
        this.list = list;
    }

    protected ListIterator<ARCRecord> getIterator() {
        return this.iterator;
    }

    protected void setIterator(ListIterator<ARCRecord> iterator) {
        this.iterator = iterator;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void open() {
        // TODO Auto-generated method stub

    }
}
