/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import com.joelsgarage.util.FatalException;

/**
 * Static utilities for Skiploader. Maybe move them into the util package.
 * 
 * @author joel
 */
public class SkiploaderUtil {

    static final String NAMESPACE = "namespace"; //$NON-NLS-1$
    static final String TYPE = "type"; //$NON-NLS-1$
    static final String KEY = "key"; //$NON-NLS-1$
    /** The name field of ModelEntity */
    private static final String NAME = "name"; //$NON-NLS-1$
    private static final String CONTAINER = "container"; //$NON-NLS-1$

    /**
     * Compare Elements that represent ModelEnity records.
     * 
     * The real key is externalKey. An externalKey is a container for three fields, so this
     * comparator looks at each of them.
     * 
     * If externalKey is null, then the key is name.
     * 
     * if name is null, we're fucked.
     * 
     * TODO: implement this in SQL "order by", so that the file and db can be sorted the same way,
     * so i can stream the diff.
     * 
     * @author joel
     * 
     */
    protected static class CompareExternalKey implements Comparator<Element> {
        @Override
        public int compare(Element o1, Element o2) {
            Node k1 = o1.selectSingleNode(Skiploader.EXTERNAL_KEY);
            Node k2 = o2.selectSingleNode(Skiploader.EXTERNAL_KEY);
            if (k1 == null && k2 == null) { // this should never actually happen.
                Node n1 = o1.selectSingleNode(SkiploaderUtil.NAME);
                Node n2 = o2.selectSingleNode(SkiploaderUtil.NAME);

                return compareNodes(n1, n2);
            }
            if (k1 == null)
                return -1;

            if (k2 == null)
                return 1;

            // Try each component of the key...
            int nsCompare =
                compareNodes(k1.selectSingleNode(SkiploaderUtil.NAMESPACE), k2
                    .selectSingleNode(SkiploaderUtil.NAMESPACE));
            if (nsCompare != 0)
                return nsCompare;

            nsCompare =
                compareNodes(k1.selectSingleNode(SkiploaderUtil.TYPE), k2
                    .selectSingleNode(SkiploaderUtil.TYPE));
            if (nsCompare != 0)
                return nsCompare;

            // That's all we've got; if these are identical, then the whole key is.
            return compareNodes(k1.selectSingleNode(SkiploaderUtil.KEY), k2
                .selectSingleNode(SkiploaderUtil.KEY));
        }

        protected int compareNodes(Node n1, Node n2) {
            if (n1 == null && n2 == null)
                return 0;
            if (n1 == null)
                return -1;
            if (n2 == null)
                return 1;
            String ns1 = n1.getText();
            String ns2 = n2.getText();
            if (ns1 == null && ns2 == null)
                return 0;
            if (ns1 == null)
                return -1;
            if (ns2 == null)
                return 1;
            return ns1.compareTo(ns2);
        }
    }

    /**
     * Write the specified element to the file, or barf, kinda like WriteOrDie.
     * 
     * Doesn't know the context of what it's writing; caller worries about containing tags, etc.
     * 
     * @param element
     * @throws FatalException
     */
    protected static void writeElement(XMLWriter writer, Element element) throws FatalException {
        if (writer == null)
            return;
        try {
            writer.write(element);
        } catch (IOException e) {
            throw new FatalException(e);
        }
    }

