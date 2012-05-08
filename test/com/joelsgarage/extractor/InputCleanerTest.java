/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
public class InputCleanerTest extends TestCase {
	@SuppressWarnings("nls")
	protected String getCrapHtml() {
		String crap = "<html><head><title>this is crap</title></head>"
			+ "<body><p>typical html circa 1993"
			+ "<ul><li>i love lists<li>can't get enough of them</ul>"
			+ "and images <img src=\"foo\"> oh and my favorite "
			+ "the break tag <br> is so good <br> for making spaces. </body></html>";
		return crap;
	}
	
	@SuppressWarnings("nls")
	protected String getContent() {
		String content = "<?xml version=\"1.0\"?>\n" + "<tag>this should produce very little output</tag>\n";
		return content;
	}
	
	@SuppressWarnings("nls")
	protected String getExpectedXMLOutput() {
		String expected = "<?xml version=\"1.0\"?>\n" //
			+ "<html><head/>\n"
			+ "<body>&lt;?xml version=&quot;1.0&quot;?&gt;\n"
			+ "<tag>this should produce very little output</tag>\n"
			+ "</body>\n"
			+ "</html>\n";
		return expected;
	}

	@SuppressWarnings("nls")
	protected String getExpectedOutput() {
		String expected = "<?xml version=\"1.0\"?>\n"
			+ "<html><head><title>this is crap</title>\n" //
			+ "</head>\n" //
			+ "<body><p>typical html circa 1993</p>\n" //
			+ "<ul><li>i love lists</li>\n"//
			+ "<li>can&apos;t get enough of them</li>\n" //
			+ "</ul>\n" //
			+ "and images <img src=\"foo\"/>\n" //
			+ " oh and my favorite the break tag <br/>\n" //
			+ " is so good <br/>\n"
			+ " for making spaces. </body>\n"
			+ "</html>\n";
		return expected;
	}

	public void testCleanAndWrite() {
		try {
			
			ByteArrayInputStream input = new ByteArrayInputStream(getCrapHtml().getBytes());
			InputCleaner cleaner = new InputCleaner(input);
			
			cleaner.clean();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			cleaner.writeXmlToStream(output);
			assertEquals(getExpectedOutput(), output.toString());
		} catch (IOException e) {
			fail();
		}
	}
	
	public void testCleanXMLAndWrite() {
		try {
			
			ByteArrayInputStream input = new ByteArrayInputStream(getContent().getBytes());
			InputCleaner cleaner = new InputCleaner(input);
			
			cleaner.clean();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			cleaner.writeXmlToStream(output);
			assertEquals(getExpectedXMLOutput(), output.toString());
		} catch (IOException e) {
			fail();
		}
	}
}
