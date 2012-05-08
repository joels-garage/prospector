/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;

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
 * ---
 * 
 * OK, this does the work. Since the maps involved in resolving the unit are held in the lookup
 * process node, we pass it as a constructor arg. i really don't like that. i also don't like having
 * to trust that the correct type of maps exist.
 * 
 * So instead I guess we could just pass the maps themselves.
 * 
 * 
 * 
 * @author joel
 * 
 */
public class StringToMeasurementFactConverter {
    // private static final String CREATOR_NAMESPACE = "internal-agent"; //$NON-NLS-1$
    // /** This process is treated as a user. */
    // private static final String CREATOR_TYPE = "user"; //$NON-NLS-1$
    // /** Delimiter compatible with application/x-www-form-urlencoded, to separate property from
    // fact
    // */
    // private static final String DELIMITER = "&"; //$NON-NLS-1$
    // private String shortClassName;
    // private String iso8601Date;
    // private ExternalKey creatorKey;
    // /** All the patterns to try */
    // private List<QuantityPattern> patterns;
    // /** We only care about producing an output property for each UNIQUE input property */
    // private Set<ExternalKey> propertyKeys;
    //
    // private final Map<ExternalKey, ModelEntity> affineMeasurementUnitMap;
    // @SuppressWarnings("unused")
    // private final Map<ExternalKey, ModelEntity> measurementQuantityMap;
    // private final Map<ExternalKey, ModelEntity> standardMeasurementUnitMap;
    // private final Map<ExternalKey, ModelEntity> unitSynonymMap;
    // private boolean first = true;
    //
    // public StringToMeasurementFactConverter(Map<ExternalKey, ModelEntity>
    // affineMeasurementUnitMap,
    // Map<ExternalKey, ModelEntity> measurementQuantityMap,
    // Map<ExternalKey, ModelEntity> standardMeasurementUnitMap,
    // Map<ExternalKey, ModelEntity> unitSynonymMap) {
    //
    // this.affineMeasurementUnitMap = affineMeasurementUnitMap;
    // this.measurementQuantityMap = measurementQuantityMap;
    // this.standardMeasurementUnitMap = standardMeasurementUnitMap;
    // this.unitSynonymMap = unitSynonymMap;
    //
    // setShortClassName(ClassUtil.shortClassName(this.getClass()));
    // setIso8601Date(DateUtil.formatDateToISO8601(DateUtil.now()));
    // setCreatorKey(new ExternalKey(CREATOR_NAMESPACE, CREATOR_TYPE, getShortClassName()));
    // setPatterns(new ArrayList<QuantityPattern>());
    // setPropertyKeys(new HashSet<ExternalKey>());
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
    // public void start() throws FatalException {
    //
    // // first create the pattern.
    // Iterator<ModelEntity> unitSynonyms = this.unitSynonymMap.values().iterator();
    //
    // while (unitSynonyms.hasNext()) {
    // ModelEntity item = unitSynonyms.next();
    //
    // if (!(item instanceof UnitSynonym)) {
    // Logger.getLogger(StringToMeasurementFactConverter.class).info(
    // "wrong type: " + item.getClass());
    // continue;
    // }
    // UnitSynonym unitSynonym = (UnitSynonym) item;
    // Logger.getLogger(StringToMeasurementFactConverter.class).info(
    // "Working on synonym " + unitSynonym.getValue());
    // // The name is *anything* other than the delimiter or paren. Negating the paren means we
    // // don't have to back off to match the unit
    // String startPattern = "^";
    // // String propertyNameCapturePattern = "([^&()]*)";
    // String propertyNameCapturePattern = "([^&()]*)";
    // // The unit is always within parens
    // // String unitPattern = "\\(\\s*" + unitSynonym.getValue() + "\\s*\\)";
    // String unitPattern = "%28\\+*" + NameUtil.encode(unitSynonym.getValue()) + "\\+*%29";
    //
    // // The delimiter may have whitespace around it, or not
    // // String delimiterPattern = "\\s*&\\s*";
    // String delimiterPattern = "\\+*&\\+*";
    //
    // // Positive, negative, floating point. I don't think there's exponents in there but
    // // may as well look for them.
    // String numericValueCapturePattern = "([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)";
    // String endPattern = "$";
    // String pattern = startPattern + //
    // propertyNameCapturePattern + //
    // unitPattern + //
    // delimiterPattern + //
    // numericValueCapturePattern + //
    // endPattern;
    // Logger.getLogger(StringToMeasurementFactConverter.class).info(
    // "compiling pattern: " + pattern);
    // Pattern p = Pattern.compile(pattern);
    //
    // ExternalKey unitKey = unitSynonym.getMeasurementUnitKey();
    //
    // QuantityPattern qp = new QuantityPattern(p, unitKey);
    // addPattern(qp);
    // }
    // if (getPatterns() == null || getPatterns().size() == 0) {
    // throw new FatalException("No patterns created!");
    // }
    // }
    //
    // /**
    // * Put the fact and property into a single string for matching.
    // *
    // * We encode both the needle and haystack so that the delimiters can't be matched.
    // */
    // protected static String makeSerializedMatchString(StringFact fact, StringProperty property) {
    // String factString = NameUtil.encode(fact.getValue());
    // String propertyString = NameUtil.encode(property.getName());
    // return propertyString + DELIMITER + factString;
    // }
    //
    // // @Override
    // // protected boolean handleRecord(ModelEntity mainRecord, ModelEntity lookupRecord) {
    // // // Logger.getLogger(StringToMeasurementFactGardener.class).info(
    // // // "Handle main Record: " + mainRecord.toString() + //$NON-NLS-1$
    // // // " lookup Record: " + lookupRecord.toString()); //$NON-NLS-1$
    // // if (!(mainRecord instanceof StringFact)) {
    // // Logger.getLogger(StringToMeasurementFactGardener.class).error(
    // // "Bad main record type: " + mainRecord.getClass().getName()); //$NON-NLS-1$
    // // return false;
    // // }
    // // if (!(lookupRecord instanceof StringProperty)) {
    // // Logger.getLogger(StringToMeasurementFactGardener.class).error(
    // // "Bad lookup record type: " + lookupRecord.getClass().getName()); //$NON-NLS-1$
    // // return false;
    // // }
    // // StringFact inputFact = (StringFact) mainRecord;
    // // StringProperty inputProperty = (StringProperty) lookupRecord;
    // //
    // // String serializedMatchString = makeSerializedMatchString(inputFact, inputProperty);
    // //
    // // if (getPatterns() == null || getPatterns().size() == 0) {
    // // Logger.getLogger(StringToMeasurementFactGardener.class).error(
    // // "No patterns! " + lookupRecord.getClass().getName()); //$NON-NLS-1$
    // // }
    // //
    // // // Try matching against each pattern. My god!
    // // for (QuantityPattern qp : getPatterns()) {
    // // Pattern p = qp.getPattern();
    // // // Logger.getLogger(StringToMeasurementFactGardener.class).info(
    // // // "trying pattern " + p.pattern()); //$NON-NLS-1$
    // // // Logger.getLogger(StringToMeasurementFactGardener.class).info(
    // // // "trying match string " + serializedMatchString); //$NON-NLS-1$
    // //
    // // Matcher m = p.matcher(serializedMatchString);
    // // if (m.matches()) {
    // // // Logger.getLogger(StringToMeasurementFactGardener.class).info("matched!");
    // // // //$NON-NLS-1$
    // // String newPropertyName = NameUtil.decode(m.group(1)).trim();
    // // String number = NameUtil.decode(m.group(2)).trim();
    // // List<ModelEntity> newEntities = makeNewEntities(inputFact, inputProperty,
    // // newPropertyName, number, qp.getMeasurementUnitKey());
    // // // if (newEntities != null) {
    // // // for (ModelEntity newEntity : newEntities) {
    // // // // Logger.getLogger(StringToMeasurementFactGardener.class).info(
    // // // // "output " + newEntity.getKey().toString()); //$NON-NLS-1$
    // // //
    // // // output(newEntity);
    // // // }
    // // // }
    // // }
    // // }
    // // return true;
    // // }
    //
    // /** Given the input, produce some new assertions, if possible */
    // public List<ModelEntity> convert(StringFact inputFact, StringProperty inputProperty) {
    // List<ModelEntity> newEntities = new ArrayList<ModelEntity>();
    //
    // if (this.first) {
    // User user = new User();
    // user.setKey(getCreatorKey());
    // user.setCreatorKey(getCreatorKey());
    // user.setLastModified(getIso8601Date());
    // user.setName(getShortClassName());
    // user.setRealName(getShortClassName());
    // newEntities.add(user);
    // }
    // this.first = false;
    //
    // String serializedMatchString = makeSerializedMatchString(inputFact, inputProperty);
    //
    // if (getPatterns() == null || getPatterns().size() == 0) {
    // Logger.getLogger(StringToMeasurementFactConverter.class).error(
    // "No patterns! " + inputProperty.getClass().getName()); //$NON-NLS-1$
    // }
    //
    // // Try matching against each pattern. My god!
    // for (QuantityPattern qp : getPatterns()) {
    // Pattern p = qp.getPattern();
    // // Logger.getLogger(StringToMeasurementFactGardener.class).info(
    // // "trying pattern " + p.pattern()); //$NON-NLS-1$
    // // Logger.getLogger(StringToMeasurementFactGardener.class).info(
    // // "trying match string " + serializedMatchString); //$NON-NLS-1$
    //
    // Matcher m = p.matcher(serializedMatchString);
    // if (m.matches()) {
    // // Logger.getLogger(StringToMeasurementFactGardener.class).info("matched!");
    // // //$NON-NLS-1$
    // String newPropertyName = NameUtil.decode(m.group(1)).trim();
    // String number = NameUtil.decode(m.group(2)).trim();
    // List<ModelEntity> entities =
    // makeNewEntities(inputFact, inputProperty, newPropertyName, number, qp
    // .getMeasurementUnitKey());
    // if (entities != null) {
    // newEntities.addAll(entities);
    // }
    // // if (newEntities != null) {
    // // for (ModelEntity newEntity : newEntities) {
    // // // Logger.getLogger(StringToMeasurementFactGardener.class).info(
    // // // "output " + newEntity.getKey().toString()); //$NON-NLS-1$
    // //
    // // output(newEntity);
    // // }
    // // }
    // }
    // }
    // return newEntities;
    // }
    //
    // /**
    // * Produce quantity facts and properties. The facts produced will use the expressed unit; If
    // you
    // * want unit normalization, do it somewhere else -- if you don't use the expressed unit here,
    // * you can never find it again (I mean as a unit).
    // *
    // * A unique input Property may only produce one unique output property, with associated
    // * provenance, so we maintain a list of the input properties we've seen (i.e. their keys).
    // *
    // * @param inputFact
    // * @param inputProperty
    // * @param newPropertyName
    // * @param number
    // * @param measurementUnitKey
    // * @return
    // */
    // protected List<ModelEntity> makeNewEntities(StringFact inputFact, StringProperty
    // inputProperty,
    // String newPropertyName, String number, ExternalKey measurementUnitKey) {
    // if (number == null)
    // return null;
    //
    // ExternalKey standardMeasurementUnitKey;
    //
    // if (measurementUnitKey.getType().equals(ExternalKey.AFFINE_MEASUREMENT_UNIT_TYPE)) {
    //
    // ModelEntity e = this.affineMeasurementUnitMap.get(measurementUnitKey);
    //
    // if (e == null) {
    // Logger.getLogger(StringToMeasurementFactConverter.class).info(
    // "null for : " + measurementUnitKey.toString()); //$NON-NLS-1$
    // return null;
    // }
    // if (!(e instanceof AffineMeasurementUnit)) {
    // Logger.getLogger(StringToMeasurementFactConverter.class).info(
    // "bad type: " + e.getClass().getName()); //$NON-NLS-1$
    // return null;
    // }
    // AffineMeasurementUnit affine = (AffineMeasurementUnit) e;
    // standardMeasurementUnitKey = affine.getStandardMeasurementUnitKey();
    // } else if (measurementUnitKey.getType().equals(ExternalKey.STANDARD_MEASUREMENT_UNIT_TYPE)) {
    // standardMeasurementUnitKey = measurementUnitKey;
    // } else {
    // Logger.getLogger(StringToMeasurementFactConverter.class).info(
    // "bad key type: " + measurementUnitKey.getType()); //$NON-NLS-1$
    // return null;
    // }
    //
    // ModelEntity e = this.standardMeasurementUnitMap.get(standardMeasurementUnitKey);
    //
    // if (e == null) {
    // Logger.getLogger(StringToMeasurementFactConverter.class).info(
    // "null for : " + standardMeasurementUnitKey.toString()); //$NON-NLS-1$
    // return null;
    // }
    // if (!(e instanceof StandardMeasurementUnit)) {
    // Logger.getLogger(StringToMeasurementFactConverter.class).info(
    // "bad type: " + e.getClass().getName()); //$NON-NLS-1$
    // return null;
    // }
    // StandardMeasurementUnit unit = (StandardMeasurementUnit) e;
    //
    // ExternalKey measurementQuantityKey = unit.getMeasurementQuantityKey();
    //
    // // ModelEntity mUnit = get(new ReaderConstraint(MeasurementUnit.class), measurementUnitKey);
    // // if (!(mUnit instanceof MeasurementUnit))
    // // return null;
    // // MeasurementUnit unit = (MeasurementUnit) mUnit;
    // //
    // // // ModelEntity mQuantity = get(new ReaderConstraint(MeasurementQuantity.class), unit.get)
    // //
    // // if (!(mUnit instanceof MeasurementQuantity))
    // // return null;
    // // MeasurementQuantity quantity = (MeasurementQuantity) mQuantity;
    //
    // String factNamespace = inputFact.getNamespace();
    // String propertyNamespace = inputProperty.getNamespace();
    //
    // List<ModelEntity> result = new ArrayList<ModelEntity>();
    //
    // QuantityProperty outputProperty = new QuantityProperty();
    // // With the name as the key, it could certainly collide with an existing,
    // // differently-derived key; it's just "ns/quantity/height". So add some more fields.
    // // TODO: a more rigorous version of this, like in NameUtil.
    // String newName = newPropertyName + " for quantity " + measurementQuantityKey.toString()
    // //$NON-NLS-1$
    // + " in domain " + inputProperty.getDomainClassKey().toString(); //$NON-NLS-1$
    // outputProperty.setKey(new ExternalKey(propertyNamespace,
    // ExternalKey.QUANTITY_PROPERTY_TYPE, newName));
    // outputProperty.setCreatorKey(getCreatorKey());
    // outputProperty.setLastModified(getIso8601Date());
    // outputProperty.setDomainClassKey(inputProperty.getDomainClassKey());
    // outputProperty.setMeasurementQuantityKey(measurementQuantityKey);
    // outputProperty.setName(newPropertyName);
    //
    // String ppNamespace = inputProperty.getNamespace();
    // DerivedProvenance propertyProvenance =
    // new DerivedProvenance(outputProperty.getKey(), inputProperty.getKey(),
    // getShortClassName(), ppNamespace);
    //
    // // Only produce output if it's unique.
    // if (!(getPropertyKeys().contains(inputProperty.getKey()))) {
    // result.add(outputProperty);
    // result.add(propertyProvenance);
    // getPropertyKeys().add(inputProperty.getKey());
    // }
    //
    // QuantityFact outputFact = new QuantityFact();
    //
    // outputFact.setCreatorKey(getCreatorKey());
    // outputFact.setLastModified(getIso8601Date());
    // outputFact.setSubjectKey(inputFact.getSubjectKey());
    // outputFact.setPropertyKey(outputProperty.getKey());
    // outputFact.setExpressedUnitKey(measurementUnitKey);
    // outputFact.setValue(Double.valueOf(number));
    // outputFact.setName(new String());
    // outputFact.setKey(outputFact.compositeKey(factNamespace));
    //
    // result.add(outputFact);
    //
    // DerivedProvenance factProvenance = new DerivedProvenance();
    // String pNamespace = inputFact.getNamespace();
    // String name =
    // outputFact.getKey().toString() + " derived from " + inputFact.getKey().toString();
    // //$NON-NLS-1$
    // factProvenance
    // .setKey(new ExternalKey(pNamespace, ExternalKey.DERIVED_PROVENANCE_TYPE, name));
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
    // protected List<QuantityPattern> getPatterns() {
    // return this.patterns;
    // }
    //
    // protected void setPatterns(List<QuantityPattern> patterns) {
    // this.patterns = patterns;
    // }
    //
    // protected void addPattern(QuantityPattern p) {
    // getPatterns().add(p);
    // }
    //
    // public Set<ExternalKey> getPropertyKeys() {
    // return this.propertyKeys;
    // }
    //
    // public void setPropertyKeys(Set<ExternalKey> propertyKeys) {
    // this.propertyKeys = propertyKeys;
    // }
}
