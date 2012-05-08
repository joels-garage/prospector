/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.kohsuke.args4j.Option;

/**
 * Options for the Gardener application.
 * 
 * @author joel
 * 
 */
public class CallCenterOptions {

    // enum Task {
    // /** Split string facts at commas */
    // SPLIT,
    // /** Convert unitized measures to the correct representation */
    // MEASUREMENT,
    // /** Convert strings to individuals */
    // INDIVIDUAL,
    // /** All of the above */
    // ALL
    // }

    // @Option(name = "-t", usage = "Which gardener to run.\n", required = true)
    // private Task task;

    // /**
    // * This is a String rather than a File because it's just the basename, so we have to create
    // the
    // * File anyway.
    // */
    // @Option(name = "-f", usage = "Output file name.\n", required = true)
    // private String filename = ""; //$NON-NLS-1$
    //
    // /**
    // * Skip the namespace if you want everything. Ignored, I think, for now.
    // */
    // @Option(name = "-ns", usage = "namespace to restrict the DB queries.\n", required = false)
    // private String namespace = ""; //$NON-NLS-1$

    /**
     * The name of the database to use, i.e. "use <database>"
     */
    @Option(name = "-db", usage = "database name.\n", required = true)
    private String database = ""; //$NON-NLS-1$

    public CallCenterOptions() {
        // foo
    }

    // public String getFilename() {
    // return this.filename;
    // }
    //
    // public void setFilename(String filename) {
    // this.filename = filename;
    // }
    //
    // public String getNamespace() {
    //        return this.namespace;
    //    }
    //
    // public void setNamespace(String namespace) {
    // this.namespace = namespace;
    //    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    // public Task getTask() {
    // return this.task;
    // }
    //
    // public void setTask(Task task) {
    // this.task = task;
    //    }
}
