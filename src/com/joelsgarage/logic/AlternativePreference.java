package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualPreference;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.FatalException;

/**
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
 * 
 */
public class AlternativePreference {
    private static final String N_NAMESPACE = "n"; //$NON-NLS-1$

    // where did the result come from?
    public enum Source {
        DIRECT, REVERSE, UTILITY, IMPLIED, PROPERTY
    }

    private AlternativeStore store;
    /**
     * The relevant stakeholder.
     */
    private Stakeholder e;
    /**
     * One of the individuals at hand.
     */
    private Individual i;
    /**
     * The other individual at hand.
     */
    private Individual j;

    /**
     * Stated. Empty if no direct relative preferences exist.
     */
    private List<IndividualPreference> direct;
    /**
     * Stated in reverse. This contains preferences in the direct direction, obtained from the
     * reverse preferences.
     */
    private List<IndividualPreference> reverse;
    /**
     * Derived from pairs of utility statements. Empty if no such pairs exist.
     */
    private List<UtilityDerivedIndividualPreference> utilityDerived;
    /**
     * Derived from pairs of individual preferences. Empty if no such pairs exist.
     */
    private List<ImpliedPreference> implied;
    /**
     * Derived from properties of the individuals. Empty if no comparable properties exist.
     * 
     * TODO: capture the variance of these preferences.
     */
    private List<PropertyDerivedPreferenceSummary> propertyDerived;

    /**
     * The final answer.
     */
    private IndividualPreference result;
    /**
     * Where this answer came from. Like the "reason" field.
     */
    private Source source;

    /**
     * Simple concatenation of reasons.
     */
    private String reason;

