package com.joelsgarage.util;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.joelsgarage.logic.AlternativeStore;
import com.joelsgarage.logic.StaticAlternativeStore;
import com.joelsgarage.model.AffineMeasurementUnit;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.MeasurementQuantity;
import com.joelsgarage.model.MeasurementUnit;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.StandardMeasurementUnit;

@SuppressWarnings("nls")
public class QuantityFactUtilTest extends TestCase {
    private static final int PAGE_SIZE = 1000;
    private AlternativeStore store;
    private QuantityFactUtil util;
    private TestData testData;

    @Override
    protected void setUp() throws FatalException {
        setStore(new StaticAlternativeStore());
        setUtil(new QuantityFactUtil(getStore().getMeasurementUnitList(PAGE_SIZE)));
        this.testData = new TestData();
    }

    public void testGetValueQuantity() throws FatalException {
        QuantityFact fact =
            StaticAlternativeStore.makeQuantityFact("ns", "subj", "domain", "prop",
                this.testData.meter, Double.valueOf(1.0));
        Double value = getUtil().getValue(fact);
        assertNotNull(value);
        assertEquals(1.0, value.doubleValue(), 0.001);
    }

    public void testGetValueMeasurement() throws FatalException {
        QuantityFact fact =
            StaticAlternativeStore.makeQuantityFact("ns", "subj", "domain", "prop",
                this.testData.foot, Double.valueOf(1.0));
        Double value = getUtil().getValue(fact);
        assertNotNull(value);
        assertEquals(0.3048, value.doubleValue(), 0.001);
    }

    public void testGetMeasurementUnit() throws FatalException {
        Logger.getLogger(QuantityFactUtilTest.class).info("testGetMeasurementUnit()");
        MeasurementQuantity q = new MeasurementQuantity("length", "");
        Logger.getLogger(QuantityFactUtilTest.class).info("quantity: " + q.makeKey());
        Logger.getLogger(QuantityFactUtilTest.class).info(
            "testdata quantity: " + this.testData.length.makeKey());

        ExternalKey key = new StandardMeasurementUnit(q.makeKey(), "meter", "").makeKey();
        MeasurementUnit unit = getUtil().getMeasurementUnit(key);
        assertNotNull(unit);
        assertEquals(this.testData.meter.makeKey(), unit.makeKey());
    }

    public void testGetMeasurementUnitNotFound() throws FatalException {
        ExternalKey key =
            new StandardMeasurementUnit(new MeasurementQuantity("length", "").makeKey(),
                "non-existent", "").makeKey();
        MeasurementUnit unit = getUtil().getMeasurementUnit(key);
        assertNull(unit);
    }

    /**
     * Verify that converting a standard unit does not change the value.
     * 
     * @throws FatalException
     */
    public void testConvertStandard() throws FatalException {
        Double value =
            QuantityFactUtil.convert(new StandardMeasurementUnit(new MeasurementQuantity("length",
                "ns").makeKey(), "foot", "ns"), 1.0);
        assertNotNull(value);
        assertEquals(1.0, value.doubleValue(), 0.001);
    }

    public void testConvertLinear() throws FatalException {
        AffineMeasurementUnit unit =
            new AffineMeasurementUnit(
                Double.valueOf(2.0), // this is the thing under test
                new StandardMeasurementUnit(//
                    new MeasurementQuantity("length", "foo").makeKey(), "meter", "foo").makeKey(),
                Double.valueOf(0.0), "foo");

        Double value = QuantityFactUtil.convert(unit, 1.0);
        assertNotNull(value);
        assertEquals(0.5, value.doubleValue(), 0.001);
    }

    public void testConvertAffine() throws FatalException {
        AffineMeasurementUnit unit =
            new AffineMeasurementUnit(
                Double.valueOf(2.0), // this is the thing under test
                new StandardMeasurementUnit(//
                    new MeasurementQuantity("length", "foo").makeKey(), "meter", "foo").makeKey(),
                Double.valueOf(5.0), // AND THIS
                "foo");

        Double value = QuantityFactUtil.convert(unit, 1.0);
        assertNotNull(value);
        assertEquals(-2.0, value.doubleValue(), 0.001);
    }

    /** Verify linear interpolation */
    public void testInterpolateUtility() {
        try {
            assertEquals(1.0, QuantityFactUtil.interpolateUtility(1.0, 2.0, 0.0, 2.0, 1.5), 0.001);
        } catch (QuantityFactUtil.BoundsException ex) {
            fail();
        }
        try {
            // this assertion should not be reached
            assertEquals(1.0, QuantityFactUtil.interpolateUtility(1.0, 2.0, 0.0, 2.0, 3.0), 0.001);
        } catch (QuantityFactUtil.BoundsException ex) {
            assertTrue(true);
        }
    }

    public void testAreCompatibleType1() throws FatalException {
        assertTrue(QuantityFactUtil.areCompatibleType(StaticAlternativeStore.makeQuantityFact("ns",
            "subjectName", "domainClass", "prop", this.testData.foot, Double.valueOf(1.0)),
            StaticAlternativeStore.makeQuantityFact("ns", "subjectName", "domainClass", "prop",
                this.testData.foot, Double.valueOf(1.0))));
    }

    public void testAreCompatibleType2() throws FatalException {
        QuantityFact qf1 =
            StaticAlternativeStore.makeQuantityFact("ns", "subjectName", "domainClass", "prop",
                this.testData.foot, Double.valueOf(0.0));
        QuantityFact qf2 =
            StaticAlternativeStore.makeQuantityFact("ns", "differentSubjectName", "domainClass",
                "prop", this.testData.meter, Double.valueOf(1.0));
        assertTrue(QuantityFactUtil.areCompatibleType(qf1, qf2));
    }

    /** unitized and nonunitized are not compatible. */
    public void testAreCompatibleType3() throws FatalException {
        QuantityFact qf1 =
            StaticAlternativeStore.makeQuantityFact("ns", "subjectName", "domainClass", "prop",
                this.testData.meter, Double.valueOf(1.0));
        QuantityFact qf2 =
            StaticAlternativeStore.makeQuantityFact("ns", "differentSubjectName", "domainClass",
                "prop", null, Double.valueOf(1.0));
        assertFalse(QuantityFactUtil.areCompatibleType(qf1, qf2));
    }

    /**
     * Different quantities are not comparable.
     * 
     * FIXME: currently broken, requires looking up the measurement quantity.
     */
    public void testAreCompatibleType4() throws FatalException {
        QuantityFact qf1 =
            StaticAlternativeStore.makeQuantityFact("ns", "subjectName", "domainClass", "prop",
                this.testData.meter, Double.valueOf(1.0));
        QuantityFact qf2 =
            StaticAlternativeStore.makeQuantityFact("ns", "differentSubjectName", "domainClass",
                "prop", this.testData.squareMeter, Double.valueOf(1.0));
        assertFalse("THIS WILL FAIL UNTIL I FIX IT.", QuantityFactUtil.areCompatibleType(qf1, qf2));
    }

    //

    public AlternativeStore getStore() {
        return this.store;
    }

    public void setStore(AlternativeStore store) {
        this.store = store;
    }

    public QuantityFactUtil getUtil() {
        return this.util;
    }

    public void setUtil(QuantityFactUtil util) {
        this.util = util;
    }
}
