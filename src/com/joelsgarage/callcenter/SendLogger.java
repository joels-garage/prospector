/*
 * Copyright 2008 Joel Truher
 * Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * Logs the content of "send" messages.
 * 
 * @author joel
 */
public abstract class SendLogger {
	
	/**
	 * Log the message as debug.
	 * 
	 * Ignores internalNodes.
	 */
	@SuppressWarnings( { "unchecked", "nls" })
	protected static void logSend(final String sendId, final String target, final String targetType,
		final String event, final Map params, final Object hints, final long delay,
		@SuppressWarnings("unused")
		final List externalNodes) {
		Logger logger = Logger.getLogger(SendLogger.class);
		if (logger.isEnabledFor(Level.DEBUG)) {
			StringBuffer buf = new StringBuffer();
			buf.append("foofoo send ( sendId: ").append(sendId);
			buf.append(", target: ").append(target);
			buf.append(", targetType: ").append(targetType);
			buf.append(", event: ").append(event);
			if (params != null) {
				buf.append(", params: ");
				// look at BeanUtils.populate() for this.
				for (Object o : params.entrySet()) {
					if (o instanceof Map.Entry) {
						Map.Entry entry = (Map.Entry) o;
						buf.append(" key: " + entry.getKey().toString());
						Object p = entry.getValue();
						// String valStr;
						if (p instanceof Node) {
							Node n = (Node) p;

							Source input = new DOMSource(n);
							StringWriter out = new StringWriter();
							Result output = new StreamResult(out);
							try {
								getTransformer().transform(input, output);
							} catch (TransformerException te) {
								logger.error(te.getMessage(), te);
								buf.append("<!-- Not all body content was serialized -->");
							}
							buf.append(out.toString()).append("\n");
						} else {
							buf.append("Not a node!");
						}
					} else {
						logger.error("bad send namelist type");
					}
				}
				buf.append(", num-of-params: ").append(String.valueOf(params.size()));
			} else {
				buf.append(", no params ");
			}
			buf.append(", hints: ").append(String.valueOf(hints));
			buf.append(", delay: ").append(delay);

			buf.append(')');
			String logStr = buf.toString();
			logger.debug(logStr);
		}
	}
	
	@SuppressWarnings("nls")
	protected static Transformer getTransformer() {
		Transformer transformer = null;
		Properties outputProps = new Properties();
		outputProps.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
		outputProps.put(OutputKeys.STANDALONE, "no");
		outputProps.put(OutputKeys.INDENT, "yes");
		try {
			TransformerFactory tfFactory = TransformerFactory.newInstance();
			transformer = tfFactory.newTransformer();
			transformer.setOutputProperties(outputProps);
		} catch (Throwable t) {
			return null;
		}
		return transformer;
	}

}
