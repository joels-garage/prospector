/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */

package com.joelsgarage.extractor;

import java.io.File;

import org.kohsuke.args4j.Option;

/**
 * Options for the Extractor application.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ExtractorOptions {
    /** Input format */
    enum Source {
        ARC, FREEBASE, GOOGLE, WORDNET
    }

    /** Output format, i.e. which writer to use */
    enum OutputFormat {
        XML, TSV
    }

    @Option(name = "-s", usage = "which kind of source.\n", required = true)
    private Source source;

    @Option(name = "-outlimit", usage = "stop after this many output records", required = false)
    private int outLimit = 0;

    @Option(name = "-inlimit", usage = "stop after this many input records", required = false)
    private int inLimit = 0;

    @Option(name = "-in", usage = "input from this file", required = true)
    private File in = new File("");

    @Option(name = "-out", usage = "output to this file", required = false)
    private File out = new File("");

    @Option(name = "-fmt", usage = "output file format", required = true)
    private OutputFormat fmt;

    @Option(name = "-basename", usage = "basename for TSV file output", required = false)
    private String basename;

    @Option(name = "-progress", usage = "time every N input rows", required = false)
    private int progressCount = 10000;

    public ExtractorOptions() {
        // foo
    }

    public int getOutLimit() {
        return this.outLimit;
    }

    public void setOutLimit(int outLimit) {
        this.outLimit = outLimit;
    }

    public int getInLimit() {
        return this.inLimit;
    }

    public void setInLimit(int inLimit) {
        this.inLimit = inLimit;
    }

    public File getIn() {
        return this.in;
    }

    public void setIn(File in) {
        this.in = in;
    }

    public File getOut() {
        return this.out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public Source getSource() {
        return this.source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public OutputFormat getFmt() {
        return this.fmt;
    }

    public void setFmt(OutputFormat fmt) {
        this.fmt = fmt;
    }

    public String getBasename() {
        return this.basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public int getProgressCount() {
        return this.progressCount;
    }

    public void setProgressCount(int progressCount) {
        this.progressCount = progressCount;
    }
}
