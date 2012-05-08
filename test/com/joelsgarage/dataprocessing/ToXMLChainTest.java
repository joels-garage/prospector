/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.Element;

import com.joelsgarage.dataprocessing.nodes.ToXMLProcessNode;
import com.joelsgarage.dataprocessing.readers.ListRecordReader;
import com.joelsgarage.dataprocessing.writers.XMLFileRecordWriter;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.StandardMeasurementUnit;
import com.joelsgarage.model.StringProperty;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.TestData;

/**
 * Tests the ToXMLProcessNode and the XMLFileRecordWriter.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ToXMLChainTest extends TestCase {
    private RecordReader<ModelEntity> reader;
    private RecordWriter<Element> writer;
    private ProcessNode<ModelEntity, Element> processNode;
    private ByteArrayOutputStream outputStream;
    private TestData testData;

    private List<ModelEntity> input = new ArrayList<ModelEntity>();

    private void initializeInput() throws FatalException {
        this.input.add(new Individual("my name", "foo"));
        this.input.add(new StringProperty(this.testData.clazz.makeKey(), "another name", "boo"));
        this.input.add(new StandardMeasurementUnit(this.testData.measurementQuantity.makeKey(),
            "another name", "boo"));
    }

    private String expectedOutput = //
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
            "<container>\n" + //
            "<Individual>\n" + //
            "  <id>ZmWSa5YfEKc_Zo5k</id>\n" + //
            "  <name>my name</name>\n" + //
            "  <namespace>foo</namespace>\n" + //
            "</Individual>\n" + //
            "<StringProperty>\n" + //
            "  <domainClassKey>\n" + //
            "    <key>kDwrNyFfaRhzLlON</key>\n" + //
            "    <type>class</type>\n" + //
            "  </domainClassKey>\n" + //
            "  <id>0SNlN87hLwgo8nLO</id>\n" + //
            "  <name>another name</name>\n" + //
            "  <namespace>boo</namespace>\n" + //
            "</StringProperty>\n" + //
            "<StandardMeasurementUnit>\n" + //
            "  <id>jtDXgYaqqAPEHEDj</id>\n" + //
            "  <measurementQuantityKey>\n" + //
            "    <key>pM9FfMvdqh2geeca</key>\n" + //
            "    <type>measurement_quantity</type>\n" + //
            "  </measurementQuantityKey>\n" + //
            "  <name>another name</name>\n" + //
            "  <namespace>boo</namespace>\n" + //
            "</StandardMeasurementUnit></container>";

    @Override
    protected void setUp() throws FatalException {
        this.testData = new TestData();
        initializeInput();
        setOutputStream(new ByteArrayOutputStream());
        setReader(new ListRecordReader<ModelEntity>(getInput()));
        setWriter(new XMLFileRecordWriter(getOutputStream()));
        setProcessNode(new ToXMLProcessNode(getReader(), getWriter(), 0, 0));
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

        String testOutput = getOutputStream().toString();

        // It's a pass-through; these lists should have identical contents.
        assertEquals(getExpectedOutput(), testOutput);
    }

    // Accessors

    public RecordReader<ModelEntity> getReader() {
        return this.reader;
    }

    public void setReader(RecordReader<ModelEntity> reader) {
        this.reader = reader;
    }

    public ProcessNode<ModelEntity, Element> getProcessNode() {
        return this.processNode;
    }

    public void setProcessNode(ProcessNode<ModelEntity, Element> processNode) {
        this.processNode = processNode;
    }

    public List<ModelEntity> getInput() {
        return this.input;
    }

    public void setInput(List<ModelEntity> input) {
        this.input = input;
    }

    public RecordWriter<Element> getWriter() {
        return this.writer;
    }

    public void setWriter(RecordWriter<Element> writer) {
        this.writer = writer;
    }

    public ByteArrayOutputStream getOutputStream() {
        return this.outputStream;
    }

    public void setOutputStream(ByteArrayOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public String getExpectedOutput() {
        return this.expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }
}
