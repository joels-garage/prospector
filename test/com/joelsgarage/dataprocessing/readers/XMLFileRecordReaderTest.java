/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.io.InputStream;

import junit.framework.TestCase;

import org.dom4j.Element;

import com.joelsgarage.dataprocessing.readers.XMLFileRecordReader;

/**
 * Verify the operation of the stream-based XML file parser.
 * 
 * @author joel
 */
@SuppressWarnings("nls")
public class XMLFileRecordReaderTest extends TestCase {
	private static final String INPUT_FILE = "dataprocessing/elements.xml";

	public void testSimple() {
		ClassLoader loader = this.getClass().getClassLoader();
		InputStream input = loader.getResourceAsStream(INPUT_FILE);
		XMLFileRecordReader reader = new XMLFileRecordReader(input);
		reader.open();
		Element element = reader.read();
		assertEquals("<StringProperty>\n" + //
			"  <creatorKey>\n" + //
			"    <key>usda-plants-characteristics-importer</key>\n" + //
			"    <namespace>p1</namespace>\n" + //
			"    <type>individual</type>\n" + //
			"  </creatorKey>\n" + //
			"  <domainClassKey>\n" + //
			"    <key>Plantae</key>\n" + //
			"    <namespace>plants.usda.gov</namespace>\n" + //
			"    <type>class</type>\n" + //
			"  </domainClassKey>\n" + //
			"  <key>\n" + //
			"    <key/>\n" + // //
			"    <namespace/>\n" + //
			"    <type/>\n" + //
			"  </key>\n" + //
			"  <lastModified>2008-06-13T14:44:07</lastModified>\n" + //
			"  <name>Common Name</name>\n" + //
			"</StringProperty>", element.asXML());
		element = reader.read();
		assertEquals("<StringProperty>\n" + //
			"  <creatorKey>\n" + //
			"    <key>usda-plants-characteristics-importer</key>\n" + //
			"    <namespace>p1</namespace>\n" + //
			"    <type>individual</type>\n" + //
			"  </creatorKey>\n" + //
			"  <domainClassKey>\n" + //
			"    <key>Plantae</key>\n" + //
			"    <namespace>plants.usda.gov</namespace>\n" + //
			"    <type>class</type>\n" + //
			"  </domainClassKey>\n" + //
			"  <key>\n" + //
			"    <key>Active Growth Period</key>\n" + // //
			"    <namespace>plants.usda.gov</namespace>\n" + //
			"    <type>string_property</type>\n" + //
			"  </key>\n" + //
			"  <lastModified>2008-06-13T14:44:07</lastModified>\n" + //
			"  <name>Active Growth Period</name>\n" + //
			"</StringProperty>", element.asXML());
		element = reader.read();
		assertEquals("<Individual>\n" + //
			"  <creatorKey>\n" + //
			"    <key>usda-plants-characteristics-importer</key>\n" + //
			"    <namespace>p1</namespace>\n" + //
			"    <type>individual</type>\n" + //
			"  </creatorKey>\n" + //
			"  <key>\n" + //
			"    <key>MEOF</key>\n" + // //
			"    <namespace>plants.usda.gov</namespace>\n" + //
			"    <type>individual</type>\n" + //
			"  </key>\n" + //
			"  <lastModified>2008-06-15T01:49:45</lastModified>\n" + //
			"  <name>MEOF</name>\n" + //
			"</Individual>", element.asXML());
		element = reader.read();
		assertEquals("<User>\n" + //
			"  <admin>true</admin>\n" + //
			"  <cookie/>\n" + //
			"  <creatorKey>\n" + //
			"    <key/>\n" + //
			"    <namespace/>\n" + //
			"    <type/>\n" + //
			"  </creatorKey>\n" + //
			"  <emailAddress>joel</emailAddress>\n" + //
			"  <guest>false</guest>\n" + //
			"  <key>\n" + //
			"    <key/>\n" + // //
			"    <namespace/>\n" + //
			"    <type/>\n" + //
			"  </key>\n" + //
			"  <lastModified/>\n" + //
			"  <name>joel</name>\n" + //
			"  <password>joel</password>\n" + //
			"  <realName>joel</realName>\n" + //
			"</User>", element.asXML());
		element = reader.read();
		assertNull(element);
		reader.close();
	}
}
