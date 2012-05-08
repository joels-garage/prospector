/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.archive.io.ArchiveFileConstants;
import org.archive.io.ArchiveRecordHeader;
import org.archive.io.arc.ARCConstants;
import org.archive.io.arc.ARCRecord;
import org.archive.io.arc.ARCRecordMetaData;

import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.readers.ARCFileRecordReader;
import com.joelsgarage.extractor.Util.FatalException;

/**
 * Test XSLTransformerExtractor, i.e. run through real ARC input and do real XSLT transformation on
 * the records.
 * 
 * @author joel
 * 
 */
public class XSLTransformerExtractorTest extends TestCase {
    // this will match one of the patterns in the config.
    // TODO: extract out this config for testing
    private static final String TEST_URL = "http://plants.usda.gov/java/profile?foo"; //$NON-NLS-1$

    // private static final String TEST_URL = "http://www.joelsgarage.com/java/profile?foo";
    // //$NON-NLS-1$

    @SuppressWarnings("nls")
    protected String getContent() {
        String content =
            "<?xml version=\"1.0\"?>\n" + "<tag>this should produce very little output</tag>\n";
        return content;
    }

    @SuppressWarnings("nls")
    protected Map<String, String> getHeaders() {
        // see ARCConstants.REQUIRED_VERSION_1_HEADER_FIELDS
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ArchiveFileConstants.URL_FIELD_KEY, TEST_URL);
        headers.put(ARCConstants.IP_HEADER_FIELD_KEY, "foo");
        headers.put(ArchiveFileConstants.DATE_FIELD_KEY, "foo");
        headers.put(ArchiveFileConstants.MIMETYPE_FIELD_KEY, "text/html");
        headers.put(ArchiveFileConstants.LENGTH_FIELD_KEY, "100");
        headers.put(ArchiveFileConstants.VERSION_FIELD_KEY, "foo");
        headers.put(ArchiveFileConstants.ABSOLUTE_OFFSET_KEY, "foo");
        return headers;
    }

    @SuppressWarnings("nls")
    protected String getMinimumOutput() {
        String result =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><result xmlns:html=\"http://www.w3.org/1999/xhtml\">\n"
                + "</result>\n";
        return result;
    }

    @SuppressWarnings("nls")
    protected String getRealOutput() {
        String result =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><result xmlns:html=\"http://www.w3.org/1999/xhtml\">\n"
                + "<common-name>cous biscuitroot </common-name>\n"
                + "<latin-name>Lomatium  cous (S. Watson) J.M. Coult. &amp; Rose </latin-name>\n"
                + "<duration>Perennial</duration>\n" + "<habit>Forb/herb</habit>\n" + "</result>\n";
        return result;
    }

    public void testPropertiesLoad() {
        XSLTransformerExtractor ex =
            new XSLTransformerExtractor(null, null, 0, 0, Util.TEST_PROPERTIES);
        assertEquals(2, ex.getProperties().size());
        assertEquals(2, ex.getTransformers().size());
    }

    public static class FakeXSLTransformerExtractor extends XSLTransformerExtractor {
        public FakeXSLTransformerExtractor(RecordReader<ARCRecord> reader,
            RecordWriter<String> writer, int inLimit, int outLimit, String propertiesFile) {
            super(reader, writer, inLimit, outLimit, propertiesFile);
        }

        @Override
        public void done() {
            super.done();
        }

        @Override
        public void start() {
            super.start();
        }
    }

    /** Verify handling just one record */
    public void testHandleRecord() {
        MockExtractorWriter writer = new MockExtractorWriter();
        FakeXSLTransformerExtractor extractor =
            new FakeXSLTransformerExtractor(null, writer, 0, 0, Util.TEST_PROPERTIES);

        InputStream in = new ByteArrayInputStream(getContent().getBytes());
        Map<String, String> headers = getHeaders();

        try {
            ArchiveRecordHeader meta = new ARCRecordMetaData("foo", headers); //$NON-NLS-1$
            ARCRecord record = new ARCRecord(in, meta, 0, true, false, false);
            extractor.open();
            extractor.start();
            extractor.handleRecord(record);
            extractor.done();
            extractor.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(1, writer.getList().size());
        assertEquals(getMinimumOutput(), writer.getList().get(0));
    }

    /** Verify run() with just one record does the same thing. */
    public void testRun() {
        MockExtractorWriter writer = new MockExtractorWriter();

        try {
            Map<String, String> headers = getHeaders();
            ArchiveRecordHeader meta = new ARCRecordMetaData("foo", headers); //$NON-NLS-1$
            InputStream in = new ByteArrayInputStream(getContent().getBytes());
            ARCRecord record = new ARCRecord(in, meta, 0, true, false, false);
            List<ARCRecord> list = new ArrayList<ARCRecord>();
            list.add(record);
            MockExtractorReader reader = new MockExtractorReader(list);
            ARCExtractor extractor =
                new XSLTransformerExtractor(reader, writer, 0, 0, Util.TEST_PROPERTIES);
            extractor.run();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(1, writer.getList().size());
        assertEquals(getMinimumOutput(), writer.getList().get(0));
    }

    /** Read a real input file and extract stuff from it. */
    public void testRunWithData() {
        MockExtractorWriter writer = new MockExtractorWriter();
        try {
            ARCFileRecordReader reader = new ARCFileRecordReader(Util.getFile(Util.ARC1));
            // The 37th record in this file is a useful record.
            ARCExtractor extractor =
                new XSLTransformerExtractor(reader, writer, 37, 0, Util.TEST_PROPERTIES);
            extractor.run();
        } catch (FatalException e) {
            e.printStackTrace();
            fail();
        }
        // TODO: decouple the production
        assertEquals(1, writer.getList().size());
        assertEquals(getRealOutput(), writer.getList().get(0));
    }
}
