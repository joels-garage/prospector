/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.NameUtil;

/**
 * Key type for references, including references in external URLs.
 * 
 * In brief, this is a string key derived exclusively from the data source (thus "external"), so
 * that the loader can generate unique keys by itself, i.e. the "central" database id scheme is no
 * longer exported anywhere.
 * 
 * It could be a composite key, e.g. namespace + type + key, e.g. linkedin + individual + joel, so
 * that the serialized escaped URL form of the key is not in the DB. This seems just as easy to
 * generate, actually, and it removes the odd duplication with the "namespace" field in ModelEntity.
 * 
 * See http://sites.google.com/a/joelsgarage.com/wik/Home/design/keys-for-loading-and-serving
 * 
 * Maps as a <component> in Hibernate. (Can't use a custom value type without separating the GWT
 * model from the Hibernate model.)
 * 
 * Note: I've read that there is a bug in Hibernate that seems to prevent the use of a component as
 * a natural-id, which is what we should really do.
 * 
 * TODO(joel): after that bug is fixed, add the natural-id to ModelEntity.hbm.xml
 * 
 * @author joel
 */
public final class ExternalKey implements Serializable, Comparable<ExternalKey> {
    private static final long serialVersionUID = 1L;

    /** Used to separate the fields in the serialized key; see toString() below. */
    public static final String FIELD_DELIMITER = "/"; //$NON-NLS-1$

    /**
     * The set of allowed types. These are human-readable strings that are visible in the db and in
     * urls. This could just be the (short) classname of the referent.
     * 
     * Do I really need this list?
     * 
     * Should it go somewhere else (e.g. some "validator" class? It's certainly OK to have crap
     * values in here, i.e. nothing will break.
     * 
     * Maybe use a static method that produces a key with the type filled in, e.g. newFactKey()?
     * 
     * Maybe write checker methods (e.g. isFactKey()) or a separate static validator class, with the
     * same methods, so that referring classes can check.
     */

