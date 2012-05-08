/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.IndividualProperty;
import com.joelsgarage.model.IndividualPropertyUtility;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Subclass;

/**
 * Tests stuff about ModelEntities and some subclasses.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ModelEntityTest extends TestCase {
    private TestData testData;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.testData = new TestData();
    }

    public void testStakeholderName() {
        assertEquals("stakeholder/toxAEYmI6N2tyeFs", this.testData.stakeholder.makeKey().toString());
    }

    public void testClassMemberKeyKey() throws FatalException {
        ClassMember foo =
            new ClassMember(this.testData.individual.makeKey(), new com.joelsgarage.model.Class(
                "decisionclassname", "decisionclassnamespace").makeKey(), "classmembernamespace");

        assertEquals("class_member/AtnuaPyjS3roPjH1", foo.makeKey().toString());
    }

    public void testIndividualPropertyUtilityName() throws FatalException {
        IndividualPropertyUtility foo =
            new IndividualPropertyUtility(this.testData.stakeholder.makeKey(),
                new IndividualProperty(this.testData.clazz.makeKey(), "propertyname",
                    this.testData.clazz.makeKey(), "propertynamespace").makeKey(),
                this.testData.individual.makeKey(), Double.valueOf(0.5), "utilitynamespace");
        assertEquals("individual_property_utility/5GsZX8Ta1DpVQh3F", foo.makeKey().toString());
    }

    public void testIndividualUtilityName() throws FatalException {
        IndividualUtility foo =
            new IndividualUtility(this.testData.stakeholder.makeKey(), this.testData.individual
                .makeKey(), Double.valueOf(0.5), "utilitynamespace");
        assertEquals("individual_utility/5GsZX8Ta1DpVQh3F", foo.makeKey().toString());
    }

    public void testToMap() {
        Subclass foo = this.testData.subclass;
        Map<String, String> output = new HashMap<String, String>();
        foo.toMap(output);
        assertEquals(4, output.size());
        assertEquals("class/i8kIz6NzedNZKW5h", output.get("subject"));
        assertEquals("class/xKjoij5a4IKw7YLV", output.get("object"));
        assertEquals("subclassNamespace", output.get("namespace"));
        assertEquals("zYbh8SLkxc_U232F", output.get("id"));
    }

    public void testFromMap() {
        Map<String, String> input = new HashMap<String, String>();
        input.put("subject", this.testData.childClass.makeKey().toString());
        input.put("object", this.testData.parentClass.makeKey().toString());
        input.put("namespace", "ns");
        input.put("id", "idfoo");
        Subclass foo = ModelEntity.newInstanceFromMap(Subclass.class, input);
        assertNotNull(foo);
        assertEquals("class/i8kIz6NzedNZKW5h", foo.getSubjectKey().toString());
        assertEquals("class/xKjoij5a4IKw7YLV", foo.getObjectKey().toString());
        assertEquals("ns", foo.getNamespace());
        assertEquals("idfo", KeyUtil.encode(foo.getId()));
    }

    public void testFromMapWithNulls() {
        Map<String, String> input = new HashMap<String, String>();
        input.put("id", "idfoo");
        Subclass foo = ModelEntity.newInstanceFromMap(Subclass.class, input);
        assertNotNull(foo);
        assertEquals("/", foo.getSubjectKey().toString());
        assertEquals("/", foo.getObjectKey().toString());
        assertNull(foo.getNamespace());
        assertEquals("idfo", KeyUtil.encode(foo.getId()));
    }
}
