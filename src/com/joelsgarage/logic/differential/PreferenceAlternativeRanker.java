/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic.differential;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.logic.AlternativePreference;
import com.joelsgarage.logic.AlternativePreferenceCollection;
import com.joelsgarage.logic.AlternativeRanker;
import com.joelsgarage.logic.AlternativeStore;
import com.joelsgarage.logic.MostOWAOperator;
import com.joelsgarage.logic.OWAOperator;
import com.joelsgarage.model.AnnotatedAlternative;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.RankedAlternativeCollection;
import com.joelsgarage.util.FatalException;

/**
 * Takes a pile of data and sorts it. The idea is that this class would not do any data-access, it
 * would be supplied all the data it needs by the caller.
 * 
 * This makes one list. It would be better, I think, to come up with two or three lists,
 * corresponding to the application's natural "like it" "don't like it" and "dunno" classes.
 * 
 * Implements AlternativeRanker using AlternativePreference input.
 * 
 * This implementation is SLOW and scales BADLY. Don't use it.
 * 
 * @author joel
 * 
 */
public class PreferenceAlternativeRanker implements AlternativeRanker {
    private Logger logger;
    private AlternativeStore store;

    public PreferenceAlternativeRanker(AlternativeStore store) {
        setLogger(Logger.getLogger(PreferenceAlternativeRanker.class));
        if (store == null) {
            // complain
            return;
        }
        setStore(store);
    }

    public List<RankedAlternativeCollection> getRankedList(int pageSize, ExternalKey decisionKey)
        throws FatalException {
        getLogger().info("Working on decision: " + decisionKey.toString()); //$NON-NLS-1$
        List<RankedAlternativeCollection> result = new ArrayList<RankedAlternativeCollection>();

        List<Decision> dList = getStore().getDecisions(pageSize);

        Logger.getLogger(PreferenceAlternativeRanker.class).info(
            "Decision list size: " + dList.size()); //$NON-NLS-1$

        for (Decision d : dList) {
            Logger.getLogger(PreferenceAlternativeRanker.class).info(
                "Working on decision: " + d.makeKey().toString()); //$NON-NLS-1$
            // TODO: replace this scan
            if (d.makeKey().equals(decisionKey)) {
                RankedAlternativeCollection r = getRankedAlternativeCollection(pageSize, d);
                result.add(r);
            }
        }
        getLogger().info("All done"); //$NON-NLS-1$
        return result;
    }

    protected RankedAlternativeCollection getRankedAlternativeCollection(int pageSize,
        Decision decision) throws FatalException {

        RankedAlternativeCollection rankedAlternativeCollection = new RankedAlternativeCollection();
        rankedAlternativeCollection.setDecision(decision);

        // this is the list of all individuals, so we can find the ones we don't have
        // preferences for.
        // TODO: This is potentially a huge list, so don't do it this way.

        Logger.getLogger(AlternativeRanker.class).info("going to get collection"); //$NON-NLS-1$

        // TODO: this can't work, it's N x N.
        AlternativePreferenceCollection aCP =
            new AlternativePreferenceCollection(getStore(), decision, pageSize);
        Map<ExternalKey, Map<ExternalKey, Map<ExternalKey, AlternativePreference>>> rMap =
            aCP.getRMap();

        // this is the resulting consensus measure, p_ij
        // TODO: this can't work, it's N x N.
        Map<ExternalKey, Map<ExternalKey, Double>> consensusMap =
            new HashMap<ExternalKey, Map<ExternalKey, Double>>();

        // First we populate the result matrix
        populateConsensus(rMap, consensusMap);

        // Now we populate the score maps
        Map<ExternalKey, Double> dominanceI = new HashMap<ExternalKey, Double>();
        Map<ExternalKey, Double> nonDominanceI = new HashMap<ExternalKey, Double>();
        // TODO: calculate this directly
        populateScores(consensusMap, dominanceI, nonDominanceI);

        Map<ExternalKey, Individual> leftoverMap = new HashMap<ExternalKey, Individual>();
        for (Individual i : getStore().getAlternatives(decision, pageSize)) {
            leftoverMap.put(i.makeKey(), i);
        }

        // Just use dominance for now.
        addAlternatives(dominanceI, rankedAlternativeCollection, leftoverMap);

        // Sort each little list of individuals by their scores.
        // THIS LIST IS SORTED IN REVERSE ORDER!
        List<AnnotatedAlternative> listRef = rankedAlternativeCollection.getAlternatives();
        Collections.sort(listRef, new Comparator<AnnotatedAlternative>() {
            // @Override
            public int compare(AnnotatedAlternative o1, AnnotatedAlternative o2) {
                // Note reversed arguments!
                return o2.getScore().compareTo(o1.getScore());
            }
        });

        // And then add on the leftovers at the end.
        /*
         * TODO: something a little smarter than this -- assume a prior for the preference (like,
         * say, indifference), and put these in between the items that you definitely like and the
         * items you definitely don't like.
         * 
         * TODO: since most of the leftovers will probably not be seen, nor sorted (because they are
         * unscored), this step could be entirely omitted and probably be ok. At the least, fill the
         * leftovers with rejects along the way.
         */
        // // So for now just skip it
        for (Individual i : leftoverMap.values()) {
            AnnotatedAlternative a = new AnnotatedAlternative();
            a.setIndividual(i);
            a.setScore(Double.valueOf(0.0));
            a.setScoreString(String.format("%.2f", Double.valueOf(0.0))); //$NON-NLS-1$
            a.setReason("leftover"); //$NON-NLS-1$
            rankedAlternativeCollection.getAlternatives().add(a);
        }
        return rankedAlternativeCollection;
    }

