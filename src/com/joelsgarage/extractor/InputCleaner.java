/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.htmlcleaner.HtmlCleaner;

/**
 * A trivial wrapper for HTMLCleaner or JTidy or whatever, that makes HTML evaluatable via XSLT.
 * 
 * @author joel
 * 
 */
public class InputCleaner {
	HtmlCleaner cleaner;

	public InputCleaner(InputStream input) {
		setCleaner(new HtmlCleaner(input));
	}

	public void clean() throws IOException {
		getCleaner().clean();
	}

	public void writeXmlToStream(OutputStream out) throws IOException {
		getCleaner().writeXmlToStream(out);
	}

	public HtmlCleaner getCleaner() {
		return this.cleaner;
	}

	public void setCleaner(HtmlCleaner cleaner) {
		this.cleaner = cleaner;
	}
}
