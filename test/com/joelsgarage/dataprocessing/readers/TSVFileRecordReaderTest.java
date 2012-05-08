/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.joelsgarage.model.Individual;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.StringFact;
import com.joelsgarage.model.Subclass;
import com.joelsgarage.util.FakeStreamFactory;
import com.joelsgarage.util.KeyUtil;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class TSVFileRecordReaderTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }

    public void testReadSubclass() {
        Map<String, ByteArrayInputStream> inputs = new HashMap<String, ByteArrayInputStream>();
        String subclassInput = "id\tsubject\tobject\tnamespace\n" + //
            "aK0-wj_vlJuKUzxf\tclass/2mg4vGHRLo-7Scza\tclass/cJrMm0MgVfNdmgZt\tsubclassNamespace\n";
        inputs.put("foo.Subclass", new ByteArrayInputStream(subclassInput.getBytes()));
        FakeStreamFactory factory = new FakeStreamFactory(inputs);
        String basename = "foo";

        List<Class<? extends ModelEntity>> types = new ArrayList<Class<? extends ModelEntity>>();
        types.add(Subclass.class);

        TSVFileRecordReader reader = new TSVFileRecordReader(factory, basename, types);
        reader.open();
        ModelEntity entity = reader.read();
        assertNotNull(entity);
        reader.close();
        if (entity instanceof Subclass) {
            Subclass subclass = (Subclass) entity;
            assertEquals("aK0-wj_vlJuKUzxf", KeyUtil.encode(subclass.getId()));
            assertEquals("subclassNamespace", subclass.getNamespace());
            assertEquals("class/cJrMm0MgVfNdmgZt", subclass.getObjectKey().toString());
            assertEquals("class/2mg4vGHRLo-7Scza", subclass.getSubjectKey().toString());
        } else {
            fail();
        }
    }

    public void testReadMultipleClasses() {
        Map<String, ByteArrayInputStream> inputs = new HashMap<String, ByteArrayInputStream>();
        String subclassInput = "id\tsubject\tobject\tnamespace\n" + //
            "aK0-wj_vlJuKUzxf\tclass/2mg4vGHRLo-7Scza\tclass/cJrMm0MgVfNdmgZt\tsubclassNamespace\n";
        inputs.put("foo.Subclass", new ByteArrayInputStream(subclassInput.getBytes()));
        String individualInput = "id\tnamespace\n" + //
            "aK0-wj_vlJuKUabc\tindividualNamespace\n";
        inputs.put("foo.Individual", new ByteArrayInputStream(individualInput.getBytes()));
        FakeStreamFactory factory = new FakeStreamFactory(inputs);
        String basename = "foo";

        List<Class<? extends ModelEntity>> types = new ArrayList<Class<? extends ModelEntity>>();
        types.add(Subclass.class);
        types.add(Individual.class);
        types.add(StringFact.class);  // this is not present and should silently fail

        TSVFileRecordReader reader = new TSVFileRecordReader(factory, basename, types);
        reader.open();
        ModelEntity entity = reader.read();
        assertNotNull(entity);
        validateRead(entity);
        entity = reader.read();
        assertNotNull(entity);
        validateRead(entity);
        entity = reader.read();
        assertNull(entity);
        reader.close();
    }

    protected void validateRead(ModelEntity entity) {
        if (entity instanceof Subclass) {
            Subclass subclass = (Subclass) entity;
            assertEquals("aK0-wj_vlJuKUzxf", KeyUtil.encode(subclass.getId()));
            assertEquals("subclassNamespace", subclass.getNamespace());
            assertEquals("class/cJrMm0MgVfNdmgZt", subclass.getObjectKey().toString());
            assertEquals("class/2mg4vGHRLo-7Scza", subclass.getSubjectKey().toString());
        } else if (entity instanceof Individual) {
            Individual individual = (Individual) entity;
            assertEquals("aK0-wj_vlJuKUabc", KeyUtil.encode(individual.getId()));
            assertEquals("individualNamespace", individual.getNamespace());
        } else {
            fail();
        }
    }
}
