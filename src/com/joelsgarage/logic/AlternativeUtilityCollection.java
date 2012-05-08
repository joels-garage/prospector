package com.joelsgarage.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualPropertyPreference;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.FatalException;

/**
 * NEW! UTILITY-BASED, O(N)!
 * 
 * This class contains all the data necessary for ranking. It is a set of alternative UTILITIES,
 * ideally spanning all pairs of alternatives, and all stakeholders.
 * 
 * To fill up this class, we iterate as follows:
 * 
 * <pre>
 * For a single decision, d {
 *   for each stakeholder, e {
 *     for each alternative, i (individual of the decision class) {
 *       create an alternative preference given (e, i).
 *     }
 *   }
 * }
 * </pre>
 * 
 * @author joel
 */
public class AlternativeUtilityCollection {
    private AlternativeStore store;
    private Decision d;
    /** (I => (E => AlternativeUtility)) */
    private Map<ExternalKey, Map<ExternalKey, AlternativeUtility>> rMap;

    public AlternativeUtilityCollection(final AlternativeStore store, final Decision d, int pageSize)
        throws FatalException {
        Logger.getLogger(AlternativeUtilityCollection.class).info("ctor"); //$NON-NLS-1$

        if (store == null) {
            // complain
            return;
        }
        setStore(store);
        setD(d);
        setRMap(new HashMap<ExternalKey, Map<ExternalKey, AlternativeUtility>>());

        // This is *all* the possible individuals for d.
        final List<Individual> iList = getStore().getAlternatives(getD(), pageSize);
        final List<Stakeholder> eList = getStore().getStakeholders(getD(), pageSize);

        // this is the first-order one, i.e. using only stuff in the DB as input.
        for (final Stakeholder e : eList) {
            // Construct the utility factories for this stakeholder, so we can just look up
            // utilities by individual key.
            final List<IndividualPropertyPreference> iPPList = //
                getStore().getIndividualPropertyPreferences(e, pageSize);
            // (Property => Factory)
            Map<ExternalKey, PreferenceDerivedIndividualPropertyUtilityFactory> propertyUtilityFactoryMap = //
                new HashMap<ExternalKey, PreferenceDerivedIndividualPropertyUtilityFactory>();

            for (IndividualPropertyPreference iPP : iPPList) {
                // Find the factory for this property and add this preference to that factory
                ExternalKey propertyKey = iPP.getPropertyKey();
                PreferenceDerivedIndividualPropertyUtilityFactory factory =
                    propertyUtilityFactoryMap.get(propertyKey);
                if (factory == null) {
                    factory = new PreferenceDerivedIndividualPropertyUtilityFactory(e.makeKey());
                    propertyUtilityFactoryMap.put(propertyKey, factory);
                }
                factory.addPreference(iPP.getPrimaryIndividualKey(), iPP
                    .getSecondaryIndividualKey(), iPP.getValue());
            }
            // Now we have all the factories loaded, and we need to do the computation.
            for (PreferenceDerivedIndividualPropertyUtilityFactory factory : propertyUtilityFactoryMap
                .values()) {
                factory.compute();
            }

            // There's one more factory, which is the IndividualUtility factory. It populates itself
            // upon construction.
            PreferenceDerivedIndividualUtilityFactory individualUtilityFactory =
                new PreferenceDerivedIndividualUtilityFactory(getStore(), e.makeKey(), pageSize);

            for (final Individual i : iList) {
                Logger.getLogger(AlternativeUtilityCollection.class).info(
                    "looking at individual " + i.makeKey().toString()); //$NON-NLS-1$

                IndividualUtility individualUtility =
                    individualUtilityFactory.newInstance(i.makeKey());
                if (individualUtility == null) {
                    Logger.getLogger(AlternativeUtilityCollection.class).info("null utility"); //$NON-NLS-1$
                }
                final AlternativeUtility rI =
                    new AlternativeUtility(getStore(), propertyUtilityFactoryMap,
                        individualUtility, e, i, pageSize);
                if (rI.getResult() != null) {
                    // TODO: if null, add to the "leftover" bucket.
                    addToRMap(i, e, rI);
                }
            }
        }
    }

    /**
     * Populate the result map as specified. This assumes that there is only one AlternativeUtility
     * for each e, i.
     */
    private void addToRMap(Individual i, Stakeholder e, AlternativeUtility r) {
        addToRMap(i.makeKey(), e.makeKey(), r);
    }

    private void addToRMap(ExternalKey iKey, ExternalKey eKey, AlternativeUtility r) {
        Map<ExternalKey, AlternativeUtility> rForI = getRMap().get(iKey);
        if (rForI == null) {
            rForI = new HashMap<ExternalKey, AlternativeUtility>();
            getRMap().put(iKey, rForI);
        }
        // If multiple, this replaces the old one.
        rForI.put(eKey, r);
    }

    public AlternativeUtility getAlternativeUtility(Individual i, Stakeholder e) {
        return getAlternativeUtilityFromKeys(i.makeKey(), e.makeKey());
    }

    public AlternativeUtility getAlternativeUtilityFromKeys(ExternalKey iKey, ExternalKey eKey) {
        Map<ExternalKey, AlternativeUtility> rForI = getRMap().get(iKey);
        if (rForI == null) {
            return null;
        }
        AlternativeUtility r = rForI.get(eKey);
        if (r == null) {
            // foo
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
    public Map<ExternalKey, Map<ExternalKey, AlternativeUtility>> getRMap() {
        return this.rMap;
    }

    private void setRMap(final Map<ExternalKey, Map<ExternalKey, AlternativeUtility>> map) {
        this.rMap = map;
    }
}
