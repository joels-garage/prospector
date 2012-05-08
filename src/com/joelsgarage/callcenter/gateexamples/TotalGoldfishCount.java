package com.joelsgarage.callcenter.gateexamples;

/**
 * A standalone application that makes use of GATE PR's as well as a user defined one. The program
 * displays the features of each document as created by the PR "Goldfish".
 * 
 * @author Andrew Golightly (acg4@cs.waikato.ac.nz) -- last updated 16/05/2003
 */

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.creole.SerialAnalyserController;
import gate.creole.splitter.SentenceSplitter;
import gate.creole.tokeniser.DefaultTokeniser;
import gate.util.GateException;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class TotalGoldfishCount {
    private static final String GATEEXAMPLES_PLUGINS_GOLDFISH = "/gateexamples/plugins/Goldfish"; //$NON-NLS-1$

    private Corpus corpus;

    private SerialAnalyserController pipeline;

    /** Initialize Gate */
    public TotalGoldfishCount() throws URISyntaxException, GateException {
        if (Gate.getGateHome() == null)
            Gate.setGateHome(new File(this.getClass().getResource(
                Util.GATEEXAMPLES).toURI()));

        if (Gate.getUserConfigFile() == null)
            Gate.setUserConfigFile(new File(this.getClass().getResource(
                Util.GATEEXAMPLES_USER_GATE_XML).toURI()));

        Gate.init();

        // ANNIE plug-in, copied to our bin dir
        Gate.getCreoleRegister().registerDirectories(
            this.getClass().getResource(Util.GATEEXAMPLES_PLUGINS_ANNIE));
        Gate.getCreoleRegister().registerDirectories(
            this.getClass().getResource(GATEEXAMPLES_PLUGINS_GOLDFISH));

        this.pipeline =
            (SerialAnalyserController) Factory.createResource(SerialAnalyserController.class
                .getName());

        String[] processingResources = { DefaultTokeniser.class.getName(), // in ANNIE
            SentenceSplitter.class.getName(), // in ANNIE
            Goldfish.class.getName() // in Goldfish
            };

        for (int pr = 0; pr < processingResources.length; pr++) {
            Logger.getLogger(TotalGoldfishCount.class).info("\t* Loading " //$NON-NLS-1$
                + processingResources[pr] + " ... "); //$NON-NLS-1$
            this.pipeline.add((gate.LanguageAnalyser) Factory
                .createResource(processingResources[pr]));
            Logger.getLogger(TotalGoldfishCount.class).info("done"); //$NON-NLS-1$
        }
    }

    /** Do everything: create a corpus, process it, and display the results */
    public void run(String input) throws GateException {

        Logger.getLogger(TotalGoldfishCount.class).info("== CREATING CORPUS =="); //$NON-NLS-1$
        createCorpus(input);

        Logger.getLogger(TotalGoldfishCount.class)
            .info("== USING GATE TO PROCESS THE DOCUMENTS =="); //$NON-NLS-1$

        runProcessingResources();

        Logger.getLogger(TotalGoldfishCount.class).info("== DOCUMENT FEATURES =="); //$NON-NLS-1$
        displayDocumentFeatures();

        Logger.getLogger(TotalGoldfishCount.class).info("Demo done... :)"); //$NON-NLS-1$
    }

    /** Create a corpus with a single document containing the specified input */
    @SuppressWarnings("unchecked")
    private void createCorpus(String input) throws GateException {
        this.corpus = Factory.newCorpus("Transient Gate Corpus"); //$NON-NLS-1$

        Logger.getLogger(TotalGoldfishCount.class).info("input: " + String.valueOf(input)); //$NON-NLS-1$
        try {
            this.corpus.add(Factory.newDocument(input));
            Logger.getLogger(TotalGoldfishCount.class).info(" -- success"); //$NON-NLS-1$
        } catch (gate.creole.ResourceInstantiationException e) {
            Logger.getLogger(TotalGoldfishCount.class).info(" -- failed (" //$NON-NLS-1$
                + e.getMessage() + ")"); //$NON-NLS-1$
        } catch (Exception e) {
            Logger.getLogger(TotalGoldfishCount.class).info(" -- " + e.getMessage()); //$NON-NLS-1$
        }

    }

    /** Operate on the corpus, leaving the output of the operation in the document(s) */
    private void runProcessingResources() throws GateException {

        Logger.getLogger(TotalGoldfishCount.class).info(
            "Creating corpus from documents obtained..."); //$NON-NLS-1$

        this.pipeline.setCorpus(this.corpus);

        Logger.getLogger(TotalGoldfishCount.class).info("done"); //$NON-NLS-1$

        Logger.getLogger(TotalGoldfishCount.class).info(
            "Running processing resources over corpus..."); //$NON-NLS-1$

        this.pipeline.execute();

        Logger.getLogger(TotalGoldfishCount.class).info("done"); //$NON-NLS-1$
    }

    /** Display features of the documents in the corpus produced by the above operations */
    @SuppressWarnings("unchecked")
    private void displayDocumentFeatures() {
        Iterator documentIterator = this.corpus.iterator();

        while (documentIterator.hasNext()) {
            Document currDoc = (Document) documentIterator.next();
            if (currDoc == null) {
                Logger.getLogger(TotalGoldfishCount.class).info("null currDoc"); //$NON-NLS-1$
                continue;
            }

            Logger.getLogger(TotalGoldfishCount.class).info("The features of the document are:"); //$NON-NLS-1$

            gate.FeatureMap documentFeatures = currDoc.getFeatures();

            Iterator featureIterator = documentFeatures.keySet().iterator();
            while (featureIterator.hasNext()) {
                String key = (String) featureIterator.next();
                Logger.getLogger(TotalGoldfishCount.class).info("key: " + key //$NON-NLS-1$
                    + " --> " + documentFeatures.get(key)); //$NON-NLS-1$
            }
        }
    }

    /** Return the number of words found in the corpus. */
    public int numberOfWords() {
        return intFeature(Goldfish.NUMBER_OF_WORDS);
    }

    /** Return the number of occurrences of the word "Goldfish" found in the corpus. */
    public int numberOfGoldfish() {
        return intFeature(Goldfish.TOTAL_GOLDFISH_COUNT);
    }

    /** Return the value of the specified integer doc feature, total over the corpus. */
    @SuppressWarnings("unchecked")
    public int intFeature(String featureName) {
        int total = 0;

        Iterator documentIterator = this.corpus.iterator();

        while (documentIterator.hasNext()) {
            Document currDoc = (Document) documentIterator.next();
            if (currDoc == null) {
                Logger.getLogger(TotalGoldfishCount.class).info("null currDoc"); //$NON-NLS-1$
                continue;
            }

            Logger.getLogger(TotalGoldfishCount.class).info("The features of the document are:"); //$NON-NLS-1$

            gate.FeatureMap documentFeatures = currDoc.getFeatures();

            Iterator featureIterator = documentFeatures.keySet().iterator();
            while (featureIterator.hasNext()) {
                String key = (String) featureIterator.next();
                if (key == null)
                    continue;
                if (key.equals(featureName)) {
                    Object value = documentFeatures.get(key);
                    if (value instanceof String) {
                        String stringValue = (String) value;
                        try {
                            int wordCount = Integer.valueOf(stringValue).intValue();
                            total += wordCount;
                        } catch (NumberFormatException ex) {
                            // swallow it
                        }
                    }
                }
            }
        }
        return total;
    }
}
