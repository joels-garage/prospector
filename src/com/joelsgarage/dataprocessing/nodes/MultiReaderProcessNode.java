/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.nodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.util.FatalException;

/**
 * A process node with more than one (sequential) reader. What the node does with the readers is left open. For
 * example, they could be sorted, and this could be a merge-sort node. Or all of N streams could be
 * read in as lookup tables.
 * 
 * @author joel
 * 
 * @param <T>
 *            the input record type, i.e. supertype of all the input types.
 * @param <U>
 *            the record type to write
 */
public abstract class MultiReaderProcessNode<T, U> {
    /**
     * The input. Note these readers are (probably) all constrained to subtypes, but the parameter
     * type is the supertype. Whatever. Note this is ALL the readers, including the main one.
     */
    private Map<ReaderConstraint, RecordReader<T>> readers;
    private RecordReaderFactory<T> readerFactory;
    /** The output */
    private RecordWriter<U> writer;
    /** The maximum number of (total?) input records to read */
    private int inLimit = 0;
    /** The maximum number of output records to write */
    private int outLimit = 0;
    /** How many records we have written */
    private int outCount = 0;

    public MultiReaderProcessNode(RecordReaderFactory<T> readerFactory, RecordWriter<U> writer,
        int inLimit, int outLimit) {
        setWriter(writer);
        setInLimit(inLimit);
        setOutLimit(outLimit);
        setReaders(new HashMap<ReaderConstraint, RecordReader<T>>());
        setReaderFactory(readerFactory);
    }

    /** Specifies the readers this class wants to read from */
    public abstract Iterator<ReaderConstraint> getConstraints();

    /** Add the reader described by the specified constraint. Only useful before run() */
    public void addReader(ReaderConstraint constraint, RecordReader<T> reader) {
        getReaders().put(constraint, reader);
    }

    public RecordReader<T> getReader(ReaderConstraint constraint) {
        return getReaders().get(constraint);
    }

    /**
     * Your run() should call this before starting work, to do setup (e.g. populating lookups).
     * 
     * This method instantiates the readers from the factory.
     * 
     * @throws FatalException
     *             if we can't proceed
     * 
     */
    @SuppressWarnings("unused")
    protected void start() throws FatalException {
        Iterator<ReaderConstraint> iter = getConstraints();
        while (iter.hasNext()) {
            ReaderConstraint constraint = iter.next();
            Logger.getLogger(MultiReaderProcessNode.class).info(
                "working on constraint " + constraint.getClassConstraint().getName()); //$NON-NLS-1$
            // Don't instantiate any reader twice
            if (getReaders().containsKey(constraint)) {
                Logger.getLogger(MultiReaderProcessNode.class).info("reader configured twice"); //$NON-NLS-1$
                continue;
            }

            // This reader has its own session
            RecordReaderFactory<T> factory = getReaderFactory();
            if (factory == null) {
                Logger.getLogger(MultiReaderProcessNode.class).error("no factory!"); //$NON-NLS-1$
                continue;
            }
            RecordReader<T> reader = factory.newInstance(constraint);
            if (reader == null) {
                Logger.getLogger(MultiReaderProcessNode.class).error(
                    "no reader for constraint " + constraint.getClassConstraint().getName()); //$NON-NLS-1$
                continue;
            }

            // Add this reader to the reader map
            addReader(constraint, reader);
        }

    }

    /**
     * Process the input, write the output
     * 
     * @throws FatalException
     *             if we can't proceed
     */
    public abstract void run() throws FatalException;

    /**
     * Output a row to the writer. Updates the output record count. Probably don't need to override
     * this.
     */
    protected void output(U payload) {
        setOutCount(getOutCount() + 1);
        getWriter().write(payload);
    }

    /**
     * Your run() should call this when done, to do cleanup (e.g. output aggregates).
     */
    protected abstract void done();

    //

    protected Map<ReaderConstraint, RecordReader<T>> getReaders() {
        return this.readers;
    }

    protected void setReaders(Map<ReaderConstraint, RecordReader<T>> readers) {
        this.readers = readers;
    }

    protected RecordWriter<U> getWriter() {
        return this.writer;
    }

    protected void setWriter(RecordWriter<U> writer) {
        this.writer = writer;
    }

    public int getInLimit() {
        return this.inLimit;
    }

    public void setInLimit(int inLimit) {
        this.inLimit = inLimit;
    }

    public int getOutLimit() {
        return this.outLimit;
    }

    public void setOutLimit(int outLimit) {
        this.outLimit = outLimit;
    }

    public int getOutCount() {
        return this.outCount;
    }

    public void setOutCount(int outCount) {
        this.outCount = outCount;
    }

    protected RecordReaderFactory<T> getReaderFactory() {
        return this.readerFactory;
    }

    protected void setReaderFactory(RecordReaderFactory<T> readerFactory) {
        this.readerFactory = readerFactory;
    }
}
