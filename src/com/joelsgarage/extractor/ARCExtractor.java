/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import org.archive.io.arc.ARCRecord;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * Transform an ARC file into a single XML file to load with Skiploader.
 * 
 * Configure it with some extraction patterns, on a per-site basis. Can work with multiple sites
 * commingled into the same ARC file (as the crawl would usually have).
 * 
 * Ultimately, I suppose the XML file could be obviated, and this could load directly into the db,
 * but for now, the file gives me a chance to look at the output, quickly, and the diff function in
 * skiploader is a useful check too.
 * 
 * This is for offline processing of offline crawls. Eventually, some of this will be seen by
 * end-users, but for now, it's private.
 * 
 * The kinds of things to extract from pages include:
 * 
 * <ul>
 * <li>Classes (i.e. class names)
 * <li>Class Axioms indicating subsumption, i.e. names of parent classes, child classes
 * <li>Individuals, including the "canonical individual" (TODO: denote that somehow)
 * <li>Facts, e.g. Juniperus virginiana height=25-40 feet
 * </ul>
 * 
 * Every extracted record is actually two records -- the entity itself, and an "evidence" record
 * linking the entity with the URL (and any other data about the evidence, e.g. the xpath that found
 * it)
 * 
 * The basic flow is as follows:
 * 
 * <pre>
 * For each ARC file specified on the commandline, {
 *   Read a record from the file
 *   If the record type is inappropriate (e.g. html), bail
 *   Normalize the content into XML, e.g. with HtmlCleaner, JTidy, whatever
 *   Classify the page into the namespace of field extractors (e.g. oregon.edu/plants/species)
 *   For each field extractor for this page {
 *     For each xpath match {
 *       Apply the regex on the match to find the field contents
 *       Transform the field contents into the correct type, e.g. measurement range, if required.
 *     }
 *   }
 * }
 * </pre>
 * 
 * I changed my mind about the virtue of propagating the "Options" object here; instead all the
 * options are separate constructor args.
 * 
 * I'd like the "input file" to be some sort of easily-mockable thing, but, the underlying arc
 * reader really wants a file.
 * 
 * @author joel
 * 
 */
public class ARCExtractor extends ProcessNode<ARCRecord, String> {
	public ARCExtractor(RecordReader<ARCRecord> reader, RecordWriter<String> writer, int inLimit,
		int outLimit) {
		super(reader, writer, inLimit, outLimit);
	}

	@Override
	protected boolean handleRecord(ARCRecord record) {
		// by default, output the URI of each record.
		output(record.getMetaData().getUrl());
		return true;
	}
}
