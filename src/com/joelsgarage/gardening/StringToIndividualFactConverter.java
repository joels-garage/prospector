/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;


/**
 * * See http://sites.google.com/a/joelsgarage.com/wik/Home/design/gardening
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
 * Assumes something upstream has filtered the input to eliminate measurements, etc.
 * 
 * @author joel
 */
public class StringToIndividualFactConverter {
    // private static final String CREATOR_NAMESPACE = "internal-agent"; //$NON-NLS-1$
    // /** This process is treated as a user. */
    // private static final String CREATOR_TYPE = "user"; //$NON-NLS-1$
    // private String shortClassName;
    // private String iso8601Date;
    // private ExternalKey creatorKey;
    // /** We only care about producing an output property for each UNIQUE input property */
    // private Set<ExternalKey> propertyKeys;
    // /** Only emit *unique* individuals */
    // private Set<ExternalKey> individualKeys;
    // private boolean first = true;
    //
    // public StringToIndividualFactConverter() {
    //
    // setShortClassName(ClassUtil.shortClassName(this.getClass()));
    // setIso8601Date(DateUtil.formatDateToISO8601(DateUtil.now()));
    // setCreatorKey(new ExternalKey(CREATOR_NAMESPACE, CREATOR_TYPE, getShortClassName()));
    // setPropertyKeys(new HashSet<ExternalKey>());
    // setIndividualKeys(new HashSet<ExternalKey>());
    // }
    //
    // /**
    // * Given the input, produce some new assertions, if possible
    // *
    // * <pre>
    // * given fact, property (joined on property key):
    // * if (!propertykeys contains property.key) {
    // * emit new property // i.e. only if it's new
    // * emit property provenance
    // * }
    // * if (!individuals contains individual derived from fact) {
    // * new individual = construct new individual
    // * emit new individual // i.e. only if it's new
    // * emit individual provenance
    // * }
    // * emit new fact // 1:1 with input facts
    // * emit fact provenance
    // *
    // * </pre>
    // */
    // public List<ModelEntity> convert(StringFact inputFact, StringProperty inputProperty) {
    // List<ModelEntity> result = new ArrayList<ModelEntity>();
    //
    // if (this.first) {
    // User user = new User();
    // user.setKey(getCreatorKey());
    // user.setCreatorKey(getCreatorKey());
    // user.setLastModified(getIso8601Date());
    // user.setName(getShortClassName());
    // user.setRealName(getShortClassName());
    // result.add(user);
    // }
    // this.first = false;
    //
    // String factNamespace = inputFact.getNamespace();
    // String propertyNamespace = inputProperty.getNamespace();
    //
    // // TODO: i18n.
    // String className = "Range of " + inputProperty.getName();//$NON-NLS-1$
    //
    // // The key for the class of which the new individual is a member.
    // ExternalKey classKey =
    // new ExternalKey(propertyNamespace, ExternalKey.CLASS_TYPE, className);
    //
    // ExternalKey outputPropertyKey = new ExternalKey(propertyNamespace, // same namespace
    // ExternalKey.INDIVIDUAL_PROPERTY_TYPE, // different type
    // inputProperty.getKey().getKey()); // same key
    //
    // if (!getPropertyKeys().contains(inputProperty.getKey())) {
    // getPropertyKeys().add(inputProperty.getKey());
    //
    // // It's a new property, so make a new output property and provenance.
    // // Output property is 1:1 with unique input property.
    // IndividualProperty outputProperty = new IndividualProperty();
    // outputProperty.setKey(outputPropertyKey);
    // outputProperty.setCreatorKey(getCreatorKey());
    // outputProperty.setLastModified(getIso8601Date());
    // outputProperty.setDomainClassKey(inputProperty.getDomainClassKey());
    // outputProperty.setRangeClassKey(classKey);
    // outputProperty.setName(inputProperty.getName());
    // result.add(outputProperty);
    //
    // DerivedProvenance propertyProvenance = new DerivedProvenance();
    //            
    // String ppName = outputProperty.getKey().toString() + " derived from " //$NON-NLS-1$
    // + inputProperty.getKey().toString();
    // propertyProvenance.setKey(new ExternalKey(inputProperty.getNamespace(),
    // ExternalKey.DERIVED_PROVENANCE_TYPE, ppName));
    // 
    // propertyProvenance.setName(ppName);
    // propertyProvenance.setSubjectKey(outputProperty.getKey());
    // propertyProvenance.setAntecedentKey(inputProperty.getKey());
    // propertyProvenance.setDerivation(getShortClassName());
    // result.add(propertyProvenance);
    //
    // // There's one range class per property.
    // Class outputClass = new Class(className, propertyNamespace);
    // result.add(outputClass);
    //
    // DerivedProvenance classProvenance = new DerivedProvenance();
    // String cpName = outputClass.getKey().toString() + " derived from " //$NON-NLS-1$
    // + inputProperty.getKey().toString();
    // classProvenance.setKey(new ExternalKey(inputProperty.getNamespace(),
    // ExternalKey.DERIVED_PROVENANCE_TYPE, cpName));
    // classProvenance.setCreatorKey(getCreatorKey());
    // classProvenance.setLastModified(getIso8601Date());
    // classProvenance.setName(cpName);
    // classProvenance.setSubjectKey(outputClass.getKey());
    // classProvenance.setAntecedentKey(inputProperty.getKey());
    // classProvenance.setDerivation(getShortClassName());
    // result.add(classProvenance);
    // }
    //
    // // ok this name is, like "ns/property/foo
    // String individualName =
    // NameUtil.makeIndividualName(inputProperty.getNamespace(),
    // inputFact.getValue(), inputProperty.getKey());
    //
    // ExternalKey individualKey =
    // new ExternalKey(factNamespace, ExternalKey.INDIVIDUAL_TYPE, individualName);
    //
    // if (!getIndividualKeys().contains(individualKey)) {
    // getIndividualKeys().add(individualKey);
    // // It's a new individual, so emit.
    // Individual outputIndividual = new Individual();
    // outputIndividual.setKey(individualKey);
    // outputIndividual.setCreatorKey(getCreatorKey());
    // outputIndividual.setLastModified(getIso8601Date());
    // outputIndividual.setName(inputFact.getValue()); // note this is human-readable
    // result.add(outputIndividual);
    //
    // DerivedProvenance individualProvenance = new DerivedProvenance();
    // String ipName = outputIndividual.getKey().toString() + " derived from " //$NON-NLS-1$
    // + inputFact.getKey().toString();
    // individualProvenance.setKey(new ExternalKey(factNamespace,
    // ExternalKey.DERIVED_PROVENANCE_TYPE, ipName));
    // individualProvenance.setCreatorKey(getCreatorKey());
    // individualProvenance.setLastModified(getIso8601Date());
    // individualProvenance.setName(ipName);
    // individualProvenance.setSubjectKey(outputIndividual.getKey());
    // individualProvenance.setAntecedentKey(inputFact.getKey());
    // individualProvenance.setDerivation(getShortClassName());
    // result.add(individualProvenance);
    //
    // ClassMember classMember = new ClassMember();
    // String newName = individualKey.toString() + " member of " + classKey.toString();
    // //$NON-NLS-1$
    // classMember.setKey(new ExternalKey(factNamespace, ExternalKey.CLASS_MEMBER_TYPE,
    // newName));
    // classMember.setCreatorKey(getCreatorKey());
    // classMember.setLastModified(getIso8601Date());
    // classMember.setClassKey(classKey);
    // classMember.setIndividualKey(individualKey);
    // classMember.setName(newName);
    // result.add(classMember);
    //
    // DerivedProvenance memberProvenance = new DerivedProvenance();
    // String ppNamespace = inputProperty.getNamespace();
    // String ppName = classMember.getKey().toString() + " derived from " //$NON-NLS-1$
    // + inputProperty.getKey().toString();
    // memberProvenance.setKey(new ExternalKey(ppNamespace,
    // ExternalKey.DERIVED_PROVENANCE_TYPE, ppName));
    // memberProvenance.setCreatorKey(getCreatorKey());
    // memberProvenance.setLastModified(getIso8601Date());
    // memberProvenance.setName(ppName);
    // memberProvenance.setSubjectKey(classMember.getKey());
    // memberProvenance.setAntecedentKey(inputProperty.getKey());
    // memberProvenance.setDerivation(getShortClassName());
    // result.add(individualProvenance);
    // }
    //
    // IndividualFact outputFact = new IndividualFact();
    // outputFact.setCreatorKey(getCreatorKey());
    // outputFact.setLastModified(getIso8601Date());
    // outputFact.setSubjectKey(inputFact.getSubjectKey());
    // outputFact.setPropertyKey(outputPropertyKey);
    // outputFact.setObjectKey(individualKey);
    // outputFact.setName(new String()); // no name
    // outputFact.setKey(outputFact.compositeKey(factNamespace));
    //
    // result.add(outputFact);
    //
    // DerivedProvenance factProvenance = new DerivedProvenance();
    // String name =
    // outputFact.getKey().toString() + " derived from " + inputFact.getKey().toString();
    // //$NON-NLS-1$
    // factProvenance.setKey(new ExternalKey(inputFact.getNamespace(),
    // ExternalKey.DERIVED_PROVENANCE_TYPE, name));
    // factProvenance.setCreatorKey(getCreatorKey());
    // factProvenance.setLastModified(getIso8601Date());
    // factProvenance.setName(name);
    // factProvenance.setSubjectKey(outputFact.getKey());
    // factProvenance.setAntecedentKey(inputFact.getKey());
    // factProvenance.setDerivation(getShortClassName());
    // result.add(factProvenance);
    //
    // return result;
    // }
    //
    // public String getShortClassName() {
    // return this.shortClassName;
    // }
    //
    // public void setShortClassName(String shortClassName) {
    // this.shortClassName = shortClassName;
    // }
    //
    // public String getIso8601Date() {
    // return this.iso8601Date;
    // }
    //
    // public void setIso8601Date(String iso8601Date) {
    // this.iso8601Date = iso8601Date;
    // }
    //
    // public ExternalKey getCreatorKey() {
    // return this.creatorKey;
    // }
    //
    // public void setCreatorKey(ExternalKey creatorKey) {
    // this.creatorKey = creatorKey;
    // }
    //
    // public Set<ExternalKey> getPropertyKeys() {
    // return this.propertyKeys;
    // }
    //
    // public void setPropertyKeys(Set<ExternalKey> propertyKeys) {
    // this.propertyKeys = propertyKeys;
    // }
    //
    // public Set<ExternalKey> getIndividualKeys() {
    // return this.individualKeys;
    // }
    //
    // public void setIndividualKeys(Set<ExternalKey> individualKeys) {
    // this.individualKeys = individualKeys;
    // }
}
