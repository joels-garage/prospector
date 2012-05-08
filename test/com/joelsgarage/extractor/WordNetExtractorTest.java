/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.util.List;

import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.readers.ListRecordReader;
import com.joelsgarage.dataprocessing.writers.ListRecordWriter;
import com.joelsgarage.extractor.WordNetExtractor.ParseError;
import com.joelsgarage.extractor.WordNetExtractor.ParsedRecord;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.FatalException;

/**
 * Run the extractor on a snippet of input.
 * 
 * @author joel
 */
@SuppressWarnings("nls")
public class WordNetExtractorTest extends NewExtractorTest {
    @Override
    public String rawInput() {
        return "s(100001740,1,'entity',n,1,11).\n" //
            + "s(100001930,1,'physical entity',n,1,0).\n" //
            + "s(100002137,1,'abstraction',n,6,0).\n" //
            + "g(100001740,'that which is perceived or known or inferred to have its own distinct existence (living or nonliving)').\n" //
            + "g(100001930,'an entity that has physical existence').\n" //
            + "g(100002137,'my own description with ''quotes'', and commas,''like that''').\n" //
            + "hyp(100001930,100001740).\n" //
            + "hyp(100002137,100001740).\n" //
            + "hyp(100002452,100001930).\n" //
            + "ins(100060548,100058743).\n" //
            + "ins(100060817,100058743).\n" //
            + "ins(100208141,100207761).\n";

    }

    @Override
    public String[] expectedOutput() {
        return new String[] {
            "{\n" //
                + "  \"admin\": \"false\",\n" //
                + "  \"cookie\": \"\",\n" //
                + "  \"email_address\": \"WordNetExtractor\",\n" //
                + "  \"id\": \"r1UQ_VL2Wh3NdS_x\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"password\": \"\",\n" //
                + "  \"real_name\": \"\"\n" //
                + "}", //
                "{\n" //
                + "  \"canonical\": \"false\",\n" //
                + "  \"id\": \"HS5FMQnMvHAhto8j\",\n" //
                + "  \"lang\": \"en-US\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"referent\": \"class/QpPjwsM2Uadwgoxr\",\n" //
                + "  \"word\": \"physical entity\"\n" //
                + "}", //
                "{\n" //
                + "  \"canonical\": \"false\",\n" //
                + "  \"id\": \"HSY9k4TCE1BA5PBF\",\n" //
                + "  \"lang\": \"en-US\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"referent\": \"class/1QyE7eaIAPjK4iS7\",\n" //
                + "  \"word\": \"abstraction\"\n" //
                + "}", //
                "{\n" //
                + "  \"canonical\": \"false\",\n" //
                + "  \"id\": \"Qit_mV-bF66x-Afk\",\n" //
                + "  \"lang\": \"en-US\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"referent\": \"class/VxWptOuZktMM3vVY\",\n" //
                + "  \"word\": \"entity\"\n" //
                + "}", //
                "{\n" //
                + "  \"class\": \"class/0ruKsFT562Z7UAcA\",\n" //
                + "  \"id\": \"ikHOLWQ3lR1QDebO\",\n" //
                + "  \"individual\": \"individual/SrtZclK-rzGmSuvX\",\n" //
                + "  \"namespace\": \"w0\"\n" //
                + "}", //
                "{\n" //
                + "  \"class\": \"class/3Iqen86vgvBI49G1\",\n" //
                + "  \"id\": \"HzNo09cRfYh6kTTs\",\n" //
                + "  \"individual\": \"individual/Qvt7ltcvxPcu-Ycp\",\n" //
                + "  \"namespace\": \"w0\"\n" //
                + "}", //
                "{\n" //
                + "  \"class\": \"class/3Iqen86vgvBI49G1\",\n" //
                + "  \"id\": \"pHL5R3uKeWIWI8Lh\",\n" //
                + "  \"individual\": \"individual/c_KeQy5zCT-rur6V\",\n" //
                + "  \"namespace\": \"w0\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"class/1QyE7eaIAPjK4iS7\",\n" //
                + "  \"id\": \"k3zjHF0BondYc0Qd\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"class/QpPjwsM2Uadwgoxr\",\n" //
                + "  \"id\": \"5SigeFiWuq-Pg69t\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"class/VxWptOuZktMM3vVY\",\n" //
                + "  \"id\": \"_JPn1saKuk8N0uDC\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"class_member/HzNo09cRfYh6kTTs\",\n" //
                + "  \"id\": \"kFuv-f8y0ph1z1NU\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"class_member/ikHOLWQ3lR1QDebO\",\n" //
                + "  \"id\": \"Swl2W2PdtKssPFCP\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"class_member/pHL5R3uKeWIWI8Lh\",\n" //
                + "  \"id\": \"NJolRTwuRI2HGz5g\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"description/GydthrURFTWuoqES\",\n" //
                + "  \"id\": \"lpTjHKfVXPawFs7y\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"description/VNvjM0ZLG-ZgfPZQ\",\n" //
                + "  \"id\": \"VNcJPB6kyxlrKsZf\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"description/eMa9rroISl1p-E85\",\n" //
                + "  \"id\": \"lXQcDSLoCEx_SlcV\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"individual/Qvt7ltcvxPcu-Ycp\",\n" //
                + "  \"id\": \"sLG_gzfPXgfRyHAF\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"individual/SrtZclK-rzGmSuvX\",\n" //
                + "  \"id\": \"ugJxUbZz6mlYgp3e\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"individual/c_KeQy5zCT-rur6V\",\n" //
                + "  \"id\": \"cwgkN1VugkUi3VaL\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"subclass/HdsokOXItfNrtGYF\",\n" //
                + "  \"id\": \"uYnzc6Cz-ePtP3tp\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"subclass/XvIaxVOh5f6G6CL8\",\n" //
                + "  \"id\": \"jVPGsXLXuPz1mpl-\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"subclass/tOLd32SIuXyOfn3b\",\n" //
                + "  \"id\": \"WzvY_aEU53tp7Q4w\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"user/r1UQ_VL2Wh3NdS_x\",\n" //
                + "  \"id\": \"82MmL1YT3GdIXL4u\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"word_sense/HS5FMQnMvHAhto8j\",\n" //
                + "  \"id\": \"QoLc4be6qTMimjSP\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"word_sense/HSY9k4TCE1BA5PBF\",\n" //
                + "  \"id\": \"c24t_b0DbV8Q1CD3\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"word_sense/Qit_mV-bF66x-Afk\",\n" //
                + "  \"id\": \"8cgUtVIDzRjWBQi1\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"entity\": \"write_event/EDTZn6_TP0odXUB9\",\n" //
                + "  \"id\": \"JrAQKNZwNULVVTaP\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"write_event\": \"write_event/EDTZn6_TP0odXUB9\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"1QyE7eaIAPjK4iS7\",\n" //
                + "  \"name\": \"100002137\",\n" //
                + "  \"namespace\": \"w0\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"EDTZn6_TP0odXUB9\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"time\": \"fake date\",\n" //
                + "  \"user\": \"user/r1UQ_VL2Wh3NdS_x\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"GydthrURFTWuoqES\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"referent\": \"class/VxWptOuZktMM3vVY\",\n" //
                + "  \"text\": \"that which is perceived or known or inferred to have its own distinct existence (living or nonliving)\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"HdsokOXItfNrtGYF\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"object\": \"class/VxWptOuZktMM3vVY\",\n" //
                + "  \"subject\": \"class/1QyE7eaIAPjK4iS7\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"QpPjwsM2Uadwgoxr\",\n" //
                + "  \"name\": \"100001930\",\n" //
                + "  \"namespace\": \"w0\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"Qvt7ltcvxPcu-Ycp\",\n" //
                + "  \"name\": \"100060817\",\n" //
                + "  \"namespace\": \"w0\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"SrtZclK-rzGmSuvX\",\n" //
                + "  \"name\": \"100208141\",\n" //
                + "  \"namespace\": \"w0\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"VNvjM0ZLG-ZgfPZQ\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"referent\": \"class/1QyE7eaIAPjK4iS7\",\n" //
                + "  \"text\": \"my own description with 'quotes', and commas,'like that'\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"VxWptOuZktMM3vVY\",\n" //
                + "  \"name\": \"100001740\",\n" //
                + "  \"namespace\": \"w0\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"XvIaxVOh5f6G6CL8\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"object\": \"class/VxWptOuZktMM3vVY\",\n" //
                + "  \"subject\": \"class/QpPjwsM2Uadwgoxr\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"c_KeQy5zCT-rur6V\",\n" //
                + "  \"name\": \"100060548\",\n" //
                + "  \"namespace\": \"w0\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"eMa9rroISl1p-E85\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"referent\": \"class/QpPjwsM2Uadwgoxr\",\n" //
                + "  \"text\": \"an entity that has physical existence\"\n" //
                + "}", //
                "{\n" //
                + "  \"id\": \"tOLd32SIuXyOfn3b\",\n" //
                + "  \"namespace\": \"w0\",\n" //
                + "  \"object\": \"class/QpPjwsM2Uadwgoxr\",\n" //
                + "  \"subject\": \"class/-QqUhWvsY5orA-OA\"\n" //
                + "}"   
          };
    }

