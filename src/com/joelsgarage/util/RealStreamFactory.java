/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface to real files.
 * 
 * @author joel
 * 
 */
public class RealStreamFactory implements StreamFactory {
    @Override
    public InputStream getStreamForInput(String filename) throws FileNotFoundException {
        return new FileInputStream(filename);
    }

    @Override
    public OutputStream getStreamForOutput(String filename) throws FileNotFoundException {
        return new FileOutputStream(filename);
    }
}
