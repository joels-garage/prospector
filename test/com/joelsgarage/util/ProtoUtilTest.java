/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import com.google.protobuf.TextFormat;
import com.joelsgarage.proto.AddressBookProtos.Person;

/**
 * @author joel
 */
@SuppressWarnings("nls")
public class ProtoUtilTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }

    public void testSimple() {
        Person person = ProtoUtil.foo();
        assertEquals("name foo", person.getName());
    }

    public void testTwo() {
        Person person = ProtoUtil.foo();
        if (!ProtoUtil.writeToFile(person, "/tmp/ProtoUtilTestFile")) {
            fail();
        }
        Person anotherPerson = ProtoUtil.readFromFile("/tmp/ProtoUtilTestFile");
        assertNotNull(anotherPerson);
        assertTrue(person.equals(anotherPerson));
    }

    public void testThree() {
        try {
            Person person = ProtoUtil.foo();
            StringBuilder builder = new StringBuilder();
            TextFormat.print(person, builder);
            System.out.println("PROTOBUF OUTPUT START");
            System.out.println(builder.toString());
            System.out.println("PROTOBUF OUTPUT END");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testFour() {
        Person person = ProtoUtil.foo();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ProtoUtil.writeToStream(person, outputStream);
        byte[] outputBytes = outputStream.toByteArray();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputBytes);
        Person anotherPerson = ProtoUtil.readFromStream(inputStream);

        assertNotNull(anotherPerson);

        assertTrue(person.equals(anotherPerson));
    }
}
