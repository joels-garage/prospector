/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class EigensolverTest extends TestCase {
    /** Verify that the eigenvector method inverts the utility-derived-preference method for a 2x2 */
    public void testSimple2x2() {
        Logger.getLogger(EigensolverTest.class).info("BEGIN TEST SPARSE 2x2");

        double uA = 0.8;
        double uB = 0.2;
        double nUA = uA / uA;
        double nUB = uB / uA;

        double pAB = nUA / (nUA + nUB);

        assertEquals(0.8, pAB, 0.001);

        Eigensolver<String> solver = new Eigensolver<String>();
        solver.addPreference("a", "b", Double.valueOf(pAB));
        Map<String, Double> result = solver.getFirstEigenvector();

        assertNotNull(result);
        assertEquals(2, result.size());

        Double value = result.get("a");
        assertNotNull(value);
        assertEquals(nUA, value.doubleValue(), 0.001);

        value = result.get("b");
        assertNotNull(value);
        assertEquals(nUB, value.doubleValue(), 0.001);
    }

    /** Verify that the eigenvector method inverts the utility-derived-preference formula for a 3x3 */
    public void testSimple3x3() {
        Logger.getLogger(EigensolverTest.class).info("BEGIN TEST SIMPLE 3X3");

        double uA = 0.8;
        double uB = 0.2;
        double uC = 0.5;

        double nUA = uA / uA;
        double nUB = uB / uA;
        double nUC = uC / uA;

        double pAB = nUA / (nUA + nUB);
        double pAC = nUA / (nUA + nUC);
        double pBC = nUB / (nUB + nUC);

        assertEquals(0.8, pAB, 0.001);
        assertEquals(0.6154, pAC, 0.001);
        assertEquals(0.2857, pBC, 0.001);

        Eigensolver<String> solver = new Eigensolver<String>();
        solver.addPreference("a", "b", Double.valueOf(pAB));
        solver.addPreference("a", "c", Double.valueOf(pAC));
        solver.addPreference("b", "c", Double.valueOf(pBC));

        Map<String, Double> result = solver.getFirstEigenvector();
        assertNotNull(result);
        assertEquals(3, result.size());

        Double value = result.get("a");
        assertNotNull(value);
        assertEquals(nUA, value.doubleValue(), 0.001);

        value = result.get("b");
        assertNotNull(value);
        assertEquals(nUB, value.doubleValue(), 0.001);

        value = result.get("c");
        assertNotNull(value);
        assertEquals(nUC, value.doubleValue(), 0.001);
    }

    /**
     * Verify that we get the utilities back that we started with, even if we're missing some
     * preference values
     */
    public void testSparse3x3() {
        Logger.getLogger(EigensolverTest.class).info("BEGIN TEST SPARSE 3X3");

        double uA = 0.8;
        double uB = 0.2;
        double uC = 0.5;
        double nUA = uA / uA;
        double nUB = uB / uA;
        double nUC = uC / uA;

        double pAB = nUA / (nUA + nUB);
        double pAC = nUA / (nUA + nUC);
        double pBC = nUB / (nUB + nUC);
        assertEquals(0.8, pAB, 0.001);
        assertEquals(0.6154, pAC, 0.001);
        assertEquals(0.2857, pBC, 0.001);

        Eigensolver<String> solver = new Eigensolver<String>();
        solver.addPreference("a", "b", Double.valueOf(pAB));
        // Leave one out:
        // solver.addPreference("a", "c", Double.valueOf(pAC));
        solver.addPreference("b", "c", Double.valueOf(pBC));

        Map<String, Double> result = solver.getFirstEigenvector();
        assertNotNull(result);
        assertEquals(3, result.size());

        Double value = result.get("a");
        assertNotNull(value);
        assertEquals(nUA, value.doubleValue(), 0.001);

        value = result.get("b");
        assertNotNull(value);
        assertEquals(nUB, value.doubleValue(), 0.001);

        value = result.get("c");
        assertNotNull(value);
        assertEquals(nUC, value.doubleValue(), 0.001);
    }

    public void testBig() {
        Logger.getLogger(EigensolverTest.class).info("BEGIN TESTBIG");
        int testSize = 10;
        double[] utilities = new double[testSize];
        for (int iter = 0; iter < testSize; ++iter) {
            utilities[iter] = Math.random();
            Logger.getLogger(EigensolverTest.class).info(
                "iter: " + String.valueOf(iter) + " utility: " + utilities[iter]);
        }
        // no way for the eigenvector to match the *magnitude* of the utility, so make a normalized
        // version.
        double[] normalizedUtilities = new double[testSize];
        double max = 0;
        for (int iter = 0; iter < testSize; ++iter) {
            if (max < utilities[iter])
                max = utilities[iter];
        }
        Logger.getLogger(EigensolverTest.class).info("max: " + String.valueOf(max));
        double ratio = 1.0 / max;
        for (int iter = 0; iter < testSize; ++iter) {
            normalizedUtilities[iter] = utilities[iter] * ratio;
            Logger.getLogger(EigensolverTest.class).info(
                "iter: " + String.valueOf(iter) + " normalized: "
                    + String.valueOf(normalizedUtilities[iter]));
        }

        Eigensolver<Integer> solver = new Eigensolver<Integer>();
        for (int iter = 0; iter < (testSize - 2); ++iter) {
            int nextIter = iter + 1;
            int nextNextIter = iter + 2;
            double preference = utilities[iter] / (utilities[iter] + utilities[nextIter]);
            double preference2 = utilities[iter] / (utilities[iter] + utilities[nextNextIter]);

            Logger.getLogger(EigensolverTest.class).info(
                "preference: " + String.valueOf(preference));
            solver.addPreference(Integer.valueOf(iter), Integer.valueOf(nextIter), Double
                .valueOf(preference));
            solver.addPreference(Integer.valueOf(iter), Integer.valueOf(nextNextIter), Double
                .valueOf(preference2));
        }
        // around the end
        solver.addPreference(Integer.valueOf(testSize - 1), Integer.valueOf(0), Double
            .valueOf(utilities[testSize - 1] / (utilities[testSize - 1] + utilities[0])));
        solver.addPreference(Integer.valueOf(testSize - 1), Integer.valueOf(1), Double
            .valueOf(utilities[testSize - 1] / (utilities[testSize - 1] + utilities[1])));
        solver.addPreference(Integer.valueOf(testSize - 2), Integer.valueOf(0), Double
            .valueOf(utilities[testSize - 2] / (utilities[testSize - 2] + utilities[0])));

        Map<Integer, Double> result = solver.getFirstEigenvector();
        for (int iter = 0; iter < testSize; ++iter) {
            double actual = result.get(Integer.valueOf(iter)).doubleValue();
            // assertEquals(normalizedUtilities[iter], actual, 0.2);
            Logger.getLogger(EigensolverTest.class).info(
                String.valueOf(iter)
                    + ": expected: "
                    + normalizedUtilities[iter]
                    + " actual: "
                    + String.valueOf(actual)
                    + " error: "
                    + String.valueOf((normalizedUtilities[iter] - actual)
                        / normalizedUtilities[iter]));
        }

        SortedMap<Double, Integer> sortedActual = new TreeMap<Double, Integer>();
        SortedMap<Double, Integer> sortedExpected = new TreeMap<Double, Integer>();
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            sortedActual.put(entry.getValue(), entry.getKey());
        }
        for (int iter = 0; iter < testSize; ++iter) {
            sortedExpected.put(Double.valueOf(normalizedUtilities[iter]), Integer.valueOf(iter));
        }
        Collection<Integer> actualIndex = sortedActual.values();
        Collection<Integer> expectedIndex = sortedExpected.values();
        Iterator<Integer> actualIter = actualIndex.iterator();
        Iterator<Integer> expectedIter = expectedIndex.iterator();
        while (actualIter.hasNext() && expectedIter.hasNext()) {
            Integer actual = actualIter.next();
            Integer expected = expectedIter.next();
            Logger.getLogger(EigensolverTest.class).info(
                "expected: " + String.valueOf(expected) + " actual: " + String.valueOf(actual));
        }
        actualIter = actualIndex.iterator();
        expectedIter = expectedIndex.iterator();
        // We count the number of mismatches and require it to be low.
        // I dunno how low it needs to be. This doesn't seem to be so good to me.
        // Maybe mismatches high in the list are worse than mismatches low in the list?
        // Note that 50% error rate isn't *that* high, it's one two-swap and one three-swap.
        // Would be good to weight this by how far off the actual is.

        ArrayList<Integer> actualInvert = new ArrayList<Integer>();
        for (int iter = 0; iter < actualIndex.size(); ++iter) {
            actualInvert.add(Integer.valueOf(0));
        }

        int index = 0;
        while (actualIter.hasNext()) {
            Integer actual = actualIter.next();
            actualInvert.set(actual.intValue(), Integer.valueOf(index));
            Logger.getLogger(EigensolverTest.class).info(
                "actual " + String.valueOf(actual) + " for position " + String.valueOf(index));
            ++index;
        }

        int totalMismatch = 0;
        int expectedIdx = 0; // expected position
        while (expectedIter.hasNext()) {
            Integer expected = expectedIter.next();
            // actual position
            int actualIdx = actualInvert.get(expected.intValue()).intValue();
            Logger.getLogger(EigensolverTest.class).info(
                "expected " + String.valueOf(expected) + " to be in position "
                    + String.valueOf(expectedIdx) + " and got actual " + String.valueOf(actualIdx));

            totalMismatch += Math.abs(expectedIdx - actualIdx);
            ++expectedIdx;
        }
        Logger.getLogger(EigensolverTest.class).info(
            "totalMismatch: " + String.valueOf(totalMismatch));
        // random would be 50 (eh?)
        assertTrue("totalMismatch: " + String.valueOf(totalMismatch), totalMismatch < 15);
    }

    /**
     * Shows that fully specifying the preference matrix makes the extraction of utilities more
     * accurate.
     */
    public void testFull10() {
        Logger.getLogger(EigensolverTest.class).info("BEGIN TEST BIG FULL");

        int testSize = 10;
        double[] utilities = new double[testSize];
        for (int iter = 0; iter < testSize; ++iter) {
            utilities[iter] = Math.random();
            Logger.getLogger(EigensolverTest.class).info(
                "iter: " + String.valueOf(iter) + " utility: " + utilities[iter]);
        }
        // no way for the eigenvector to match the *magnitude* of the utility, so make a normalized
        // version.
        double[] normalizedUtilities = new double[testSize];
        double max = 0;
        for (int iter = 0; iter < testSize; ++iter) {
            if (max < utilities[iter])
                max = utilities[iter];
        }
        Logger.getLogger(EigensolverTest.class).info("max: " + String.valueOf(max));
        double ratio = 1.0 / max;
        for (int iter = 0; iter < testSize; ++iter) {
            normalizedUtilities[iter] = utilities[iter] * ratio;
            Logger.getLogger(EigensolverTest.class).info(
                "iter: " + String.valueOf(iter) + " normalized: "
                    + String.valueOf(normalizedUtilities[iter]));
        }

        Eigensolver<Integer> solver = new Eigensolver<Integer>();
        // Fully populate
        for (int iterI = 0; iterI < testSize; ++iterI) {
            for (int iterJ = 0; iterJ < testSize; ++iterJ) {
                double preferenceIJ = utilities[iterI] / (utilities[iterI] + utilities[iterJ]);

                Logger.getLogger(EigensolverTest.class).info(
                    "preferenceIJ: " + String.valueOf(preferenceIJ));

                solver.addPreference(Integer.valueOf(iterI), Integer.valueOf(iterJ), Double
                    .valueOf(preferenceIJ));
            }
        }

        Map<Integer, Double> result = solver.getFirstEigenvector();
        for (int iter = 0; iter < testSize; ++iter) {
            double actual = result.get(Integer.valueOf(iter)).doubleValue();
            assertEquals(normalizedUtilities[iter], actual, 0.1);
            Logger.getLogger(EigensolverTest.class).info(
                String.valueOf(iter)
                    + ": expected: "
                    + normalizedUtilities[iter]
                    + " actual: "
                    + String.valueOf(actual)
                    + " error: "
                    + String.valueOf((normalizedUtilities[iter] - actual)
                        / normalizedUtilities[iter]));
        }

        SortedMap<Double, Integer> sortedActual = new TreeMap<Double, Integer>();
        SortedMap<Double, Integer> sortedExpected = new TreeMap<Double, Integer>();
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            sortedActual.put(entry.getValue(), entry.getKey());
        }
        for (int iter = 0; iter < testSize; ++iter) {
            sortedExpected.put(Double.valueOf(normalizedUtilities[iter]), Integer.valueOf(iter));
        }
        Collection<Integer> actualIndex = sortedActual.values();
        Collection<Integer> expectedIndex = sortedExpected.values();
        Iterator<Integer> actualIter = actualIndex.iterator();
        Iterator<Integer> expectedIter = expectedIndex.iterator();
        while (actualIter.hasNext() && expectedIter.hasNext()) {
            Integer actual = actualIter.next();
            Integer expected = expectedIter.next();
            Logger.getLogger(EigensolverTest.class).info(
                "expected: " + String.valueOf(expected) + " actual: " + String.valueOf(actual));
        }
        actualIter = actualIndex.iterator();
        expectedIter = expectedIndex.iterator();
        while (actualIter.hasNext() && expectedIter.hasNext()) {
            Integer actual = actualIter.next();
            Integer expected = expectedIter.next();
            assertEquals(expected, actual);
        }
    }

    /**
     * Shows that fully specifying the preference matrix makes the extraction of utilities more
     * accurate.
     */
    public void testFull50() {
        Logger.getLogger(EigensolverTest.class).info("BEGIN TEST BIG FULL");

        int testSize = 50;
        double[] utilities = new double[testSize];
        for (int iter = 0; iter < testSize; ++iter) {
            utilities[iter] = Math.random();
            Logger.getLogger(EigensolverTest.class).info(
                "iter: " + String.valueOf(iter) + " utility: " + utilities[iter]);
        }
        // no way for the eigenvector to match the *magnitude* of the utility, so make a normalized
        // version.
        double[] normalizedUtilities = new double[testSize];
        double max = 0;
        for (int iter = 0; iter < testSize; ++iter) {
            if (max < utilities[iter])
                max = utilities[iter];
        }
        Logger.getLogger(EigensolverTest.class).info("max: " + String.valueOf(max));
        double ratio = 1.0 / max;
        for (int iter = 0; iter < testSize; ++iter) {
            normalizedUtilities[iter] = utilities[iter] * ratio;
            Logger.getLogger(EigensolverTest.class).info(
                "iter: " + String.valueOf(iter) + " normalized: "
                    + String.valueOf(normalizedUtilities[iter]));
        }

        Eigensolver<Integer> solver = new Eigensolver<Integer>();
        // Fully populate
        for (int iterI = 0; iterI < testSize; ++iterI) {
            for (int iterJ = 0; iterJ < testSize; ++iterJ) {
                double preferenceIJ = utilities[iterI] / (utilities[iterI] + utilities[iterJ]);

                Logger.getLogger(EigensolverTest.class).info(
                    "preferenceIJ: " + String.valueOf(preferenceIJ));

                solver.addPreference(Integer.valueOf(iterI), Integer.valueOf(iterJ), Double
                    .valueOf(preferenceIJ));
            }
        }

        Map<Integer, Double> result = solver.getFirstEigenvector();
        for (int iter = 0; iter < testSize; ++iter) {
            double actual = result.get(Integer.valueOf(iter)).doubleValue();
            assertEquals(normalizedUtilities[iter], actual, 0.1);
            Logger.getLogger(EigensolverTest.class).info(
                String.valueOf(iter)
                    + ": expected: "
                    + normalizedUtilities[iter]
                    + " actual: "
                    + String.valueOf(actual)
                    + " error: "
                    + String.valueOf((normalizedUtilities[iter] - actual)
                        / normalizedUtilities[iter]));
        }

        SortedMap<Double, Integer> sortedActual = new TreeMap<Double, Integer>();
        SortedMap<Double, Integer> sortedExpected = new TreeMap<Double, Integer>();
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            sortedActual.put(entry.getValue(), entry.getKey());
        }
        for (int iter = 0; iter < testSize; ++iter) {
            sortedExpected.put(Double.valueOf(normalizedUtilities[iter]), Integer.valueOf(iter));
        }
        Collection<Integer> actualIndex = sortedActual.values();
        Collection<Integer> expectedIndex = sortedExpected.values();
        Iterator<Integer> actualIter = actualIndex.iterator();
        Iterator<Integer> expectedIter = expectedIndex.iterator();
        while (actualIter.hasNext() && expectedIter.hasNext()) {
            Integer actual = actualIter.next();
            Integer expected = expectedIter.next();
            Logger.getLogger(EigensolverTest.class).info(
                "expected: " + String.valueOf(expected) + " actual: " + String.valueOf(actual));
        }
        actualIter = actualIndex.iterator();
        expectedIter = expectedIndex.iterator();
        while (actualIter.hasNext() && expectedIter.hasNext()) {
            Integer actual = actualIter.next();
            Integer expected = expectedIter.next();
            assertEquals(expected, actual);
        }
    }

    public void testFull100() {
        Logger.getLogger(EigensolverTest.class).info("BEGIN TEST BIG FULL");

        int testSize = 100;
        double[] utilities = new double[testSize];
        for (int iter = 0; iter < testSize; ++iter) {
            utilities[iter] = Math.random();
            Logger.getLogger(EigensolverTest.class).info(
                "iter: " + String.valueOf(iter) + " utility: " + utilities[iter]);
        }
        // no way for the eigenvector to match the *magnitude* of the utility, so make a normalized
        // version.
        double[] normalizedUtilities = new double[testSize];
        double max = 0;
        for (int iter = 0; iter < testSize; ++iter) {
            if (max < utilities[iter])
                max = utilities[iter];
        }
        Logger.getLogger(EigensolverTest.class).info("max: " + String.valueOf(max));
        double ratio = 1.0 / max;
        for (int iter = 0; iter < testSize; ++iter) {
            normalizedUtilities[iter] = utilities[iter] * ratio;
            Logger.getLogger(EigensolverTest.class).info(
                "iter: " + String.valueOf(iter) + " normalized: "
                    + String.valueOf(normalizedUtilities[iter]));
        }

        Eigensolver<Integer> solver = new Eigensolver<Integer>();
        // Fully populate
        for (int iterI = 0; iterI < testSize; ++iterI) {
            for (int iterJ = 0; iterJ < testSize; ++iterJ) {
                double preferenceIJ = utilities[iterI] / (utilities[iterI] + utilities[iterJ]);

                Logger.getLogger(EigensolverTest.class).info(
                    "preferenceIJ: " + String.valueOf(preferenceIJ));

                solver.addPreference(Integer.valueOf(iterI), Integer.valueOf(iterJ), Double
                    .valueOf(preferenceIJ));
            }
        }

        Map<Integer, Double> result = solver.getFirstEigenvector();
        for (int iter = 0; iter < testSize; ++iter) {
            double actual = result.get(Integer.valueOf(iter)).doubleValue();
            assertEquals(normalizedUtilities[iter], actual, 0.1);
            Logger.getLogger(EigensolverTest.class).info(
                String.valueOf(iter)
                    + ": expected: "
                    + normalizedUtilities[iter]
                    + " actual: "
                    + String.valueOf(actual)
                    + " error: "
                    + String.valueOf((normalizedUtilities[iter] - actual)
                        / normalizedUtilities[iter]));
        }

        SortedMap<Double, Integer> sortedActual = new TreeMap<Double, Integer>();
        SortedMap<Double, Integer> sortedExpected = new TreeMap<Double, Integer>();
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            sortedActual.put(entry.getValue(), entry.getKey());
        }
        for (int iter = 0; iter < testSize; ++iter) {
            sortedExpected.put(Double.valueOf(normalizedUtilities[iter]), Integer.valueOf(iter));
        }
        Collection<Integer> actualIndex = sortedActual.values();
        Collection<Integer> expectedIndex = sortedExpected.values();
        Iterator<Integer> actualIter = actualIndex.iterator();
        Iterator<Integer> expectedIter = expectedIndex.iterator();
        while (actualIter.hasNext() && expectedIter.hasNext()) {
            Integer actual = actualIter.next();
            Integer expected = expectedIter.next();
            Logger.getLogger(EigensolverTest.class).info(
                "expected: " + String.valueOf(expected) + " actual: " + String.valueOf(actual));
        }
        actualIter = actualIndex.iterator();
        expectedIter = expectedIndex.iterator();
        while (actualIter.hasNext() && expectedIter.hasNext()) {
            Integer actual = actualIter.next();
            Integer expected = expectedIter.next();
            assertEquals(expected, actual);
        }
    }

    public void testFull200() {
        Logger.getLogger(EigensolverTest.class).info("BEGIN TEST BIG FULL");

        int testSize = 200;
        double[] utilities = new double[testSize];
        for (int iter = 0; iter < testSize; ++iter) {
            utilities[iter] = Math.random();
            Logger.getLogger(EigensolverTest.class).info(
                "iter: " + String.valueOf(iter) + " utility: " + utilities[iter]);
        }
        // no way for the eigenvector to match the *magnitude* of the utility, so make a normalized
        // version.
        double[] normalizedUtilities = new double[testSize];
        double max = 0;
        for (int iter = 0; iter < testSize; ++iter) {
            if (max < utilities[iter])
                max = utilities[iter];
        }
        Logger.getLogger(EigensolverTest.class).info("max: " + String.valueOf(max));
        double ratio = 1.0 / max;
        for (int iter = 0; iter < testSize; ++iter) {
            normalizedUtilities[iter] = utilities[iter] * ratio;
            Logger.getLogger(EigensolverTest.class).info(
                "iter: " + String.valueOf(iter) + " normalized: "
                    + String.valueOf(normalizedUtilities[iter]));
        }

        Eigensolver<Integer> solver = new Eigensolver<Integer>();
        // Fully populate
        for (int iterI = 0; iterI < testSize; ++iterI) {
            for (int iterJ = 0; iterJ < testSize; ++iterJ) {
                double preferenceIJ = utilities[iterI] / (utilities[iterI] + utilities[iterJ]);

                Logger.getLogger(EigensolverTest.class).info(
                    "preferenceIJ: " + String.valueOf(preferenceIJ));

                solver.addPreference(Integer.valueOf(iterI), Integer.valueOf(iterJ), Double
                    .valueOf(preferenceIJ));
            }
        }

        Map<Integer, Double> result = solver.getFirstEigenvector();
        for (int iter = 0; iter < testSize; ++iter) {
            double actual = result.get(Integer.valueOf(iter)).doubleValue();
            assertEquals(normalizedUtilities[iter], actual, 0.1);
            Logger.getLogger(EigensolverTest.class).info(
                String.valueOf(iter)
                    + ": expected: "
                    + normalizedUtilities[iter]
                    + " actual: "
                    + String.valueOf(actual)
                    + " error: "
                    + String.valueOf((normalizedUtilities[iter] - actual)
                        / normalizedUtilities[iter]));
        }

        SortedMap<Double, Integer> sortedActual = new TreeMap<Double, Integer>();
        SortedMap<Double, Integer> sortedExpected = new TreeMap<Double, Integer>();
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            sortedActual.put(entry.getValue(), entry.getKey());
        }
        for (int iter = 0; iter < testSize; ++iter) {
            sortedExpected.put(Double.valueOf(normalizedUtilities[iter]), Integer.valueOf(iter));
        }
        Collection<Integer> actualIndex = sortedActual.values();
        Collection<Integer> expectedIndex = sortedExpected.values();
        Iterator<Integer> actualIter = actualIndex.iterator();
        Iterator<Integer> expectedIter = expectedIndex.iterator();
        while (actualIter.hasNext() && expectedIter.hasNext()) {
            Integer actual = actualIter.next();
            Integer expected = expectedIter.next();
            Logger.getLogger(EigensolverTest.class).info(
                "expected: " + String.valueOf(expected) + " actual: " + String.valueOf(actual));
        }
        actualIter = actualIndex.iterator();
        expectedIter = expectedIndex.iterator();
        while (actualIter.hasNext() && expectedIter.hasNext()) {
            Integer actual = actualIter.next();
            Integer expected = expectedIter.next();
            assertEquals(expected, actual);
        }
    }

    /** Try a chain of preferences */
    public void testChain() {
        Logger.getLogger(EigensolverTest.class).info("BEGIN TEST CHAIN");

        double uA = 0.9;
        double uB = 0.5;
        double uC = 0.4;
        double nUA = uA / uA;
        double nUB = uB / uA;
        double nUC = uC / uA;

        double pAB = nUA / (nUA + nUB);
        double pBC = nUB / (nUB + nUC);

        assertEquals(0.6428, pAB, 0.001);
        assertEquals(0.5555, pBC, 0.001);

        Eigensolver<String> solver = new Eigensolver<String>();
        solver.addPreference("a", "b", Double.valueOf(pAB));
        solver.addPreference("b", "c", Double.valueOf(pBC));

        Map<String, Double> result = solver.getFirstEigenvector();
        assertNotNull(result);
        assertEquals(3, result.size());

        Double value = result.get("a");
        assertNotNull(value);
        assertEquals(nUA, value.doubleValue(), 0.001);

        value = result.get("b");
        assertNotNull(value);
        assertEquals(nUB, value.doubleValue(), 0.001);

        value = result.get("c");
        assertNotNull(value);
        assertEquals(nUC, value.doubleValue(), 0.001);
    }

    /** Try a chain of preferences */
    public void testLongChain() {
        Logger.getLogger(EigensolverTest.class).info("BEGIN TEST LONG CHAIN");

        double uA = 1.0;
        double uB = 0.9;
        double uC = 0.8;
        double uD = 0.7;
        double uE = 0.6;
        double uF = 0.5;
        double uG = 0.4;
        double uH = 0.3;
        double uI = 0.2;
        double uJ = 0.1;

        double pAB = uA / (uA + uB);
        double pBC = uB / (uB + uC);
        double pCD = uC / (uC + uD);
        double pDE = uD / (uD + uE);
        double pEF = uE / (uE + uF);
        double pFG = uF / (uF + uG);
        double pGH = uG / (uG + uH);
        double pHI = uH / (uH + uI);
        double pIJ = uI / (uI + uJ);
        double pJA = uJ / (uJ + uA);

        assertEquals(0.5263, pAB, 0.001);
        assertEquals(0.5294, pBC, 0.001);
        assertEquals(0.5333, pCD, 0.001);
        assertEquals(0.5385, pDE, 0.001);
        assertEquals(0.5454, pEF, 0.001);
        assertEquals(0.5555, pFG, 0.001);
        assertEquals(0.5714, pGH, 0.001);
        assertEquals(0.6, pHI, 0.001);
        assertEquals(0.6666, pIJ, 0.001);
        assertEquals(0.0909, pJA, 0.001);

        Eigensolver<String> solver = new Eigensolver<String>();
        solver.addPreference("a", "b", Double.valueOf(pAB));
        solver.addPreference("b", "c", Double.valueOf(pBC));
        solver.addPreference("c", "d", Double.valueOf(pCD));
        solver.addPreference("d", "e", Double.valueOf(pDE));
        solver.addPreference("e", "f", Double.valueOf(pEF));
        solver.addPreference("f", "g", Double.valueOf(pFG));
        solver.addPreference("g", "h", Double.valueOf(pGH));
        solver.addPreference("h", "i", Double.valueOf(pHI));
        solver.addPreference("i", "j", Double.valueOf(pIJ));
        solver.addPreference("j", "a", Double.valueOf(pJA));

        Map<String, Double> result = solver.getFirstEigenvector();
        assertNotNull(result);
        assertEquals(10, result.size());

        Double value = result.get("a");
        assertNotNull(value);
        assertEquals(uA, value.doubleValue(), 0.001);

        value = result.get("b");
        assertNotNull(value);
        assertEquals(uB, value.doubleValue(), 0.001);

        value = result.get("c");
        assertNotNull(value);
        assertEquals(uC, value.doubleValue(), 0.001);

        value = result.get("d");
        assertNotNull(value);
        assertEquals(uD, value.doubleValue(), 0.001);

        value = result.get("e");
        assertNotNull(value);
        assertEquals(uE, value.doubleValue(), 0.001);

        value = result.get("f");
        assertNotNull(value);
        assertEquals(uF, value.doubleValue(), 0.001);

        value = result.get("g");
        assertNotNull(value);
        assertEquals(uG, value.doubleValue(), 0.001);

        value = result.get("h");
        assertNotNull(value);
        assertEquals(uH, value.doubleValue(), 0.001);

        value = result.get("i");
        assertNotNull(value);
        assertEquals(uI, value.doubleValue(), 0.001);

        value = result.get("j");
        assertNotNull(value);
        assertEquals(uJ, value.doubleValue(), 0.001);
    }
}
