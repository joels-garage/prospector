/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.readers.ListRecordReader;
import com.joelsgarage.dataprocessing.writers.ListRecordWriter;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.FatalException;

/**
 * Run the extractor on a snippet of input.
 * 
 * @author joel
 */
@SuppressWarnings("nls")
public class GoogleExtractorTest extends NewExtractorTest {
    @Override
    public String rawInput() {
        return "Animals\n" //
            + "Animals > Pet Supplies\n" //
            + "Animals > Pet Supplies > Amphibian Supplies\n";
    }

    @Override
    public String[] expectedOutput() {
        return new String[] {

        "{\n" //
            + "  \"admin\": \"false\",\n" //
            + "  \"cookie\": \"\",\n" //
            + "  \"email_address\": \"GoogleExtractor\",\n" //
            + "  \"id\": \"Tcnaq39EHl7MDlTa\",\n" //
            + "  \"namespace\": \"g0\",\n" //
            + "  \"password\": \"\",\n" //
            + "  \"real_name\": \"\"\n" //
            + "}", //
            "{\n" //
                + "  \"canonical\": \"true\",\n" //
                + "  \"id\": \"OEkh6bhMI8QCGnEO\",\n" //
                + "  \"lang\": \"en-US\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"referent\": \"class/lVv8kcaNPi0RPlK0\",\n" //
                + "  \"word\": \"Animals\"\n" //
                + "}", //
            "{\n" //
                + "  \"canonical\": \"true\",\n" //
                + "  \"id\": \"U0fQeHPGiTcsW2Oa\",\n" //
                + "  \"lang\": \"en-US\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"referent\": \"class/a-Y1isgPoBCSaPHH\",\n" //
                + "  \"word\": \"Amphibian Supplies\"\n" //
                + "}", //
            "{\n" //
                + "  \"canonical\": \"true\",\n" //
                + "  \"id\": \"_4n8wdVQNBRgod_z\",\n" //
                + "  \"lang\": \"en-US\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"referent\": \"class/rvzcr9GQyuHf68wa\",\n" //
                + "  \"word\": \"Pet Supplies\"\n" //
                + "}", //
            "{\n" //
                + "  \"entity\": \"class/a-Y1isgPoBCSaPHH\",\n" //
                + "  \"id\": \"0Gj0yqfVinH6LTuh\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"write_event\": \"write_event/81ybyGW1G52PFSat\"\n" //
                + "}", //
            "{\n" //
                + "  \"entity\": \"class/lVv8kcaNPi0RPlK0\",\n" //
                + "  \"id\": \"HoxuUYSaRDE3QWfG\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"write_event\": \"write_event/81ybyGW1G52PFSat\"\n" //
                + "}", //
            "{\n" //
                + "  \"entity\": \"class/rvzcr9GQyuHf68wa\",\n" //
                + "  \"id\": \"Jgc_AH99lBFuO4Eh\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"write_event\": \"write_event/81ybyGW1G52PFSat\"\n" //
                + "}", //
            "{\n" //
                + "  \"entity\": \"subclass/i-CCyrCUvf2GmIZo\",\n" //
                + "  \"id\": \"vtm0IUszfjxTqPEC\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"write_event\": \"write_event/81ybyGW1G52PFSat\"\n" //
                + "}", //
            "{\n" //
                + "  \"entity\": \"subclass/kZa24PnFfXhwlSl3\",\n" //
                + "  \"id\": \"CLkMfI3bOa3BPU2s\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"write_event\": \"write_event/81ybyGW1G52PFSat\"\n" //
                + "}", //
            "{\n" //
                + "  \"entity\": \"word_sense/OEkh6bhMI8QCGnEO\",\n" //
                + "  \"id\": \"gtH76gUS4STksg34\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"write_event\": \"write_event/81ybyGW1G52PFSat\"\n" //
                + "}", //
            "{\n" //
                + "  \"entity\": \"word_sense/U0fQeHPGiTcsW2Oa\",\n" //
                + "  \"id\": \"lWO1hNy-LZdefji7\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"write_event\": \"write_event/81ybyGW1G52PFSat\"\n" //
                + "}", //
            "{\n" //
                + "  \"entity\": \"word_sense/_4n8wdVQNBRgod_z\",\n" //
                + "  \"id\": \"V2POjQuYUDr3bg-5\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"write_event\": \"write_event/81ybyGW1G52PFSat\"\n" //
                + "}", //
            "{\n" //
                + "  \"id\": \"81ybyGW1G52PFSat\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"time\": \"fake date\",\n" //
                + "  \"user\": \"user/Tcnaq39EHl7MDlTa\"\n" //
                + "}", //
            "{\n" //
                + "  \"id\": \"a-Y1isgPoBCSaPHH\",\n" //
                + "  \"name\": \"Amphibian Supplies\",\n" //
                + "  \"namespace\": \"g0\"\n" //
                + "}", //
            "{\n" //
                + "  \"id\": \"i-CCyrCUvf2GmIZo\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"object\": \"class/lVv8kcaNPi0RPlK0\",\n" //
                + "  \"subject\": \"class/rvzcr9GQyuHf68wa\"\n" //
                + "}", //
            "{\n" //
                + "  \"id\": \"kZa24PnFfXhwlSl3\",\n" //
                + "  \"namespace\": \"g0\",\n" //
                + "  \"object\": \"class/rvzcr9GQyuHf68wa\",\n" //
                + "  \"subject\": \"class/a-Y1isgPoBCSaPHH\"\n" //
                + "}", //
            "{\n" //
                + "  \"id\": \"lVv8kcaNPi0RPlK0\",\n" //
                + "  \"name\": \"Animals\",\n" //
                + "  \"namespace\": \"g0\"\n" //
                + "}", //
            "{\n" //
                + "  \"id\": \"rvzcr9GQyuHf68wa\",\n" //
                + "  \"name\": \"Pet Supplies\",\n" //
                + "  \"namespace\": \"g0\"\n" //
                + "}" };
    }

    public void testSimple() throws FatalException {
        RecordReader<String> reader = new ListRecordReader<String>(input());
        ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();
        int inLimit = 0; // no limit
        int outLimit = 0; // no limit

        GoogleExtractor extractor = new GoogleExtractor(reader, writer, inLimit, outLimit);
        extractor.setIso8601Date("fake date");
        extractor.run();

        // I think this is WRONG, i.e. need to produce more WordSense records.

        verifyAll(writer.getList());
    }
}