    @SuppressWarnings("nls")
    public AlternativePreference(final AlternativeStore store, final Stakeholder e,
        final Individual i, final Individual j, int pageSize) throws FatalException {
        Logger.getLogger(AlternativePreference.class).info("Constructor");
        if (store == null) {
            // complain
            return;
        }
        setStore(store);
        setE(e);
        setI(i);
        setJ(j);
        setDirect(new ArrayList<IndividualPreference>());
        setReverse(new ArrayList<IndividualPreference>());
        setUtilityDerived(new ArrayList<UtilityDerivedIndividualPreference>());
        setImplied(new ArrayList<ImpliedPreference>());
        setPropertyDerived(new ArrayList<PropertyDerivedPreferenceSummary>());
        // default result value is null, which differentiates indifference from don't-know.
        // It would be good to have a way to represent "know for sure" orthogonally from the value.
        // I guess that's the probability distribution idea?
        setResult(null);

        PropertyDerivedPreferenceSummaryFactory factory =
            new PropertyDerivedPreferenceSummaryFactory(getStore(), pageSize);

        Logger.getLogger(AlternativePreference.class).info(
            "TIMING: about to make new IndividualPreference");

        Logger.getLogger(AlternativePreference.class).info(
            "Looking for direct for e " + getE().toString() + " i " + getI().toString() + " j "
                + getJ().toString());
        // First, find the direct preferences, pairwise only. Note that this may be multivalued,
        // but it makes little sense to be.
        List<IndividualPreference> rDirectList =
            getStore().getIndividualPreferences(getE(), getI(), getJ(), pageSize);

        Logger.getLogger(AlternativePreference.class).info(
            "size is " + String.valueOf(rDirectList.size()));

        List<IndividualPreference> rReverseList =
            getStore().getIndividualPreferences(getE(), getJ(), getI(), pageSize);
        // Just take the first one
        if (rDirectList.size() > 0) {
            // prefer the direct preference, if available
            getDirect().add(rDirectList.get(0));
        } else if (rReverseList.size() > 0) {
            // take the reverse preference as an alternative
            // maybe this isn't the right place to do this.
            IndividualPreference reverseR = rReverseList.get(0);
            IndividualPreference newR =
                new IndividualPreference(reverseR.getStakeholderKey(), reverseR
                    .getSecondaryIndividualKey(), reverseR.getPrimaryIndividualKey(), Double
                    .valueOf(1 - reverseR.getValue().doubleValue()), N_NAMESPACE);
            getReverse().add(newR);
        }

        Logger.getLogger(AlternativePreference.class).info(
            "TIMING: about to make new UtilityDerivedIndividualPreference");

        // Then, find the utility-derived preferences. For this we need to find utility
        // statements for each i and j.
        // This populates rIJ and rJI using the same uI and uJ values, which is good.
        final List<IndividualUtility> uIList =
            getStore().getIndividualUtilities(getE(), getI(), pageSize);
        final List<IndividualUtility> uJList =
            getStore().getIndividualUtilities(getE(), getJ(), pageSize);

        for (final IndividualUtility uI : uIList) {
            for (final IndividualUtility uJ : uJList) {
                // I'm assuming here that uI.individualId == i, etc, i.e. we got what we asked for.
                final UtilityDerivedIndividualPreference uIJD =
                    new UtilityDerivedIndividualPreference(getStore(), getE(), uI, uJ);
                if (uIJD.getR() != null) {
                    // if it's not valid, don't add it.
                    getUtilityDerived().add(uIJD);
                }
            }
        }

        Logger.getLogger(AlternativePreference.class).info(
            "TIMING: about to make new ImpliedPreference");
        // Then, the implied. For this, we need to find all the possible "wheel" individuals, k,
        // (i.e. all other individuals) and search for preferences, ik, ki, jk, and kj.

        // note, ideally we would want this derivation to include derived values as input, so,
        // we need to change the way we store derived values, so we can get at them.

        // This finds rIJ based on forward and reverse evidence -- if rIJ can be derived, it is.
        final List<Individual> kList =
            getStore().getAlternatives(getE().getDecisionKey(), pageSize);
        for (final Individual k : kList) {
            // Could use Ids here just as well.
            if (!(k.makeKey().equals(getI().makeKey()) && !(k.makeKey().equals(getJ().makeKey())))) {
                Logger.getLogger(AlternativePreference.class).info(
                    "Looking for implied preference for e: " + getE().makeKey().toString() + " i: "
                        + getI().makeKey().toString() + " j: " + getJ().makeKey().toString()
                        + " k: " + k.makeKey().toString());
                // k != i and k != j
                final ImpliedPreference iP =
                    ImpliedPreference.newIfValid(getStore(), getE(), getI(), getJ(), k, pageSize);
                if (iP != null) {
                    getImplied().add(iP);
                }
            }
        }

        Logger.getLogger(AlternativePreference.class).info(
            "TIMING: about to make new PropertyDerivedPreference");

        // Then, the property-derived. Constructor does all the work -- it might return a
        // preference indicating indifference.
        // Since the propertyderivedpreference combines all the possible property preferences,
        // it just has one value. But it's a list anyway. :-(
        // This currently does *not* use reverse preferences nor implied preferences in
        // object space.
        // TODO: make it do so.
        PropertyDerivedPreferenceSummary pD = factory.newIfValid(getE(), getI(), getJ());
        if (pD == null) {
            Logger.getLogger(AlternativePreference.class).info(
                "null PropertyDerivedPreferenceSummary");
        } else {
            Logger.getLogger(AlternativePreference.class).info(
                "good PropertyDerivedPreferenceSummary");
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
            setReason("Direct: " + this.result.makeKey().toString() + " score: "
                + String.format("%.2f", this.result.getValue()));
            // Logger.getLogger(AlternativePreference.class).info(
            // "got direct: " + this.result.getExternalKey().toString());
        } else if (getReverse().size() > 0) {
            // Prefer the stated one absolutely.
            setResult(getReverse().get(0));
            setSource(Source.REVERSE);
            setReason("Reverse: " + this.result.makeKey().toString() + " score: "
                + String.format("%.2f", this.result.getValue()));
            // Logger.getLogger(AlternativePreference.class).info(
            // "got reverse: " + this.result.getExternalKey().toString());
        } else if (getUtilityDerived().size() > 0) {
            // Utility-derived is almost as good
            UtilityDerivedIndividualPreference uDP = getUtilityDerived().get(0);
            setResult(uDP.getR());
            setSource(Source.UTILITY);
            setReason("Utility-derived: i: " + uDP.getUI().makeKey().toString() + " j: "
                + uDP.getUJ().makeKey().toString() + " score: "
                + String.format("%.2f", this.result.getValue()));
            // Logger.getLogger(AlternativePreference.class).info(
            // "got utilty-derived: " + this.result.getExternalKey().toString());
        } else if (getImplied().size() > 0) {
            /*
             * Implied might be ok. Note in general there are many implied preferences, for several
             * "wheel" individuals. These are not suitable for the CompromiseOperator -- ideally
             * they would be all the same. So, take the average.
             */
            List<Double> inputValues = new ArrayList<Double>();
            OWAOperator averageOp = new AverageOWAOperator();
            String allReasons = "Implied:";
            for (ImpliedPreference iP : getImplied()) {
                IndividualPreference rIJ = iP.getRIJ();
                inputValues.add(rIJ.getValue());
                allReasons +=
                    " k: " + iP.getK().getName() + " score: "
                        + String.format("%.2f", rIJ.getValue());
            }
            Double averageValue = averageOp.F(inputValues);

            final IndividualPreference r =
                new IndividualPreference(getE().makeKey(), getI().makeKey(), getJ().makeKey(),
                    averageValue, N_NAMESPACE);

            setResult(r);
            setSource(Source.IMPLIED);
            allReasons += " score: " + String.format("%.2f", this.result.getValue());
            setReason(allReasons);
            // Logger.getLogger(AlternativePreference.class).info(
            // "got implied: " + this.result.getExternalKey().toString());
        } else if (getPropertyDerived().size() > 0) {
            // Property-derived is really useful too
            PropertyDerivedPreferenceSummary pDP = getPropertyDerived().get(0);
            setResult(pDP.getR());
            setSource(Source.PROPERTY);
            String allReasons = "Property:";
            for (String s : pDP.getReasons()) {
                allReasons += " " + s;
            }
            allReasons += " score: " + String.format("%.2f", this.result.getValue());
            setReason(allReasons);
            // Logger.getLogger(AlternativePreference.class).info(
            // "got property-derived: " + this.result.getExternalKey().toString());
        }
    }

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

