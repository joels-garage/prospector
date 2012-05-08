/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Skiploader is the XML dumper/loader application. It goes into its own JAR.
 * 
 * This class does nothing but contain the main(), and handle command line arguments.
 * 
 * @author joel
 * 
 */
public class SkiploaderMain {
	public static void main(String[] args) {
		System.out.println("hello skiploader main"); //$NON-NLS-1$

		SkiploaderOptions skiploaderOptions = new SkiploaderOptions();
		CmdLineParser parser = new CmdLineParser(skiploaderOptions);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			// handling of wrong arguments
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			return;
		}

		// Now we have valid options.
		DataAccess dataAccess = new HibernateDataAccess(null, skiploaderOptions.getDatabase());
		Skiploader skiploader = new Skiploader(dataAccess);
		skiploader.doWork(skiploaderOptions.getTask(), skiploaderOptions.getFilename(),
			skiploaderOptions.getDatabase(), skiploaderOptions.getFmt());
	}
}