    public void testSimple() throws FatalException {
        RecordReader<String> reader = new ListRecordReader<String>(input());
        ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();
        int inLimit = 0; // no limit
        int outLimit = 0; // no limit

        WordNetExtractor extractor = new WordNetExtractor(reader, writer, inLimit, outLimit);
        extractor.setIso8601Date("fake date");
        extractor.run();

        verifyAll(writer.getList());
    }

    public void testParseRecord1() {
        String body = "der(100006484,1,300327031,1).";
        try {
            ParsedRecord parsedRecord = WordNetExtractor.parseRecord(body);
            assertEquals("der", parsedRecord.type);
            assertEquals("100006484,1,300327031,1", parsedRecord.body);
        } catch (ParseError e) {
            e.printStackTrace();
            fail();
            return;
        }
    }

    public void testParseRecord2() {
        String body = "sk(100005930,1,'dwarf%1:03:00::').";
        try {
            ParsedRecord parsedRecord = WordNetExtractor.parseRecord(body);
            assertEquals("sk", parsedRecord.type);
            assertEquals("100005930,1,'dwarf%1:03:00::'", parsedRecord.body);
        } catch (ParseError e) {
            e.printStackTrace();
            fail();
            return;
        }
    }

    public void testParseBody1() {
        String body =
            "104550840,'articles of the same kind or material; usually used in combination: `silverware'', `software'''";
        List<String> fields = WordNetExtractor.RecordHandler.parseBody(body);
        assertEquals(2, fields.size());
        assertEquals("104550840", fields.get(0));
        assertEquals(
            "articles of the same kind or material; usually used in combination: `silverware', `software'",
            fields.get(1));
    }

    public void testParseBody2() {
        String body = "100002137,2,'abstract_entity%1:03:00::'";
        List<String> fields = WordNetExtractor.RecordHandler.parseBody(body);
        assertEquals(3, fields.size());
        assertEquals("100002137", fields.get(0));
        assertEquals("2", fields.get(1));
        assertEquals("abstract_entity%1:03:00::", fields.get(2));
    }
}