    /**
     * Produces a list of alternative collections, one for each decision.
     * 
     * TODO: provide access on a per-decision basis rather than all together.
     * 
     * @return
     * @throws FatalException
     */
    @SuppressWarnings("nls")
    public List<RankedAlternativeCollection> getRankedList(int pageSize) throws FatalException {
        getLogger().info("Working");
        List<RankedAlternativeCollection> result = new ArrayList<RankedAlternativeCollection>();

        List<Decision> dList = this.store.getDecisions(pageSize);

        for (Decision d : dList) {
            RankedAlternativeCollection r = getRankedAlternativeCollection(pageSize, d);
            result.add(r);
        }
        getLogger().info("All done");
        return result;
    }

    /**
     * Take the 3-d preference map: all individuals X all individuals X all stakeholders, and
     * project it into a 2-d map, all individuals X all individuals.
     * 
     * @param rawMap
     *            INPUT: a three level preference map: (i, (j, (e, preference)))
     * @param consensusMap
     *            OUTPUT: a two-level preference map: (i, (j, score))
     */
    protected void populateConsensus(
        Map<ExternalKey, Map<ExternalKey, Map<ExternalKey, AlternativePreference>>> rawMap,
        Map<ExternalKey, Map<ExternalKey, Double>> consensusMap) {
        OWAOperator owaOp = new MostOWAOperator();

        // Loop over "i"
        for (Map.Entry<ExternalKey, Map<ExternalKey, Map<ExternalKey, AlternativePreference>>> outerEntry : rawMap
            .entrySet()) {
            ExternalKey iKey = outerEntry.getKey();
            Map<ExternalKey, Map<ExternalKey, AlternativePreference>> iEM = outerEntry.getValue();

            for (Map.Entry<ExternalKey, Map<ExternalKey, AlternativePreference>> innerEntry : iEM
                .entrySet()) {
                ExternalKey jKey = innerEntry.getKey();
                Map<ExternalKey, AlternativePreference> iEN = innerEntry.getValue();

                // the list of consensus scores that compare this I with this J, over all E

                List<Double> vCList = new ArrayList<Double>();
                // loop over stakeholders
                for (Map.Entry<ExternalKey, AlternativePreference> p : iEN.entrySet()) {
                    AlternativePreference aP = p.getValue();
                    Double preferenceValue = aP.getResult().getValue();

                    getLogger().info("raw score: " + String.format("%.2f", preferenceValue) + //$NON-NLS-1$ //$NON-NLS-2$
                        " iId: " + iKey.toString() + //$NON-NLS-1$
                        " jId: " + jKey.toString() + //$NON-NLS-1$
                        " reason " + aP.getReason()); //$NON-NLS-1$

                    if (preferenceValue == null) {
                        // this shouldn't happen
                    } else {
                        vCList.add(preferenceValue);
                    }
                }
                // this is the aggregate preference of i over j
                Double consensusPIJ = owaOp.F(vCList);

                getLogger().info("consensus score: " + String.format("%.2f", consensusPIJ) + //$NON-NLS-1$ //$NON-NLS-2$
                    " iId: " + iKey.toString() + " jId: " + jKey.toString()); //$NON-NLS-1$ //$NON-NLS-2$

                // Now store the result.

                Map<ExternalKey, Double> pI = consensusMap.get(iKey);
                if (pI == null) {
                    pI = new HashMap<ExternalKey, Double>();
                    consensusMap.put(iKey, pI);
                }
                pI.put(jKey, consensusPIJ);
            }
        }
    }

