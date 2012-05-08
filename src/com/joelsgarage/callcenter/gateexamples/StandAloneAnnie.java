package com.joelsgarage.callcenter.gateexamples;

import gate.Annotation;
import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ANNIEConstants;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * This class illustrates how to use ANNIE as a sausage machine in another application - put
 * ingredients in one end (URLs pointing to documents) and get sausages (e.g. Named Entities) out
 * the other end.
 * <P>
 * <B>NOTE:</B><BR>
 * For simplicity's sake, we don't do any exception handling.
 */
public class StandAloneAnnie {
    private static final String ANNIE_ = "ANNIE_"; //$NON-NLS-1$
    /** generic analyzer controller */
    private SerialAnalyserController annieController;

    /**
     * Initialise the ANNIE system. This creates a "corpus pipeline" application that can be used to
     * run sets of documents through the extraction system.
     */
    public StandAloneAnnie() throws GateException {
        Logger.getLogger(StandAloneAnnie.class).info("Initialising ANNIE..."); //$NON-NLS-1$

        // create a serial analyser controller to run ANNIE with
        this.annieController =
            (SerialAnalyserController) Factory.createResource(SerialAnalyserController.class
                .getName(), Factory.newFeatureMap(), // no parameters
                Factory.newFeatureMap(), // no features
                ANNIE_ + Gate.genSym());

        // load each PR as defined in ANNIEConstants
        for (int i = 0; i < ANNIEConstants.PR_NAMES.length; i++) {
            Logger.getLogger(StandAloneAnnie.class)
                .info("loading PR " + ANNIEConstants.PR_NAMES[i]); //$NON-NLS-1$

            FeatureMap params = Factory.newFeatureMap(); // use default parameters
            ProcessingResource pr =
                (ProcessingResource) Factory.createResource(ANNIEConstants.PR_NAMES[i], params);

            // add the PR to the pipeline controller
            this.annieController.add(pr);
        }

        Logger.getLogger(StandAloneAnnie.class).info("...ANNIE loaded"); //$NON-NLS-1$
    }

    /** Tell ANNIE's controller about the corpus you want to run on */
    public void setCorpus(Corpus corpus) {
        this.annieController.setCorpus(corpus);
    }

    /** Run ANNIE */
    public void execute() throws GateException {
        Logger.getLogger(StandAloneAnnie.class).info("Running ANNIE..."); //$NON-NLS-1$
        this.annieController.execute();
        Logger.getLogger(StandAloneAnnie.class).info("...ANNIE complete"); //$NON-NLS-1$
    }

    /**
     * Collection of annotations in offset order. Rejects overlaps.
     */
    @SuppressWarnings("unchecked")
    public static class SortedAnnotationList extends Vector {
        private static final long serialVersionUID = 1L;

        public SortedAnnotationList() {
            super();
        }

        /**
         * Insert the specified annotation into the collection, in offset order. Return false if it
         * overlaps an existing annotation. Does a scan to find overlaps and another to find the
         * right insertion point, so, O(n^2) to populate the whole list.
         */
        public boolean addSortedExclusive(Annotation annot) {
            Annotation currAnot = null;

            for (int i = 0; i < size(); ++i) {
                currAnot = (Annotation) get(i);
                if (annot.overlaps(currAnot)) {
                    return false;
                }
            }

            long annotStart = annot.getStartNode().getOffset().longValue();
            long currStart;

            for (int i = 0; i < size(); ++i) {
                currAnot = (Annotation) get(i);
                currStart = currAnot.getStartNode().getOffset().longValue();
                if (annotStart < currStart) {
                    insertElementAt(annot, i);
                    return true;
                }
            }

            int size = size();
            insertElementAt(annot, size);
            return true;
        }
    }
}
