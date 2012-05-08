/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.joelsgarage.util.FakeStreamFactory;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.TestData;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class TSVFileRecordWriterTest extends TestCase {
    public void testMakeRow() {
        List<String> names = new ArrayList<String>();
        names.add("foo");
        names.add("bar");
        Map<String, String> data = new HashMap<String, String>();
        data.put("bar", "bardata");
        data.put("foo", "foodata");
        List<String> output = TSVFileRecordWriter.makeRow(names, data);
        assertEquals(2, output.size());
        assertEquals("foodata", output.get(0));
        assertEquals("bardata", output.get(1));
    }

    public void testWriteRow() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<String> list = new ArrayList<String>();
        list.add("foo");
        list.add("bar");
        TSVFileRecordWriter.writeRow(outputStream, list);
        assertEquals("foo\tbar\n", outputStream.toString());
    }

    public void testWriteSubclass() throws FatalException {
        Map<String, ByteArrayInputStream> inputs = new HashMap<String, ByteArrayInputStream>();
        String subclassInput = "foo";
        inputs.put("foo.Subclass", new ByteArrayInputStream(subclassInput.getBytes()));
        FakeStreamFactory factory = new FakeStreamFactory(inputs);
        String basename = "foo";
        TSVFileRecordWriter writer = new TSVFileRecordWriter(factory, basename);
        writer.open();
        TestData testData = new TestData();
        writer.write(testData.subclass);
        writer.close();

        OutputStream stream = factory.getStreamForOutput("foo.Subclass");
        if (stream instanceof ByteArrayOutputStream) {
            ByteArrayOutputStream bStream = (ByteArrayOutputStream) stream;
            String output = bStream.toString();
            assertNotNull(output);
            assertEquals(
                "id\tsubject\tobject\tnamespace\n" + //
                    "zYbh8SLkxc_U232F\tclass/i8kIz6NzedNZKW5h\tclass/xKjoij5a4IKw7YLV\tsubclassNamespace\n",
                output);
        } else {
            fail();
        }
    }

    public void testWriteMultipleTypes() throws FatalException {
        Map<String, ByteArrayInputStream> inputs = new HashMap<String, ByteArrayInputStream>();
        String subclassInput = "foo";
        inputs.put("foo.Subclass", new ByteArrayInputStream(subclassInput.getBytes()));
        FakeStreamFactory factory = new FakeStreamFactory(inputs);
        String basename = "foo";
        TSVFileRecordWriter writer = new TSVFileRecordWriter(factory, basename);
        writer.open();
        TestData testData = new TestData();
        writer.write(testData.subclass);
        writer.write(testData.individual);
        writer.close();

        OutputStream stream = factory.getStreamForOutput("foo.Subclass");
        if (stream instanceof ByteArrayOutputStream) {
            ByteArrayOutputStream bStream = (ByteArrayOutputStream) stream;
            String output = bStream.toString();
            assertNotNull(output);
            assertEquals(
                "id\tsubject\tobject\tnamespace\n" + //
                    "zYbh8SLkxc_U232F\tclass/i8kIz6NzedNZKW5h\tclass/xKjoij5a4IKw7YLV\tsubclassNamespace\n",
                output);
        } else {
            fail();
        }
        stream = factory.getStreamForOutput("foo.Individual");
        if (stream instanceof ByteArrayOutputStream) {
            ByteArrayOutputStream bStream = (ByteArrayOutputStream) stream;
            String output = bStream.toString();
            assertNotNull(output);
            assertEquals("id\tname\tnamespace\n" + //
                "XR499eUeQqoB4Njc\tindividualname\tindividualnamespace\n", output);
        } else {
            fail();
        }
    }

}
