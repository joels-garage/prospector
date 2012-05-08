/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.readers.HibernateRecordReader;
import com.joelsgarage.dataprocessing.readers.HibernateRecordReaderFactory;
import com.joelsgarage.dataprocessing.writers.TranslatingXMLWriter;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.StringFact;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.HibernateUtil;

/**
 * A gardener reads from the DB and produces an XML file.
 * 
 * @author joel
 * 
 */
public class GardenerMain {
    public static void main(String[] args) {
        GardenerOptions gardenerOptions = new GardenerOptions();
        CmdLineParser parser = new CmdLineParser(gardenerOptions);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException ex) {
            // handling of wrong arguments
            System.err.println(ex.getMessage());
            parser.printUsage(System.err);
            return;
        }

        SessionFactory factory = HibernateUtil.createSessionFactory(null, // default config
            gardenerOptions.getDatabase()); // configured database
        if (factory == null) {
            System.err.println("can't make session factory"); //$NON-NLS-1$
            return;
        }

        Session readerSession = null;
        try {
            Gardener gardener;
            OutputStream output = new FileOutputStream(gardenerOptions.getFilename());
            RecordWriter<ModelEntity> writer = new TranslatingXMLWriter(output);
            RecordReaderFactory<ModelEntity> readerFactory =
                new HibernateRecordReaderFactory(gardenerOptions.getDatabase());

            switch (gardenerOptions.getTask()) {
                case SPLIT:
                    readerSession = factory.openSession();
                    RecordReader<StringFact> reader =
                        new HibernateRecordReader<StringFact>(readerSession, null) {
                            // this anonymous class shows the compiler the parameter
                        };
                    gardener = new FactValueSplitGardener(reader, writer, 0, 0);
                    break;
                case MEASUREMENT:
                    gardener = new StringToMeasurementFactGardener(readerFactory, writer, 0, 0);
                    break;
                case INDIVIDUAL:
                    gardener = new StringToIndividualFactGardener(readerFactory, writer, 0, 0);
                    break;
                case ALL:
                    gardener = new StringFactGardener(readerFactory, writer, 0, 0);
                    break;
                default:
                    System.err.println("strange task: " + gardenerOptions.getTask().toString()); //$NON-NLS-1$
                    return;
            }

            gardener.run();
            System.err.println("All done!"); //$NON-NLS-1$
            if (readerSession != null)
                readerSession.close();
            return;

        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
            return;
        } catch (FatalException ex) {
            System.err.println(ex.getMessage());
            if (readerSession != null)
                readerSession.close();
            return;
        }
    }
}
