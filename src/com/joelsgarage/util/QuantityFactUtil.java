/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.util.List;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.MeasurementUnit;
import com.joelsgarage.model.OptimizerQuantityPropertyUtility;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.StandardMeasurementUnit;

/**
 * Value conversion.
 * 
 * @author joel
 * 
 */
public class QuantityFactUtil {
    /**
     * Fact value out of the specified range.
     * 
     * @author joel
     * 
     */
    public static class BoundsException extends Exception {
        private static final long serialVersionUID = 1L;

        public BoundsException() {
            super();
        }

        public BoundsException(Exception e) {
            super(e);
        }

        public BoundsException(String s) {
            super(s);
        }
    }

    /**
     * All the units in existence, in case we need to convert. It's not a huge list, so OK to keep
     * the whole thing hot.
     * 
     * TODO: a set with a key comparator, to avoid scanning.
     */
    private List<MeasurementUnit> measurementUnitList;

    public QuantityFactUtil(final List<MeasurementUnit> measurementUnitList) {
        setMeasurementUnitList(measurementUnitList);
    }

    /**
     * Get the value of a QuantityFact, converted to the Standard Unit if it's a MeasurementFact.
     * 
     * @param qF
     *            A QuantityFact (i.e. maybe a MeasurementFact)
     * @return the value of the fact, converted to the Standard Unit if it's a MeasurementFact.
     */
    public Double getValue(final QuantityFact qF) {
        Logger.getLogger(QuantityFactUtil.class).debug("getValue"); //$NON-NLS-1$
        if (qF.measurement()) {
            Logger.getLogger(QuantityFactUtil.class).debug("isMeasurement"); //$NON-NLS-1$
            MeasurementUnit unit = getMeasurementUnit(qF.getExpressedUnitKey());
            if (unit == null) {
                // this should not happen. TODO: throw an exception.
                Logger.getLogger(QuantityFactUtil.class).debug("null unit"); //$NON-NLS-1$
                return null;
            }
            Logger.getLogger(QuantityFactUtil.class).debug("converting"); //$NON-NLS-1$
            return convert(unit, qF.getValue().doubleValue());
        }
        Logger.getLogger(QuantityFactUtil.class).debug("no conversion"); //$NON-NLS-1$
        return qF.getValue();
    }

    /** Get the preference value in the standard unit, so it can be compared to facts */
    public Double getValue(final OptimizerQuantityPropertyUtility oQPU) {
        if (oQPU.isMeasurement()) {
            MeasurementUnit unit = getMeasurementUnit(oQPU.getMeasurementUnitKey());
            if (unit == null) {
                return null;
            }
            return convert(unit, oQPU.getFactValue().doubleValue());
        }
        return oQPU.getFactValue();
    }

    /**
     * Find the specified unit in the set of all units.
     * 
     * @param measurementUnitKey
     * @return
     */
    protected MeasurementUnit getMeasurementUnit(ExternalKey measurementUnitKey) {
        Logger.getLogger(QuantityFactUtil.class).debug(
            "looking for: " + measurementUnitKey.toString()); //$NON-NLS-1$
        for (MeasurementUnit unit : getMeasurementUnitList()) {
            if (unit instanceof StandardMeasurementUnit) {
                StandardMeasurementUnit s = (StandardMeasurementUnit) unit;
                Logger.getLogger(QuantityFactUtil.class).debug(
                    "std quantity: " + s.getMeasurementQuantityKey().toString()); //$NON-NLS-1$
            }
            Logger.getLogger(QuantityFactUtil.class).debug("unit: " + unit.makeKey()); //$NON-NLS-1$
            if (unit.makeKey().equals(measurementUnitKey))
                return unit;
        }
        Logger.getLogger(QuantityFactUtil.class).debug("null unit"); //$NON-NLS-1$
        return null;
    }

    /**
     * Convert the specified value to the Standard Unit.
     * 
     * @param unit
     *            the unit in which the value is expressed
     * @param value
     *            the value to convert
     * @return the value converted to the Standard Unit.
     */
    protected static Double convert(MeasurementUnit unit, double value) {
        return Double.valueOf(unit.convertToStandard(value));
    }

    /**
     * Interpolate the utility of a fact, using a linear utility function with the given bounds on
     * the range of the fact's property.
     * 
     * @param aValue
     *            fact value for endpoint "A"
     * @param bValue
     *            fact value for endpoint "B"
     * @param aUtility
     *            utility value for endpoint "A"
     * @param bUtility
     *            utility value for endpoint "B"
     * @param value
     *            value of the fact; must be between aValue and bValue
     * @return
     */
    public static double interpolateUtility(double aValue, double bValue, double aUtility,
        double bUtility, double value) throws QuantityFactUtil.BoundsException {
        if ((value > aValue && value > bValue) || (value < aValue && value < bValue))
            throw new QuantityFactUtil.BoundsException();
        return aUtility + (value - aValue) * (bUtility - aUtility) / (bValue - aValue);
    }

    /**
     * Interpolates for optimum. Set preferred value to one of the endpoint values to do one-sided
     * interpolation.
     * 
     * TODO: combine this with the above method.
     * 
     * @param stdValue
     * @param minStdValue
     * @param preferredStdValue
     * @param maxStdValue
     * @param minUtility
     * @param preferredUtility
     * @return
     * @throws QuantityFactUtil.BoundsException
     */
    public static double interpolateUtilityForOptimum(Double stdValue, Double minStdValue,
        Double preferredStdValue, Double maxStdValue, Double minUtility, Double preferredUtility)
        throws QuantityFactUtil.BoundsException {
        double dStdValue = stdValue.doubleValue();
        double dMinStdValue = minStdValue.doubleValue();
        double dPreferredStdValue = preferredStdValue.doubleValue();
        double dMaxStdValue = maxStdValue.doubleValue();
        double dMinUtility = minUtility.doubleValue();
        double dPreferredUtility = preferredUtility.doubleValue();

        double utility;
        if (dStdValue < dPreferredStdValue) {
            // interpolate on the left side of the curve
            utility =
                QuantityFactUtil.interpolateUtility(dMinStdValue, dPreferredStdValue, dMinUtility,
                    dPreferredUtility, dStdValue);
        } else if (dStdValue > dPreferredStdValue) {
            // interpolate on the right side of the curve
            utility =
                QuantityFactUtil.interpolateUtility(dPreferredStdValue, dMaxStdValue,
                    dPreferredUtility, dMinUtility, dStdValue);
        } else {
            // it *is* the preferred value
            utility = dPreferredUtility;
        }
        return utility;
    }

    /**
     * Verify that the types of these two facts are compatible
     * 
     * FIXME: make this check that both facts measure the same quantity.
     * 
     * @param qF1
     * @param qF2
     * @return true if both are measurements, or neither.
     */
    public static boolean areCompatibleType(final QuantityFact qF1, final QuantityFact qF2) {
        if (qF1.measurement() && qF2.measurement()) {
            Logger.getLogger(QuantityFactUtil.class).info("compatible"); //$NON-NLS-1$

            return true;
        }
        if (!qF1.measurement() && !qF2.measurement()) {
            Logger.getLogger(QuantityFactUtil.class).info("compatible"); //$NON-NLS-1$

            return true;
        }
        Logger.getLogger(QuantityFactUtil.class).info("not compatible"); //$NON-NLS-1$

        return false;
    }

    public List<MeasurementUnit> getMeasurementUnitList() {
        return this.measurementUnitList;
    }

    public void setMeasurementUnitList(List<MeasurementUnit> measurementUnitList) {
        this.measurementUnitList = measurementUnitList;
    }

}