    public static final String AFFINE_MEASUREMENT_UNIT_TYPE = "affine_measurement_unit"; //$NON-NLS-1$
    public static final String CLASS_AXIOM_TYPE = "class_axiom"; //$NON-NLS-1$
    public static final String CLASS_MEMBER_TYPE = "class_member"; //$NON-NLS-1$
    public static final String CLASS_PREFERENCE_TYPE = "class_preference"; //$NON-NLS-1$
    public static final String CLASS_TYPE = "class"; //$NON-NLS-1$
    public static final String COMMENT_TYPE = "comment"; //$NON-NLS-1$
    public static final String DECISION_TYPE = "decision"; //$NON-NLS-1$
    public static final String DERIVED_PROVENANCE_TYPE = "derived_provenance"; //$NON-NLS-1$
    public static final String DESCRIPTION_TYPE = "description"; //$NON-NLS-1$
    public static final String FACT_TYPE = "fact"; //$NON-NLS-1$
    public static final String INDIVIDUAL_FACT_TYPE = "individual_fact"; //$NON-NLS-1$
    public static final String INDIVIDUAL_TYPE = "individual"; //$NON-NLS-1$
    public static final String INDIVIDUAL_PREFERENCE_TYPE = "individual_preference"; //$NON-NLS-1$
    public static final String INDIVIDUAL_PROPERTY_TYPE = "individual_property"; //$NON-NLS-1$
    public static final String INDIVIDUAL_PROPERTY_PREFERENCE_TYPE =
        "individual_property_preference"; //$NON-NLS-1$
    public static final String INDIVIDUAL_PROPERTY_UTILITY_TYPE = "individual_property_utility"; //$NON-NLS-1$
    public static final String LOG_TYPE = "log"; //$NON-NLS-1$
    public static final String MAXIMIZER_QUANTITY_PROPERTY_UTILITY =
        "maximizer_quantity_property_utility"; //$NON-NLS-1$
    public static final String MEASUREMENT_UNIT_TYPE = "measurement_unit"; //$NON-NLS-1$
    public static final String MEASUREMENT_QUANTITY_TYPE = "measurement_quantity"; //$NON-NLS-1$
    public static final String MINIMIZER_QUANTITY_PROPERTY_UTILITY =
        "minimizer_quantity_property_utility"; //$NON-NLS-1$
    public static final String MODEL_ENTITY_TYPE = "model_entity"; //$NON-NLS-1$
    public static final String OPTIMIZER_MEASUREMENT_PROPERTY_UTILITY =
        "optimizer_measurement_property_utility"; //$NON-NLS-1$
    public static final String OPTIMIZER_QUANTITY_PROPERTY_UTILITY =
        "optimizer_quantity_property_utility"; //$NON-NLS-1$
    public static final String INDIVIDUAL_UTILITY_TYPE = "individual_utility"; //$NON-NLS-1$
    public static final String PREFERENCE_TYPE = "preference"; //$NON-NLS-1$
    public static final String PROPERTY_PREFERENCE_TYPE = "property_preference"; //$NON-NLS-1$
    public static final String PROPERTY_TYPE = "property"; //$NON-NLS-1$
    public static final String PROPERTY_WEIGHT_TYPE = "property_weight"; //$NON-NLS-1$
    public static final String QUANTITY_FACT_TYPE = "quantity_fact"; //$NON-NLS-1$
    public static final String QUANTITY_PROPERTY_TYPE = "quantity_property"; //$NON-NLS-1$
    public static final String QUANTITY_PROPERTY_UTILITY_TYPE = "quantity_property_utility"; //$NON-NLS-1$
    public static final String RATING_TYPE = "rating"; //$NON-NLS-1$
    public static final String SCENE_TYPE = "scene"; //$NON-NLS-1$
    public static final String STAKEHOLDER_TYPE = "stakeholder"; //$NON-NLS-1$
    public static final String STANDARD_MEASUREMENT_UNIT_TYPE = "standard_measurement_unit"; //$NON-NLS-1$
    public static final String STRING_FACT_TYPE = "string_fact"; //$NON-NLS-1$
    public static final String STRING_PROPERTY_TYPE = "string_property"; //$NON-NLS-1$
    public static final String SUBCLASS_TYPE = "subclass"; //$NON-NLS-1$
    public static final String WRITE_EVENT_TYPE = "write_event"; //$NON-NLS-1$
    public static final String USER_TYPE = "user"; //$NON-NLS-1$
    public static final String UNIT_SYNONYM_TYPE = "unit_synonym"; //$NON-NLS-1$
    public static final String WORD_SENSE_TYPE = "word_sense"; //$NON-NLS-1$

    // TODO: make some more of these.

    /**
     * Some entities are (transformed) cache entries; this field denotes the source site. It's the
     * site domain name, I guess, though it doesn't have to be. e.g. www.linkedin.com.
     * 
     * A null or blank namespace is bad, I think.
     */
    // private String namespace = new String();
    /**
     * Like "individual" or "fact". Enables referencing class accessors to do type-checking. This
     * could be an object, I guess, to remove the set of types from the Key class. If it were an
     * enum, the db would contain ints, and changing them would be a pain. So don't do that. Note
     * that the type-checking would actually be hard to do -- the checker would need to know about
     * the hierarchy (e.g. stringfact isa fact). TODO: worry about this later.
     * 
     * A null or blank type is bad, but maybe it just means "dunno".
     * 
     * This is useful for references, to choose the target repository.
     */
    private String type = new String();
    /**
     * Globally unique, this is the "id" field of an entity.
     * 
     * serialized using base64.
     * 
     * Old comment: So, like, the path part of the URL. e.g. pub/0/17/734
     * 
     * A null or blank key is very bad.
     */
    private byte[] key;

    protected ExternalKey() {
        super();
    }

    public ExternalKey(final String type, final byte[] key) {
        super();
        setType(type);
        setKey(key);
    }

    /** Check that both fields have non-empty values */
    public boolean valid() {
        return (getType() != null && !getType().isEmpty() && getKey() != null && getKey().length == 0);
    }

    /** Produce an ExternalKey based on the serialized form (type/key) */
    public ExternalKey(final String keyString) {
        super();
        if (keyString == null || keyString.equals("null")) //$NON-NLS-1$
            return;
        int firstDelimiterOffset = keyString.indexOf(FIELD_DELIMITER, 0);
        if (firstDelimiterOffset < 0) {
            setType(null);
            setKey(null);
            return;
        }

        setType(keyString.substring(0, firstDelimiterOffset));
        setKey(KeyUtil.decodeKeyToBytes(keyString.substring(firstDelimiterOffset + 1)));
    }

