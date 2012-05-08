/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.gateexamples;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.GateConstants;
import gate.corpora.DocumentImpl;
import gate.creole.ANNIEConstants;
import gate.util.GateException;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.joelsgarage.callcenter.gateexamples.StandAloneAnnie.SortedAnnotationList;
import com.joelsgarage.util.InitializationException;

/**
 * This is from the GATE examples, modified, e.g. to operate on a single string at a time, for
 * style. Also I don't care about positioning.
 * 
 * @author joel
 */
public class StandAloneAnnieDriver {
    /** The ANNIE instance that does the work */
    StandAloneAnnie annie;
    /** The current run's corpus */
    Corpus corpus;

    /**
     * Instantiate and initialize the Annie environment.
     * 
     * @throws InitializationException
     *             if instantiation or nitializaiton is impossible
     */
    public StandAloneAnnieDriver() throws InitializationException {
        try {
            // Should not be null.
            if (Gate.getGateHome() == null) {
                Gate.setGateHome(new File(StandAloneAnnieDriver.class.getResource(
                    Util.GATEEXAMPLES).toURI()));
            } else {
                Logger.getLogger(StandAloneAnnieDriver.class).info(
                    "GateHome set early; pressing on."); //$NON-NLS-1$
            }

            if (Gate.getUserConfigFile() == null) {
                Gate.setUserConfigFile(new File(StandAloneAnnieDriver.class
                    .getResource(Util.GATEEXAMPLES_USER_GATE_XML).toURI()));
            } else {
                Logger.getLogger(StandAloneAnnieDriver.class).info(
                    "UserConfigFile set early; pressing on."); //$NON-NLS-1$
            }

            Logger.getLogger(StandAloneAnnieDriver.class).info("Initialising GATE..."); //$NON-NLS-1$

            Gate.init();

            Logger.getLogger(StandAloneAnnieDriver.class).info("...GATE initialised"); //$NON-NLS-1$

            /** Register ANNIE plugins */
            Gate.getCreoleRegister().registerDirectories(
                StandAloneAnnieDriver.class.getResource(
                    Util.GATEEXAMPLES_PLUGINS_ANNIE));

            Logger.getLogger(StandAloneAnnieDriver.class).info("Initialising ANNIE..."); //$NON-NLS-1$

            // initialise ANNIE (this may take several minutes)
            this.annie = new StandAloneAnnie();

            Logger.getLogger(StandAloneAnnieDriver.class).info("...ANNIE initialised"); //$NON-NLS-1$

        } catch (GateException e) {
            throw new InitializationException(e);
        } catch (URISyntaxException e) {
            throw new InitializationException(e);
        }
    }

    /**
     * Initialize the corpus with the specified input string and process it.
     * 
     * 
     * @param input
     *            the raw text to operate on
     * @return
     * @throws GateException
     *             if Gate can't make a corpus, or a document, or if Annie barfs
     * @throws InitializationException
     *             if this method is called somehow without initializing Annie
     */
    @SuppressWarnings("unchecked")
    public void run(String input) throws GateException, InitializationException {
        if (this.annie == null)
            throw new InitializationException("Must initialize Annie first"); //$NON-NLS-1$
        if (input == null || input.length() == 0)
            return;

        this.corpus = Factory.newCorpus("Foo Corpus Label"); //$NON-NLS-1$

        FeatureMap params = Factory.newFeatureMap();
        params.put(Document.DOCUMENT_PRESERVE_CONTENT_PARAMETER_NAME, new Boolean(true));
        params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, input);

        Logger.getLogger(StandAloneAnnieDriver.class).info(
            "Creating doc for " + String.valueOf(input)); //$NON-NLS-1$

        Document doc = (Document) Factory.createResource(DocumentImpl.class.getName(), params);

        this.corpus.add(doc);

