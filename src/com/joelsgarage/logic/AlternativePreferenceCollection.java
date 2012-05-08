package com.joelsgarage.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.FatalException;

/**
 * This class contains all the data necessary for ranking. It is a set of alternative preferences,
 * ideally spanning all pairs of alternatives, and all stakeholders.
 * 
 * To fill up this class, we iterate as follows:
 * 
 * <pre>
 * For a single decision, d {
 *   for each stakeholder, e {
 *     for each alternative, i (individual of the decision class) {
 *       for each alternative, j (i != j) {
 *         create an alternative preference given (e, i, j).
 *       }
 *     }
 *   }
 * }
 * </pre>
 * 
 * @author joel
 * 
 */
public class AlternativePreferenceCollection {
    private AlternativeStore store;

    private Decision d;
    // TODO: provide iterator access to this list.
    // List<AlternativePreference> rList;
    // i, j, e, alternativepreference.
    // This covers the items for which we can assign a preference.
    // What about the items we can't assign a preference to?
    Map<ExternalKey, Map<ExternalKey, Map<ExternalKey, AlternativePreference>>> rMap;

    public AlternativePreferenceCollection(final AlternativeStore store, final Decision d,
        int pageSize) throws FatalException {
        Logger.getLogger(AlternativePreferenceCollection.class).info("ctor"); //$NON-NLS-1$

        if (store == null) {
            Logger.getLogger(AlternativePreferenceCollection.class).info("null store"); //$NON-NLS-1$
            return;
        }
        setStore(store);
        setD(d);
        // setRList(new ArrayList<AlternativePreference>());
        setRMap(new HashMap<ExternalKey, Map<ExternalKey, Map<ExternalKey, AlternativePreference>>>());

        // This is *all* the possible individuals for d.
        Logger.getLogger(AlternativePreferenceCollection.class).info("about to get individuals"); //$NON-NLS-1$
        final List<Individual> iList = getStore().getAlternatives(getD(), pageSize);
        Logger.getLogger(AlternativePreferenceCollection.class).info("got individuals"); //$NON-NLS-1$
        if (iList == null) {
            Logger.getLogger(AlternativePreferenceCollection.class).info("null individuals list"); //$NON-NLS-1$
        }

        Logger.getLogger(AlternativePreferenceCollection.class).info("about to get stakeholders"); //$NON-NLS-1$
        final List<Stakeholder> eList = getStore().getStakeholders(getD(), pageSize);
        Logger.getLogger(AlternativePreferenceCollection.class).info("got stakeholders"); //$NON-NLS-1$
        Logger.getLogger(AlternativePreferenceCollection.class).info(
            "size: " + String.valueOf(eList.size())); //$NON-NLS-1$

        // this is the first-order one, i.e. using only stuff in the DB as input.
        for (final Stakeholder e : eList) {
            Logger.getLogger(AlternativePreferenceCollection.class).info(
                "e: " + e.makeKey().toString()); //$NON-NLS-1$
            for (final Individual i : iList) {
                Logger.getLogger(AlternativePreferenceCollection.class).info(
                    "i: " + i.makeKey().toString()); //$NON-NLS-1$
                for (final Individual j : iList) {
                    Logger.getLogger(AlternativePreferenceCollection.class).info(
                        "j: " + j.makeKey().toString()); //$NON-NLS-1$

                    if (!(i.makeKey().equals(j.makeKey()))) {
                        // self-preference is always indifference, so skip it.
                        final AlternativePreference rIJ =
                            new AlternativePreference(getStore(), e, i, j, pageSize);
                        if (rIJ.getResult() != null) {
                            // maybe do newIfValid thing here too?
                            // getRList().add(rIJ);
                            Logger.getLogger(AlternativePreferenceCollection.class).info(
                                "adding: " + rIJ.getResult().makeKey().toString()); //$NON-NLS-1$
                            addToRMap(i, j, e, rIJ);
                        }
                    }
                }
            }
        }

        // this is where to do higher-order things. To do that, use this collection as the store,
        // since it already includes all the data we need. Iterate until there are no new derived
        // values.

    }

    /**
     * Populate the result map as specified. This assumes that there is only one
     * AlternativePreference for each e, i
     * 
     * This arg order is the most natural ordering for scoring.
     */
    private void addToRMap(Individual i, Individual j, Stakeholder e, AlternativePreference r) {
        addToRMap(i.makeKey(), j.makeKey(), e.makeKey(), r);
    }

    private void addToRMap(ExternalKey iKey, ExternalKey jKey, ExternalKey eKey,
        AlternativePreference r) {
        // Logger.getLogger(AlternativePreferenceCollection.class).info(
        // "adding to rmap i: " + iKey.toString() + " j: " + jKey.toString() + " e: "
        // + eKey.toString());
        Map<ExternalKey, Map<ExternalKey, AlternativePreference>> rForI = getRMap().get(iKey);
        if (rForI == null) {
            // Logger.getLogger(AlternativePreferenceCollection.class)
            // .info("null rForI, creating map");
            rForI = new HashMap<ExternalKey, Map<ExternalKey, AlternativePreference>>();
            getRMap().put(iKey, rForI);
        }
        Map<ExternalKey, AlternativePreference> rForIJ = rForI.get(jKey);
        if (rForIJ == null) {
            // Logger.getLogger(AlternativePreferenceCollection.class).info(
            // "null rForIJ, creating map");
            rForIJ = new HashMap<ExternalKey, AlternativePreference>();
            rForI.put(jKey, rForIJ);
        }
        // If multiple, this replaces the old one.
        rForIJ.put(eKey, r);
    }

    public AlternativePreference getAlternativePreference(Individual i, Stakeholder e, Individual j) {
        return getAlternativePreference(i.makeKey(), j.makeKey(), e.makeKey());
    }

    public AlternativePreference getAlternativePreference(ExternalKey iKey, ExternalKey jKey,
        ExternalKey eKey) {
        Map<ExternalKey, Map<ExternalKey, AlternativePreference>> rForI = getRMap().get(iKey);

        if (rForI == null) {
            Logger.getLogger(AlternativePreferenceCollection.class).info(
                "null rforI when looking for " + iKey.toString()); //$NON-NLS-1$
            return null;
        }
        Map<ExternalKey, AlternativePreference> rForIJ = rForI.get(jKey);
        if (rForIJ == null) {
            Logger.getLogger(AlternativePreferenceCollection.class).info("null rforIJ"); //$NON-NLS-1$
            return null;
        }
        AlternativePreference r = rForIJ.get(eKey);
        if (r == null) {
            Logger.getLogger(AlternativePreferenceCollection.class).info("null r"); //$NON-NLS-1$
        }
        return r;
    }

    //

    public Decision getD() {
        return this.d;
    }

    public void setD(final Decision d) {
        this.d = d;
    }

    public AlternativeStore getStore() {
        return this.store;
    }

    public void setStore(final AlternativeStore store) {
        this.store = store;
    }

    // TODO: replace this access with something more controlled.
    public Map<ExternalKey, Map<ExternalKey, Map<ExternalKey, AlternativePreference>>> getRMap() {
        return this.rMap;
    }

    private void setRMap(
        final Map<ExternalKey, Map<ExternalKey, Map<ExternalKey, AlternativePreference>>> map) {
        this.rMap = map;
    }

}
