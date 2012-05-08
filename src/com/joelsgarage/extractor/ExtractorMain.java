/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.archive.io.arc.ARCRecord;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.readers.ARCFileRecordReader;
import com.joelsgarage.dataprocessing.readers.FileRecordReader;
import com.joelsgarage.dataprocessing.writers.FileRecordWriter;
import com.joelsgarage.dataprocessing.writers.TSVFileRecordWriter;
import com.joelsgarage.dataprocessing.writers.TranslatingXMLWriter;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.RealStreamFactory;

/**
 * The extractor operates on a single input file; thus the usage is as follows:
 * 
 * <pre>
 * java -jar extractor.jar -in &lt;filename&gt; -out &lt;filename&gt; -inlimit &lt;num&gt; -outlimit &lt;num&gt;
 * </pre>
 * 
 * To process more than one input file, use xargs or something.
 * 
 * &#064;author joel
 * 
 */
public class ExtractorMain {
    public static void main(String[] args) throws FatalException {
        ExtractorOptions options = new ExtractorOptions();
        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            return;
        }

        File inputFile = options.getIn();
        File outputFile = options.getOut();
        int inLimit = options.getInLimit();
        int outLimit = options.getOutLimit();

        try {
            InputStream input = null;
            // OutputStream output = null;
            RecordReader<String> stringReader = null;
            RecordWriter<ModelEntity> entityWriter = null;
            if (options.getFmt().equals(ExtractorOptions.OutputFormat.TSV)) {
                if (options.getBasename() != null && !(options.getBasename().isEmpty())) {
                    entityWriter =
                        new TSVFileRecordWriter(new RealStreamFactory(), options.getBasename());
                } else {
                    System.err.println("must supply basename with TSV"); //$NON-NLS-1$
                    return;
                }
            } else {
                entityWriter = new TranslatingXMLWriter(new FileOutputStream(outputFile));
            }
            ModelEntityExtractor<String> entityExtractor = null;

            switch (options.getSource()) {
                case ARC:
                    RecordReader<ARCRecord> reader = new ARCFileRecordReader(inputFile);
                    RecordWriter<String> writer = new FileRecordWriter(outputFile);
                    ARCExtractor extractor =
                        new XSLTransformerExtractor(reader, writer, inLimit, outLimit);
                    extractor.run();
                    break;
                case FREEBASE:
                    input = new FileInputStream(inputFile);
                    stringReader = new FileRecordReader(input);
                    // output = new FileOutputStream(outputFile);
                    // entityWriter = new TranslatingXMLWriter(output);
                    entityExtractor =
                        new FreebaseExtractor(stringReader, entityWriter, inLimit, outLimit);
                    entityExtractor.setProgressCount(options.getProgressCount());
                    entityExtractor.run();
                    break;
                case GOOGLE:
                    input = new FileInputStream(inputFile);
                    stringReader = new FileRecordReader(input);
                    // output = new FileOutputStream(outputFile);
                    // entityWriter = new TranslatingXMLWriter(output);
                    entityExtractor =
                        new GoogleExtractor(stringReader, entityWriter, inLimit, outLimit);
                    entityExtractor.run();
                    break;
                case WORDNET:
                    input = new FileInputStream(inputFile);
                    stringReader = new FileRecordReader(input);
                    // output = new FileOutputStream(outputFile);
                    // entityWriter = new TranslatingXMLWriter(output);
                    entityExtractor =
                        new WordNetExtractor(stringReader, entityWriter, inLimit, outLimit);
                    entityExtractor.run();
                    break;
                default:
                    System.err.println("strange task: " + options.getSource().toString()); //$NON-NLS-1$
                    return;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
