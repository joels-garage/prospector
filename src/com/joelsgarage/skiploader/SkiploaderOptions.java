/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import org.kohsuke.args4j.Option;

/**
 * Options for the Skiploader application.
 * 
 * @author joel
 * 
 */
public class SkiploaderOptions {

    enum Task {
        /** db -> xml */
        DUMP,
        /** xml -> db, destructive */
        LOAD,
        /** compare db and xml */
        DIFF,
        /** load the output of the extractor, which is many concatenated XML files */
        LOADARC,
        /** db -> GATE Gazetteer files */
        DUMPGATE
    }

    enum InputFormat {
        XML, TSV
    }

    @Option(name = "-t", usage = "Task, i.e. what to do.", required = true)
    public Task task = Task.DUMP;

    /**
     * This is a String rather than a File because it's just the basename, so we have to create the
     * File anyway.
     */
    @Option(name = "-f", usage = "use this input file; also used as the basename for diff output files.", required = true)
    private String filename = ""; //$NON-NLS-1$

    /**
     * Skip the namespace if you want everything. Currently ignored.
     */
    @Option(name = "-ns", usage = "namespace to restrict the DB queries.", required = false)
    private String namespace = ""; //$NON-NLS-1$

    /**
     * The name of the database to use, i.e. "use <database>"
     */
    @Option(name = "-db", usage = "database name.", required = true)
    private String database = ""; //$NON-NLS-1$

    @Option(name = "-fmt", usage = "input file format", required = true)
    private InputFormat fmt;

    @Option(name = "-progress", usage = "time every N input rows", required = false)
    private int progressCount = 10000;

    public SkiploaderOptions() {
        // foo
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public InputFormat getFmt() {
        return this.fmt;
    }

    public void setFmt(InputFormat fmt) {
        this.fmt = fmt;
    }

    public int getProgressCount() {
        return this.progressCount;
    }

    public void setProgressCount(int progressCount) {
        this.progressCount = progressCount;
    }

}
