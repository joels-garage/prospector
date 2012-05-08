/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class TranslatorTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // @SuppressWarnings("nls")
    // public void testObjectName() {
    // Logger.getLogger(Translator.class).info("testobjectname");
    // Individual entity = new Individual();
    // String name = ClassUtil.shortClassName(entity.getClass());
    // assertEquals("Individual", name);
    //
    // String string = new String();
    // name = ClassUtil.shortClassName(string.getClass());
    // assertEquals("String", name);
    // }
    //
    // protected void assertXmlEquals(String expected, Element test) {
    // String testXMLString = test.asXML();
    // Logger.getLogger(TranslatorTest.class).info(
    // "expected string:\nSTART\n" + expected + "\nEND\n");
    // Logger.getLogger(TranslatorTest.class)
    // .info("test string start\n" + testXMLString + "end\n");
    // assertEquals(expected, testXMLString);
    // }
    //
    // protected String shortXML() {
    // String result =
    // "<ExternalKey><key>fookey</key><namespace>ns</namespace><type>typ</type></ExternalKey>";
    // return result;
    // }
    //
    // protected String shortXMLNS() {
    // String result =
    // "<fooPrefix:ExternalKey xmlns:fooPrefix=\"fooURI\">"
    // + "<fooPrefix:key>fookey</fooPrefix:key>"
    // + "<fooPrefix:namespace>ns</fooPrefix:namespace>"
    // + "<fooPrefix:type>typ</fooPrefix:type>" + "</fooPrefix:ExternalKey>";
    // return result;
    // }
    //
    // protected String shortXMLWithExtra() {
    // String result = //
    // "<ExternalKey>\n" //
    // + "<namespace>ns</namespace>\n"//
    // + "<type>typ</type>\n" //
    // + "<key>fookey</key>\n" //
    // + "<extra>foo</extra>\n" //
    // + "</ExternalKey>";
    // return result;
    // }
    //
    // public void testGetProperties() {
    // Element input = shortElement();
    // Map<String, String> properties = Translator.getProperties(input);
    // assertEquals(3, properties.size());
    // assertEquals("ns", properties.get("namespace"));
    // assertEquals("typ", properties.get("type"));
    // assertEquals("fookey", properties.get("key"));
    // }
    //
    // protected Element shortElement() {
    // Element element = keyElement("ExternalKey", "ns", "typ", "fookey");
    // return element;
    // }
    //
    // protected Element shortElementWithExtra() {
    // Element element = keyElement("ExternalKey", "ns", "typ", "fookey");
    // Element xElement = element.addElement("extra");
    // xElement.setText("foo");
    // return element;
    // }
    //
    // protected Element keyElement(String name, String ns, String t, String k) {
    // Element element = DocumentHelper.createElement(name);
    // Element nsElement = element.addElement("namespace");
    // Element tElement = element.addElement("type");
    // Element kElement = element.addElement("key");
    // nsElement.setText(ns);
    // tElement.setText(t);
    // kElement.setText(k);
    // return element;
    // }
    //
    // protected Element longElement() {
    // Element element = DocumentHelper.createElement("Individual");
    // element.add(keyElement("key", "ns", "typ", "fookey"));
    // element.add(keyElement("creatorKey", "ns2", "typ2", "fookey2"));
    // Element lmElement = element.addElement("lastModified");
    // Element nElement = element.addElement("name");
    // lmElement.setText("some string");
    // nElement.setText("namestring");
    // return element;
    // }
    //
    // // I have alphabetized the subtrees here.
    // protected String longXML() {
    // String expectedXML = "<Individual>" //
    // + "<creatorKey>" //
    // + "<key>fookey2</key>" //
    // + "<namespace>ns2</namespace>" //
    // + "<type>typ2</type>" //
    // + "</creatorKey>" //
    // + "<key>" //
    // + "<key>fookey</key>" //
    // + "<namespace>ns</namespace>" //
    // + "<type>typ</type>" //
    // + "</key>" //
    // + "<lastModified>some string</lastModified>" //
    // + "<name>namestring</name>" //
    // + "</Individual>";
    // return expectedXML;
    // }
    //
    // protected String longXMLNS() {
    // String expectedXML = "<fooPrefix:Individual xmlns:fooPrefix=\"fooURI\">" //
    // + "<fooPrefix:creatorKey>" //
    // + "<fooPrefix:key>fookey2</fooPrefix:key>" //
    // + "<fooPrefix:namespace>ns2</fooPrefix:namespace>" //
    // + "<fooPrefix:type>typ2</fooPrefix:type>" //
    // + "</fooPrefix:creatorKey>" //
    // + "<fooPrefix:key>" //
    // + "<fooPrefix:key>fookey</fooPrefix:key>" //
    // + "<fooPrefix:namespace>ns</fooPrefix:namespace>" //
    // + "<fooPrefix:type>typ</fooPrefix:type>" //
    // + "</fooPrefix:key>" //
    // + "<fooPrefix:lastModified>some string</fooPrefix:lastModified>" //
    // + "<fooPrefix:name>namestring</fooPrefix:name>" //
    // + "</fooPrefix:Individual>";
    // return expectedXML;
    // }
    //
    // protected Object longObject() {
    // Individual individual = new Individual();
    // individual.setKey(new ExternalKey("ns", "typ", "fookey"));
    // individual.setCreatorKey(new ExternalKey("ns2", "typ2", "fookey2"));
    // individual.setLastModified("some string");
    // individual.setName("namestring");
    // return individual;
    // }
    //
    // protected Object shortObject() {
    // ExternalKey key = new ExternalKey("ns", "typ", "fookey");
    // return key;
    // }
    //
    // public void testSkip() {
    // assertTrue(Translator.skip(Class.class));
    // assertFalse(Translator.skip(String.class));
    // assertFalse(Translator.skip(Individual.class));
    // }
    //
    // public void testToXMLShort() {
    // ExternalKey key = (ExternalKey) shortObject();
    // String expectedXML = shortXML();
    // Element testXML = Translator.toXML(key);
    // assertXmlEquals(expectedXML, testXML);
    // }
    //
    // public void testToXMLNSShort() {
    // ExternalKey key = (ExternalKey) shortObject();
    // String expectedXML = shortXMLNS();
    // Element testXML = Translator.toXMLNS(key, "fooPrefix", "fooURI");
    // assertXmlEquals(expectedXML, testXML);
    // }
    //
    // public void testToXML() {
    // Individual individual = (Individual) longObject();
    // String expectedXML = longXML();
    // Element testXML = Translator.toXML(individual);
    // assertXmlEquals(expectedXML, testXML);
    // }
    //
    // public void testToXMLNS() {
    // Individual individual = (Individual) longObject();
    // String expectedXML = longXMLNS();
    // Element testXML = Translator.toXMLNS(individual, "fooPrefix", "fooURI");
    // assertXmlEquals(expectedXML, testXML);
    // }
    //
    // public void testFromXMLShort() {
    // Element element = shortElement();
    // ExternalKey testObject = (ExternalKey) Translator.fromXML(element);
    // ExternalKey expectedObject = (ExternalKey) shortObject();
    // assertTrue(expectedObject.equals(testObject));
    // }
    //
    // public void testFromXMLNSShort() {
    // ExternalKey key = (ExternalKey) shortObject();
    // Element testXML = Translator.toXMLNS(key, "fooPrefix", "fooURI");
    // ExternalKey testObject = (ExternalKey) Translator.fromXML(testXML);
    // assertEquals(key.toString(), testObject.toString());
    // }
    //
    // public void testFromXMLNS() {
    // Individual individual = (Individual) longObject();
    // Element testXML = Translator.toXMLNS(individual, "fooPrefix", "fooURI");
    // Individual testObject = (Individual) Translator.fromXML(testXML);
    // assertEquals(individual.toString(), testObject.toString());
    // }
    //
    // /**
    // * Verify that extra elements (not in the bean) are silently ignored
    // */
    // public void testFromXMLWithExtra() {
    // Element element = shortElementWithExtra();
    //        ExternalKey testObject = (ExternalKey) Translator.fromXML(element);
    //        ExternalKey expectedObject = (ExternalKey) shortObject();
    //        assertTrue(expectedObject.equals(testObject));
    //    }
    //
    //    public void testFromXMLLong() {
    //        Element element = longElement();
    //        Individual testObject = (Individual) Translator.fromXML(element);
    //        Individual expectedObject = (Individual) longObject();
    //        assertTrue(expectedObject.getKey().equals(testObject.getKey()));
    //        assertTrue(expectedObject.getCreatorKey().equals(testObject.getCreatorKey()));
    //        assertEquals(expectedObject.getLastModified(), testObject.getLastModified());
    //        assertEquals(expectedObject.getName(), testObject.getName());
    //    }
}
