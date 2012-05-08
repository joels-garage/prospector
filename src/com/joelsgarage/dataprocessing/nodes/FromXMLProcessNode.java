/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.nodes;

import org.dom4j.Element;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.skiploader.Translator;

/**
 * Translate model entity record to XML Element.
 * 
 * @author joel
 * 
 */
public class FromXMLProcessNode extends ProcessNode<Element, ModelEntity> {
    public FromXMLProcessNode(RecordReader<Element> reader, RecordWriter<ModelEntity> writer,
        int inLimit, int outLimit) {
        super(reader, writer, inLimit, outLimit);
        setProgressCount(1000);
    }

    /**
     * @see ProcessNode#handleRecord(Object)
     */
    @Override
    protected boolean handleRecord(Element record) {
        // want to change to this version
        // TODO: eliminate all the dom4j stuff
        // ModelEntity entity = (ModelEntity) XMLUtil.fromXML(record);

        ModelEntity entity = (ModelEntity) Translator.fromXML(record);
        output(entity);
        return true;
    }
}
