package com.joelsgarage.dataprocessing.writers;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.joelsgarage.dataprocessing.writers.StringRecordWriter;

@SuppressWarnings("nls")
public class StringRecordWriterTest extends TestCase {
    public void testWrite() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        StringRecordWriter writer = new StringRecordWriter(output);

        writer.write("foo");
        writer.write("bar");

        assertEquals("foo\nbar\n", output.toString());
    }

}
