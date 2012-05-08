/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.joelsgarage.proto.AddressBookProtos.Person;


/**
 * Just fiddling with proto buffers, now that I am truly fed up with XML.
 * 
 * @author joel
 * 
 */
public class ProtoUtil {
     @SuppressWarnings("nls")
    static Person foo() {
        Person.Builder person = Person.newBuilder();
        person.setId(1234);
        person.setName("name foo");
        person.setEmail("email foo");
        Person.PhoneNumber.Builder phoneNumber =
            Person.PhoneNumber.newBuilder().setNumber("number foo");
        phoneNumber.setType(Person.PhoneType.MOBILE);
        person.addPhone(phoneNumber);
        return person.build();
    }

    static void writeToStream(Person person, OutputStream outputStream) {
        try {
            person.writeTo(outputStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** Try to write the record to the file, true if success */
    static boolean writeToFile(Person person, String filename) {
        try {
            OutputStream outputStream = new FileOutputStream(filename);
            ProtoUtil.writeToStream(person, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Parse a record. For my purposes, these records are small.
     * 
     * And they can't be guaranteed to stay in the mmapped part of the input, during their entire
     * lifetime, so the parsed version shouldn't be zero-copy.
     * 
     * Still, it would be nice for the parse itself to operate on the mmapped data directly.
     * 
     * For now it's the simple read-the-whole-stream version.
     */
    static Person readFromFile(String filename) {
        try {
            FileInputStream inputStream = new FileInputStream(filename);
            FileChannel inputChannel = inputStream.getChannel();
            // return readFromStream(inputStream);
            return readFromChannel(inputChannel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tried a mapped buffer, but couldn't do it without a copy; if it's going to be copied it may
     * as well be read() rather than map(). So now it's read(), without a direct buffer. Block moves
     * are fast.
     * 
     * @param channel
     * @return
     */
    static Person readFromChannel(FileChannel channel) {
        try {
            Person.Builder builder = Person.newBuilder();
            if (!channel.isOpen())
                return null;
            ByteBuffer dst = ByteBuffer.allocate(1000000);
            int length = channel.read(dst);
            builder.mergeFrom(dst.array(), 0, length);
            return builder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Person readFromStream(InputStream inputStream) {
        try {
            Person.Builder builder = Person.newBuilder();
            builder.mergeFrom(inputStream);
            return builder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
