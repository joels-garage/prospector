/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.nodes.OneToManyLookupProcessNode;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;

/**
 * See http://sites.google.com/a/joelsgarage.com/wik/Home/design/gardening
 * 
 * <pre>
 *  stringProperty.namespace = plants.usda.gov
 *  stringProperty.type = string_property
 *  stringProperty.key = Height, mature (feet)
 *  stringProperty.name = Height, mature (feet)
 *  stringFact.namespace = plants.udsa.gov
 *  stringFact.type = string_fact
 *  stringFact.key = CABL/Height, mature (feet)/2
 *  stringFact.propertyKey = {key for Height, mature (feet)}
 *  stringFact.subjectKey = {key for CABL}
 *  stringFact.value = None
 * </pre>
 * 
 * given this input, the StringToMeasurementFactGardener should produce a (maybe) new
 * measurementproperty ...
 * 
 * <pre>
 *  measurementProperty.namespace
 *  measurementProperty.type = measurement_property
 *  measurementProperty.key = Height, mature
 *  measurementProperty.domainClassKey = (same as stringproperty)
 * </pre>
 * 
 * ... a (maybe) new measurementfact:
 * 
 * <pre>
 *  measurementFact.namespace = source-namespace, e.g. plants.udsa.gov
 *  measurementFact.type = individual_fact
 *  measurementFact.key = CABL/height/0.6
 *  measurementFact.measurementType =  length (because we have feet somewhere)
 *  measurementFact.propertyKey = (the above property)
 *  measurementFact.value = (is this the value in feet or meters?)  anyway 0.6 m
 * </pre>
 * 
 * So this assumes we have a complete library of measurements, i.e. we don't need to derive new
 * measurement types from the source, which is, i think, a good assumption.
 * 
 * So there's a set of patterns; I guess that goes in a properties file?
 * 
 * the property pattern is something like (.*) \($UNIT), where $UNIT is a giant set of alternatives.
 * 
 * then you look at the match to figure out which unit it actually is, so you can scale the value.
 * 
 * I could just use a map of regex's, i.e. Map<MeasurementUnit, Pattern>, and try them one by one.
 * 
 * So it would be NxM for N total unit synonyms and M patterns. That seems pretty crazy, but maybe
 * OK for now.
 * 
 * Also, do serialization with the property, so the single match takes care of both the unit and
 * number recognition.
 * 
 * 
 * OK, crap, I forgot that this is not just a join. It's a join, but the pattern set is also derived
 * from the DB.
 * 
 * So, I need, not just one-to-many lookups (I'd like to retain that) but also access to a few more
 * inputstreams, which i will scan to populate the regexes.
 * 
 * So I need UnitSynonyms, to get the string, MeasurementUnit (i.e. its subclasses), to get the
 * conversion, and MeasurementQuantity, to derive the QuantityProperty. So it's 4 or 5 lookup
 * tables.
 * 
 * 
 * 
 * @author joel
 * 
 */
