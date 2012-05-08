/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.util.List;

import junit.framework.TestCase;

import org.archive.io.arc.ARCRecord;
import org.archive.io.arc.ARCRecordMetaData;

import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.readers.ARCFileRecordReader;
import com.joelsgarage.extractor.Util.FatalException;

/**
 * Test a few features of reading files.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ExtractorTest extends TestCase {
	RecordReader<ARCRecord> reader;
	RecordWriter<String> writer;
	ARCExtractor extractor;

	int recordCount = 0;

	@Override
	protected void setUp() throws FatalException {
		setReader(new ARCFileRecordReader(Util.getFile(Util.ARC1)));
		setWriter(new MockExtractorWriter());
		setExtractor(new ARCExtractor(getReader(), getWriter(), 0, 0));
	}

	/**
	 * Verifies construction, i.e. doesn't do anything.
	 */
	public void testNothing() {
		assertNotNull(getExtractor());
	}

	/**
	 * Can we read an ARC file in testdata and find a record in it using the iterator?
	 */
	public void testArcReader() {
		MockExtractorWriter mockWriter = new MockExtractorWriter();
		// zero below means take all records.
		setExtractor(new ARCExtractor(getReader(), mockWriter, 0, 0) {
			@Override
			public boolean handleRecord(ARCRecord record) {
				// this outputs the record.
				super.handleRecord(record);
				ExtractorTest.this.setRecordCount(ExtractorTest.this.getRecordCount() + 1);
				if (ExtractorTest.this.getRecordCount() == 4) {
					ARCRecordMetaData metaData = record.getMetaData();
					String recordURI = metaData.getUrl();
					assertEquals("http://plants.usda.gov/java/characteristics", recordURI);
					return false; // false should terminate iteration
				} else if (ExtractorTest.this.getRecordCount() > 4) {
					// this shouldn't happen; we returned false previously.
					fail();
					return false;
				} else { // record 0, 1, or 2
					return true;
				}
			}

			@Override
			public void output(String payload) {
				getWriter().write(payload);
			}
		});
		this.getExtractor().run();

		List<String> output = mockWriter.getList();
		// we called output four times.
		assertEquals(4, output.size());
		assertEquals("http://plants.usda.gov/java/characteristics", output.get(3));
	}

	/**
	 * Verify the record count in the datafile.
	 */
	public void testRecordCount() {
		getExtractor().run();
		assertEquals(Util.ARC_ROWS, getExtractor().getInCount());
	}

	/**
	 * Verify the function of the "recordLimit" argument.
	 */
	public void testRecordLimit() {
		int recordLimit = 10;
		setExtractor(new ARCExtractor(getReader(), getWriter(), recordLimit, 0));
		getExtractor().run();
		assertEquals(recordLimit, getExtractor().getInCount());
	}

	// Accessors

	public RecordReader<ARCRecord> getReader() {
		return this.reader;
	}

	public void setReader(RecordReader<ARCRecord> reader) {
		this.reader = reader;
	}

	public RecordWriter<String> getWriter() {
		return this.writer;
	}

	public void setWriter(RecordWriter<String> writer) {
		this.writer = writer;
	}

	public ARCExtractor getExtractor() {
		return this.extractor;
	}

	public void setExtractor(ARCExtractor extractor) {
		this.extractor = extractor;
	}

	public int getRecordCount() {
		return this.recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
}
