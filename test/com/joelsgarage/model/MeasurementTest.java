package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;

import junit.framework.TestCase;

/**
 * Some simple tests of the measurement conversion code. Not a GWT test.
 * 
 * @author joel
 * 
 */
public class MeasurementTest extends TestCase {
    public void testLinearIdentity() throws FatalException {
        AffineMeasurementUnit linearUnit =
            new AffineMeasurementUnit(Double.valueOf(1.0), null, Double.valueOf(0.0), null);
        assertEquals(1.0, linearUnit.convertFromStandard(1.0), 0.001);
        assertEquals(1.0, linearUnit.convertToStandard(1.0), 0.001);
    }

    public void testLinear() throws FatalException {
        // 1m in feet
        AffineMeasurementUnit linearUnit =
            new AffineMeasurementUnit(Double.valueOf(3.2808), null, Double.valueOf(0.0), null);
        assertEquals(3.2808, linearUnit.convertFromStandard(1.0), 0.001);
        assertEquals(1.0, linearUnit.convertToStandard(3.2808), 0.001);
    }

    public void testAffineIdentity() throws FatalException {
        AffineMeasurementUnit affineUnit =
            new AffineMeasurementUnit(Double.valueOf(1.0), null, Double.valueOf(0.0), null);
        assertEquals(1.0, affineUnit.convertFromStandard(1.0), 0.001);
        assertEquals(1.0, affineUnit.convertToStandard(1.0), 0.001);

    }

    public void testAffine() throws FatalException {
        // 1.8 = 1C in fahrenheit
        // 32 = 0C in fahrenheit
        AffineMeasurementUnit affineUnit =
            new AffineMeasurementUnit(Double.valueOf(1.8), null, Double.valueOf(32.0), null);
        assertEquals(33.8, affineUnit.convertFromStandard(1.0), 0.001);
        assertEquals(1.0, affineUnit.convertToStandard(33.8), 0.001);
    }
}