public class StringToMeasurementFactGardener extends
	OneToManyLookupProcessNode<ExternalKey, ModelEntity, ModelEntity> implements Gardener {

    public StringToMeasurementFactGardener(RecordReaderFactory<ModelEntity> readerFactory,
        RecordWriter<ModelEntity> writer, int inLimit, int outLimit) {
        super(readerFactory, writer, inLimit, outLimit);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean handleRecord(ModelEntity mainRecord, ModelEntity lookupRecord) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected ExternalKey extractForeignKey(ModelEntity record) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ExternalKey extractLookupKey(ModelEntity record) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReaderConstraint getLookupConstraint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReaderConstraint getMainConstraint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void done() {
        // TODO Auto-generated method stub
        
    }
	// private static final String CREATOR_NAMESPACE = "internal-agent"; //$NON-NLS-1$
	// /** This process is treated as a user. */
	// private static final String CREATOR_TYPE = "user"; //$NON-NLS-1$
	// /** Delimiter compatible with application/x-www-form-urlencoded, to separate property from
	// fact */
	// private static final String DELIMITER = "&"; //$NON-NLS-1$
	// private String shortClassName;
	// private String iso8601Date;
	// private ExternalKey creatorKey;
	// /** All the patterns to try */
	// private List<QuantityPattern> patterns;
	// /** We only care about producing an output property for each UNIQUE input property */
	// private Set<ExternalKey> propertyKeys;

    // /** does all the work */
    // private StringToMeasurementFactConverter converter;
    //
    // public StringToMeasurementFactGardener(RecordReaderFactory<ModelEntity> readerFactory,
    // RecordWriter<ModelEntity> writer, int inLimit, int outLimit) {
    // super(readerFactory, writer, inLimit, outLimit);
    // setProgressCount(1000);
    //
    // addConstraint(new ReaderConstraint(AffineMeasurementUnit.class));
    // addConstraint(new ReaderConstraint(MeasurementQuantity.class));
    // addConstraint(new ReaderConstraint(StandardMeasurementUnit.class));
    // addConstraint(new ReaderConstraint(UnitSynonym.class));
    //
    // }
    //
    // /**
    // * Take the tables loaded by super.start() and convert them into regular expressions. At the
    // * moment this recognizes only some of the quantites, e.g. "height (feet)" but not "ph,
    // minimum"
    // * or "density per acre, maximum".
    // *
    // * TODO: more patterns like that.
    // *
    // * TODO: move the patterns somewhere else.
    // *
    // * @throws FatalException
    // * if no patterns were created.
    // */
    // @SuppressWarnings("nls")
    // @Override
    // protected void start() throws FatalException {
    // super.start();
    // this.converter = new StringToMeasurementFactConverter( //
    // getMap(new ReaderConstraint(AffineMeasurementUnit.class)), //
    // getMap(new ReaderConstraint(MeasurementQuantity.class)), //
    // getMap(new ReaderConstraint(StandardMeasurementUnit.class)), //
    // getMap(new ReaderConstraint(UnitSynonym.class)));
    //
    // this.converter.start();
    // }
    //
    // @Override
    // protected boolean handleRecord(ModelEntity mainRecord, ModelEntity lookupRecord) {
    // if (!(mainRecord instanceof StringFact)) {
    // Logger.getLogger(StringToMeasurementFactGardener.class).error(
    // "Bad main record type: " + mainRecord.getClass().getName()); //$NON-NLS-1$
    // return false;
    // }
    // if (!(lookupRecord instanceof StringProperty)) {
    // Logger.getLogger(StringToMeasurementFactGardener.class).error(
    // "Bad lookup record type: " + lookupRecord.getClass().getName()); //$NON-NLS-1$
    // return false;
    // }
    //
    // StringFact inputFact = (StringFact) mainRecord;
    // StringProperty inputProperty = (StringProperty) lookupRecord;
    //
    // List<ModelEntity> newEntities = this.converter.convert(inputFact, inputProperty);
    //
    // if (newEntities == null)
    // return true;
    // for (ModelEntity newEntity : newEntities) {
    // output(newEntity);
    // }
    // return true;
    // }
    //
    // /** Given a StringFact, return the key of the associated property */
    // @Override
    // protected ExternalKey extractForeignKey(ModelEntity record) {
    // if (record instanceof StringFact)
    // return ((StringFact) record).getPropertyKey();
    // return null;
    // }
    //
    // /** Extract the primary key. */
    // @Override
    // protected ExternalKey extractLookupKey(ModelEntity record) {
    // return record.getKey();
    //
    // }
    //
    // /** The lookup table is the set of String Properties */
    // @Override
    // public ReaderConstraint getLookupConstraint() {
    // return new ReaderConstraint(StringProperty.class);
    //	}
    //
    //	/** The main table is the set of String Facts */
    //	@Override
    //	public ReaderConstraint getMainConstraint() {
    //		return new ReaderConstraint(StringFact.class);
    //	}
    //
    //	/** I don't think this needs to do anything but I'm not sure */
    //	@Override
    //	protected void done() {
    //		// nothing
    //	}
}
