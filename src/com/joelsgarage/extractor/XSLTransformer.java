/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Uses XSLT to transform a well-formed XML document into something we can load.
 * 
 * This class contains a single template.
 * 
 * @author joel
 * 
 */
public class XSLTransformer {
	/** Bad input filename, or invalid template */
	public static class InitializationException extends Exception {
		private static final long serialVersionUID = 1L;

		public InitializationException() {
			super();
		}

		public InitializationException(Exception e) {
			super(e);
		}
	}

	private Transformer transformer;

	/**
	 * Construct an XSLTransformer using the specified inputstream as the stylesheet. Will not work
	 * with templates that contain any external references, e.g. xsl:include.
	 * 
	 * @param transform
	 *            the input stylesheet
	 * @throws InitializationException
	 */
	public XSLTransformer(InputStream transform) throws InitializationException {
		initialize(transform, null);
	}

	/**
	 * Construct an XSLTransformer using the specified inputstream as the main stylesheet, and the
	 * specified systemId to resolve any relative URLs encountered therein.
	 * 
	 * @param transform
	 *            the input stylesheet
	 * @param systemId
	 *            the URL of that stylesheet
	 * @throws InitializationException
	 */
	public XSLTransformer(InputStream transform, String systemId) throws InitializationException {
		initialize(transform, systemId);
	}

	protected void initialize(InputStream transform, String systemId)
		throws InitializationException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		// This picky listener allows us to capture any exception that would prevent us
		// from using the Transformer.
		tFactory.setErrorListener(new ErrorListener() {
			@Override
			public void error(TransformerException exception) throws TransformerException {
				throw exception;
			}

			@Override
			public void fatalError(TransformerException exception) throws TransformerException {
				throw exception;
			}

			@Override
			public void warning(TransformerException exception) throws TransformerException {
				throw exception;
			}
		});

		StreamSource streamSource;
		if (systemId == null) {
			streamSource = new StreamSource(transform);
		} else {
			streamSource = new StreamSource(transform, systemId);
		}

		try {
			setTransformer(tFactory.newTransformer(streamSource));

		} catch (TransformerConfigurationException e) {
			// The listener above should put us here in case of any unrecoverable error.
			// So, fail entirely, the xsl is wrong
			setTransformer(null);
			bail(e);
		}
	}

	protected void bail(Exception e) throws InitializationException {
		// e.printStackTrace();
		throw new InitializationException(e);
	}

	@SuppressWarnings("nls")
	public void setParameter(String name, Object value) {
		// Logger.getLogger(XSLTransformer.class).info(
		// "setting parameter name: " + name + " value: " + value.toString());
		getTransformer().setParameter(name, value);
	}

	public void clearParameters() {
		getTransformer().clearParameters();
	}

	public void transform(InputStream data, OutputStream output) {
		if (getTransformer() == null)
			return;
		try {
			getTransformer().transform(new StreamSource(data), new StreamResult(output));
		} catch (TransformerException e) {
			e.printStackTrace();
			// could be something wrong with the input data; try the next one.
			return;
		}
	}

	public Transformer getTransformer() {
		return this.transformer;
	}

	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}
}
