/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.solver;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Functions;

/**
 * Obtains the first eigenvector of a matrix.
 * 
 * The matrix represents preferences, and so the values range is (0,1).
 * 
 * The eigenvector is scaled to have mean of 0.5.
 * 
 * Implementation just wraps Colt, which seems superior to JAMA (and identical in API, i.e. it's
 * newer and informed by JAMA). It's not really a generic eigensolver API obviously, it's about
 * preferences.
 * 
 * I'm not that happy with this thing. It works great when the preference matrix is dense, but it
 * doesn't handle missing values that well.
 * 
 * My very unscientific performance test, using the unit test (random dense matrix):
 * 
 * <pre>
 * nodes runtime(sec)
 * ----- ------------
 *    10         0.07
 *    50         1.0
 *   100         2.8
 *   200         7.6
 * </pre>
 * 
 * It runs out of heap at 500 nodes, though I could probably increase the heap size to deal with it.
 * 
 * @author joel
 * 
 */
public class Eigensolver<T> {
    /** Keys we have seen. Value is the matrix address. */
    Map<T, Integer> keys = new HashMap<T, Integer>();
    /** Inverse of keys */
    Map<Integer, T> coordinates = new HashMap<Integer, T>();
    /** Preferences we have been given, i => (j => preference)) */
    Map<Integer, Map<Integer, Double>> preferences = new HashMap<Integer, Map<Integer, Double>>();

    public Eigensolver() {
        // foo
    }

    /** Add a preference value for object i over object j */
    public void addPreference(T iKey, T jKey, Double preference) {
        Logger.getLogger(Eigensolver.class).info(
            "i: " + String.valueOf(iKey) + " j: " + String.valueOf(jKey) //$NON-NLS-1$ //$NON-NLS-2$
                + " preference: " + String.valueOf(preference)); //$NON-NLS-1$
        if (iKey == null) {
            Logger.getLogger(Eigensolver.class).error("iKey must not be null"); //$NON-NLS-1$
            return;
        }
        if (jKey == null) {
            Logger.getLogger(Eigensolver.class).error("jKey must not be null"); //$NON-NLS-1$
            return;
        }
        if (preference == null) {
            Logger.getLogger(Eigensolver.class).error("preference must not be null"); //$NON-NLS-1$
            return;
        }

        if (preference.doubleValue() < 0 || preference.doubleValue() > 1) {
            Logger.getLogger(Eigensolver.class).error("preference must be between 0 and 1"); //$NON-NLS-1$
            return;
        }
        if (iKey.equals(jKey)) {
            Logger.getLogger(Eigensolver.class).error("preference keys must be different"); //$NON-NLS-1$
            return;
        }
        Integer i = coordinateOf(iKey);
        Integer j = coordinateOf(jKey);

        if (!getPreferences().containsKey(i)) {
            getPreferences().put(i, new HashMap<Integer, Double>());
        }
        getPreferences().get(i).put(j, preference);
    }

    /**
     * Find the coordinate for the specified key in the keymap, or make a new entry, and return the
     * index of the stored key.
     */
    protected Integer coordinateOf(T key) {
        Integer coordinate = getKeys().get(key);
        if (coordinate != null) {
            return coordinate;
        }
        coordinate = Integer.valueOf(getKeys().size());
        getKeys().put(key, coordinate);
        getCoordinates().put(coordinate, key);
        return coordinate;
    }

    /** Return the first eigenvector of the preference matrix, scaled so that max == 1 */
    public Map<T, Double> getFirstEigenvector() {
        DoubleMatrix2D preferenceMatrix = populateMatrix();
        Logger.getLogger(Eigensolver.class).info(
            "preference matrix: " + preferenceMatrix.toString()); //$NON-NLS-1$

        EigenvalueDecomposition decomp = new EigenvalueDecomposition(preferenceMatrix);
        DoubleMatrix2D eigenvectors = decomp.getV();

        Logger.getLogger(Eigensolver.class).info("eigenvector matrix: " + eigenvectors.toString()); //$NON-NLS-1$

        DoubleMatrix1D realEigenvalues = decomp.getRealEigenvalues();

        Logger.getLogger(Eigensolver.class).info("realEigenvalues: " + realEigenvalues.toString()); //$NON-NLS-1$

        Logger.getLogger(Eigensolver.class).info(
            "imaginary Eigenvalues: " + decomp.getImagEigenvalues().toString()); //$NON-NLS-1$
        int maxIndex = 0;
        double maxValue = Double.MIN_VALUE;
        for (int iter = 0; iter < realEigenvalues.size(); ++iter) {
            double eigenvalue = Math.abs(realEigenvalues.get(iter));
            if (maxValue < eigenvalue) {
                maxValue = eigenvalue;
                maxIndex = iter;
            }
        }

        Logger.getLogger(Eigensolver.class).info("chosen index: " + String.valueOf(maxIndex)); //$NON-NLS-1$

        DoubleMatrix1D firstEigenvector = eigenvectors.viewColumn(maxIndex);

        Logger.getLogger(Eigensolver.class).info(
            "firstEigenvector: " + firstEigenvector.toString()); //$NON-NLS-1$

        // Normalize so that max == 1
        double max = firstEigenvector.aggregate(Functions.max, Functions.abs);
        double ratio = 1.0 / max;

        DoubleMatrix1D scaledEigenvector = firstEigenvector.copy();
        scaledEigenvector.assign(Functions.chain(Functions.abs, Functions.mult(ratio)));

        Logger.getLogger(Eigensolver.class).info(
            "scaledEigenvector: " + scaledEigenvector.toString()); //$NON-NLS-1$

        // Now construct the result map.

        Map<T, Double> resultMap = new HashMap<T, Double>();
        for (int iter = 0; iter < firstEigenvector.size(); ++iter) {
            T key = getCoordinates().get(Integer.valueOf(iter));
            double value = scaledEigenvector.get(iter);
            Logger.getLogger(Eigensolver.class).info("key: " + key //$NON-NLS-1$
                + " value: " + String.valueOf(value)); //$NON-NLS-1$
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                Logger.getLogger(Eigensolver.class).info("skipping out-of-bounds value"); //$NON-NLS-1$

                continue;
            }
            resultMap.put(key, Double.valueOf(value));
        }

