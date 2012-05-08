/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.model.IndividualPreference;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.FatalException;

/**
 * An individual preference derived from differential utility. Records the provenance and the
 * result.
 * 
 * See Chiclana 98 (http://citeseer.ist.psu.edu/357921.html). The basic expression is
 * 
 * <pre>
 * p_ij = s(u_i) / (s(u_i) + s(u_j))
 * </pre>
 * 
 * Where s(x) = x or s(x) = x^2 or any other monotonically increasing function where f(0)=0. For
 * this class, we define s(x) = x.
 * 
 * @author joel
 * 
 */
public class UtilityDerivedIndividualPreference {
    private static final String N_NAMESPACE = "namespace"; //$NON-NLS-1$
    private AlternativeStore store;
    /**
     * The relevant stakeholder.
     */
    private Stakeholder e;
    /**
     * The utility of i.
     */
    private IndividualUtility uI;
    /**
     * The utility of j.
     */
    private IndividualUtility uJ;
    /**
     * The derived preference.
     */
    private IndividualPreference r;

    // Store is not needed.
    public UtilityDerivedIndividualPreference(final AlternativeStore store, final Stakeholder e,
        final IndividualUtility uI, final IndividualUtility uJ) throws FatalException {
        setStore(store);
        setE(e);
        setUI(uI);
        setUJ(uJ);
        setR(null);

        final double i = getUI().getValue().doubleValue();
        final double j = getUJ().getValue().doubleValue();
        if ((i < 0) || (j < 0) || (i > 1) || (j > 1)) {
            // don't do that.
            // TODO: raise an error
            // getR().setValue(Double.valueOf(0));
        } else if ((i > 0) && (j > 0)) {
            setR(new IndividualPreference(getE().makeKey(), getUI().getIndividualKey(), getUJ()
                .getIndividualKey(), Double.valueOf(utilityDerivedPreferenceValue(i, j)),
                N_NAMESPACE));
        } else {
            // both are zero, which gives indifference. In that case, better to do nothing.
            // getR().setValue(Double.valueOf(0.5));
        }
    }

    /** Compare two utilities for two individuals, and produce a preference value */
    public static double utilityDerivedPreferenceValue(double i, double j) {
        return s(i) / (s(i) + s(j));
    }

    // This can be any monotonically increasing function. Play with it.
    private static double s(final double x) {
        return x;
    }

    public AlternativeStore getStore() {
        return this.store;
    }

    public void setStore(final AlternativeStore store) {
        this.store = store;
    }

    public Stakeholder getE() {
        return this.e;
    }

    public void setE(final Stakeholder e) {
        this.e = e;
    }

    public IndividualUtility getUI() {
        return this.uI;
    }

    public void setUI(final IndividualUtility ui) {
        this.uI = ui;
    }

    public IndividualUtility getUJ() {
        return this.uJ;
    }

    public void setUJ(final IndividualUtility uj) {
        this.uJ = uj;
    }

    public IndividualPreference getR() {
        return this.r;
    }

    public void setR(final IndividualPreference r) {
        this.r = r;
    }

}