    /** TODO: replace the String constructor with this */
    public static ExternalKey newInstance(String keyString) {
        if (keyString == null || keyString.equals("null")) //$NON-NLS-1$
            return null;
        int firstDelimiterOffset = keyString.indexOf(FIELD_DELIMITER, 0);
        if (firstDelimiterOffset < 0) {
            return null;
        }

        String lType = keyString.substring(0, firstDelimiterOffset);
        byte[] lKey = KeyUtil.decodeKeyToBytes(keyString.substring(firstDelimiterOffset + 1));
        return new ExternalKey(lType, lKey);
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object anything) {
        if (anything instanceof ExternalKey)
            return equals((ExternalKey) anything);
        return false;
    }

    /**
     * Test value equality
     * 
     * @param other
     *            another ExternalKey
     * @return true if all the fields have the same strings
     */
    public boolean equals(ExternalKey other) {
        return typeEquals(this.getType(), other.getType())
            && idEquals(this.getKey(), other.getKey());
    }

    /** true if the specified type is the same as this type */
    public boolean typeEquals(String aType) {
        return typeEquals(getType(), aType);
    }

    /** true if the two strings are the same, or both null */
    public static boolean typeEquals(String l, String r) {
        if (l == r)
            return true;
        if (l == null && r == null)
            return true;
        if (l == null || r == null)
            return false;
        Logger.getLogger(ExternalKey.class).debug("l: " + l + " r: " + r); //$NON-NLS-1$//$NON-NLS-2$
        if (l.equals(r))
            return true;
        return false;
    }

    /** Because equals() doesn't work on byte[] */
    public static boolean idEquals(byte[] l, byte[] r) {
        if (l == r)
            return true;
        if (l == null && r == null)
            return true;
        if (l == null || r == null)
            return false;
        Logger.getLogger(ExternalKey.class).debug(
            "l: " + KeyUtil.encode(l) + " r: " + KeyUtil.encode(r)); //$NON-NLS-1$//$NON-NLS-2$

        if (l.length != r.length)
            return false;
        for (int index = 0; index < l.length; ++index) {
            if (l[index] != r[index])
                return false;
        }
        return true;
    }

    /** Quick and dirty; compare the serialized versions */
    @Override
    public int compareTo(ExternalKey other) {
        return this.toString().compareTo(String.valueOf(other));
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        // OK I know this is lame but it only took me 3 seconds to type.
        return toString().hashCode();
        // int hash = 1;
        // // hash = hash * 31 + (getNamespace() == null ? 0 : getNamespace().hashCode());
        // hash = hash * 31 + (getType() == null ? 0 : getType().hashCode());
        // hash = hash * 31 + (getKey() == null ? 0 : getKey().hashCode());
        // return hash;
    }

    /**
     * The serialized form is type/key
     * 
     * Old comments:
     * 
     * I guess this is THE serialization used externally?
     * 
     * If the namespace is the domain, and the key is the path part, it would be nice to stick those
     * together in this string. So that means putting the type first:
     * 
     * individual/www.linkedin.com/pub/0/17/734
     * 
     * property/www.linkedin.com/#education
     * 
     * So in general,
     * 
     * {type}/{namespace}/{key}
     * 
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return constructString(getType(), KeyUtil.encode(getKey()));
    }

    /** Produce a version with escaped fields (e.g. + for space), but not escaped delimiters */
    public String escapedString() {
        return constructString(NameUtil.encode(getType()), KeyUtil.encode(getKey()));
    }

    /** The serial form is type/key */
    protected String constructString(String aType, String aKey) {
        StringBuilder builder = new StringBuilder();
        if (aType != null) {
            builder.append(aType);
        }
        // builder.append(FIELD_DELIMITER);
        // if (aNamespace != null) {
        // builder.append(aNamespace);
        // }
        builder.append(FIELD_DELIMITER);
        if (aKey != null) {
            builder.append(aKey);
        }
        return builder.toString();
    }

    //

    // public String getNamespace() {
    // return this.namespace;
    // }
    //
    // protected void setNamespace(String namespace) {
    // this.namespace = namespace;
    // }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getKey() {
        return this.key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }
}
