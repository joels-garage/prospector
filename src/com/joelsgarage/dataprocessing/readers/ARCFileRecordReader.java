/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.archive.io.ArchiveRecord;
import org.archive.io.arc.ARCReader;
import org.archive.io.arc.ARCReaderFactory;
import org.archive.io.arc.ARCRecord;

/**
 * Read ARCRecords from a file.
 * 
 * @author joel
 * 
 */
public class ARCFileRecordReader extends RecordReaderBase<ARCRecord> {
    private File inputFile;
    /** The reader for the input file */
    private ARCReader arcReader;
    /** The iterator over that reader */
    private Iterator<ArchiveRecord> iterator;

    public ARCFileRecordReader(File file) {
        setInputFile(file);
    }

    @Override
    public ARCRecord read() {
        if (getIterator().hasNext()) {
            return (ARCRecord) getIterator().next();
        }
        return null;
    }

    protected void setARCReaderOptions(ARCReader reader) {
        reader.setStrict(false); // don't fail if file is incorrectly formatted
        reader.setParseHttpHeaders(false); // don't parse the headers, just dump
        reader.setDigest(false); // don't make a SHA-1 digest
    }

    public File getInputFile() {
        return this.inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public ARCReader getArcReader() {
        return this.arcReader;
    }

    public void setArcReader(ARCReader arcReader) {
        this.arcReader = arcReader;
    }

    public Iterator<ArchiveRecord> getIterator() {
        return this.iterator;
    }

    public void setIterator(Iterator<ArchiveRecord> iterator) {
        this.iterator = iterator;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void open() {
        try {
            setArcReader(ARCReaderFactory.get(getInputFile()));
        } catch (IOException e) {
            // Can't open the URL? Press on.
            e.printStackTrace();
            return;
        }
        if (getArcReader() == null)
            return;
        setARCReaderOptions(getArcReader());
        setIterator(getArcReader().iterator());
    }
}
