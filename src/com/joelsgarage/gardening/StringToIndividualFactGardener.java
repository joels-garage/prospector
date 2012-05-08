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
 * Note: slashes are problematic delimiters, so don't use them.
 * 
 * See http://sites.google.com/a/joelsgarage.com/wik/Home/design/gardening
 * 
 * <pre>
 *  stringProperty.namespace = plants.usda.gov
 *  stringProperty.type = string_property
 *  stringProperty.key = Bloat
 *  stringProperty.name = Bloat
 *  stringFact.namespace = plants.udsa.gov
 *  stringFact.type = string_fact
 *  stringFact.key = CABL/Bloat/None
 *  stringFact.propertyKey = {key for Bloat}
 *  stringFact.subjectKey = {key for CABL}
 *  stringFact.value = None
 * </pre>
 * 
 * given this input, the StringToIndividualFactGardener should produce a (maybe) new individual for
 * the value ...
 * 
 * <pre>
 *  individual.namespace = plants.udsa.gov
 *  individual.type = individual
 *  individual.key = Bloat/None
 *  individual.name = bloat/none
 * </pre>
 * 
 * ... a (maybe) new class for the set of values for this property ...
 * 
 * <pre>
 *  class.namespace = plants.usda.gov
 *  class.type = class
 *  class.key = kinds of Bloat
 *  class.name = kinds of Bloat
 * </pre>
 * 
 * ... a (maybe) new classmember assertion ...
 * 
 * <pre>
 *  classMember.namespace = plants.usda.gov
 *  classMember.type = classmember
 *  classMember.key = kinds of bloat/bloat/none
 *  classMember.individualKey = {key for None}
 *  classMember.classKey = {key for kinds of bloat}
 * </pre>
 * 
 * ... a (maybe) new individual property ...
 * 
 * <pre>
 *  individualProperty.namespace = (same)
 *  individualProperty.type = individual_property
 *  individualProperty.key = Bloat
 *  individualProperty.domainClassKey = (whatever the value for the stringproperty is)
 *  individualProperty.rangeClassKey = {the above new class}
 * </pre>
 * 
 * ... and finally a (maybe) new individual fact ...
 * 
 * <pre>
 *  individualFact.namespace = e.g. plants.udsa.gov
 *  individualFact.type = individual_fact
 *  individualFact.key = CABL/bloat/none
 *  individualFact.name = CABL/bloat/none
 *  individualFact.subjectKey = {key for CABL}
 *  individualFact.objectKey = {key for bloat/none}
 * </pre>
 * 
 * So, the plan is to create a class for every property, to cover its values. the problem with this
 * approach is that many value-sets span properties, e.g. the set of "yes" and "no." maybe those can
 * be handled differently, e.g. with a StringToBooleanFactGardener or something. but there's nothing
 * really *bad* about having lots of these valuesets -- the fact that two properties are both
 * boolean doesn't affect the relationship between the properties per se. so maybe this approach is
 * ok.
 * 
 * Ah, shit, also I shouldn't turn numbers into individuals.  Crap.
 * 
 * The implementation is simple: one scan through all the facts, accumulating unique values for each
 * property. Look up the properties lazily, and emit a new property for each input property. Emit an
 * individual for each unique value, and one output fact per input fact, referencing the new
 * individual and property.
 * 
 * Thus:
 * 
 * <pre>
 * foreach fact {
 *   if (fact value is numeric)
 *     skip it
 *   if (!propertykeys contains fact.propertykey) {
 *     property = lookup(fact.propertykey)
 *     emit new property  // i.e. only if it's new
 *     emit property provenance
 *   }
 *   if (!individuals contains individual derived from fact) {
 *     new individual = construct new individual
 *     emit new individual  // i.e. only if it's new
 *     emit individual provenance
 *   }
 *   emit new fact  // 1:1 with input facts
 *   emit fact provenance
 * }
 * </pre>
 * 
 * 
 * @author joel
 * 
 */
public class StringToIndividualFactGardener extends
	OneToManyLookupProcessNode<ExternalKey, ModelEntity, ModelEntity> implements Gardener {

	public StringToIndividualFactGardener(RecordReaderFactory<ModelEntity> readerFactory,
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
}