        this.annie.setCorpus(this.corpus);
        this.annie.execute();
    }

    /** Get the (single) document in the corpus */
    @SuppressWarnings("unchecked")
    public Document getDocument() {
        if (this.corpus == null)
            return null;
        Iterator corpusIter = this.corpus.iterator();

        if (!corpusIter.hasNext()) {
            Logger.getLogger(StandAloneAnnieDriver.class).error("No documents in corpus."); //$NON-NLS-1$
            return null;
        }

        Document doc = (Document) corpusIter.next();

        if (corpusIter.hasNext()) {
            Logger.getLogger(StandAloneAnnieDriver.class).error(
                "Ignoring extra document in corpus."); //$NON-NLS-1$
        }
        return doc;
    }

    /** Fetch the specified annotations. */
    @SuppressWarnings("unchecked")
    public AnnotationSet getAnnotations(String annotationName) {
        Document inputDoc = getDocument();
        if (inputDoc == null)
            return null;
        AnnotationSet defaultAnnotSet = inputDoc.getAnnotations();

        Logger.getLogger(StandAloneAnnieDriver.class).info(
            "annotation set size: " + String.valueOf(defaultAnnotSet.size())); //$NON-NLS-1$

        Set annotTypesRequired = new HashSet();
        annotTypesRequired.add(annotationName);
        return defaultAnnotSet.get(annotTypesRequired);
    }

    /** Fetch the specified annotations. */
    @SuppressWarnings("unchecked")
    public AnnotationSet getMultipleAnnotations(Collection<String> annotationNames) {
        Document inputDoc = getDocument();
        if (inputDoc == null)
            return null;
        AnnotationSet defaultAnnotSet = inputDoc.getAnnotations();

        Logger.getLogger(StandAloneAnnieDriver.class).info(
            "annotation set size: " + String.valueOf(defaultAnnotSet.size())); //$NON-NLS-1$

        Set annotTypesRequired = new HashSet();
        annotTypesRequired.addAll(annotationNames);
        return defaultAnnotSet.get(annotTypesRequired);
    }

    /** Produce a pretty print of the results */
    @SuppressWarnings("unchecked")
    public String toXML() {
        String results = ""; //$NON-NLS-1$

        Document inputDoc = getDocument();

        Set annotTypesRequired = new HashSet();
        annotTypesRequired.add(ANNIEConstants.PERSON_ANNOTATION_TYPE);
        annotTypesRequired.add(ANNIEConstants.LOCATION_ANNOTATION_TYPE);
        AnnotationSet peopleAndPlaces = getMultipleAnnotations(annotTypesRequired);
        if (peopleAndPlaces == null)
            return null;

        // Get them all
        // AnnotationSet peopleAndPlaces = defaultAnnotSet.get();

        FeatureMap features = inputDoc.getFeatures();
        Object contentFeature = features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);

        if (!(contentFeature instanceof String)) {
            Logger.getLogger(StandAloneAnnieDriver.class).error(
                "Wrong feature type for content feature"); //$NON-NLS-1$
            return null;
        }

        String originalContent =
            (String) features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);

        if (originalContent != null) {
            Iterator annotationIter = peopleAndPlaces.iterator();
            SortedAnnotationList sortedAnnotationList = new SortedAnnotationList();

            while (annotationIter.hasNext()) {
                Annotation currAnnot = (Annotation) annotationIter.next();
                sortedAnnotationList.addSortedExclusive(currAnnot);
            }

            StringBuffer editableContent = new StringBuffer(originalContent);
            long insertPositionEnd;
            long insertPositionStart;
            // insert anotation tags backward
            Logger.getLogger(StandAloneAnnieDriver.class).info(
                "Unsorted annotations count: " + peopleAndPlaces.size()); //$NON-NLS-1$
            Logger.getLogger(StandAloneAnnieDriver.class).info(
                "Sorted annotations count: " + sortedAnnotationList.size()); //$NON-NLS-1$

            String startTagPart_1 = "<span GateID=\""; //$NON-NLS-1$
            String startTagPart_2 = "\" title=\""; //$NON-NLS-1$
            String startTagPart_3 = "\" style=\"background:Red;\">"; //$NON-NLS-1$
            String endTag = "</span>"; //$NON-NLS-1$

            for (int i = sortedAnnotationList.size() - 1; i >= 0; --i) {
                Annotation currAnnot = (Annotation) sortedAnnotationList.get(i);
                insertPositionStart = currAnnot.getStartNode().getOffset().longValue();
                insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
                if (insertPositionEnd != -1 && insertPositionStart != -1) {
                    editableContent.insert((int) insertPositionEnd, endTag);
                    editableContent.insert((int) insertPositionStart, startTagPart_3);
                    editableContent.insert((int) insertPositionStart, currAnnot.getType());
                    editableContent.insert((int) insertPositionStart, startTagPart_2);
                    editableContent.insert((int) insertPositionStart, currAnnot.getId().toString());
                    editableContent.insert((int) insertPositionStart, startTagPart_1);
                }
            }
            results += editableContent.toString();
        } else {
            Logger.getLogger(StandAloneAnnieDriver.class).info("No original content!"); //$NON-NLS-1$
        }

        // Get just the specified ones
        // results += inputDoc.toXml(peopleAndPlaces, true);
        // Get all
        results += inputDoc.toXml(inputDoc.getAnnotations(), true);

        return results;
    }
}
