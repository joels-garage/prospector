/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.nodes;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.readers.ListRecordReader;
import com.joelsgarage.dataprocessing.writers.ListRecordWriter;
import com.joelsgarage.model.WordSense;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.TestData;

/**
 * Verify production of Gazetteer entries.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ModelEntityToGazetteerProcessNodeTest extends TestCase {
    private RecordReader<WordSense> reader;
    private ListRecordWriter<String> writer;
    private ProcessNode<WordSense, String> processNode;
    private TestData testData;

    private List<WordSense> input = new ArrayList<WordSense>();

    private void initializeInput() {
        this.input.add(this.testData.wordSense1);
        this.input.add(this.testData.wordSense2);
        this.input.add(this.testData.wordSense3);
    }

    private List<String> expectedOutput = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;
        {

            add("individual 21&type=individual&key=individual/E63a4nepjZGYxJyQ");
            add("A Property&type=string_property&key=string_property/hOrM1tj7YR3sy_6b");
            add("Square Meter&type=standard_measurement_unit&key=standard_measurement_unit/LNj3SOP-qK6cyi7L");

            // add("<Individual><creatorKey><key>foe</key><namespace>fee</namespace><type>fie</type></creatorKey><key><key>baz</key><namespace>foo</namespace><type>bar</type></key><lastModified>some
            // date</lastModified><name>my name</name></Individual>");
            // add("<StringProperty><creatorKey><key>boe</key><namespace>bee</namespace><type>bie</type></creatorKey><domainClassKey><key></key><namespace></namespace><type></type></domainClassKey><key><key>faz</key><namespace>boo</namespace><type>far</type></key><lastModified>another
            // date</lastModified><name>another name</name></StringProperty>");
            // add("<StandardMeasurementUnit><creatorKey><key>boe</key><namespace>bee</namespace><type>bie</type></creatorKey><key><key>faz</key><namespace>boo</namespace><type>far</type></key><lastModified>another
            // date</lastModified><measurementQuantityKey><key></key><namespace></namespace><type></type></measurementQuantityKey><name>another
            // name</name></StandardMeasurementUnit>");
        }
    };

    int recordCount = 0;

    @Override
    protected void setUp() throws FatalException {
        this.testData = new TestData();
        initializeInput();
        setReader(new ListRecordReader<WordSense>(getInput()));
        setWriter(new ListRecordWriter<String>());
        setProcessNode(new ModelEntityToGazetteerProcessNode(getReader(), getWriter(), 0, 0));
    }

    /**
     * Verifies construction, i.e. doesn't do anything.
     */
    public void testNothing() {
        assertNotNull(getProcessNode());
    }

    /**
     * Verify duplication of the input on the output.
     */
    public void testPassThrough() {
        this.getProcessNode().run();

        List<String> testOutput = getWriter().getList();
        assertEquals(3, testOutput.size());
        for (int i = 0; i < getExpectedOutput().size(); ++i) {
            assertEquals(getExpectedOutput().get(i), testOutput.get(i));
        }
    }

    // Accessors

    public int getRecordCount() {
        return this.recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public RecordReader<WordSense> getReader() {
        return this.reader;
    }

    public void setReader(RecordReader<WordSense> reader) {
        this.reader = reader;
    }

    public ListRecordWriter<String> getWriter() {
        return this.writer;
    }

    public void setWriter(ListRecordWriter<String> writer) {
        this.writer = writer;
    }

    public ProcessNode<WordSense, String> getProcessNode() {
        return this.processNode;
    }

    public void setProcessNode(ProcessNode<WordSense, String> processNode) {
        this.processNode = processNode;
    }

    public List<WordSense> getInput() {
        return this.input;
    }

    public void setInput(List<WordSense> input) {
        this.input = input;
    }

    public List<String> getExpectedOutput() {
        return this.expectedOutput;
    }

    public void setExpectedOutput(List<String> expectedOutput) {
        this.expectedOutput = expectedOutput;
    }
}
