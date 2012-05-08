package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.FatalException;

/**
 * NEW!
 * 
 * AlternativeUtility is a score per individual, not N x N.
 * 
 * The utility can be obtained in serveral ways:
 * 
 * <ul>
 * <li>Stated. Directly, using IndividualUtility.
 * <li>Preference-derived. This is single-valued, obtained from the factory (pass it in the ctor)
 * <li>Property. Several kinds of property preference are combined.
 * </ul>
 * 
 * There is no "transitively implied" utility, and no "reverse" utility.
 * 
 * The aggregate of multiple utility scores is a simple linear combination, i.e. weighted average.
 * 
 * --- ---
 * 
 * AlternativePreference is the measure used for ranking.
 * 
 * It is expressed by a single Stakeholder, e, about two Individuals, i and j. It contains all
 * preferences that can be known about this tuple.
 * 
 * A preference may be obtained in several ways:
 * <ul>
 * <li>Stated. Directly, using IndividualPreference.
 * <li>Utility-derived. This is a multivalued quantity -- different pairs of utilities may result
 * in different preferences. We record them all here, and also summarize them. Maybe the right thing
 * is to take some sort of mean; I dunno.
 * <li>Transitively implied. This is a multivalued quantity, as above.
 * <li>Property. Combining PropertyPreferences about a pair of individuals. The combination has a
 * mean of some kind, and a variance. We record all the relevant PropertyPreferences here, and
 * summarize them.
 * </ul>
 * 
 * Maybe the right kind of summary is a probability distribution over preference space.
 * 
 * Anyway it seems obvious that stated preferences are preferred, and either utility-derived or
 * transitively-implied preferences are preferred over property preferences. But the property
 * preferences may be used to derive *weights*.
 * 
 * The derived preferences may be first-order, i.e. derived directly from stated preferences (on
 * properties or individuals), or they may be higher-order, i.e. derived from other derived
 * preferences. It would be good to capture this fact somehow, since higher-order preferences are
 * more likely to be crap.
 * 
 * All the lists might be empty, which indicates we don't know anything about the specified tuple
 * (e, i, j).
 * 
 * It's a server-side thing, so I'm freely using the actual entities rather than their id's.
 * 
 * @author joel
 */
public class AlternativeUtility {
    /** Where did the result come from? */
    public enum Source {
        DIRECT, PREFERENCE, PROPERTY
    }

    /** Database access */
    private AlternativeStore store;
    /** The relevant stakeholder. */
    private Stakeholder e;
    /** The individual at hand. */
    private Individual i;
    /** Stated. Empty if no direct utility exists. */
    private List<IndividualUtility> direct;
    /** Derived from the set of preferences. Empty if no such set exists. */
    private List<IndividualUtility> preferenceDerived;
    /** Derived from properties. Empty if no comparable properties exist. */
    private List<PropertyDerivedUtilitySummary> propertyDerived;
    /** The final answer. */
    private IndividualUtility result;
    /** Where this answer came from. Like the "reason" field. */
    private Source source;
    /** Simple concatenation of reasons. */
    private String reason;