    /**
     * Make a new XMLWriter for this output stream.
     * 
     * @param output
     * @return
     * @throws FatalException
     */
    protected static XMLWriter makeXMLWriter(OutputStream output) throws FatalException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            return new XMLWriter(output, format);
        } catch (UnsupportedEncodingException e) {
            throw new FatalException(e);
        }
    }

    /**
     * Write the open tag for an element called "container" to the XMLWriter. You should use this
     * before writing the contents of the XML document to the writer.
     * 
     * @param writer
     * @throws FatalException
     */
    protected static void startDoc(XMLWriter writer) throws FatalException {
        try {
            writer.startDocument();
            Element container = DocumentHelper.createElement(CONTAINER);
            writer.writeOpen(container);
        } catch (SAXException e) {
            throw new FatalException(e);
        } catch (IOException e) {
            throw new FatalException(e);
        }
    }

    /**
     * Write the close tag for an element called "container" to the XMLWriter. You should use this
     * after writing the contents of the XML document to the writer.
     * 
     * @param writer
     * @throws FatalException
     */
    protected static void endDoc(XMLWriter writer) throws FatalException {
        try {
            Element container = DocumentHelper.createElement(CONTAINER);
            writer.writeClose(container);
            writer.endDocument();
        } catch (SAXException e) {
            throw new FatalException(e);
        } catch (IOException e) {
            throw new FatalException(e);
        }
    }

    /**
     * Compare elements by name, i.e. the text contents of a child "name" element.
     * 
     * @param o1
     * @param o2
     * @return true if the "name" fields of both elements are identical; false otherwise.
     */
    protected static boolean elementNameEquals(Element o1, Element o2) {
        Node k1 = o1.selectSingleNode(SkiploaderUtil.NAME);
        Node k2 = o2.selectSingleNode(SkiploaderUtil.NAME);
        String s1 = k1.getText();
        String s2 = k2.getText();
        return s1.equals(s2);
    }

    /**
     * Match the prolog to the buffer at the current position
     * 
     * @param buf
     *            input byte buffer containing (maybe) XML
     * @param pos
     *            offset into the buffer
     * @return true if the position is pointing at the XML Prolog ("<?XML ...")
     * @throws FatalException
     *             if the position is out of bounds (e.g. negative or larger than the buffer size)
     */
    protected static boolean matchBuffer(byte[] buf, int pos) throws FatalException {
        int prologSize = Skiploader.XML_PROLOG.length();
        int bufSize = buf.length;

        // min(), in case we screw up the size
        int size = (prologSize > bufSize ? bufSize : prologSize);

        if (pos < 0 || pos > size - 1)
            throw new FatalException("pos out of bounds"); //$NON-NLS-1$
        for (int i = pos; i < pos + size; ++i) {
            if (Skiploader.XML_PROLOG.charAt(i - pos) != buf[i % bufSize])
                return false;
        }
        return true;
    }

    /**
     * Return the element list corresponding to the specified document.
     * 
     * TODO: OK, since we're loading the whole doc anyway (it's the size of a transaction), then we
     * may as well make this the set of *unique* elements in the document. Between transactions, we
     * can use the DIFF feature to get the new elements, but within a single transaction, we need to
     * do it ourselves.
     * 
     * @param document
     * @return
     */
    protected static List<Element> documentElements(Document document) {
        List<Element> fileElements = new ArrayList<Element>();
        Element root = document.getRootElement();
        for (Iterator<?> i = root.elementIterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof Element) {
                Element element = (Element) o;
                fileElements.add(element);
            } else {
                Logger.getLogger(Skiploader.class).info(
                    "skipping weird type: " + o.getClass().getName()); //$NON-NLS-1$
            }
        }
        return fileElements;
    }

    /**
     * Read an XML doc, produce a list of the (top level) elements it contains.
     * 
     * @return
     */
    protected static List<Element> readElementsFromStream(InputStream input) throws FatalException {
        return documentElements(SkiploaderUtil.readDocumentFromStream(input));
    }

    /**
     * Load the specified stream into a Document, or throw a fatal exception, kinda like
     * "ReadFileOrDie". Loads the entire thing into RAM, so ...
     * 
     * TODO: when this method runs out of RAM, shard the file into sorted subfiles.
     * 
     * @param input
     * @return
     * @throws FatalException
     */
    protected static Document readDocumentFromStream(InputStream input) throws FatalException {
        DOMDocumentFactory factory = new DOMDocumentFactory();
        SAXReader saxReader = new SAXReader(factory);
        try {
            Document dom4jDoc = saxReader.read(input);
            return dom4jDoc;
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new FatalException(e);
        }
    }

}
