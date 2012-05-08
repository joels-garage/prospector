/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import java.io.OutputStream;

import org.dom4j.Element;

import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.skiploader.Translator;

/**
 * Translates the entity to xml and writes it. Delegates to XMLFileRecordWriter.
 * 
 * This is a somewhat confusing class, in that it appears to write ModelEntity records but actually
 * writes Element records.
 * 
 * It might be more straightforward from an API perspective to build some sort of "pipe" reader/writer.
 * 
 * TODO: try that someday.
 * 
 * @author joel
 * 
 */
public class TranslatingXMLWriter implements RecordWriter<ModelEntity> {
    private XMLFileRecordWriter writer;

    public TranslatingXMLWriter(OutputStream output) {
        this.writer = new XMLFileRecordWriter(output);
    }

    @Override
    public void close() {
        this.writer.close();

    }

    @Override
    public void open() {
        this.writer.open();
    }

    @Override
    public void write(ModelEntity payload) {
        Element element = Translator.toXML(payload, null);
        this.writer.write(element);
    }

}
