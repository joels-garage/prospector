/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.nodes;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * Copy input T to output T.
 * 
 * @author joel
 * 
 */
public class PassThroughProcessNode<T> extends ProcessNode<T, T> {
    public PassThroughProcessNode(RecordReader<T> reader, RecordWriter<T> writer, int inLimit,
        int outLimit) {
        super(reader, writer, inLimit, outLimit);
    }

    /**
     * @see ProcessNode#handleRecord(Object)
     */
    @Override
    protected boolean handleRecord(T record) {
        output(record);
        return true;
    }
}
