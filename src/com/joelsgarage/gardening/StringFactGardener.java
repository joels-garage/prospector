/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.nodes.OneToManyLookupProcessNode;
import com.joelsgarage.model.AffineMeasurementUnit;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.MeasurementQuantity;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.StandardMeasurementUnit;
import com.joelsgarage.model.StringFact;
import com.joelsgarage.model.StringProperty;
import com.joelsgarage.model.UnitSynonym;
import com.joelsgarage.util.FatalException;

/**
 * Generates all the possible progeny of StringFacts.
 * 
 * @author joel
 */
public class StringFactGardener extends
    OneToManyLookupProcessNode<ExternalKey, ModelEntity, ModelEntity> implements Gardener {
    /** does all the work */
    @SuppressWarnings("unused")
    private StringToMeasurementFactConverter measurementConverter;
    private FactValueSplitter splitter;
    @SuppressWarnings("unused")
    private StringToIndividualFactConverter individualConverter;
    /** Matches numbers */
    private final String NUMERIC_PATTERN = "([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)"; //$NON-NLS-1$
    /** Matches e.g. "common name" */
    // TODO: i18n
    private final String NAME_PATTERN = "(.*(name|Name|NAME).*)";//$NON-NLS-1$
    private Pattern numericPattern;
    private Pattern namePattern;

    public StringFactGardener(RecordReaderFactory<ModelEntity> readerFactory,
        RecordWriter<ModelEntity> writer, int inLimit, int outLimit) {
        super(readerFactory, writer, inLimit, outLimit);
        setProgressCount(1000);

        addConstraint(new ReaderConstraint(AffineMeasurementUnit.class));
        addConstraint(new ReaderConstraint(MeasurementQuantity.class));
        addConstraint(new ReaderConstraint(StandardMeasurementUnit.class));
        addConstraint(new ReaderConstraint(UnitSynonym.class));

        this.numericPattern = Pattern.compile(this.NUMERIC_PATTERN);
        this.namePattern = Pattern.compile(this.NAME_PATTERN);

        // These tables are actually resolved upon start().
    }

    /**
     * Take the tables loaded by super.start() and convert them into regular expressions. At the
     * moment this recognizes only some of the quantites, e.g. "height (feet)" but not "ph, minimum"
     * or "density per acre, maximum".
     * 
     * TODO: more patterns like that.
     * 
     * TODO: move the patterns somewhere else.
     * 
     * @throws FatalException
     *             if no patterns were created.
     */
    @SuppressWarnings("nls")
    @Override
    protected void start() throws FatalException {
        super.start();
        // this.measurementConverter = new StringToMeasurementFactConverter( //
        // getMap(new ReaderConstraint(AffineMeasurementUnit.class)), //
        // getMap(new ReaderConstraint(MeasurementQuantity.class)), //
        // getMap(new ReaderConstraint(StandardMeasurementUnit.class)), //
        // getMap(new ReaderConstraint(UnitSynonym.class)));

        // this.measurementConverter.start();
        // fix me
        System.exit(0);

        this.splitter = new FactValueSplitter();

        this.individualConverter = new StringToIndividualFactConverter();
    }

    @Override
    protected boolean handleRecord(ModelEntity mainRecord, ModelEntity lookupRecord) {
        if (mainRecord == null) {
            Logger.getLogger(StringFactGardener.class).error("Null main record"); //$NON-NLS-1$
            return true;
        }
        if (lookupRecord == null) {
            Logger.getLogger(StringFactGardener.class).error("Null lookup record."); //$NON-NLS-1$
            return true;
        }
        if (!(mainRecord instanceof StringFact)) {
            Logger.getLogger(StringFactGardener.class).error(
                "Bad main record type: " + mainRecord.getClass().getName()); //$NON-NLS-1$
            return false;
        }
        if (!(lookupRecord instanceof StringProperty)) {
            Logger.getLogger(StringFactGardener.class).error(
                "Bad lookup record type: " + lookupRecord.getClass().getName()); //$NON-NLS-1$
            return false;
        }

        StringFact inputFact = (StringFact) mainRecord;
        StringProperty inputProperty = (StringProperty) lookupRecord;

        List<ModelEntity> newEntities;
        try {
            newEntities = makeNewEntities(inputFact, inputProperty);

            if (newEntities == null)
                return true;
            for (ModelEntity newEntity : newEntities) {
                output(newEntity);
            }

            return true;
        } catch (FatalException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected List<ModelEntity> makeNewEntities(StringFact inputFact, StringProperty inputProperty)
        throws FatalException {
        // if the fact value is blank, skip it.
        if (inputFact.getValue() == null || inputFact.getValue().length() == 0) {
            Logger.getLogger(StringFactGardener.class).debug("missing input fact value"); //$NON-NLS-1$
            return null;
        }

        // First try to split the value.
        List<ModelEntity> newEntities = this.splitter.convert(inputFact);
        if (newEntities != null && newEntities.size() != 0) {
            Logger.getLogger(StringFactGardener.class).debug("Found split"); //$NON-NLS-1$
            // Note that we don't do anything with the split values, which is probably wrong.
            // TODO: recurse over the results produced here.
            return newEntities;
        }

        // then look for units
        // newEntities = this.measurementConverter.convert(inputFact, inputProperty);
        // fix me.
        System.exit(0);
        if (newEntities != null && newEntities.size() != 0) {
            Logger.getLogger(StringFactGardener.class).debug("Found measurement"); //$NON-NLS-1$
            return newEntities;
        }

        // if the value is numeric, skip it for now; some later rev of the measurement converter
        // should get it.
        Matcher m = this.numericPattern.matcher(inputFact.getValue());
        if (m.matches()) {
            Logger.getLogger(StringFactGardener.class).debug("skipping numeric input fact value"); //$NON-NLS-1$
            return null;
        }

        // if the property looks like some sort of "name" then leave it alone.
        m = this.namePattern.matcher(inputProperty.getName());
        if (m.matches()) {
            Logger.getLogger(StringFactGardener.class).debug("skipping name input property"); //$NON-NLS-1$
            return null;
        }

        // finally convert what's left to individuals.
        // newEntities = this.individualConverter.convert(inputFact, inputProperty);
        // fix me
        System.exit(0);
        if (newEntities != null && newEntities.size() != 0) {
            Logger.getLogger(StringFactGardener.class).debug("converted string to individual"); //$NON-NLS-1$
            return newEntities;
        }
        Logger.getLogger(StringFactGardener.class).debug("did nothing"); //$NON-NLS-1$
        return null;
    }

    /** Given a StringFact, return the key of the associated property */
    @Override
    protected ExternalKey extractForeignKey(ModelEntity record) {
        if (record instanceof StringFact)
            return ((StringFact) record).getPropertyKey();
        return null;
    }

    /** Extract the primary key. */
    @Override
    protected ExternalKey extractLookupKey(ModelEntity record) {
        return record.makeKey();

    }

    /** The lookup table is the set of String Properties */
    @Override
    public ReaderConstraint getLookupConstraint() {
        return new ReaderConstraint(StringProperty.class);
    }

    /** The main table is the set of String Facts */
    @Override
    public ReaderConstraint getMainConstraint() {
        return new ReaderConstraint(StringFact.class);
    }

    /** I don't think this needs to do anything but I'm not sure */
    @Override
    protected void done() {
        // nothing
    }
}