    /**
     * @param store
     * @param factoryMap
     *            maps property key to utility factory
     * @param individualUtility
     *            the preference-derived utility. it's a little weird to pass it in, but since the
     *            caller needs to manage the factory, it's equally weird to pass the factory in
     *            order to call it once.
     * @param e
     *            stakeholder
     * @param i
     *            individual in question
     * @param pageSize
     * @throws FatalException
     *             if key population fails
     */
    public AlternativeUtility(final AlternativeStore store,
        final Map<ExternalKey, PreferenceDerivedIndividualPropertyUtilityFactory> factoryMap,
        IndividualUtility individualUtility, final Stakeholder e, final Individual i, int pageSize)
        throws FatalException {
        Logger.getLogger(AlternativePreference.class).info("Constructor"); //$NON-NLS-1$
        if (store == null) {
            // complain
            return;
        }
        setStore(store);
        setE(e);
        setI(i);
        setDirect(new ArrayList<IndividualUtility>());
        setPreferenceDerived(new ArrayList<IndividualUtility>());
        // If we were called with a preference derived utility, add it to our list.
        if (individualUtility != null)
            getPreferenceDerived().add(individualUtility);
        setPropertyDerived(new ArrayList<PropertyDerivedUtilitySummary>());
        // default result value is null, which differentiates indifference from don't-know.
        // It would be good to have a way to represent "know for sure" orthogonally from the value.
        // I guess that's the probability distribution idea?
        setResult(null);

        Logger.getLogger(AlternativePreference.class).info(
            "TIMING: about to make new IndividualUtility"); //$NON-NLS-1$

        // First, find the direct IndividualUtility. Note that this may be multivalued,
        // but it makes little sense to be.
        List<IndividualUtility> rDirectList =
            getStore().getIndividualUtilities(getE(), getI(), pageSize);

        // Just take the first one
        if (rDirectList.size() > 0) {
            // prefer the direct preference, if available
            getDirect().add(rDirectList.get(0));
        }

        Logger.getLogger(AlternativePreference.class).info(
            "TIMING: about to make new PropertyDerivedUtility"); //$NON-NLS-1$

        // Kinda weird to keep instantiating this but it doesn't seem to hurt anything.
        PropertyDerivedUtilitySummaryFactory factory =
            new PropertyDerivedUtilitySummaryFactory(getStore(), pageSize);

        // Then, the property-derived. Constructor does all the work.
        PropertyDerivedUtilitySummary pD = factory.newIfValid(factoryMap, getE(), getI());
        if (pD == null) {
            Logger.getLogger(AlternativePreference.class)
                .info("null PropertyDerivedUtilitySummary"); //$NON-NLS-1$
        } else {
            Logger.getLogger(AlternativePreference.class)
                .info("good PropertyDerivedUtilitySummary"); //$NON-NLS-1$
            getPropertyDerived().add(pD);
        }

        //
        // And now finally compute the result.
        // This is a simplistic way to do it.
        // TODO: expose inconsistencies here -- part of the point here is to show the user areas
        // where they may be unaware of positive or negative attributes, despite their indicated
        // preference.

        if (getDirect().size() > 0) {
            // Prefer the stated one absolutely.
            setResult(getDirect().get(0));
            setSource(Source.DIRECT);
            setReason("Direct: " + getResult().makeKey().toString() + " score: " //$NON-NLS-1$//$NON-NLS-2$
                + String.format("%.2f", getResult().getValue())); //$NON-NLS-1$

        } else if (getPreferenceDerived().size() > 0) {
            // Preference-derived is almost as good unless it's eigenvalue crap. :-)
            IndividualUtility uDP = getPreferenceDerived().get(0);
            setResult(uDP);
            setSource(Source.PREFERENCE);
            setReason("Preference-derived: score: " + String.format("%.2f", getResult().getValue())); //$NON-NLS-1$//$NON-NLS-2$

        } else if (getPropertyDerived().size() > 0) {
            // Property-derived is really useful too
            PropertyDerivedUtilitySummary pDP = getPropertyDerived().get(0);
            setResult(pDP.getR());
            setSource(Source.PROPERTY);
            String allReasons = "Property:"; //$NON-NLS-1$
            for (String s : pDP.getReasons()) {
                allReasons += " " + s; //$NON-NLS-1$
            }
            allReasons += " score: " + String.format("%.2f", getResult().getValue()); //$NON-NLS-1$ //$NON-NLS-2$
            setReason(allReasons);
        }
    }

    //

    public Stakeholder getE() {
        return this.e;
    }

    public void setE(final Stakeholder e) {
        this.e = e;
    }

    public Individual getI() {
        return this.i;
    }

    public void setI(final Individual i) {
        this.i = i;
    }

    /** Get stated utility. Empty if no direct utility exists. */
    public List<IndividualUtility> getDirect() {
        return this.direct;
    }

    public void setDirect(final List<IndividualUtility> direct) {
        this.direct = direct;
    }

    /**
     * Return a list of PropertyDerivedUtility Summary, i.e. utilities derived from properties.
     * Empty if no comparable properties exist.
     */
    public List<PropertyDerivedUtilitySummary> getPropertyDerived() {
        return this.propertyDerived;
    }

    public void setPropertyDerived(final List<PropertyDerivedUtilitySummary> propertyDerived) {
        this.propertyDerived = propertyDerived;
    }

    public AlternativeStore getStore() {
        return this.store;
    }

    public void setStore(final AlternativeStore store) {
        this.store = store;
    }

    public Source getSource() {
        return this.source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public IndividualUtility getResult() {
        return this.result;
    }

    public void setResult(IndividualUtility result) {
        this.result = result;
    }

    public List<IndividualUtility> getPreferenceDerived() {
        return this.preferenceDerived;
    }

    public void setPreferenceDerived(List<IndividualUtility> preferenceDerived) {
        this.preferenceDerived = preferenceDerived;
    }
}
