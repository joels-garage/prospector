/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.Element;

import com.joelsgarage.dataprocessing.nodes.ToXMLProcessNode;
import com.joelsgarage.dataprocessing.readers.ListRecordReader;
import com.joelsgarage.dataprocessing.writers.ListRecordWriter;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.StandardMeasurementUnit;
import com.joelsgarage.model.StringProperty;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.TestData;

/**
 * Test dataprocessing wrapper of Translator.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ToXMLProcessNodeTest extends TestCase {
    private RecordReader<ModelEntity> reader;
    private ListRecordWriter<Element> writer;
    private ProcessNode<ModelEntity, Element> processNode;

    private List<ModelEntity> input = new ArrayList<ModelEntity>();
    private TestData testData;

    private void initializeInput() throws FatalException {
        this.input.add(new Individual("my name", "foo"));
        this.input.add(new StringProperty(this.testData.clazz.makeKey(), "another name", "boo"));
        this.input.add(new StandardMeasurementUnit(this.testData.measurementQuantity.makeKey(),
            "another name", "boo"));
    }

    private List<String> expectedOutput = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;
        {
            add("<Individual><id>ZmWSa5YfEKc_Zo5k</id><name>my name</name><namespace>foo</namespace></Individual>");
            add("<StringProperty><domainClassKey><key>kDwrNyFfaRhzLlON</key><type>class</type></domainClassKey><id>0SNlN87hLwgo8nLO</id><name>another name</name><namespace>boo</namespace></StringProperty>");
            add("<StandardMeasurementUnit><id>jtDXgYaqqAPEHEDj</id><measurementQuantityKey><key>pM9FfMvdqh2geeca</key><type>measurement_quantity</type></measurementQuantityKey><name>another name</name><namespace>boo</namespace></StandardMeasurementUnit>");
        }
    };

    int recordCount = 0;

    @Override
    protected void setUp() throws FatalException {
        this.testData = new TestData();
        initializeInput();
        setReader(new ListRecordReader<ModelEntity>(getInput()));
        setWriter(new ListRecordWriter<Element>());
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

        List<Element> testOutput = getWriter().getList();
        // we called output four times.
        assertEquals(3, testOutput.size());
        // It's a pass-through; these lists should have identical contents.
        for (int i = 0; i < getExpectedOutput().size(); ++i) {
            assertEquals(getExpectedOutput().get(i), testOutput.get(i).asXML());
        }
    }

    // Accessors

    public int getRecordCount() {
        return this.recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public RecordReader<ModelEntity> getReader() {
        return this.reader;
    }

    public void setReader(RecordReader<ModelEntity> reader) {
        this.reader = reader;
    }

    public ListRecordWriter<Element> getWriter() {
        return this.writer;
    }

    public void setWriter(ListRecordWriter<Element> writer) {
        this.writer = writer;
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

    public List<String> getExpectedOutput() {
        return this.expectedOutput;
    }

    public void setExpectedOutput(List<String> expectedOutput) {
        this.expectedOutput = expectedOutput;
    }
}
