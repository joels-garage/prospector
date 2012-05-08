/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * Writer implementation that writes Element records to an OutputStream, for the dumper.
 * 
 * @author joel
 * 
 */
public class XMLFileRecordWriter implements RecordWriter<Element> {
    /** XML tag to contain the elements written */
    private static final String CONTAINER = "container"; //$NON-NLS-1$

    private XMLWriter writer;

    /** container element for the output file */
    private Element container;

    /** Results are flushed in batches this big */
    private int batchSize = 5000;
    private int recordNumber = 0;

    public XMLFileRecordWriter(OutputStream output) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            setWriter(new XMLWriter(output, format));
        } catch (UnsupportedEncodingException e) {
            // do nothing;
        }
    }

    /**
     * @see com.joelsgarage.extractor.ExtractorWriter#write(java.lang.String)
     */
    @Override
    public void write(Element payload) {
        if (getWriter() == null)
            return;
        try {
            getWriter().write(payload);
            setRecordNumber(getRecordNumber() + 1);
            if (getRecordNumber() % getBatchSize() == 0) {
                Logger.getLogger(XMLFileRecordWriter.class).info(
                    "flushing after record: " + getRecordNumber()); //$NON-NLS-1$

                getWriter().flush(); // flush in batches
            }
        } catch (IOException e) {
            setWriter(null); // don't try to use it again
        }
    }

    @Override
    public void close() {
        if (getWriter() == null)
            return;
        try {
            getWriter().writeClose(getContainer());
            getWriter().endDocument();
        } catch (SAXException e) {
            setWriter(null); // don't try to use it
        } catch (IOException e) {
            setWriter(null); // don't try to use it
        }
    }

    @Override
    public void open() {
        if (getWriter() == null)
            return;
        try {
            getWriter().startDocument();
            setContainer(DocumentHelper.createElement(CONTAINER));
            getWriter().writeOpen(getContainer());
        } catch (SAXException e) {
            setWriter(null); // don't try to use it
        } catch (IOException e) {
            setWriter(null); // don't try to use it
        }

    }

    public XMLWriter getWriter() {
        return this.writer;
    }

    public void setWriter(XMLWriter writer) {
        this.writer = writer;
    }

    public Element getContainer() {
        return this.container;
    }

    public void setContainer(Element container) {
        this.container = container;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getRecordNumber() {
        return this.recordNumber;
    }

    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }
}
