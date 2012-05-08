/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.query;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.StringFact;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.Membership;
import com.joelsgarage.util.Norm;

/**
 * This takes a JSON tree query and matches it against a big graph, starting at a specified "root"
 * node.
 * 
 * @author joel
 */
public class Matcher {
    private RecordFetcher<ExternalKey, ModelEntity> fetcher;
    private Norm norm;

    /**
     * @param fetcher
     *            access to the db
     * @param tNorm
     *            aggregator for this matcher
     */
    public Matcher(RecordFetcher<ExternalKey, ModelEntity> fetcher, Norm norm) {
        setFetcher(fetcher);
        setNorm(norm);
    }

    public Membership matchArray(String query, ExternalKey root, String namespace)
        throws QueryException {
        try {
            JSONArray ja = new JSONArray(query);
            if (ja.length() == 0)
                return Membership.TRUE;
            JSONObject jo = ja.getJSONObject(0);
            Membership result = match(jo.toString(2), root, namespace);
            for (int index = 1; index < ja.length(); ++index) {
                jo = ja.getJSONObject(index);
                result = getNorm().f(result, match(jo.toString(2), root, namespace));
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new QueryException("crap query: " + query); //$NON-NLS-1$
        }
    }

    /**
     * Match the specified query against the graph starting at root. The query should be an Object,
     * since it's matching a single object.
     * 
     * The keys of the object correspond to properties. The values, to fact values for those
     * properties.
     * 
     * There's a special key corresponding to ClassMember, at least until I get around to making
     * ClassMember into a property.
     * 
     * The keys may be prepended with "!" which means "reverse."
     * 
     * The keys may have operators appended (see above).
     * 
     * The value may be an object, or array containing an object, corresponding to an unbound
     * Individual, which would be recursively matched.
     * 
     * The value may be an array of primitives. That only occurs in MQL with the "one of" operator, |=
     * which says "this name may be bound to any one of the following values". I don't need that, so
     * arrays of primitives are disallowed.
     * 
     * ISSUES
     * 
     * How are properties identified? MQL uses "name" (e.g. '/person/gender') which includes both
     * domain type (the ROOT type not the matched type) and the name. My domain key includes both of
     * these as well, so I could make the same requirement, e.g. require a fully qualified
     * "classname/propname" ... the problem is that the name is nowhere recorded, neither in class
     * nor property. :-( it would be easier if it were (and simple enough). On the other hand I
     * could just use keys. It's equivalent; it's just kinda opaque. Also note that in JSON a
     * property name cannot be an object; it's a string only, and so can't be unbound (e.g. to look
     * up it's NLS name in WordSense).
     * 
     * Probably the easiest thing would be to allow *both* the serialized ExternalKey *and* the
     * classname/propertyname combination. First the former, since it's easy and it's what I do now,
     * and then later the latter after I think of a reason to do it.
     * 
     * The distinguishing feature of classname/propertyname is that it starts with a slash (e.g.
     * "/album/track"), whereas the ExternalKey property never does.
     * 
     * TODO: use the "limit" thing to control what the processor does.
     * 
     * @param query
     *            an array of scoring filters
     * @param root
     *            the individual to match -- it's just the key, so you don't need to actually fetch
     *            the individual.
     * @param namespace
     *            A horrible hack. The namespace to look in. TODO: find a way around it.
     * @return the fuzzy membership of the root with respect to the query. Never returns null.
     * @throws QueryException
     *             if the query is crap
     */
    public Membership match(String query, ExternalKey root, String namespace) throws QueryException {
        try {
            if (query == null) {
                Logger.getLogger(Matcher.class).error("Null query"); //$NON-NLS-1$
                throw new QueryException("Null query"); //$NON-NLS-1$
            }

            Logger.getLogger(Matcher.class).info("query:" + query); //$NON-NLS-1$
            JSONObject jo = new JSONObject(query);
            if (jo.length() == 0)
                return Membership.TRUE;

            String[] keys = JSONObject.getNames(jo);
            if (keys == null)
                return Membership.TRUE;
            if (keys.length == 0)
                return Membership.TRUE;
            // default mappings
            Membership outM = Membership.FALSE;
            Membership inM = Membership.TRUE;
            JSONArray scoreArray = jo.optJSONArray(PropertyRestriction.SCORE);
            if (scoreArray != null) {
                double outScore = scoreArray.optDouble(0, 0.0);
                double inScore = scoreArray.optDouble(1, 1.0);
                Logger.getLogger(Matcher.class).info("found score [" + String.valueOf(outScore) + //$NON-NLS-1$
                    ", " + String.valueOf(inScore) + //$NON-NLS-1$
                    "]"); //$NON-NLS-1$
                inM = Membership.newInstance(inScore);
                outM = Membership.newInstance(outScore);
            }

            Membership result = handleQueryProperty(root, jo, keys[0], namespace);
            for (int index = 1; index < keys.length; ++index) {
                result = getNorm().f(result, handleQueryProperty(root, jo, keys[index], namespace));
                Logger.getLogger(Matcher.class).info("got result"); //$NON-NLS-1$
            }
            // now the result [0,1] is scaled to the bounds [outM, inM].
            return scale(result, outM, inM);
        } catch (JSONException e) {
            Logger.getLogger(Matcher.class).error("Bad query: " + query); //$NON-NLS-1$
            throw new QueryException(e);
        }
    }

    protected static Membership scale(Membership input, Membership lower, Membership upper)
        throws QueryException {
        if (lower.compareTo(upper) > 0)
            throw new QueryException("bad scale bounds"); //$NON-NLS-1$
        double offset = lower.getM().doubleValue();
        double range = upper.getM().doubleValue() - offset;
        return Membership.newInstance(input.getM().doubleValue() * range + offset);
    }

    protected Membership handleQueryProperty(ExternalKey root, JSONObject jo, String key,
        String namespace) throws QueryException, JSONException {
        if (key.equals(PropertyRestriction.CLASS_MEMBER_PROPERTY)) {
            return handleClassMember(root, jo.getString(key), namespace);
        } else if (key.equals(PropertyRestriction.LIMIT)) {
            // limit is handled up in the scorer.
        } else if (key.equals(PropertyRestriction.SCORE)) {
            // TODO:
            // score means that other matching properties should be scaled using these bounds. So
            // it's not the right interface; it should return the bounds.
            // JSONArray ja = jo.optJSONArray(key);

            // do something here

            //

            //
        } else {
            PropertyRestriction restriction = PropertyRestriction.newInstance(key);
            String property = restriction.property;
            // what kind of property is it?
            if (property.charAt(0) == '/') {
                // it's /classname/propertyname
                throw new QueryException("I don't yet support /classname/propertyname"); //$NON-NLS-1$
            }
            ExternalKey propertyKey = ExternalKey.newInstance(property);
            if (propertyKey == null)
                throw new QueryException("can't parse property: " + property); //$NON-NLS-1$
            if (propertyKey.typeEquals(ExternalKey.STRING_PROPERTY_TYPE)) {
                if (restriction.reverse) {
                    throw new QueryException("can't reverse string property: " + property); //$NON-NLS-1$
                }
                if (!(restriction.operator.equals(Operator.EQUALS))) {
                    throw new QueryException(
                        "only EQUALS allowed with string property: " + property); //$NON-NLS-1$
                }
                return handleStringProperty(root, propertyKey, jo.getString(key), namespace);
            } else if (propertyKey.typeEquals(ExternalKey.QUANTITY_PROPERTY_TYPE)) {
                if (restriction.reverse) {
                    throw new QueryException("can't reverse quantity property: " + property); //$NON-NLS-1$
                }
                return handleQuantityProperty(root, jo.getString(key), restriction.operator,
                    namespace);
            } else if (propertyKey.typeEquals(ExternalKey.INDIVIDUAL_PROPERTY_TYPE)) {
                if (!(restriction.operator.equals(Operator.EQUALS))) {
                    throw new QueryException(
                        "only EQUALS allowed with string property: " + property); //$NON-NLS-1$
                }
                JSONArray ja = jo.optJSONArray(key);
                if (ja != null) {
                    return handleIndividualPropertyArray(root, ja, restriction.reverse, namespace);
                }
                return handleIndividualProperty(root, jo.getString(key), restriction.reverse,
                    namespace);
            } else {
                throw new QueryException("unsupported property type: " + propertyKey.getType()); //$NON-NLS-1$
            }
        }
        return Membership.TRUE; // FIXME: ??
    }

    /**
     * Check the class membership of an individual.
     * 
     * @param root
     *            the individual whose membership to check
     * @param valueStr
     *            the serialized key of the class to check
     * @param namespace
     *            the namespace of the membership (FIXME)
     * @return Membership.TRUE if the root individual is a member of the class described by
     *         valueStr, otherwise Membership.FALSE.
     * @throws QueryException
     *             if something screwy happens with keys
     */
    protected Membership handleClassMember(ExternalKey root, String valueStr, String namespace)
        throws QueryException {
        try {
            // the key is the class member property, so the value should be a class
            ExternalKey classKey = ExternalKey.newInstance(valueStr);
            if (classKey == null)
                throw new QueryException("null key"); //$NON-NLS-1$
            // gah. which namespace?
            // maybe the individual's namespace as a convention?
            // maybe it could be specified in the query?
            ClassMember cm = new ClassMember(root, classKey, namespace);
            ExternalKey testKey = cm.makeKey();
            Logger.getLogger(Matcher.class).info("looking at key: " + testKey.toString()); //$NON-NLS-1$
            Logger.getLogger(Matcher.class).info("looking at root: " + root.toString()); //$NON-NLS-1$
            ModelEntity me = getFetcher().get(testKey);
            if (me == null) {
                Logger.getLogger(Matcher.class).info("no classmember " + testKey.toString()); //$NON-NLS-1$
                return Membership.FALSE;
            }
            return Membership.TRUE;
        } catch (FatalException e) {
            throw new QueryException(e);
        }
    }

    /**
     * Verify that the specified String Fact exists.
     * 
     * Note if you use a property that doesn't exist, you get a Query Exception.
     * 
     * @param root
     *            subject of the string fact
     * @param propertyKey
     *            property of the string fact
     * @param valueStr
     *            value of the string fact
     * @return TRUE if a string fact with the specified subject and property exist
     * @throws QueryException
     */
    protected Membership handleStringProperty(ExternalKey root, ExternalKey propertyKey,
        String valueStr, String namespace) throws QueryException {
        try {
            if (valueStr == null)
                throw new QueryException("null value"); //$NON-NLS-1$

            if (getFetcher().get(propertyKey) == null)
                throw new QueryException("bad property: " + propertyKey.toString()); //$NON-NLS-1$

            StringFact probe = new StringFact(root, propertyKey, valueStr, namespace);

            ModelEntity me = getFetcher().get(probe.makeKey());
            if (me == null)
                return Membership.FALSE;
            return Membership.TRUE;

            // Another way to do it, which requires secondary keys
            // Map<String, Object> queryTerms = new HashMap<String, Object>();
            // queryTerms.put(Fact.SUBJECT, root);
            // queryTerms.put(Fact.PROPERTY, propertyKey);
            // StringFact sf = getFetcher().getCompound(StringFact.class, queryTerms);
            // if (sf == null) {
            // Logger.getLogger(Matcher.class).info("can't find stringfact"); //$NON-NLS-1$
            // return Membership.FALSE;
            // }
            // if (valueStr.equals(sf.getValue()))
            // return Membership.TRUE;
            // return Membership.FALSE;

        } catch (FatalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new QueryException(e);
        }

    }

    protected Membership handleQuantityProperty(ExternalKey root, String valueStr, Operator op,
        String namespace) throws QueryException {
        return null;
    }

    protected Membership handleIndividualPropertyArray(ExternalKey root, JSONArray valueArray,
        boolean reverse, String namespace) throws QueryException {
        return null;
    }

    protected Membership handleIndividualProperty(ExternalKey root, String valueStr,
        boolean reverse, String namespace) throws QueryException {
        return null;
    }

    //
    //

    public RecordFetcher<ExternalKey, ModelEntity> getFetcher() {
        return this.fetcher;
    }

    public void setFetcher(RecordFetcher<ExternalKey, ModelEntity> fetcher) {
        this.fetcher = fetcher;
    }

    public Norm getNorm() {
        return this.norm;
    }

    public void setNorm(Norm norm) {
        this.norm = norm;
    }

}
