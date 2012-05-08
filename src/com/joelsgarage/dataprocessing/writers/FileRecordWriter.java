/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * Writer implementation that uses a real file.
 * 
 * @author joel
 */
public class FileRecordWriter implements RecordWriter<String> {
	private File outputFile;
	private FileOutputStream output;
	private OutputStreamWriter writer;

	public FileRecordWriter(File file) {
		setOutputFile(file);
	}

	@Override
	public void open() {
		try {
			if (getOutputFile() != null) {
				setOutput(new FileOutputStream(getOutputFile()));
			} else {
				Logger.getLogger(FileRecordWriter.class).error("couldn't get output file"); //$NON-NLS-1$
			}
			if (getOutput() != null) {
				setWriter(new OutputStreamWriter(getOutput()));
			} else {
				Logger.getLogger(FileRecordWriter.class).error("couldn't get output stream"); //$NON-NLS-1$
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void write(String payload) {
		// Logger.getLogger(FileExtractorWriter.class).info("Writing payload: " + payload);
		// //$NON-NLS-1$
		try {
			getWriter().write(payload);
			// TODO(joel): Consider flushing less often?
			getWriter().flush();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	// Accessors

	public File getOutputFile() {
		return this.outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public FileOutputStream getOutput() {
		return this.output;
	}

	public void setOutput(FileOutputStream output) {
		this.output = output;
	}

	public OutputStreamWriter getWriter() {
		return this.writer;
	}

	public void setWriter(OutputStreamWriter writer) {
		this.writer = writer;
	}

}