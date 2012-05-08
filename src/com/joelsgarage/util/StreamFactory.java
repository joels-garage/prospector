/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface to files. For testing.
 * 
 * @author joel
 * 
 */
public interface StreamFactory {
    public InputStream getStreamForInput(String filename) throws FileNotFoundException;

    public OutputStream getStreamForOutput(String filename) throws FileNotFoundException;
}