        return resultMap;
    }

    /**
     * The cell value -- note, this should really be some expression involving s() (see Chiclana
     * 98), but we just use s(x)=x.
     */
    protected double M(double p) {
        return p / (1 - p);
    }

    protected DoubleMatrix2D populateMatrix() {
        // First count the keys.
        int numKeys = getKeys().size();
        Logger.getLogger(Eigensolver.class).info("matrix size: " + numKeys); //$NON-NLS-1$

        DoubleMatrix2D preferenceMatrix = new SparseDoubleMatrix2D(numKeys, numKeys);

        // // Diagonal == indifference == 0.5.
        // for (int iter = 0; iter < numKeys; ++iter) {
        // preferenceMatrix.set(iter, iter, M(0.5));
        // }

        Logger.getLogger(Eigensolver.class).info(
            "preference matrix: " + String.valueOf(preferenceMatrix)); //$NON-NLS-1$

        // Outer key == i
        for (Map.Entry<Integer, Map<Integer, Double>> outer : getPreferences().entrySet()) {
            // Inner key = j
            for (Map.Entry<Integer, Double> inner : outer.getValue().entrySet()) {
                int iCoordinate = outer.getKey().intValue();
                int jCoordinate = inner.getKey().intValue();

                double value = inner.getValue().doubleValue();

                // Set the direct preference
                preferenceMatrix.set(iCoordinate, jCoordinate, M(value));
                // Set the inverse preference, only if the user hasn't specified one.
                // TODO: avoid these lookups.
                Map<Integer, Double> prefs = getPreferences().get(inner.getKey());
                if (prefs == null) {
                    preferenceMatrix.set(jCoordinate, iCoordinate, M(1 - value));
                } else {
                    if (!prefs.containsKey(outer.getKey())) {
                        preferenceMatrix.set(jCoordinate, iCoordinate, M(1 - value));
                    }
                }
            }
        }

        Logger.getLogger(Eigensolver.class).info(
            "preference matrix: " + String.valueOf(preferenceMatrix)); //$NON-NLS-1$

        // Scale by the degree of the node
        for (int iter = 0; iter < preferenceMatrix.columns(); ++iter) {
            DoubleMatrix1D column = preferenceMatrix.viewColumn(iter);
            Logger.getLogger(Eigensolver.class).info("column before: " + String.valueOf(column)); //$NON-NLS-1$
            double scale = (double) column.cardinality() / (double) (column.size() - 1);
            Logger.getLogger(Eigensolver.class).info(
                "cardinality: " + String.valueOf(column.cardinality())); //$NON-NLS-1$
            Logger.getLogger(Eigensolver.class).info("size: " + String.valueOf(column.size())); //$NON-NLS-1$

            Logger.getLogger(Eigensolver.class).info("scale: " + String.valueOf(scale)); //$NON-NLS-1$

            column.assign(Functions.mult(scale));
            Logger.getLogger(Eigensolver.class).info("column after: " + String.valueOf(column)); //$NON-NLS-1$
        }

        // Diagonal == indifference == 0.5.
        for (int iter = 0; iter < numKeys; ++iter) {
            preferenceMatrix.set(iter, iter, M(0.5));
            // Hm. Try zeroes.
            // preferenceMatrix.set(iter, iter, 0.0);
            // Hm, try the Laplacian
            // DoubleMatrix1D column = preferenceMatrix.viewColumn(iter);
            // // compute the total degree of each node
            // double diagonal = column.aggregate(Functions.plus, Functions.identity);
            // diagonal = -1.0 * diagonal - column.cardinality() + 1;
            // preferenceMatrix.set(iter, iter, diagonal);
        }

        Logger.getLogger(Eigensolver.class).info(
            "preference matrix: " + String.valueOf(preferenceMatrix)); //$NON-NLS-1$
        return preferenceMatrix;
    }

    //

    protected Map<T, Integer> getKeys() {
        return this.keys;
    }

    protected void setKeys(Map<T, Integer> keys) {
        this.keys = keys;
    }

    protected Map<Integer, Map<Integer, Double>> getPreferences() {
        return this.preferences;
    }

    protected void setPreferences(Map<Integer, Map<Integer, Double>> preferences) {
        this.preferences = preferences;
    }

    protected Map<Integer, T> getCoordinates() {
        return this.coordinates;
    }

    protected void setCoordinates(Map<Integer, T> coordinates) {
        this.coordinates = coordinates;
    }
}
