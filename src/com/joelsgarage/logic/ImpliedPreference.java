/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.List;

import org.apache.log4j.Logger;

import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualPreference;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.FatalException;

/**
 * An individual preference derived from two other individual preferences. Records the provenance
 * and the result.
 * 
 * See Fang 2007 (https://nscnt12.nsc.gov.tw/APPLYFORM/WRITINGS/U120353377/20080220221959.pdf). To
 * estimate a missing preference involving two individuals, we find a third individual (the "wheel")
 * that we do have preferences for, between the two desired individuals. Fang specifies a complex
 * procedure that assumes that r_ij + r_ji != 1. Hm. I'll try it anyway I guess.
 * 
 * @author joel
 * 
 */
public class ImpliedPreference {
    /**
     * The relevant stakeholder.
     */
    private Stakeholder e;
    /**
     * One of the individuals we want to have a preference about.
     */
    private Individual i;
    /**
     * The other individual we want to have a preference about.
     */
    private Individual j;
    /**
     * The individual used as the "wheel". ( "k" and "j" are reversed relative to Lee)
     */
    private Individual k;
    /**
     * This is the preference we estimate.
     */
    private IndividualPreference rIJ;

    public ImpliedPreference(final Stakeholder e, final Individual i, final Individual j,
        final Individual k, final IndividualPreference r) {
        setE(e);
        setI(i);
        setJ(j);
        setK(k);
        setRIJ(r);
    }

    /**
     * Return a new ImpliedPreference if the supplied arguments can be used to create one.
     * Otherwise, null.
     * 
     * @param store
     * @param e
     * @param i
     *            the primary individual
     * @param j
     *            the secondary individual
     * @param k
     *            the possible wheel individual
     * @return a new instance if the wheel is useful, null if not
     * @throws FatalException
     *             if key population fails
     */
    @SuppressWarnings("nls")
    public static ImpliedPreference newIfValid(final AlternativeStore store, final Stakeholder e,
        final Individual i, final Individual j, final Individual k, int pageSize)
        throws FatalException {
        Logger.getLogger(ImpliedPreference.class).info("ImpliedPreference.newIfValid()");
        // maybe this is a valid k. there are four ways to be useful. (reversing j and k)
        // rIJ1 = rIK + rKJ - 0.5
        // rIJ2 = rKJ - rKI + 0.5
        // rIJ3 = rIK - rJK + 0.5
        // rIJ4 = 1.5 - rKI - rJK
        // so first let's find all the preferences that we can.
        // double rIJValue = 0.0;
        // how many are valid?
        // int F = 0;
        //
        // Double rIK = null;
        // Double rKJ = null;
        // Double rJK = null;
        // Double rKI = null;

        Double rIJValue = null;

        Logger.getLogger(ImpliedPreference.class).info("fetching i, k");
        final List<IndividualPreference> rIKList =
            store.getIndividualPreferences(e, i, k, pageSize);

        Logger.getLogger(ImpliedPreference.class).info("fetching k, j");
        final List<IndividualPreference> rKJList =
            store.getIndividualPreferences(e, k, j, pageSize);

        Logger.getLogger(ImpliedPreference.class).info("fetching j, k");
        final List<IndividualPreference> rJKList =
            store.getIndividualPreferences(e, j, k, pageSize);

        Logger.getLogger(ImpliedPreference.class).info("fetching k, i");
        final List<IndividualPreference> rKIList =
            store.getIndividualPreferences(e, k, i, pageSize);

        ImplicationOperator.Inputs inputs = new ImplicationOperator.Inputs();

        // Just take the first one.
        // TODO: if multiple, say something.
        if (rIKList.size() > 0) {
            inputs.setRIK(rIKList.get(0).getValue());
        }
        if (rKJList.size() > 0) {
            inputs.setRKJ(rKJList.get(0).getValue());
        }
        if (rJKList.size() > 0) {
            inputs.setRJK(rJKList.get(0).getValue());
        }
        if (rKIList.size() > 0) {
            inputs.setRKI(rKIList.get(0).getValue());
        }

        // System.out.println("implied preference for k " + k.getId().toString());
        ImplicationOperator implicationOp = new ImplicationOperator(inputs);
        rIJValue = implicationOp.getValue();

        // for (final IndividualPreference rIK : rIKList) {
        // for (final IndividualPreference rKJ : rKJList) {
        // // there might be more than one. take the last one, for now.
        // // TODO: combine them.
        //
        // final double rIJ1 = rIK.getValue().doubleValue() + rKJ.getValue().doubleValue()
        // - 0.5;
        // // System.out.println("rIK: " + rIK.getValue().toString() + " rKJ: "
        // // + rKJ.getValue().toString() + " rJI1: " + String.valueOf(rIJ1));
        // rIJValue += rIJ1;
        // ++F;
        // }
        // }

        // for (final IndividualPreference rKJ : rKJList) {
        // for (final IndividualPreference rKI : rKIList) {
        //
        // final double rIJ2 = rKJ.getValue().doubleValue() - rKI.getValue().doubleValue()
        // + 0.5;
        // // System.out.println("rKJ: " + rKJ.getValue().toString() + " rKI: "
        // // + rKI.getValue().toString() + " rJI2: " + String.valueOf(rIJ2));
        //
        // rIJValue += rIJ2;
        // ++F;
        // }
        // }
        //
        // for (final IndividualPreference rIK : rIKList) {
        // for (final IndividualPreference rJK : rJKList) {
        // final double rIJ3 = rIK.getValue().doubleValue() - rJK.getValue().doubleValue()
        // + 0.5;
        // // System.out.println("rIK: " + rIK.getValue().toString() + " rJK: "
        // // + rJK.getValue().toString() + " rJI3: " + String.valueOf(rIJ3));
        // rIJValue += rIJ3;
        // ++F;
        // }
        // }
        // for (final IndividualPreference rKI : rKIList) {
        // for (final IndividualPreference rJK : rJKList) {
        // final double rIJ4 = 1.5 - rKI.getValue().doubleValue()
        // - rJK.getValue().doubleValue();
        // // System.out.println("rKI: " + rKI.getValue().toString() + " rJK: "
        // // + rJK.getValue().toString() + " rJI4: " + String.valueOf(rIJ4));
        // rIJValue += rIJ4;
        // ++F;
        // }
        // }

        // if (F > 0) {
        if (rIJValue != null) {
            // rIJValue = rIJValue / F;
            // System.out.println("new rIJValue: " + String.valueOf(rIJValue));
            final IndividualPreference r =
                new IndividualPreference(e.makeKey(), i.makeKey(), j.makeKey(), rIJValue, "namespace");
            return new ImpliedPreference(e, i, j, k, r);
        }
        return null;
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

    public Individual getK() {
        return this.k;
    }

    public void setK(final Individual k) {
        this.k = k;
    }

    public Individual getJ() {
        return this.j;
    }

    public void setJ(final Individual j) {
        this.j = j;
    }

    public IndividualPreference getRIJ() {
        return this.rIJ;
    }

    public void setRIJ(final IndividualPreference rij) {
        this.rIJ = rij;
    }
}
