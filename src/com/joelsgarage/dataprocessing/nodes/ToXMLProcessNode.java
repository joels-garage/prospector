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
public class ToXMLProcessNode extends ProcessNode<ModelEntity, Element> {
	public ToXMLProcessNode(RecordReader<ModelEntity> reader, RecordWriter<Element> writer,
		int inLimit, int outLimit) {
		super(reader, writer, inLimit, outLimit);
		setProgressCount(1000);
	}

	/**
	 * @see ProcessNode#handleRecord(Object)
	 */
	@Override
	protected boolean handleRecord(ModelEntity record) {
		Element element = Translator.toXML(record, null);
		output(element);
		return true;
	}
}
