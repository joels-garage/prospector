/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.nodes;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.WordSense;

/**
 * Translate model entity record to Gazetteer entry.
 * 
 * The gazetteer contains strings that indicate the entity. To these strings, we attach features
 * (name/value pairs).
 * 
 * In general, this is a complex thing to do. For example, we would want to learn strings that
 * indicate the entity, but are not actually its name, i.e. subordinate synonyms. There are
 * ambiguities, e.g. "HP," which means Hewlett-Packard and also horsepower.
 * 
 * Choosing the features to attach is also not obvious -- we could attach *all* the features of the
 * entity, but that would be a lot of features.
 * 
 * For now, ignore all these issues.
 * 
 * Just use the name, and attach specific features, hardcoded here.
 * 
 * TODO: GATE has some facility for entity supertype annotation, i.e. if you match "mouse" and you
 * have a JAPE rule that wants "mammal," it will still match. Maybe I should use this.
 * 
 * NEW: since all the NLS is in "WordSense", restrict to that type.
 * 
 * TODO: also do UnitSynonym, or, alternatively, make UnitSynonym refer to WordSense.
 * 
 * @author joel
 * 
 */
public class ModelEntityToGazetteerProcessNode extends ProcessNode<WordSense, String> {
    /**
     * The name of the type feature, i.e. class name
     */
    private static final String TYPE = "type"; //$NON-NLS-1$
    private static final String KEY = "key"; //$NON-NLS-1$

    private static final String FEATURE_SEPARATOR = "&"; //$NON-NLS-1$
    private static final String EQUALS = "="; //$NON-NLS-1$

    public ModelEntityToGazetteerProcessNode(RecordReader<WordSense> reader,
        RecordWriter<String> writer, int inLimit, int outLimit) {
        super(reader, writer, inLimit, outLimit);
        setProgressCount(1000);
    }

    /**
     * Write a Gazetteer record consisting of the name of the ModelEntity followed by a number of
     * features:
     * <ul>
     * <li>its type (the VisibleType of the entity)
     * <li>its key (ExternalKey, serialized).
     * <ul>
     * 
     * I could attach other features, e.g. ClassMember-ship (for Individuals). Note because class
     * membership is multivalued, but the FeatureMap is not, we need to do some sort of (lame!)
     * encoding here, and the JAPE rule needs to use regex matching. This is lame beyond
     * description. I thought about using the ontology-aware transducer for this purpose, but it
     * just does subsumption matching for classes. I could do it with single-valued class
     * membership, with lots of little leaf classes, but gah. Another option is to not do any
     * additional features here at all, but rather to just leave the gazetteer as a KEY-TAGGER ONLY,
     * and if further reasoning about the entity is required at matching time, to do it (ugh) on the
     * fly.
     * 
     * I think that's the most expedient path for now.
     * 
     * @see ProcessNode#handleRecord(Object)
     */
    @Override
    protected boolean handleRecord(WordSense record) {
        if (record == null) {
            Logger.getLogger(ModelEntityToGazetteerProcessNode.class).error("Null record!"); //$NON-NLS-1$
            return true;
        }

        String element = record.getWord();

        ExternalKey key = record.getReferentKey();
        if (key != null) {
            element += FEATURE_SEPARATOR //
                + TYPE //
                + EQUALS //
                + key.getType();

            element += FEATURE_SEPARATOR //
                + KEY //
                + EQUALS //
                + key.toString();
        }

        // Append a feature for the ClassMembers.

        output(element);
        return true;
    }
}
