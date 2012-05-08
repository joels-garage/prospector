/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;

/**
 * Simple verification of XML creation and serialization
 * 
 * @author joel
 */
@SuppressWarnings("nls")
public class XMLUtilTest extends TestCase {

    protected String shortXMLNS() {
        return "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
            + "<ExternalKey xmlns=\"fooURI\">\n"//
            + "   <key>Zm9va2V5</key>\n" //
            + "   <type>typ</type>\n" //
            + "</ExternalKey>\n";
    }

    /**
     * has a serialized key
     * 
     * TODO: i think this is obsolete.
     */
    protected String partialXMLNS() {
        return "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
            + "<Individual xmlns=\"fooURI\">\n" // 
            + "   <id>BT_OlbscnGHcV4vW</id>\n" //
            + "   <name>namestring</name>\n" //
            + "   <namespace>ns</namespace>\n" //
            + "</Individual>\n";
    }

    protected String longXMLNS() {
        // Note the unnecessary duplication of xmlns below.
        // TODO: figure it out
        return "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
            + "<Individual xmlns=\"fooURI\">\n" // 
            + "   <id>BT_OlbscnGHcV4vW</id>\n" //
            + "   <name>namestring</name>\n" //
            + "   <namespace>ns</namespace>\n" //
            + "</Individual>\n";
    }

    protected Object shortObject() {
        return new ExternalKey("typ", new String("fookey").getBytes());
    }

    protected Object longObject() throws FatalException {
        Individual individual = new Individual("namestring", "ns");
        return individual;
    }

    //

