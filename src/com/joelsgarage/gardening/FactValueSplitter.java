/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;

import java.util.ArrayList;
import java.util.List;

import com.joelsgarage.model.DerivedProvenance;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.StringFact;
import com.joelsgarage.model.User;
import com.joelsgarage.util.ClassUtil;
import com.joelsgarage.util.DateUtil;
import com.joelsgarage.util.FatalException;

/**
 * For each stringfact that appears to contain a delimited value, split the value, and produce a set
 * of new stringfacts, one for each split, and also provenance.
 * 
 * Example input:
 * 
 * <pre>
 * Native Status: L48 (N), CAN (N)
 * </pre>
 * 
 * Example output:
 * 
 * <pre>
 * Native Status: L48 (N)
 * Native Status: CAN (N)
 * </pre>
 * 
 * @author joel
 * 
 */
public class FactValueSplitter {
    /**
     * The namespace of this process,"internal-agent" implies that someday there may be
     * "external-agent" :-)
     */
    private static final String CREATOR_NAMESPACE = "internal-agent"; //$NON-NLS-1$
    private String shortClassName;
    private String iso8601Date;
    private ExternalKey creatorKey;
    /** First time through, emit the user record */
    private boolean first = true;

    public FactValueSplitter() throws FatalException {
        setShortClassName(ClassUtil.shortClassName(this.getClass()));
        setIso8601Date(DateUtil.formatDateToISO8601(DateUtil.now()));
        setCreatorKey(new User(getShortClassName(), CREATOR_NAMESPACE).makeKey());
    }

    /**
     * This operation is actually an aggregate. You want to find properties whose corresponding fact
     * values tend to be short and full of delimiters. One value by itself may not deserve
     * splitting.
     * 
     * So what I really want is to group the inputs by property, and load the whole property's worth
     * at once. I could do that with a reader where T is List<T>, with some kind of group-by
     * parameter in the constructor.
     * 
     * But for now just use a pattern, and for now, the pattern is just a comma.
     * 
     * @see ProcessNode#handleRecord(Object)
     */
    // @Override
    // protected boolean handleRecord(StringFact record) {
    // // Logger.getLogger(FactValueSplitGardener.class).info(
    // // "handling record " + record.getKey().toString()); //$NON-NLS-1$
    // String value = record.getValue();
    // // Logger.getLogger(FactValueSplitGardener.class).info("value: " + value); //$NON-NLS-1$
    // String[] valueArray = value.split("\\,"); //$NON-NLS-1$
    // if (valueArray.length == 1) {
    // // Logger.getLogger(FactValueSplitGardener.class).info("Nothing to split");
    // // //$NON-NLS-1$
    // return true;
    // }
    // // Logger.getLogger(FactValueSplitGardener.class).info("Stuff to split"); //$NON-NLS-1$
    //
    // // we have something to do.
    // List<ModelEntity> newEntities = makeNewEntities(valueArray, record);
    // for (ModelEntity newEntity : newEntities) {
    // output(newEntity);
    // }
    // return true;
    // }
    /**
     * Make new assertions, if possible
     * 
     * @throws FatalException
     */
    public List<ModelEntity> convert(StringFact record) throws FatalException {
        List<ModelEntity> newEntities = new ArrayList<ModelEntity>();

        if (this.first) {
            User user = new User(getShortClassName(), CREATOR_NAMESPACE);
            newEntities.add(user);
        }
        this.first = false;

        String value = record.getValue();
        // Logger.getLogger(FactValueSplitGardener.class).info("value: " + value); //$NON-NLS-1$
        String[] valueArray = value.split("\\,"); //$NON-NLS-1$
        if (valueArray.length == 1) {
            // Logger.getLogger(FactValueSplitGardener.class).info("Nothing to split");
            // //$NON-NLS-1$
            return newEntities;
        }
        // Logger.getLogger(FactValueSplitGardener.class).info("Stuff to split"); //$NON-NLS-1$

        // we have something to do.
        newEntities.addAll(makeNewEntities(valueArray, record));
        return newEntities;

    }

    // an easy-to-test splitter.
    protected List<ModelEntity> makeNewEntities(String[] values, StringFact original)
        throws FatalException {
        List<ModelEntity> result = new ArrayList<ModelEntity>();
        for (String newValue : values) {
            String trimmedValue = newValue.trim();
            StringFact newFact = newStringFact(trimmedValue, original);
            DerivedProvenance provenance = newDerivedProvenance(newFact, original);

            result.add(newFact);
            result.add(provenance);
        }
        return result;
    }

    protected StringFact newStringFact(String newValue, StringFact original) throws FatalException {
        String namespace = original.getNamespace();
        ExternalKey subject = original.getSubjectKey();
        ExternalKey property = original.getPropertyKey();

        return new StringFact(subject, property, newValue, namespace);
    }

    protected DerivedProvenance newDerivedProvenance(StringFact newFact, StringFact original)
        throws FatalException {
        return new DerivedProvenance(newFact.makeKey(), original.makeKey(), getShortClassName(),
            original.getNamespace());
    }

    public String getShortClassName() {
        return this.shortClassName;
    }

    public void setShortClassName(String shortClassName) {
        this.shortClassName = shortClassName;
    }

    public String getIso8601Date() {
        return this.iso8601Date;
    }

    public void setIso8601Date(String iso8601Date) {
        this.iso8601Date = iso8601Date;
    }

    public ExternalKey getCreatorKey() {
        return this.creatorKey;
    }

    public void setCreatorKey(ExternalKey creatorKey) {
        this.creatorKey = creatorKey;
    }
}
