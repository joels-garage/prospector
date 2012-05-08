/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.archive.io.ArchiveRecord;
import org.archive.io.arc.ARCReader;
import org.archive.io.arc.ARCReaderFactory;
import org.archive.io.arc.ARCRecord;

import com.joelsgarage.dataprocessing.readers.RecordReaderBase;

/**
 * ExtractorReader implementation that uses an InputStream, containing a compressed ARC.
 * 
 * @author joel
 * 
 */
public class StreamExtractorReader extends RecordReaderBase<ARCRecord> {
    private InputStream input;
    private String filename;
    /** The reader for the input file */
    private ARCReader arcReader;
    /** The iterator over that reader */
    private Iterator<ArchiveRecord> iterator;

    public StreamExtractorReader(String filename, InputStream input) {
        setFilename(filename);
        setInput(input);

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
    }

    @Override
    public void open() {
        try {
            setArcReader((ARCReader) ARCReaderFactory.get(getFilename(), getInput(), true));
        } catch (IOException e) {
            // Can't open the stream? Press on.
            e.printStackTrace();
            return;
        }
        if (getArcReader() == null)
            return;
        setARCReaderOptions(getArcReader());
        setIterator(getArcReader().iterator());
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

    public InputStream getInput() {
        return this.input;
    }

    public void setInput(InputStream input) {
        this.input = input;
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

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
