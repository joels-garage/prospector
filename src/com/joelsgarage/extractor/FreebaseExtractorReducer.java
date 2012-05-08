/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Fact;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualFact;
import com.joelsgarage.model.IndividualProperty;
import com.joelsgarage.model.Log;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Property;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.QuantityProperty;
import com.joelsgarage.model.StringFact;
import com.joelsgarage.model.StringProperty;
import com.joelsgarage.model.Subclass;
import com.joelsgarage.model.WriteEvent;
import com.joelsgarage.model.WordSense;
import com.joelsgarage.util.FatalException;

/**
 * Process a bundle of freebase quadruple rows. It's runnable so we can do the processing while
 * reading more rows.
 * 
 * It's kinda like a reducer but not exactly.
 * 
 * @author joel
 * 
 */
public class FreebaseExtractorReducer implements Runnable {
    /** The namespace everything goes in. f0 = 20081017 */
    private static final String NAMESPACE = "f0"; //$NON-NLS-1$

    private List<String[]> rows = new ArrayList<String[]>();

    private FreebaseExtractor extractor;

    /**
     * Domain whitelist; ignore instances outside this set.
     * 
     * May contain single-field keys (e.g. "film") as well as two-field keys (e.g. "common/topic").
     * 
     * TODO: externalize this config somehow
     */
    @SuppressWarnings("nls")
    private static final Set<String> domains = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            // the ones we want
            add("automotive");
            // also /people/person and /business/company and /business/employer
            add("dining");
            // also /people/person and /business/business_location
            add("film");
            // also /people/person, /fictional_universe/fictional_character, /business/company,
            // /business/employer, /time/event, /time/recurring_event, /media_common/media_genre
            add("tv");
            // also /people/person, /fictional_universe/fictional_character
            // /media_common/media_genre
            add("wine");
            // also /location/location, /business/company and /business/employer

