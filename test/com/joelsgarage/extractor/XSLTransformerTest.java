/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

/**
 * Test the XSLTransformer, i.e. try some simple XSLT.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class XSLTransformerTest extends TestCase {

	protected ByteArrayInputStream getTransform() {
		String transform = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" //
			+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"//
			+ "<xsl:template match=\"/\">"//
			+ "  <html>"//
			+ "  <body>"//
			+ "    <h2>My CD Collection</h2>"//
			+ "    <table border=\"1\">"//
			+ "    <tr bgcolor=\"#9acd32\">"//
			+ "      <th align=\"left\">Title</th>"//
			+ "      <th align=\"left\">Artist</th>"//
			+ "    </tr>"//
			+ "    <xsl:for-each select=\"catalog/cd\">"//
			+ "    <tr>"//
			+ "      <td><xsl:value-of select=\"title\"/></td>"//
			+ "      <td><xsl:value-of select=\"artist\"/></td>"//
			+ "    </tr>"//
			+ "    </xsl:for-each>"//
			+ "    </table>"//
			+ "  </body>"//
			+ "  </html>"//
			+ "</xsl:template>"//
			+ "</xsl:stylesheet>";
		return new ByteArrayInputStream(transform.getBytes());
	}

	protected ByteArrayInputStream getData() {
		String data = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"//
			+ "<catalog>"//
			+ "  <cd>"//
			+ "    <title>Empire Burlesque</title>"//
			+ "    <artist>Bob Dylan</artist>"//
			+ "    <country>USA</country>"//
			+ "    <company>Columbia</company>"//
			+ "    <price>10.90</price>"//
			+ "    <year>1985</year>"//
			+ "  </cd>"//
			+ "  <cd>"//
			+ "    <title>Foo Title</title>"//
			+ "    <artist>Foo Artist</artist>"//
			+ "    <country>Foo USA</country>"//
			+ "    <company>Foo Co</company>"//
			+ "    <price>0.90</price>"//
			+ "    <year>1995</year>"//
			+ "  </cd>"//
			+ "  <cd>"//
			+ "    <title>Empire</title>"//
			+ "    <artist>Bob</artist>"//
			+ "    <country>US</country>"//
			+ "    <company>Col</company>"//
			+ "    <price>10.00</price>"//
			+ "    <year>1975</year>"//
			+ "  </cd>"//
			+ "</catalog>";

		return new ByteArrayInputStream(data.getBytes());
	}

	protected String getExpectedOutput() {
		String expectedOutput = "<html>\n"//
			+ "<body>\n"//
			+ "<h2>My CD Collection</h2>\n"//
			+ "<table border=\"1\">\n"//
			+ "<tr bgcolor=\"#9acd32\">\n"//
			+ "<th align=\"left\">Title</th><th align=\"left\">Artist</th>\n"//
			+ "</tr>\n"//
			+ "<tr>\n"//
			+ "<td>Empire Burlesque</td><td>Bob Dylan</td>\n"//
			+ "</tr>\n"//
			+ "<tr>\n"//
			+ "<td>Foo Title</td><td>Foo Artist</td>\n"//
			+ "</tr>\n"//
			+ "<tr>\n"//
			+ "<td>Empire</td><td>Bob</td>\n"//
			+ "</tr>\n"//
			+ "</table>\n"//
			+ "</body>\n"//
			+ "</html>\n";
		return expectedOutput;
	}

	public void testSomething() {
		ByteArrayInputStream transform = getTransform();
		ByteArrayInputStream data = getData();
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		try {
			XSLTransformer transformer = new XSLTransformer(transform);
			transformer.transform(data, output);

			assertEquals(getExpectedOutput(), output.toString());
		} catch (XSLTransformer.InitializationException e) {
			fail();
		}
	}

	public void testCrapXSL() {
		ByteArrayInputStream crap = new ByteArrayInputStream("foo".getBytes());

		try {
			@SuppressWarnings("unused")
			XSLTransformer transformer = new XSLTransformer(crap);
			// we should never get here
			fail();
		} catch (XSLTransformer.InitializationException e) {
			assertTrue(true); // i.e. success()
		}
	}
}
