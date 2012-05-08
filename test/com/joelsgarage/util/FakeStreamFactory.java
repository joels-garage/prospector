/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Interface to "fake" streams, backed by, say, strings, or arrays, or whatever.
 * 
 * @author joel
 * 
 */
public class FakeStreamFactory implements StreamFactory {
    private Map<String, ByteArrayInputStream> inputs = new HashMap<String, ByteArrayInputStream>();
    private Map<String, ByteArrayOutputStream> outputs =
        new HashMap<String, ByteArrayOutputStream>();

    public FakeStreamFactory(Map<String, ByteArrayInputStream> inputs) {
        this.inputs.putAll(inputs);
    }

    /** return the stream from the ctor map identified with the filename key; could return null */
    @Override
    public InputStream getStreamForInput(String filename) {
        Logger.getLogger(FakeStreamFactory.class).info("looking for filename: " + filename); //$NON-NLS-1$
        return this.inputs.get(filename);
    }

    @Override
    public OutputStream getStreamForOutput(String filename) {
        Logger.getLogger(FakeStreamFactory.class).info("looking for filename: " + filename); //$NON-NLS-1$
        ByteArrayOutputStream stream = this.outputs.get(filename);
        if (stream == null) {
            stream = new ByteArrayOutputStream();
            this.outputs.put(filename, stream);
        }
        return stream;
    }
}
