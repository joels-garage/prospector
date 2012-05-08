/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.joelsgarage.util.FatalException;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class SkiploaderUtilTest extends TestCase {

    public void testWriteElement() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            XMLWriter xmlWriter = SkiploaderUtil.makeXMLWriter(byteArrayOutputStream);
            assertNotNull(xmlWriter);

            Element classElement = DocumentHelper.createElement("Class");
            Element keyElement = classElement.addElement("key");
            Element nsElement = keyElement.addElement("namespace");
            Element typeElement = keyElement.addElement("type");
            Element keyKeyElement = keyElement.addElement("key");
            nsElement.setText("p1");
            typeElement.setText("foo");
            keyKeyElement.setText("1");
            Element creatorIdElement = classElement.addElement("creatorId");
            Element nameElement = classElement.addElement("name");
            Element descriptionElement = classElement.addElement("description");
            creatorIdElement.setText("0");
            nameElement.setText("Presidential Candidate");
            descriptionElement.setText("foo");

            SkiploaderUtil.writeElement(xmlWriter, classElement);

            String outputString = byteArrayOutputStream.toString();

            assertEquals("\n"//
                + "<Class>\n"//
                + "  <key>\n"//
                + "    <namespace>p1</namespace>\n"//
                + "    <type>foo</type>\n"//
                + "    <key>1</key>\n"//
                + "  </key>\n"//
                + "  <creatorId>0</creatorId>\n"//
                + "  <name>Presidential Candidate</name>\n"//
                + "  <description>foo</description>\n"//
                + "</Class>"

            , outputString);

        } catch (FatalException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testStartAndEndDoc() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            XMLWriter xmlWriter = SkiploaderUtil.makeXMLWriter(byteArrayOutputStream);
            assertNotNull(xmlWriter);

            // an empty document.
            SkiploaderUtil.startDoc(xmlWriter);
            SkiploaderUtil.endDoc(xmlWriter);

            String outputString = byteArrayOutputStream.toString();

            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<container></container>",
                outputString);

        } catch (FatalException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testElementNameEqualsTrue() {
        Element firstElement = DocumentHelper.createElement("first");
        firstElement.addElement("name").setText("foo");
        Element secondElement = DocumentHelper.createElement("second");
        secondElement.addElement("name").setText("foo");
        assertTrue(SkiploaderUtil.elementNameEquals(firstElement, secondElement));
    }

    public void testElementNameEqualsFalse() {
        Element firstElement = DocumentHelper.createElement("first");
        firstElement.addElement("name").setText("foo");
        Element secondElement = DocumentHelper.createElement("second");
        secondElement.addElement("name").setText("bar");
        assertFalse(SkiploaderUtil.elementNameEquals(firstElement, secondElement));
    }

    public void testComparatorByKey() {
        Comparator<Element> externalKeyComparator = new SkiploaderUtil.CompareExternalKey();

        List<Element> elements = new ArrayList<Element>();

        Element firstElement = DocumentHelper.createElement("Class");
        {
            Element keyElement = firstElement.addElement("key");
            Element nsElement = keyElement.addElement("namespace");
            Element typeElement = keyElement.addElement("type");
            Element keyKeyElement = keyElement.addElement("key");
            nsElement.setText("p1");
            typeElement.setText("foo");
            keyKeyElement.setText("1");
            Element nameElement = firstElement.addElement("name");
            nameElement.setText("foo name");
        }
        elements.add(firstElement);

        Element secondElement = DocumentHelper.createElement("Class");
        {
            Element keyElement = secondElement.addElement("key");
            Element nsElement = keyElement.addElement("namespace");
            Element typeElement = keyElement.addElement("type");
            Element keyKeyElement = keyElement.addElement("key");
            nsElement.setText("p1");
            typeElement.setText("bar");
            keyKeyElement.setText("1");
            Element nameElement = secondElement.addElement("name");
            nameElement.setText("name of bar");
        }
        elements.add(secondElement);

        Collections.sort(elements, externalKeyComparator);

        assertEquals(secondElement, elements.get(0));
        assertEquals(firstElement, elements.get(1));
    }

    public void testComparatorByName() {
        Comparator<Element> externalKeyComparator = new SkiploaderUtil.CompareExternalKey();

        List<Element> elements = new ArrayList<Element>();

        Element firstElement = DocumentHelper.createElement("Class");
        {
            Element keyElement = firstElement.addElement("foo");
            Element nsElement = keyElement.addElement("namespace");
            Element typeElement = keyElement.addElement("type");
            Element keyKeyElement = keyElement.addElement("key");
            nsElement.setText("p1");
            typeElement.setText("foo");
            keyKeyElement.setText("1");
            Element nameElement = firstElement.addElement("name");
            nameElement.setText("foo name");
        }
        elements.add(firstElement);

        Element secondElement = DocumentHelper.createElement("Class");
        {
            Element keyElement = secondElement.addElement("foo");
            Element nsElement = keyElement.addElement("namespace");
            Element typeElement = keyElement.addElement("type");
            Element keyKeyElement = keyElement.addElement("key");
            nsElement.setText("p1");
            typeElement.setText("bar");
            keyKeyElement.setText("1");
            Element nameElement = secondElement.addElement("name");
            nameElement.setText("name of bar");
        }
        elements.add(secondElement);

        Collections.sort(elements, externalKeyComparator);

        assertEquals(firstElement, elements.get(0));
        assertEquals(secondElement, elements.get(1));
    }

    public void testMatchBuffer() {
        String testString = "<?xml version=\"1.0\"?><foo></foo>";

        // the buffer is too short.
        byte[] buf = testString.substring(0, 4).getBytes();
        try {
            assertFalse(SkiploaderUtil.matchBuffer(buf, 4));
            fail();
        } catch (FatalException e) {
            assertTrue(true);
        }
        // contains exactly the prolog at pos = 0.
        buf = testString.substring(0, 5).getBytes();
        try {
            assertTrue(SkiploaderUtil.matchBuffer(buf, 0));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 1));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 2));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 3));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 4));
        } catch (FatalException e) {
            fail();
        }

        // does not contain the whole prolog; shouldn't find it.
        buf = testString.substring(1, 6).getBytes();
        try {
            assertFalse(SkiploaderUtil.matchBuffer(buf, 0));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 1));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 2));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 3));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 4));
        } catch (FatalException e) {
            fail();
        }

        buf[0] = 'm';
        buf[1] = 'l';
        buf[2] = '<';
        buf[3] = '?';
        buf[4] = 'x';
        try {
            assertFalse(SkiploaderUtil.matchBuffer(buf, 0));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 1));
            assertTrue(SkiploaderUtil.matchBuffer(buf, 2));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 3));
            assertFalse(SkiploaderUtil.matchBuffer(buf, 4));
        } catch (FatalException e) {
            fail();
        }

        try {
            assertTrue(SkiploaderUtil.matchBuffer(buf, -1));
            fail(); // negative is not allowed
        } catch (FatalException e) {
            assertTrue(true); // correct throw
        }
    }

    public void testDocumentElements() {
        Element rootElement = DocumentHelper.createElement("root");

        Element firstElement = rootElement.addElement("Class");
        {
            Element keyElement = firstElement.addElement("foo");
            Element nsElement = keyElement.addElement("namespace");
            Element typeElement = keyElement.addElement("type");
            Element keyKeyElement = keyElement.addElement("key");
            nsElement.setText("p1");
            typeElement.setText("foo");
            keyKeyElement.setText("1");
            Element nameElement = firstElement.addElement("name");
            nameElement.setText("foo name");
        }

        Element secondElement = rootElement.addElement("Class");
        {
            Element keyElement = secondElement.addElement("foo");
            Element nsElement = keyElement.addElement("namespace");
            Element typeElement = keyElement.addElement("type");
            Element keyKeyElement = keyElement.addElement("key");
            nsElement.setText("p1");
            typeElement.setText("bar");
            keyKeyElement.setText("1");
            Element nameElement = secondElement.addElement("name");
            nameElement.setText("name of bar");
        }

        Document document = DocumentHelper.createDocument(rootElement);

        List<Element> elementList = SkiploaderUtil.documentElements(document);

        assertEquals(2, elementList.size());

        assertEquals(firstElement, elementList.get(0));
        assertEquals(secondElement, elementList.get(1));
    }

}
