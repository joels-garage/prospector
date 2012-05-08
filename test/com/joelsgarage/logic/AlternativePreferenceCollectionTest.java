package com.joelsgarage.logic;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.TestData;

/**
 * There are no differential utilities available in the test set, so those don't appear.
 * 
 * However there should be some property preferences.
 * 
 * TODO: make some.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class AlternativePreferenceCollectionTest extends TestCase {
    AlternativeStore store;
    AlternativePreferenceCollection rCollection;
    Decision d;
    List<AlternativePreference> rList;
    private TestData testData;

    @Override
    public void setUp() throws FatalException {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("setup");

        this.testData = new TestData();
        this.store = new StaticAlternativeStore();
        this.d = this.testData.decisionOne;
        // page size big enough to handle the test set.
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info(
            "about to construct AlternativePreferenceCollection");
        this.rCollection = new AlternativePreferenceCollection(this.store, this.d, 1000);
    }

    private AlternativePreference getR(ExternalKey e, ExternalKey i, ExternalKey j) {
        return this.rCollection.getAlternativePreference(i, j, e);
    }

    private void verifyEIJ(AlternativePreference r, ExternalKey e, ExternalKey i, ExternalKey j) {
        assertNotNull(r);
        assertEquals(e, r.getE().makeKey());
        assertEquals(i, r.getI().makeKey());
        assertEquals(j, r.getJ().makeKey());
    }

    /** Direct preference of stakeholder e on individuals i and j */
    @SuppressWarnings("nls")
    private void verifyDirect(int index, ExternalKey e, ExternalKey i, ExternalKey j, double v) {
        AlternativePreference r = getR(e, i, j);
        assertNotNull(r);
        verifyEIJ(r, e, i, j);

        assertEquals("index: " + String.valueOf(index), AlternativePreference.Source.DIRECT, r
            .getSource());
        assertNotNull("index: " + String.valueOf(index), r.getResult());
        assertEquals("index: " + String.valueOf(index), v, r.getResult().getValue().doubleValue(),
            0.001);
    }

    @SuppressWarnings("nls")
    private void verifyReverse(int index, ExternalKey e, ExternalKey i, ExternalKey j, double v) {
        AlternativePreference r = getR(e, i, j);
        verifyEIJ(r, e, i, j);

        assertEquals("index: " + String.valueOf(index), AlternativePreference.Source.REVERSE, r
            .getSource());
        assertNotNull("index: " + String.valueOf(index), r.getResult());
        assertEquals("index: " + String.valueOf(index), v, r.getResult().getValue().doubleValue(),
            0.001);
    }

    @SuppressWarnings("nls")
    private void verifyUtility(int index, ExternalKey e, ExternalKey i, ExternalKey j, double v) {
        // AlternativePreference r = this.rList.get(index);
        AlternativePreference r = getR(e, i, j);
        verifyEIJ(r, e, i, j);

        assertEquals("index: " + String.valueOf(index), AlternativePreference.Source.UTILITY, r
            .getSource());
        assertNotNull("index: " + String.valueOf(index), r.getResult());
        assertEquals("index: " + String.valueOf(index), v, r.getResult().getValue().doubleValue(),
            0.001);
    }

    @SuppressWarnings("nls")
    private void verifyImplied(int index, ExternalKey e, ExternalKey i, ExternalKey j, double[] v,
        ExternalKey[] k) {
        // AlternativePreference r = this.rList.get(index);
        AlternativePreference r = getR(e, i, j);
        verifyEIJ(r, e, i, j);

        assertEquals("index: " + String.valueOf(index), AlternativePreference.Source.IMPLIED, r
            .getSource());
        // how many implied ratings are at hand here?
        assertEquals("index: " + String.valueOf(index), v.length, r.getImplied().size());
        assertEquals(v.length, k.length);
        double sum = 0;
        int vI = 0;
        for (; vI < v.length; ++vI) {
            ImpliedPreference iP = r.getImplied().get(vI);
            // verify each implied score
            assertEquals("index: " + String.valueOf(index) + " vI: " + String.valueOf(vI), v[vI],
                iP.getRIJ().getValue().doubleValue(), 0.001);
            assertEquals(k[vI], iP.getK().makeKey());
            sum += v[vI];
        }
        assertNotNull("index: " + String.valueOf(index), r.getResult());
        // verify the mean
        assertEquals("index: " + String.valueOf(index), sum / vI, r.getResult().getValue()
            .doubleValue(), 0.001);
    }

    @SuppressWarnings( { "nls", "unchecked" })
    private void verifyProperty(int index, ExternalKey e, ExternalKey i, ExternalKey j, double[] v,
        double z) {
        AlternativePreference r = getR(e, i, j);
        assertNotNull(r);
        verifyEIJ(r, e, i, j);

        assertEquals("index: " + String.valueOf(index), AlternativePreference.Source.PROPERTY, r
            .getSource());
        // there is one reported propertyderived value but it contains more than one (maybe) inside.
        assertEquals("index: " + String.valueOf(index), v.length, r.getPropertyDerived().get(0)
            .getRPList().size());
        int c = 0;
        for (double x : v) {
            PropertyDerivedIndividualPreference pP =
                r.getPropertyDerived().get(0).getRPList().get(c);
            assertEquals("index: " + String.valueOf(index) + " i: " + String.valueOf(c), x, pP
                .getValue().doubleValue(), 0.001);
            ++c;
        }
        assertNotNull("index: " + String.valueOf(index), r.getResult());
        assertEquals("index: " + String.valueOf(index), z, r.getResult().getValue().doubleValue(),
            0.001);
    }

    /** stakeholder 41 likes individual 21 over 23 by 0.7 */
    public void test0() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test0");
        verifyDirect(0, //
            this.testData.stakeholder41.makeKey(), this.testData.individual21.makeKey(), //
            this.testData.individual23.makeKey(),//
            0.7);
    }

    public void test1() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test1");
        verifyReverse(1, //
            this.testData.stakeholder41.makeKey(), this.testData.individual21.makeKey(), //   
            this.testData.individual25.makeKey(), //
            0.9);
    }

    public void test2() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test2");
        verifyImplied(2, //
            this.testData.stakeholder41.makeKey(), this.testData.individual21.makeKey(), // 
            this.testData.individual26.makeKey(), //

            // new double[] { 0.85, 0.9 }, new long[] { 23, 25 });
            new double[] { 0.85, 0.9 }, new ExternalKey[] { //
            this.testData.individual23.makeKey(), //
                this.testData.individual25.makeKey() });

    }

    public void test3() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test3");
        verifyUtility(3, //
            this.testData.stakeholder41.makeKey(), this.testData.individual21.makeKey(), //
            this.testData.individual27.makeKey(), //
            0.5);
    }

    public void test4() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test4");
        verifyUtility(4, //
            this.testData.stakeholder41.makeKey(), this.testData.individual21.makeKey(), //
            this.testData.individual28.makeKey(), //
            0.6);
    }

    public void test5() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test5");
        verifyReverse(5, //
            this.testData.stakeholder41.makeKey(), this.testData.individual23.makeKey(), //
            this.testData.individual21.makeKey(), //
            0.3);
    }

    public void test6() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test6");
        verifyImplied(6, //
            this.testData.stakeholder41.makeKey(), this.testData.individual23.makeKey(), //
            this.testData.individual25.makeKey(), //
            new double[] { 0.7, 0.65 }, new ExternalKey[] { //
            this.testData.individual21.makeKey(), //
                this.testData.individual26.makeKey() });
    }

    public void test7() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test7");
        verifyDirect(7, //
            this.testData.stakeholder41.makeKey(), this.testData.individual23.makeKey(), //
            this.testData.individual26.makeKey(), //
            0.65);
    }

    public void test8() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test8");
        verifyUtility(8, //
            this.testData.stakeholder41.makeKey(), this.testData.individual23.makeKey(), //
            this.testData.individual27.makeKey(), //
            0.4);
    }

    public void test9() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test9");
        verifyUtility(9, //
            this.testData.stakeholder41.makeKey(), this.testData.individual23.makeKey(), //
            this.testData.individual28.makeKey(), //
            0.5);
    }

    public void test10() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test10");
        verifyDirect(10, //
            this.testData.stakeholder41.makeKey(), this.testData.individual25.makeKey(), //
            this.testData.individual21.makeKey(), //
            0.1);
    }

    public void test11() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test11");
        verifyImplied(11, //
            this.testData.stakeholder41.makeKey(), this.testData.individual25.makeKey(), //
            this.testData.individual23.makeKey(), //
            new double[] { 0.3, 0.35 }, new ExternalKey[] { //
            this.testData.individual21.makeKey(), //
                this.testData.individual26.makeKey() });
    }

    public void test12() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test12");
        verifyDirect(12, //
            this.testData.stakeholder41.makeKey(), this.testData.individual25.makeKey(), //
            this.testData.individual26.makeKey(), //
            0.5);
    }

    public void test13() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test13");
        verifyProperty(13, //
            this.testData.stakeholder41.makeKey(), this.testData.individual25.makeKey(), //
            this.testData.individual30.makeKey(), //
            new double[] { 0.2, 0.1 }, 0.04);
    }

    public void test14() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test14");
        verifyImplied(14, //
            this.testData.stakeholder41.makeKey(), this.testData.individual26.makeKey(), //
            this.testData.individual21.makeKey(), //
            new double[] { 0.15, 0.1 }, new ExternalKey[] { //
            this.testData.individual23.makeKey(), //
                this.testData.individual25.makeKey() });
    }

    public void test15() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test15");
        verifyReverse(15, //
            this.testData.stakeholder41.makeKey(), this.testData.individual26.makeKey(), //
            this.testData.individual23.makeKey(), //
            0.35);
    }

    public void test16() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test16");
        verifyReverse(16, //
            this.testData.stakeholder41.makeKey(), this.testData.individual26.makeKey(), //
            this.testData.individual25.makeKey(), //
            0.5);
    }

    public void test17() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test17");
        verifyProperty(17, //
            this.testData.stakeholder41.makeKey(), this.testData.individual26.makeKey(), //
            this.testData.individual29.makeKey(), //
            new double[] { 0.8 }, 0.8);
    }

    public void test18() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test18");
        verifyProperty(18,//
            this.testData.stakeholder41.makeKey(), this.testData.individual26.makeKey(), //
            this.testData.individual30.makeKey(), //
            new double[] { 0.4 }, 0.4);
    }

    public void test19() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test19");
        verifyUtility(19, //
            this.testData.stakeholder41.makeKey(), this.testData.individual27.makeKey(), //
            this.testData.individual21.makeKey(), //
            0.5);
    }

    public void test20() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test20");
        verifyUtility(20, //
            this.testData.stakeholder41.makeKey(), this.testData.individual27.makeKey(), //
            this.testData.individual23.makeKey(), //
            0.6);
    }

    public void test21() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test21");
        verifyUtility(21, //
            this.testData.stakeholder41.makeKey(), this.testData.individual27.makeKey(), //
            this.testData.individual28.makeKey(), //
            0.6);
    }

    public void test22() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test22");
        verifyUtility(22, //
            this.testData.stakeholder41.makeKey(), this.testData.individual28.makeKey(), //
            this.testData.individual21.makeKey(), //
            0.4);
    }

    public void test23() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test23");
        verifyUtility(23,//
            this.testData.stakeholder41.makeKey(), this.testData.individual28.makeKey(), //
            this.testData.individual23.makeKey(), //
            0.5);
    }

    public void test24() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test24");
        verifyUtility(24, //
            this.testData.stakeholder41.makeKey(), this.testData.individual28.makeKey(), //
            this.testData.individual27.makeKey(), //
            0.4);
    }

    public void test25() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test25");
        verifyProperty(25, //
            this.testData.stakeholder41.makeKey(), this.testData.individual29.makeKey(), //
            this.testData.individual26.makeKey(), //
            new double[] { 0.2 }, 0.2);
    }

    public void test26() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test26");
        verifyProperty(26, //
            this.testData.stakeholder41.makeKey(), this.testData.individual29.makeKey(), //
            this.testData.individual30.makeKey(), //
            new double[] { 0.2, 0.1 }, 0.04);
    }

    public void test27() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test27");
        verifyProperty(27, //
            this.testData.stakeholder41.makeKey(), this.testData.individual30.makeKey(), //
            this.testData.individual25.makeKey(), //
            new double[] { 0.8, 0.9 }, 0.96);
    }

    public void test28() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test28");
        verifyProperty(28,//
            this.testData.stakeholder41.makeKey(), this.testData.individual30.makeKey(), //
            this.testData.individual26.makeKey(), //
            new double[] { 0.6 }, 0.6);
    }

    public void test29() {
        Logger.getLogger(AlternativePreferenceCollectionTest.class).info("test29");
        verifyProperty(29, //
            this.testData.stakeholder41.makeKey(), this.testData.individual30.makeKey(), //
            this.testData.individual29.makeKey(), //
            new double[] { 0.8, 0.9 }, 0.96);
    }
}