/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * Writer implementation that writes String records to an OutputStream, for the dumper.
 * 
 * The string should not contain record separators; this class adds them.
 * 
 * @author joel
 * 
 */
public class StringRecordWriter implements RecordWriter<String> {
    OutputStream output;
    /** Results are flushed in batches this big */
    private int batchSize = 5000;
    private int recordNumber = 0;

    public StringRecordWriter(OutputStream output) {
        setOutput(output);
    }

    /**
     * Uses the default encoding to write the String to the OutputStream.
     * 
     * @see com.joelsgarage.extractor.ExtractorWriter#write(java.lang.String)
     */
    @Override
    public void write(String payload) {
        if (getOutput() == null) {
            Logger.getLogger(StringRecordWriter.class).error("Null output stream"); //$NON-NLS-1$
            return;
        }
        try {
            getOutput().write(payload.getBytes());
            getOutput().write('\n'); // record separator
            setRecordNumber(getRecordNumber() + 1);
            if (getRecordNumber() % getBatchSize() == 0) {
                Logger.getLogger(XMLFileRecordWriter.class).info(
                    "flushing after record: " + getRecordNumber()); //$NON-NLS-1$

                getOutput().flush(); // flush in batches
            }
        } catch (IOException e) {
            Logger.getLogger(StringRecordWriter.class).error("I/O exception on write"); //$NON-NLS-1$
            setOutput(null); // don't try to use it again
        }
    }

    @Override
    public void close() {
        if (getOutput() == null)
            return;
        try {
            getOutput().close();
        } catch (IOException e) {
            setOutput(null); // don't try to use it
        }
    }

    @Override
    public void open() {
        // do nothing
    }

    //

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getRecordNumber() {
        return this.recordNumber;
    }

    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

    public OutputStream getOutput() {
        return this.output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }
}
