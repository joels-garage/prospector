/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.STAXEventReader;
import org.dom4j.tree.DefaultText;

/**
 * Read an Element at a time from an XML file. File has an outer container; this reads the element
 * children of that.
 * 
 * @author joel
 */
public class XMLFileRecordReader extends RecordReaderBase<Element> {
    private XMLInputFactory inputFactory;
    private XMLEventReader eventReader;
    private STAXEventReader staxEventReader;

    public XMLFileRecordReader(InputStream input) {
        setInputFactory(XMLInputFactory.newInstance());
        try {
            setEventReader(getInputFactory().createXMLEventReader(input));
        } catch (XMLStreamException ex) {
            Logger.getLogger(XMLFileRecordReader.class).error(
                "failed to create reader: " + ex.getMessage()); //$NON-NLS-1$
            ex.printStackTrace();
            setEventReader(null);
            return;
        }
        setStaxEventReader(new STAXEventReader());
    }

    @Override
    public void close() {
        try {
            if (getEventReader() != null)
                getEventReader().close();
        } catch (XMLStreamException ex) {
            // nuthin
        }
    }

    @Override
    public void open() {
        if (getEventReader() == null)
            return;
        try {
            XMLEvent event = getEventReader().nextEvent();
            if (event.isStartDocument())
                event = getEventReader().nextEvent();
            else
                return;
            // next event should be the "container" event.
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                QName qname = start.getName();
                String name = qname.getLocalPart();
                Logger.getLogger(XMLFileRecordReader.class).info("name " + name); //$NON-NLS-1$
            } else
                return;
        } catch (XMLStreamException ex) {
            // nuthin
        }
    }

    @Override
    public Element read() {
        if (getStaxEventReader() == null)
            return null;
        try {
            if (getEventReader().peek() == null || getEventReader().peek().isEndElement()) {
                // it's only EndElement if that element is </container>
                return null;
            }
            Node node = getStaxEventReader().readNode(getEventReader());
            if (node instanceof Element) {
                return (Element) node;
            }
            if (node instanceof DefaultText) {
                // this appears between elements?
                return read(); // try again
            }
            return null;
        } catch (XMLStreamException ex) {
            // this means we're done. :-)
            Logger.getLogger(XMLFileRecordReader.class).error("read failed"); //$NON-NLS-1$
            ex.printStackTrace();
            return null;
        }
    }

    //
    //

    public XMLInputFactory getInputFactory() {
        return this.inputFactory;
    }

    public void setInputFactory(XMLInputFactory inputFactory) {
        this.inputFactory = inputFactory;
    }

    public XMLEventReader getEventReader() {
        return this.eventReader;
    }

    public void setEventReader(XMLEventReader eventReader) {
        this.eventReader = eventReader;
    }

    public STAXEventReader getStaxEventReader() {
        return this.staxEventReader;
    }

    public void setStaxEventReader(STAXEventReader staxEventReader) {
        this.staxEventReader = staxEventReader;
    }
}