    public Individual getJ() {
        return this.j;
    }

    public void setJ(final Individual j) {
        this.j = j;
    }

    public List<IndividualPreference> getDirect() {
        return this.direct;
    }

    public void setDirect(final List<IndividualPreference> direct) {
        this.direct = direct;
    }

    public List<IndividualPreference> getReverse() {
        return this.reverse;
    }

    public void setReverse(final List<IndividualPreference> reverse) {
        this.reverse = reverse;
    }

    public List<UtilityDerivedIndividualPreference> getUtilityDerived() {
        return this.utilityDerived;
    }

    public void setUtilityDerived(final List<UtilityDerivedIndividualPreference> utilityDerived) {
        this.utilityDerived = utilityDerived;
    }

    public List<ImpliedPreference> getImplied() {
        return this.implied;
    }

    public void setImplied(final List<ImpliedPreference> implied) {
        this.implied = implied;
    }

    public List<PropertyDerivedPreferenceSummary> getPropertyDerived() {
        return this.propertyDerived;
    }

    public void setPropertyDerived(final List<PropertyDerivedPreferenceSummary> propertyDerived) {
        this.propertyDerived = propertyDerived;
    }

    public AlternativeStore getStore() {
        return this.store;
    }

    public void setStore(final AlternativeStore store) {
        this.store = store;
    }

    public IndividualPreference getResult() {
        return this.result;
    }

    public void setResult(IndividualPreference result) {
        this.result = result;
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

}