            // instances of one of the above can also be one of the below, which I want
            add("people"); // person
            add("business"); // company and employer
            add("fictional_universe"); // fictional_character
            add("time"); // event and recurring_event
            add("media_common"); // media_genre
            add("location"); // location
            add("measurement_unit");
            add("common/phone_number");
        }
    };

    /**
     * These are bundle types that require us to look at the whole bundle. For facts, we just look
     * at a row at a time, so there's no fact outcome. other record types (e.g. class member) are
     * implicit in other bundle types (e.g. instance in that case)
     */
    protected enum Outcome {
        /** bundle represents a property */
        PROPERTY,
        /** bundle is an instance we want */
        INSTANCE,
        /** bundle represents a class */
        CLASS
    }

    public FreebaseExtractorReducer(FreebaseExtractor extractor, List<String[]> rows) {
        setExtractor(extractor);
        setRows(rows);
    }

    @Override
    public void run() {
        try {
            flushRows();
        } catch (FatalException e) {
            e.printStackTrace();
        }
    }

    protected void output(ModelEntity payload) {
        getExtractor().output(payload);
    }

    protected Double parseDouble(String value) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        try {
            Number quantity = nf.parse(value);
            return Double.valueOf(quantity.doubleValue());
        } catch (ParseException e) {
            Logger.getLogger(FreebaseExtractorReducer.class).debug("not a number value: " + value); //$NON-NLS-1$
            return null;
        }
    }

    @SuppressWarnings("nls")
    protected void flushRows() throws FatalException {
        if (getRows().isEmpty())
            return;

        Outcome outcome = null;
        // do i need a set of outcomes rather than just one?

        // This is the guid in the first column.
        // Should be the same for all rows in the bundle.
        String src = getRows().get(0)[0].trim();

        for (int index = 0; index < getRows().size(); ++index) {
            Logger.getLogger(FreebaseExtractorReducer.class).debug(
                "index: " + String.valueOf(index)); //$NON-NLS-1$

            String[] row = getRows().get(index);
            if (!(row[0].trim().equals(src))) {
                Logger.getLogger(FreebaseExtractorReducer.class).info(
                    "mismatched row key: " + String.valueOf(row[0].trim())); //$NON-NLS-1$
                break;
            }
            String property = row[1].trim();
            String value = row[2].trim();
            Logger.getLogger(FreebaseExtractorReducer.class).debug(
                "property: " + String.valueOf(property)); //$NON-NLS-1$
            Logger.getLogger(FreebaseExtractorReducer.class).debug(
                "value: " + String.valueOf(value)); //$NON-NLS-1$

            // TODO: better string matching
            if (property == null || value == null) {
                break;
            }
            if (property.equals("/type/object/type")) {
                if (value.equals("/type/property")) {
                    // /type/object/type == /type/property: The bundle describes a property, so
                    // should
                    // produce a Property record
                    Logger.getLogger(FreebaseExtractorReducer.class).debug("found property"); //$NON-NLS-1$
                    outcome = Outcome.PROPERTY;
                    // i think "break" is ok here
                } else if (value.equals("/type/type")) {
                    // /type/object/type == /type/type: The bundle describes a type, i.e. a Class
                    // record.
                    Logger.getLogger(FreebaseExtractorReducer.class).debug("found class"); //$NON-NLS-1$

                    outcome = Outcome.CLASS;
                    // i think "break" is ok here

                } else {
                    // Maybe this row describes an instance of a whitelisted domain
                    // check one of the whitelisted domains
                    if (allowedDomain(value)) {
                        Logger.getLogger(FreebaseExtractorReducer.class).debug("found individual"); //$NON-NLS-1$

                        outcome = Outcome.INSTANCE;

                        // every instance row is a classmember record.

                        ClassMember classMember =
                            new ClassMember(makeIndividualKeyFromGuid(src),
                                makeClassKeyFromType(value), namespace());
                        output(classMember);
                        output(new Log(classMember.makeKey(), getUpdate().makeKey(), namespace()));

                        // there are also facts in this bundle we should get, so we continue and
                        // get them below.
                    }
                }
            } else {
                // Maybe this row is a fact in a whitelisted domain. check the domain of the
                // property
                if (property.charAt(0) == '/') {
                    String domain = property.substring(1, property.indexOf('/', 1));
                    if (domains.contains(domain)) {
                        Logger.getLogger(FreebaseExtractorReducer.class).debug("found fact"); //$NON-NLS-1$
                        // it's a fact we want. now what type is it?
                        Fact fact = null;
                        if (value.equals("/lang/en") && row.length >= 4) {
                            Logger.getLogger(FreebaseExtractorReducer.class).debug(
                                "found string fact"); //$NON-NLS-1$
                            String factValue = row[3].trim();
                            fact =
                                new StringFact(makeIndividualKeyFromGuid(src),
                                    makeStringPropertyKeyFromProperty(property), factValue,
                                    namespace());
                        } else if (value.length() > 6 && value.substring(0, 6).equals("/guid/")) {
                            Logger.getLogger(FreebaseExtractorReducer.class).debug(
                                "found individual fact"); //$NON-NLS-1$
                            fact =
                                new IndividualFact(makeIndividualKeyFromGuid(src),
                                    makeIndividualPropertyKeyFromProperty(property),
                                    makeIndividualKeyFromGuid(value), namespace());
                        } else if (value.isEmpty() && row.length >= 4) {
                            // if there's no lang field then it's a non-localized thing, i.e. either
                            // a quantity or symbolic string property (e.g. a code of some kind).
                            String quantityString = row[3].trim();
                            Logger
                                .getLogger(FreebaseExtractorReducer.class)
                                .debug(
                                    "found nonlocal fact for property: " + property + " quantity: " + quantityString); //$NON-NLS-1$
                            // TODO: units
                            Double quantity = parseDouble(quantityString);
                            if (quantity == null) {
                                fact =
                                    new StringFact(makeIndividualKeyFromGuid(src),
                                        makeStringPropertyKeyFromProperty(property),
                                        quantityString, namespace());
                            } else {
                                fact =
                                    new QuantityFact(makeIndividualKeyFromGuid(src),
                                        makeStringPropertyKeyFromProperty(property), quantity,
                                        null, namespace());
                            }
                        } else {
                            Logger.getLogger(FreebaseExtractorReducer.class).info(
                                "weird row property: " + property + " value: " + value); //$NON-NLS-1$
                        }
                        if (fact != null) {
                            output(fact);
                            output(new Log(fact.makeKey(), getUpdate().makeKey(), namespace()));
                            // TODO: make WordSense and StringFact work together somehow.

                            // if (name != null) {
                            // WordSense wordSense =
                            // new WordSense("en-US", name, true, fact.makeKey(), namespace());
                            // output(wordSense);
                            // output(new Log(wordSense.makeKey(), getUpdate().makeKey(),
                            // namespace()));
                            // }
                        }
                    }
                }
            }
        }
        Logger.getLogger(FreebaseExtractorReducer.class).debug(
            "outcome: " + String.valueOf(outcome)); //$NON-NLS-1$

        if (outcome != null) {
            if (outcome == Outcome.PROPERTY) {
                Logger.getLogger(FreebaseExtractorReducer.class).debug("working on property"); //$NON-NLS-1$
                boolean isString = false;
                boolean isQuantity = false;
                boolean isIndividual = false;
                String schema = null;
                String name = null;
                String key = null;
                String rangeValue = null; // range of individualProperty

                for (int index = 0; index < getRows().size(); ++index) {
                    String[] row = getRows().get(index);
                    String property = row[1].trim();
                    String value = row[2].trim();
                    if (property.equals("/type/property/schema")) {
                        if (value.charAt(0) == '/') {
                            String domain = value.substring(1, value.indexOf('/', 1));
                            if (domains.contains(domain)) {
                                schema = value;
                            } else {
                                // we don't want this domain, ignore the property entirely.
                                break;
                            }
                        }
                    } else if (property.equals("/type/property/expected_type")) {
                        // this determines the type of outputProperty
                        // TODO: more efficient string matching
                        if (value.equals("/type/float") //
                            || value.equals("/type/int")) {
                            isQuantity = true;
                        } else if (value.equals("/type/text")) {
                            isString = true;
                        } else {
                            isIndividual = true;
                            rangeValue = value;
                        }
                    } else if (property.equals("/type/object/key") && row.length >= 4) {
                        // TODO: do I need this? It was used in "name".
                        // yes! multiple properties with same domain and range are possible.
                        key = value + "/" + row[3].trim();
                    } else if (property.equals("/type/object/name") && value.equals("/lang/en")
                        && row.length >= 4) {
                        // TODO: non-English names
                        name = row[3].trim();
                    }
                }
                if (schema != null && key != null) {
                    Property outputProperty = null;
                    if (isIndividual && rangeValue != null) {
                        outputProperty =
                            new IndividualProperty(makeClassKeyFromType(schema), key,
                                makeClassKeyFromType(rangeValue), namespace());
                    } else if (isQuantity) {
                        // TODO: fill in measurement fields
                        outputProperty =
                            new QuantityProperty(makeClassKeyFromType(schema), key, null, null,
                                namespace());
                    } else if (isString) {
                        outputProperty =
                            new StringProperty(makeClassKeyFromType(schema), key, namespace());
                    }

                    if (outputProperty != null) {
                        output(outputProperty);
                        output(new Log(outputProperty.makeKey(), getUpdate().makeKey(), namespace()));

                        if (name != null) {
                            // the name is in a separate message
                            // TODO: generalize language
                            WordSense wordSense =
                                new WordSense("en-US", name, true, outputProperty.makeKey(),
                                    namespace());
                            output(wordSense);
                            output(new Log(wordSense.makeKey(), getUpdate().makeKey(), namespace()));
                        }
                    }
                }

            } else if (outcome == Outcome.INSTANCE) {
                Logger.getLogger(FreebaseExtractorReducer.class).debug("working on instance"); //$NON-NLS-1$

                // just output the individual once
                Individual individual = makeIndividualFromGuid(src);
                output(new Log(individual.makeKey(), getUpdate().makeKey(), namespace()));
                output(individual);

            } else if (outcome == Outcome.CLASS) {
                Logger.getLogger(FreebaseExtractorReducer.class).debug("working on class"); //$NON-NLS-1$
                String key = null;
                String name = null;
                List<ExternalKey> subclassObjects = new ArrayList<ExternalKey>();

                for (int index = 0; index < getRows().size(); ++index) {

                    String[] row = getRows().get(index);
                    String property = row[1].trim();
                    String value = row[2].trim();

                    if (property.equals("/type/object/key") && row.length >= 4) {
                        Logger.getLogger(FreebaseExtractorReducer.class).debug("found key"); //$NON-NLS-1$
                        key = value + "/" + row[3].trim();
                    } else if (property.equals("/type/object/name") && value.equals("/lang/en")
                        && row.length >= 4) {
                        Logger.getLogger(FreebaseExtractorReducer.class).debug("found name"); //$NON-NLS-1$
                        // TODO: non-English names
                        name = row[3].trim();
                    } else if (property.equals("/freebase/type_hints/included_types")) {
                        Logger.getLogger(FreebaseExtractorReducer.class).debug("found subclass"); //$NON-NLS-1$

                        if (allowedDomain(value)) {
                            // add only subclasses within allowed domains
                            subclassObjects.add(makeClassKeyFromType(value));
                        }
                    }
                }
                if (key != null && allowedDomain(key)) {
                    // ignore keys outside allowed domains
                    com.joelsgarage.model.Class clazz = makeClassFromType(key);

                    output(clazz);
                    output(new Log(clazz.makeKey(), getUpdate().makeKey(), namespace()));

                    WordSense wordSense = null;

                    if (name != null) {
                        wordSense =
                            new WordSense("en-US", name, true, clazz.makeKey(), namespace());
                    } else {
                        // last resort is to use the key as the name. :-(
                        wordSense = new WordSense("en-US", key, true, clazz.makeKey(), namespace());
                    }
                    output(wordSense);
                    output(new Log(wordSense.makeKey(), getUpdate().makeKey(), namespace()));

                    // output any accumulated subclasses
                    for (ExternalKey objectKey : subclassObjects) {
                        Subclass subclass = new Subclass(clazz.makeKey(), objectKey, namespace());
                        output(subclass);
                        output(new Log(subclass.makeKey(), getUpdate().makeKey(), namespace()));
                    }
                }
            }
        }
        getRows().clear();
    }

    /**
     * An individual's name is its guid
     * 
     * @throws FatalException
     */
    protected Individual makeIndividualFromGuid(String guid) throws FatalException {
        return new Individual(guid, namespace());
    }

    protected ExternalKey makeIndividualKeyFromGuid(String guid) throws FatalException {
        return new Individual(guid, namespace()).makeKey();
    }

    protected ExternalKey makeStringPropertyKeyFromProperty(String property) throws FatalException {
        if (property == null)
            return null;
        int separatorIndex = property.lastIndexOf('/');
        String domainName = property.substring(0, separatorIndex);
        String propertyName = property.substring(separatorIndex + 1);

        com.joelsgarage.model.Class domainClass = makeClassFromType(domainName);

        return new StringProperty(domainClass.makeKey(), propertyName, namespace()).makeKey();
    }

    /**
     * Produce the key for this property; the actual property can't be determined because we don't
     * know the range.
     * 
     * @throws FatalException
     */
    protected ExternalKey makeIndividualPropertyKeyFromProperty(String property)
        throws FatalException {
        // the property is /<type>/<property>
        // where <type> may have slashes but (i hope) <property> does not.
        if (property == null)
            return null;
        int separatorIndex = property.lastIndexOf('/');
        String domainName = property.substring(0, separatorIndex);
        String propertyName = property.substring(separatorIndex + 1);

        return new IndividualProperty(makeClassFromType(domainName).makeKey(), propertyName, null,
            namespace()).makeKey();
    }

    /**
     * A class name is its type (e.g. /dining/restaurant), not the guid
     * 
     * @throws FatalException
     */
    protected com.joelsgarage.model.Class makeClassFromType(String type) throws FatalException {
        return new com.joelsgarage.model.Class(type, namespace());
    }

    protected ExternalKey makeClassKeyFromType(String type) throws FatalException {
        return new com.joelsgarage.model.Class(type, namespace()).makeKey();
    }

    /** Is the key prefix one of the allowed ones? */
    protected boolean allowedDomain(String key) {
        if (key.length() < 1)
            return false;
        // must start with slash
        if (key.charAt(0) != '/')
            return false;
        // is the one-field key (e.g. "/foo/") allowed?
        int delimiterIndex = key.indexOf('/', 1);
        if (delimiterIndex == -1)
            return false;
        String domain = key.substring(1, delimiterIndex);
        if (domains.contains(domain))
            return true;
        // if not, then maybe the two-field key (e.g. "/foo/bar/") *is* allowed
        if (key.length() < delimiterIndex + 1)
            return false;
        delimiterIndex = key.indexOf('/', delimiterIndex + 1);
        if (delimiterIndex == -1)
            return false;
        String extendedDomain = key.substring(1, delimiterIndex);
        if (domains.contains(extendedDomain))
            return true;
        return false;
    }

    protected String namespace() {
        return NAMESPACE;
    }

    public List<String[]> getRows() {
        return this.rows;
    }

    public void setRows(List<String[]> rows) {
        this.rows = rows;
    }

    public FreebaseExtractor getExtractor() {
        return this.extractor;
    }

    public void setExtractor(FreebaseExtractor extractor) {
        this.extractor = extractor;
    }

    // delegate
    public WriteEvent getUpdate() {
        return getExtractor().getUpdate();
    }

}
