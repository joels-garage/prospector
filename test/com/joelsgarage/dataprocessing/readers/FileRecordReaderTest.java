/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @author joel
 */
@SuppressWarnings("nls")
public class FileRecordReaderTest extends TestCase {
    public void testSimple() {
        String input = "foo\nbar\nbaz\n";
        InputStream stream = new ByteArrayInputStream(input.getBytes());
        FileRecordReader reader = new FileRecordReader(stream);
        assertEquals("foo", reader.read());
        assertEquals("bar", reader.read());
        assertEquals("baz", reader.read());
        assertNull(reader.read());
    }
}
