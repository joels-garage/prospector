/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.archive.io.arc.ARCRecord;
import org.archive.io.arc.ARCRecordMetaData;
import org.archive.util.ArchiveUtils;

import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.util.DateUtil;

/**
 * Adapts an XSLTransformer to an Extractor.
 * 
 * Keeps a map of URL patterns to XSL templates.
 * 
 * For each record, match the URL to get the template, run it, and then, uh, barf.
 * 
 * @author joel
 * 
 */
public class XSLTransformerExtractor extends ARCExtractor {
    /** We only accept records of this mime type */
	private static final String TEXT_HTML = "text/html"; //$NON-NLS-1$
    /** The name of the crawl date parameter in XSLT, used for <lastModified> */
	private static final String CRAWL_DATE = "crawl_date"; //$NON-NLS-1$
	// TODO: put this string in some sort of master properties file
	private static final String PROD_PROPERTIES_FILE = "/xsltransform-extractor.properties"; //$NON-NLS-1$
	private Logger logger;
	private Properties properties;
	private Map<Pattern, XSLTransformer> transformers;
	/** maps xsl files to url patterns */
	private String propertiesFile;

	public XSLTransformerExtractor(RecordReader<ARCRecord> reader, RecordWriter<String> writer,
		int inLimit, int outLimit, String propertiesFile) {
		super(reader, writer, inLimit, outLimit);
		setLogger(Logger.getLogger(XSLTransformerExtractor.class));
		setProperties(new Properties());
		setTransformers(new HashMap<Pattern, XSLTransformer>());
		setPropertiesFile(propertiesFile);
		loadTemplates();
	}

	public XSLTransformerExtractor(RecordReader<ARCRecord> reader, RecordWriter<String> writer,
		int inLimit, int outLimit) {
		this(reader, writer, inLimit, outLimit, PROD_PROPERTIES_FILE);
	}

	/**
	 * Load the templates specified in the properties file, and initialize the map.
	 */
	protected void loadTemplates() {
		try {
			loadProperties(getPropertiesFile());
		} catch (IOException e) {
			// can't read the properties; this extractor becomes a no-op.
			getLogger().error("couldn't load properties: " + getPropertiesFile()); //$NON-NLS-1$
			return;
		}
		for (String filename : getProperties().stringPropertyNames()) {
			String regex = getProperties().getProperty(filename);
			try {
				InputStream template = getStream(filename);
				Pattern pattern = Pattern.compile(regex);
				String systemId = getSystemId(filename);
				XSLTransformer transformer = new XSLTransformer(template, systemId);
				getTransformers().put(pattern, transformer);
				// Any exceptions here? Just log it and try the next one.
			} catch (IOException e) {
				getLogger().error("couldn't open file: " + filename  //$NON-NLS-1$
				    + " error " + e.getMessage()); //$NON-NLS-1$
			} catch (XSLTransformer.InitializationException e) {
				getLogger().error(
					"couldn't create transformer for filename: " + filename //$NON-NLS-1$
					+ " error " //$NON-NLS-1$
						+ e.getMessage());
			} catch (PatternSyntaxException e) {
				getLogger().error("invalid regex pattern: " + regex  //$NON-NLS-1$
				    + " error " + e.getMessage()); //$NON-NLS-1$
			}
		}
	}

	/** Return the stream for this file, or null */
	protected InputStream getStream(String filename) throws IOException {
		URL url = this.getClass().getResource(filename);
		if (url == null) {
			throw new IOException("can't find file: " + filename); //$NON-NLS-1$
		}
		return url.openStream();
	}

	protected String getSystemId(String filename) {
		URL url = this.getClass().getResource(filename);
		String systemId = url.toExternalForm();
		return systemId;
	}

	protected void loadProperties(String filename) throws IOException {
		getProperties().load(getStream(filename));
	}

	/**
	 * @see ARCExtractor#handleRecord(ARCRecord)
	 */
	@Override
	public boolean handleRecord(ARCRecord record) {
		if (record == null) {
			return true;
		}
		// getLogger().info("handling record number: " + getInCount());
		ARCRecordMetaData metaData = record.getMetaData();
		if (metaData == null) {
			// getLogger().error("bad metadata");
			return true;
		}
		String mimeType = metaData.getMimetype();
		if (!mimeType.equalsIgnoreCase(TEXT_HTML)) {
			// getLogger().info("bad mimetype: '"+ mimeType + "' for url: '" + metaData.getUrl() +
			// "'");
			return true;
		}

		String arcDate = metaData.getDate(); // raw crawl date
		String isoDate = new String(); // date to pass to XSLT

		try {
			Date date = ArchiveUtils.parse14DigitDate(arcDate);
			isoDate = DateUtil.formatDateToISO8601(date);
		} catch (ParseException e) {
			getLogger().info("bad crawl date: " + arcDate); //$NON-NLS-1$
		}

		String url = metaData.getUrl();
		// getLogger().info("Looking for match for url: " + url);

		for (Map.Entry<Pattern, XSLTransformer> entry : getTransformers().entrySet()) {
			Pattern pattern = entry.getKey();
			// getLogger().info("Trying pattern: " + pattern.pattern());
			Matcher matcher = pattern.matcher(url);
			if (matcher.matches()) {
				// getLogger().info("Matched url: " + url);
				// usually just one matcher will match for an input record.
				XSLTransformer transformer = entry.getValue();
				transformer.clearParameters();
				transformer.setParameter(CRAWL_DATE, isoDate);
				try {
					record.skipHttpHeader();
					ByteArrayOutputStream recordDump = new ByteArrayOutputStream();
					record.dump(recordDump);
					byte[] buf = recordDump.toByteArray();
					ByteArrayInputStream data = new ByteArrayInputStream(buf);

					// String recordString = new String(buf);
					// getLogger().info("record: " + recordString); //$NON-NLS-1$

					InputCleaner cleaner = new InputCleaner(data);
					cleaner.clean();
					ByteArrayOutputStream cleanOutput = new ByteArrayOutputStream();
					cleaner.writeXmlToStream(cleanOutput);
					byte[] cleanBuf = cleanOutput.toByteArray();
					ByteArrayInputStream cleanData = new ByteArrayInputStream(cleanBuf);

					ByteArrayOutputStream output = new ByteArrayOutputStream();
					transformer.transform(cleanData, output);
					// getLogger().info("output: " + output.toString());
					output(output.toString());
				} catch (IOException e) {
					e.printStackTrace();
					// press on
				}
			}
		}
		return true;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Logger getLogger() {
		return this.logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Map<Pattern, XSLTransformer> getTransformers() {
		return this.transformers;
	}

	public void setTransformers(Map<Pattern, XSLTransformer> transformers) {
		this.transformers = transformers;
	}

	public String getPropertiesFile() {
		return this.propertiesFile;
	}

	public void setPropertiesFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

}
