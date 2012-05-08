/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reads string records from a file, one record per line.
 * 
 * @author joel
 */
public class FileRecordReader extends RecordReaderBase<String> {
    private BufferedReader reader;

    public FileRecordReader(InputStream input) {
        InputStreamReader streamReader = new InputStreamReader(input);
        setReader(new BufferedReader(streamReader));
    }

    @Override
    public void close() {
        try {
            getReader().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open() {
        // do nothing
    }

    @Override
    public String read() {
        try {
            return getReader().readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //
    //

    public BufferedReader getReader() {
        return this.reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

}
