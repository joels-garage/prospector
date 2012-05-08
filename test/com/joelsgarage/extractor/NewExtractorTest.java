/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.Description;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualFact;
import com.joelsgarage.model.Log;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Subclass;
import com.joelsgarage.model.User;
import com.joelsgarage.model.WordSense;
import com.joelsgarage.model.WriteEvent;

/**
 * Superclass for extractor tests.
 * 
 * @author joel
 */
@SuppressWarnings("nls")
public class NewExtractorTest extends TestCase {
    /** rows of input */
    protected List<String> input() {
        List<String> result = new ArrayList<String>();
        String[] rows = rawInput().split("\n");
        for (String row : rows) {
            result.add(row);
        }
        return result;
    }

    public String rawInput() {
        return null;
    }

    public void testNothing() {
        assertTrue(true);
    }

    public String[] expectedOutput() {
        return null;
    }

    /** Ignores differences in order */
    protected void verifyAll(List<ModelEntity> entities) {
        String[] expectedRows = expectedOutput();
        assertEquals(expectedRows.length, entities.size());

        String[] actualRows = new String[entities.size()];
        for (int index = 0; index < entities.size(); ++index) {
            actualRows[index] = entities.get(index).toString();
        }

        Arrays.sort(expectedRows);
        Arrays.sort(actualRows);

        StringBuilder expectedSortedRows = new StringBuilder();
        StringBuilder actualSortedRows = new StringBuilder();
        for (int index = 0; index < entities.size(); ++index) {
            expectedSortedRows.append(expectedRows[index]);
            expectedSortedRows.append('\n');
            actualSortedRows.append(actualRows[index]);
            actualSortedRows.append('\n');
        }
        assertEquals(expectedSortedRows.toString(), actualSortedRows.toString());
    }

    protected void verifyUser(ModelEntity entity, String key) {
        verifyEntity(entity, "user", User.class, key);
    }

    protected void verifyEntity(ModelEntity entity, String classTag, Class<?> clazz, String key) {
        assertTrue(clazz.getName(), clazz.isInstance(entity));
        assertEquals(classTag + "/" + key, entity.makeKey().toString());
    }

    protected void verifyUpdate(ModelEntity entity, String key, String timestamp, String user) {
        verifyEntity(entity, "update", WriteEvent.class, key);
        assertEquals(timestamp, ((WriteEvent) entity).getTime());
        assertEquals(user, ((WriteEvent) entity).getUser().toString());
    }

    protected void verifyClass(ModelEntity entity, String keyKey) {
        verifyEntity(entity, "class", com.joelsgarage.model.Class.class, keyKey);
    }

    protected void verifyWordSense(ModelEntity entity, String keyKey, String word) {
        verifyEntity(entity, "word_sense", WordSense.class, keyKey);
        assertEquals(word, ((WordSense) entity).getWord());
    }

    protected void verifyDescription(ModelEntity entity, String keyKey, String description,
        String referent) {
        verifyEntity(entity, "description", Description.class, keyKey);
        Description desc = (Description) entity;
        assertEquals(description, desc.getText());
        assertEquals(referent, desc.getReferentKey().toString());
    }

    protected void verifySubclass(ModelEntity entity, String keyKey, String subject, String object) {
        verifyEntity(entity, "subclass", Subclass.class, keyKey);
        Subclass subclass = (Subclass) entity;
        assertEquals(subject, subclass.getSubjectKey().toString());
        assertEquals(object, subclass.getObjectKey().toString());
    }

    protected void verifyIndividual(ModelEntity entity, String keyKey) {
        verifyEntity(entity, "individual", Individual.class, keyKey);
    }

    protected void verifyClassMember(ModelEntity entity, String keyKey, String individual,
        String clazz) {
        verifyEntity(entity, "class_member", ClassMember.class, keyKey);
        com.joelsgarage.model.ClassMember classMember = (ClassMember) entity;
        assertEquals(individual, classMember.getIndividualKey().toString());
        assertEquals(clazz, classMember.getClassKey().toString());
    }

    protected void verifyIndividualFact(ModelEntity entity, String keyKey, String subject,
        String property, String object) {
        verifyEntity(entity, "individual_fact", IndividualFact.class, keyKey);
        IndividualFact individualFact = (IndividualFact) entity;
        assertEquals(subject, individualFact.getSubjectKey().toString());
        assertEquals(property, individualFact.getPropertyKey().toString());
        assertEquals(object, individualFact.getObjectKey().toString());
    }

    protected void verifyLog(ModelEntity entity, String keyKey, String ent, String upd) {
        verifyEntity(entity, "log", Log.class, keyKey);
        Log log = (Log) entity;
        assertEquals(ent, log.getEntity().toString());
        assertEquals(upd, log.getWriteEvent().toString());
    }
}