    protected String shallowXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-16\"?><a>foo</a>\n";
    }

    protected String shallowXMLNS() {
        return "<?xml version=\"1.0\" encoding=\"UTF-16\"?><a:b xmlns:a=\"bar\">foo</a:b>\n";
    }

    protected String deepXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-16\"?><a>\n" //
            + "   <b>\n" //
            + "      <c>\n" //
            + "         <d>hi</d>\n" //
            + "         <e>there</e>\n" //
            + "      </c>\n" //
            + "   </b>\n" //
            + "</a>\n";
    }

    /** Verify the function of getProperties() */
    public void testGetPropertiesShallow() {
        String shallowXML = shallowXML();
        Document doc = XMLUtil.readXML(shallowXML);
        String roundTripXML = XMLUtil.writeXML(doc);
        assertEquals(shallowXML, roundTripXML);
        Map<String, String> properties = XMLUtil.getProperties(doc);
        List<String> serializedEntries = new ArrayList<String>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            serializedEntries.add(entry.getKey() + " == " + entry.getValue());
        }
        Collections.sort(serializedEntries);
        assertEquals(1, serializedEntries.size());
        assertEquals("a == foo", serializedEntries.get(0));
    }

    /** Verify the function of getProperties(), particularly for namespaces */
    public void testGetPropertiesShallowNS() {
        String shallowXML = shallowXMLNS();
        Document doc = XMLUtil.readXML(shallowXML);
        String roundTripXML = XMLUtil.writeXML(doc);
        assertEquals(shallowXML, roundTripXML);
        Map<String, String> properties = XMLUtil.getProperties(doc);
        List<String> serializedEntries = new ArrayList<String>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            serializedEntries.add(entry.getKey() + " == " + entry.getValue());
        }
        Collections.sort(serializedEntries);
        assertEquals(1, serializedEntries.size());
        assertEquals("b == foo", serializedEntries.get(0));
    }

    /** Verify the function of getProperties(), particularly for deep nesting */
    public void testGetPropertiesDeep() {
        String deepXML = deepXML();
        Document doc = XMLUtil.readXML(deepXML);
        String roundTripXML = XMLUtil.writeXML(doc);
        assertEquals(deepXML, roundTripXML);
        Map<String, String> properties = XMLUtil.getProperties(doc);
        List<String> serializedEntries = new ArrayList<String>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            serializedEntries.add(entry.getKey() + " == " + entry.getValue());
        }
        Collections.sort(serializedEntries);
        assertEquals(2, serializedEntries.size());
        assertEquals("a.b.c.d == hi", serializedEntries.get(0));
        assertEquals("a.b.c.e == there", serializedEntries.get(1));
    }

    public void testToXMLNSShort() {
        ExternalKey key = (ExternalKey) shortObject();
        String expectedXML = shortXMLNS();
        Document testXML = XMLUtil.toXML(key, "fooURI");
        assertEquals(expectedXML, XMLUtil.writeXML(testXML));
    }

    public void testToXMLNS() throws FatalException {
        Individual individual = (Individual) longObject();
        String expectedXML = longXMLNS();
        Document testXML = XMLUtil.toXML(individual, "fooURI");
        assertEquals(expectedXML, XMLUtil.writeXML(testXML));
    }

    public void testFromXMLNSShort() {
        ExternalKey key = (ExternalKey) shortObject();
        Document testXML = XMLUtil.toXML(key, "fooURI");
        ExternalKey testObject = (ExternalKey) XMLUtil.fromXML(testXML.getDocumentElement());
        assertNotNull(key);
        assertNotNull(testObject);
        assertEquals(key.toString(), testObject.toString());
    }

    public void testFromXMLNSShort2() {
        ExternalKey key = (ExternalKey) shortObject();
        Document testXML = XMLUtil.readXML(shortXMLNS());
        ExternalKey testObject = (ExternalKey) XMLUtil.fromXML(testXML.getDocumentElement());
        assertNotNull(key);
        assertNotNull(testObject);
        assertEquals(key.toString(), testObject.toString());
    }

    public void testFromXMLNS() throws FatalException {
        Logger.getLogger(XMLUtilTest.class).info("testFromXMLNS()");
        Individual individual = (Individual) longObject();
        Document testXML = XMLUtil.toXML(individual, "fooURI");
        Individual testObject = (Individual) XMLUtil.fromXML(testXML.getDocumentElement());
        assertNotNull(individual);
        assertNotNull(testObject);
        assertEquals(XMLUtil.writeXML(testXML), individual.toString(), testObject.toString());
    }

    public void testFromXMLNS2() throws FatalException {
        Individual individual = (Individual) longObject();
        Document testXML = XMLUtil.readXML(longXMLNS());
        Individual testObject = (Individual) XMLUtil.fromXML(testXML.getDocumentElement());
        assertNotNull(individual);
        assertNotNull(testObject);
        assertEquals(XMLUtil.writeXML(testXML), individual.toString(), testObject.toString());
    }

    public void testFromPartialXMLNS() throws FatalException {
        Logger.getLogger(XMLUtilTest.class).info("testFromPartialXMLNS()");
        Individual individual = (Individual) longObject();
        Document testXML = XMLUtil.readXML(partialXMLNS());
        Logger.getLogger(XMLUtilTest.class).info("xml: " + XMLUtil.writeXML(testXML));
        Individual testObject = (Individual) XMLUtil.fromXML(testXML.getDocumentElement());
        assertNotNull(individual);
        assertNotNull(testObject);
        assertEquals(XMLUtil.writeXML(testXML), individual.toString(), testObject.toString());
    }

    /** Null elements map => nothing returned */
    public void testNullElements() {
        Document doc = XMLUtil.makeDoc(null, "foo", null);
        assertNull(doc);
    }

    /** Empty elements map => nothing returned */
    public void testEmptyELements() {
        Map<String, String> elements = new HashMap<String, String>();
        Document doc = XMLUtil.makeDoc(null, "foo", elements);
        assertNull(doc);
    }

    /** Null container name => nothing returned */
    public void testNullName() {
        Map<String, String> elements = new HashMap<String, String>();
        elements.put("foo", "bar");
        Document doc = XMLUtil.makeDoc(null, null, elements);
        assertNull(doc);
    }

    /** Empty container name => nothing returned */
    public void testEmptyName() {
        Map<String, String> elements = new HashMap<String, String>();
        elements.put("foo", "bar");
        Document doc = XMLUtil.makeDoc(null, "", elements);
        assertNull(doc);
    }

    public void testMakeDoc() {
        Map<String, String> elements = new HashMap<String, String>();
        elements.put("foo1", "bar1");
        elements.put("foo2", "bar2");
        Document doc = XMLUtil.makeDoc("uri.foo", "root", elements);
        assertNotNull(doc);

        assertEquals("root", doc.getDocumentElement().getLocalName());
        NodeList nodeList = doc.getDocumentElement().getChildNodes();
        assertEquals(2, nodeList.getLength());
        assertEquals("foo2", nodeList.item(0).getLocalName());
        assertEquals("bar2", nodeList.item(0).getTextContent());
        assertEquals("foo1", nodeList.item(1).getLocalName());
        assertEquals("bar1", nodeList.item(1).getTextContent());
    }

    public void testCombined() {
        Map<String, String> elements = new HashMap<String, String>();
        elements.put("foo1", "bar1");
        elements.put("foo2", "bar2");
        Document doc = XMLUtil.makeDoc("uri.foo", "root", elements);
        assertNotNull(doc);

        String serializedDoc = XMLUtil.writeXML(doc);
        assertEquals( //
            "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                + "<root xmlns=\"uri.foo\">\n" //
                + "   <foo2>bar2</foo2>\n" //
                + "   <foo1>bar1</foo1>\n" // 
                + "</root>\n"

            , serializedDoc);
    }

    private String getPayload() {
        return "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" + //
            "<data xmlns=\"http://www.w3.org/2005/07/scxml\" name=\"logEndScene\">\n" + //
            "   <output xmlns=\"http://www.joelsgarage.com/callcenter\">\n" + //
            "      <current_joke_index>0</current_joke_index>\n" + //
            "   </output>\n" + //
            "</data>\n";
    }

    /** Having a lot of trouble figuring this out. */
    public void testRoundTrip() {
        String docString = getPayload();

        Document doc = XMLUtil.readXML(docString);

        String roundTripString = XMLUtil.writeXML(doc);
        assertEquals(docString, roundTripString);
    }

    /** This matches the CHILDREN of the indicated node */
    public void testGetNodeByPath1() {
        verifyXpath(
            "/scxml:data/cc:output/*", //
            "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                + "<current_joke_index xmlns=\"http://www.joelsgarage.com/callcenter\">0</current_joke_index>\n");
    }

    /** Referencing the node works */
    public void testGetNodeByPath2() {
        verifyXpath("/scxml:data/cc:output", //
            "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                + "<output xmlns=\"http://www.joelsgarage.com/callcenter\">\n" //
                + "      <current_joke_index>0</current_joke_index>\n"//
                + "   </output>\n");
    }

    /** The "node()" thing doesn't seem to work */
    public void testGetNodeByPath3() {
        verifyXpath("/scxml:data/cc:output/node()", //
            "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n      ");
    }

    /** Referencing the index of the node works */
    public void testGetNodeByPath4() {
        verifyXpath("/scxml:data/cc:output[1]", //
            "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                + "<output xmlns=\"http://www.joelsgarage.com/callcenter\">\n" //
                + "      <current_joke_index>0</current_joke_index>\n" //
                + "   </output>\n");
    }

    /** Referencing "current node" (dot) works */
    public void testGetNodeByPath5() {
        verifyXpath("/scxml:data/cc:output/.", //
            "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                + "<output xmlns=\"http://www.joelsgarage.com/callcenter\">\n" //
                + "      <current_joke_index>0</current_joke_index>\n" //
                + "   </output>\n");
    }

    /** Get the whole thing */
    public void testGetNodeByPath6() {
        verifyXpath("/.", getPayload());
    }

    private void verifyXpath(String xpath, String expectedOutput) {
        String docString = getPayload();
        Document doc = XMLUtil.readXML(docString);
        Node outputNode = XMLUtil.getNodeByPath(new CallCenterNamespaceContext(), doc, xpath);
        String outputString = XMLUtil.writeXML(outputNode);
        Logger.getLogger(XMLUtilTest.class).info("output: " + outputString);
        assertEquals(expectedOutput, outputString);
    }

}