    // TODO: make this more efficient.
    /**
     * @param consensusMap
     *            INPUT: individual X individual consensus scores
     * @param dominanceI
     *            OUTPUT: individual dominance scores
     * @param nonDominanceI
     *            OUTPUT: individual nondominance scores
     */
    protected void populateScores(Map<ExternalKey, Map<ExternalKey, Double>> consensusMap,
        Map<ExternalKey, Double> dominanceI, Map<ExternalKey, Double> nonDominanceI) {
        OWAOperator owaOp = new MostOWAOperator();
        // loop over i
        for (Map.Entry<ExternalKey, Map<ExternalKey, Double>> outerEntry : consensusMap.entrySet()) {
            ExternalKey iKey = outerEntry.getKey();
            Map<ExternalKey, Double> pIJMap = outerEntry.getValue();

            List<Double> pIJList = new ArrayList<Double>();
            List<Double> nonDominanceList = new ArrayList<Double>();
            // loop over j
            for (Map.Entry<ExternalKey, Double> innerEntry : pIJMap.entrySet()) {
                // dominance terms
                Double pIJ = innerEntry.getValue();

                getLogger().info("dominance score: " + String.format("%.2f", pIJ) //$NON-NLS-1$//$NON-NLS-2$
                    + " iId: " + iKey.toString()); //$NON-NLS-1$

                pIJList.add(pIJ);

                // find the nondominance terms
                ExternalKey jKey = innerEntry.getKey();
                Map<ExternalKey, Double> rowJ = consensusMap.get(jKey);
                if (rowJ != null) {
                    Double pJI = rowJ.get(iKey);
                    if (pJI != null) {
                        Double pJIS = Double.valueOf(pJI.doubleValue() - pIJ.doubleValue());
                        if (pJIS.doubleValue() < 0) {
                            pJIS = Double.valueOf(0);
                        }
                        nonDominanceList.add(Double.valueOf(1 - pJIS.doubleValue()));
                    }
                }
            }
            dominanceI.put(iKey, owaOp.F(pIJList));
            nonDominanceI.put(iKey, owaOp.F(nonDominanceList));
        }
    }

    protected void addAlternatives(Map<ExternalKey, Double> scores,
        RankedAlternativeCollection results, Map<ExternalKey, Individual> leftovers) {
        // TODO: look at combining dominance and nondominance someday, I guess.
        // Jeez, isn't this getting kinda complex and opaque?
        for (Map.Entry<ExternalKey, Double> entry : scores.entrySet()) {

            ExternalKey iKey = entry.getKey();
            Double score = entry.getValue();

            Logger.getLogger(AlternativeRanker.class).info(
                "got scored alternative " + iKey.toString() + " score " + score.toString()); //$NON-NLS-1$ //$NON-NLS-2$

            // bleah, need to look up every individual, since i don't use the actual
            // individual as the hash key.
            // Or I could just store the key; why do i need the whole record anyway?

            Individual individual = getStore().getIndividual(iKey);

            AnnotatedAlternative annotatedAlternative = new AnnotatedAlternative();
            annotatedAlternative.setIndividual(individual);
            annotatedAlternative.setScore(score);
            annotatedAlternative.setScoreString(String.format("%.2f", score)); //$NON-NLS-1$
            annotatedAlternative.setReason("scored"); //$NON-NLS-1$

            getLogger().info("score: " + annotatedAlternative.getScoreString() //$NON-NLS-1$
                + " i: " + individual.makeKey().toString() + //$NON-NLS-1$
                "(" + individual.getName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$

            results.getAlternatives().add(annotatedAlternative);
            leftovers.remove(iKey); // drop it from the leftovers map
        }
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected void setLogger(Logger logger) {
        this.logger = logger;
    }

    public AlternativeStore getStore() {
        return this.store;
    }

    public void setStore(AlternativeStore store) {
        this.store = store;
    }
}
