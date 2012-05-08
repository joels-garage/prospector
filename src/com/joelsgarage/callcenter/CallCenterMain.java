/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.joelsgarage.dataprocessing.readers.HibernateRecordReaderFactory;

/**
 * CallCenter is the IM application. It goes into its own JAR.
 * 
 * This class does nothing but contain the main(), and, I guess, handle command line arguments, if
 * we have any.
 * 
 * @author joel
 * 
 */
public class CallCenterMain {

    // start here
    //    
    // add an "options" object with the database in it. make sure i use this db for everything.

    /**
     * @param args
     *            are ignored
     */
    public static void main(String[] args) {
        Logger.getLogger(CallCenterMain.class).info("starting callcenter"); //$NON-NLS-1$
        CallCenterOptions callCenterOptions = new CallCenterOptions();
        CmdLineParser parser = new CmdLineParser(callCenterOptions);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException ex) {
            // handling of wrong arguments
            System.err.println(ex.getMessage());
            parser.printUsage(System.err);
            return;
        }

        HibernateRecordReaderFactory sessionFactory =
            new HibernateRecordReaderFactory(callCenterOptions.getDatabase());
        // if (sessionFactory == null) {
        // System.err.println("can't make session factory"); //$NON-NLS-1$
        // return;
        //        }
        CallCenter callCenter = new CallCenter(sessionFactory);
        callCenter.doWork();
    }
}
