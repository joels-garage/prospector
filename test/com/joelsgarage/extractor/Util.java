/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Stuff that's useful in more than one place.
 * 
 * @author joel
 * 
 */
public abstract class Util {
	/** A giant test file. TODO: shrink it. */
	public static final String ARC1 = "extractor/ARCS/JOELSGARAGE-20080613214236-00000-joel-laptop.arc.gz"; //$NON-NLS-1$
	/** wc -l = 13312; -1 for the TOC line => data lines = 13311. */
	public static final int ARC_ROWS = 13311;
	public static final String TEST_PROPERTIES = "/extractor/test-xsltransform-extractor.properties"; //$NON-NLS-1$

	/**
	 * In this test, anything bad is fatal.
	 * 
	 * @author joel
	 */
	public static class FatalException extends Exception {
		private static final long serialVersionUID = 1L;

		public FatalException(Throwable e) {
			super(e);
		}
	}

	/** Return the URL corresponding to name. */
	public static URL getURL(String name) {
		ClassLoader loader = Util.class.getClassLoader();
		URL url = loader.getResource(name);
		if (url == null) {
			System.out.println("couldn't find file: " + name); //$NON-NLS-1$
		}
		return url;
	}

	/** Return the File corresponding to name */
	public static File getFile(String name) throws FatalException {
		URL url = getURL(name);
		if (url == null)
			return null;
		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new FatalException(e);
		}
	}

	/** Read a resource into a string. Obviously bad for big files. */
	public static String readFileToString(String name) throws FatalException {
		File file = getFile(name);
		try {
			FileReader fileReader = new FileReader(file);
			return readToString(fileReader);
		} catch (FileNotFoundException e) {
			throw new FatalException(e);
		}
	}

	/** Reads whatever the reader is looking at into the string. */
	public static String readToString(Reader fileReader) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bReader = new BufferedReader(fileReader);
			char[] buffer = new char[1024];
			int numRead = 0;
			while ((numRead = bReader.read(buffer)) != -1) {
				stringBuilder.append(buffer, 0, numRead);
			}
			bReader.close();
			return stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
