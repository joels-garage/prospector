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
public class FreebaseExtractorTest extends NewExtractorTest {
    @Override
    public String rawInput() {
        return
        // individual
        "/guid/9202a8c04000641f8000000003e0963e \t /type/object/type \t /business/employer  \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /dining/restaurant/cuisine \t /guid/9202a8c04000641f80000000009e8a22  \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /business/company/locations \t /guid/9202a8c04000641f8000000003e4cfe7  \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /type/object/type \t /common/topic   \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /type/object/type \t /dining/restaurant      \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /type/object/timestamp \t 2007-02-20T20:08:57.0182Z\n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /type/object/creator \t /user/mwcl_chefmoz      \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /type/object/name \t /lang/en \t Luciano's\n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /business/company/locations \t /guid/9202a8c04000641f8000000003fc05d9  \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /dining/restaurant/cuisine \t /guid/9202a8c04000641f8000000003e0ac39  \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /business/company/locations \t /guid/9202a8c04000641f8000000004056466  \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /business/company/locations \t /guid/9202a8c04000641f800000000405644c \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /business/company/locations \t /guid/9202a8c04000641f8000000003fa58a1 \n"
            + "/guid/9202a8c04000641f8000000003e0963e \t /dining/restaurant/cuisine \t /guid/9202a8c04000641f800000000003537e \n"
            +
            // quantity fact
            "/guid/9202a8c04000641f8000000005167782 \t /chemistry/chemical_compound/average_molar_mass \t 653.11548 \n"
            +
            // object valued fact
            "/guid/9202a8c04000641f8000000005167782 \t /education/education/major_field_of_study \t /guid/9202a8c04000641f800000000000ce36\n"
            +
            // this is a fact, 'performance' with fields 'actor' and 'film'.
            // it's a property of 'film' so i make 'film' the domain and 'actor' the range.
            "/guid/9202a8c04000641f8000000008c75dd4 \t /film/performance/film \t /guid/9202a8c04000641f8000000008033bf7  \n"
            +
            // the type is a property
            "/guid/9202a8c04000641f8000000008c75dd4 \t /type/object/type \t /film/performance       \n"
            + "/guid/9202a8c04000641f8000000008c75dd4 \t /film/performance/actor \t /guid/9202a8c04000641f800000000018b9c9  \n"
            +
            // this is a property record
            "/guid/9202a8c04000641f8000000000000c7d\t/type/object/key\t/atom/feed\tcontributor\n"
            + "/guid/9202a8c04000641f8000000000000c7d\t/type/property/expected_type\t/atom/feed_person       \n"
            + "/guid/9202a8c04000641f8000000000000c7d\t/type/property/schema\t/atom/feed      \n"
            + "/guid/9202a8c04000641f8000000000000c7d\t/type/object/timestamp\t2006-10-22T07:35:07.0035Z\n"
            + //
            "/guid/9202a8c04000641f8000000000000c7d\t/type/object/creator\t/user/metaweb   \n"
            + "/guid/9202a8c04000641f8000000000000c7d\t/type/object/name\t/lang/en\tcontributor\n"
            + "/guid/9202a8c04000641f8000000000000c7d\t/type/object/type\t/type/property  \n"
            +
            // string fact
            "/guid/9202a8c04000641f80000000052106d6 \t /automotive/fake_subject/fake_property \t /lang/en \t Fake Value\n"
            +
            // this describes a type.
            "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/domain      \t /automotive     \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/expected_by \t /automotive/platform/related    \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/expected_by \t /automotive/generation/platforms    \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/expected_by \t /automotive/platform/predecessor   \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/object/key       \t /automotive    \t platform\n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/object/creator   \t /user/ken       \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/object/name      \t /lang/en       \t Platform\n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/properties  \t /automotive/platform/successor  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/expected_by \t /automotive/platform/successor  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/object/type      \t /type/type      \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/properties  \t /automotive/platform/related    \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/object/timestamp         \t 2007-05-11T20:15:51.0084Z\n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/properties  \t /automotive/platform/predecessor    \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /freebase/type_hints/included_types    \t /common/topic   \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/properties  \t /automotive/platform/generations    \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  reverse_of:/community/discussion_thread/topic  \t /guid/9202a8c04000641f8000000005259216  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f800000000039d702  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /freebase/type_profile/instance_count          \t 15\n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f8000000000a5314f  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f8000000003c19e8d  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f8000000003c19fa8  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f8000000000a533f6  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f8000000003c19f51  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f8000000000a53401  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f8000000000998240  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f8000000000378289  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f800000000037872f  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f8000000000a46f5b  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f800000000038b765  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f800000000038b79d  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f800000000040da85  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  reverse_of:/community/discussion_thread/topic  \t /guid/9202a8c04000641f8000000005155e03  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /freebase/type_profile/published       \t /guid/9202a8c04000641f8000000004fb3a5a  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/type/instance    \t /guid/9202a8c04000641f800000000037807c  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /type/object/type      \t /freebase/type_profile  \n"
            + "/guid/9202a8c04000641f8000000004fc8ca6\t  /freebase/type_profile/property_count          \t 2\n";
    }

    @Override
    public String[] expectedOutput() {
        return new String[] { "{\n" //
    + "  \"admin\": \"false\",\n" //
    + "  \"cookie\": \"\",\n" //
    + "  \"email_address\": \"FreebaseExtractor\",\n" //
    + "  \"id\": \"-UPL8KaA0MHlEAnT\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"password\": \"\",\n" //
    + "  \"real_name\": \"\"\n" //
    + "}", //
    "{\n" //
    + "  \"canonical\": \"true\",\n" //
    + "  \"id\": \"N2NtkfcPhGMsj7OM\",\n" //
    + "  \"lang\": \"en-US\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"referent\": \"class/uNYbqgibUQFGLv5w\",\n" //
    + "  \"word\": \"Platform\"\n" //
    + "}", //
    "{\n" //
    + "  \"class\": \"class/PeGUocFIo7KGTovv\",\n" //
    + "  \"id\": \"60wSQcYLZzUhzW9g\",\n" //
    + "  \"individual\": \"individual/4ouYlpc5xmO9D_eH\",\n" //
    + "  \"namespace\": \"f0\"\n" //
    + "}", //
    "{\n" //
    + "  \"class\": \"class/b3nQWUON2oOcMy7-\",\n" //
    + "  \"id\": \"Uyrjvk169lSvPS30\",\n" //
    + "  \"individual\": \"individual/Q53wTaRzT18GHI09\",\n" //
    + "  \"namespace\": \"f0\"\n" //
    + "}", //
    "{\n" //
    + "  \"class\": \"class/oRAqEdkTsyilopRt\",\n" //
    + "  \"id\": \"i7oThr680thVJdMm\",\n" //
    + "  \"individual\": \"individual/4ouYlpc5xmO9D_eH\",\n" //
    + "  \"namespace\": \"f0\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"class/uNYbqgibUQFGLv5w\",\n" //
    + "  \"id\": \"i9Vuu9dv1coO3T6r\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"class_member/60wSQcYLZzUhzW9g\",\n" //
    + "  \"id\": \"z18xpl8mPhKt6QTG\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"class_member/Uyrjvk169lSvPS30\",\n" //
    + "  \"id\": \"LWOAOGPBo95lpMmc\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"class_member/i7oThr680thVJdMm\",\n" //
    + "  \"id\": \"XFFe7LdbwsO1lC7a\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual/4ouYlpc5xmO9D_eH\",\n" //
    + "  \"id\": \"TnxwzbYxLpNkOsuU\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual/Q53wTaRzT18GHI09\",\n" //
    + "  \"id\": \"f435G3xMQyARnqHw\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual_fact/-N1g69l1MC3cqh6T\",\n" //
    + "  \"id\": \"mzejw45h8S-a2R1t\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual_fact/277d418MmlHTd2gv\",\n" //
    + "  \"id\": \"yavmg9APgFL-EITs\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual_fact/BXKvWIl23od1Z0GM\",\n" //
    + "  \"id\": \"xVx4bX-bLI7qhfhB\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual_fact/PxQ5Z4By24B3_PUa\",\n" //
    + "  \"id\": \"SJJULS9pdg5tehtT\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual_fact/QdwuvitdVDBfLa0-\",\n" //
    + "  \"id\": \"nnDmxK_0pHrkReKK\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual_fact/TJrwYJmf99esnbdB\",\n" //
    + "  \"id\": \"mfTep9eE96l_71bT\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual_fact/WaoJ8sqflIL_G8oA\",\n" //
    + "  \"id\": \"pwBhhZ7LMHPLigws\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual_fact/bWACfYchAbqWhVqC\",\n" //
    + "  \"id\": \"qfMR3li676Eb4nNs\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual_fact/t44XYqK4hp0p-der\",\n" //
    + "  \"id\": \"d5HS6lqUg1jic2ev\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"individual_fact/ze209_SKzRfknQVa\",\n" //
    + "  \"id\": \"A7T5YzPPttXC3Xh6\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"string_fact/GP6kHMhA57BLwIN2\",\n" //
    + "  \"id\": \"aZYp94RCyWmx3ZIC\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"entity\": \"word_sense/N2NtkfcPhGMsj7OM\",\n" //
    + "  \"id\": \"0ZzXxUB0-agP0hQH\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"write_event\": \"write_event/1zSTsx7qpJtUWEsF\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"-N1g69l1MC3cqh6T\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"object\": \"individual/kJVrR6z6E7ApfYdb\",\n" //
    + "  \"property\": \"individual_property/-OkHcavK5pcnAg_Q\",\n" //
    + "  \"subject\": \"individual/4ouYlpc5xmO9D_eH\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"1zSTsx7qpJtUWEsF\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"time\": \"fake date\",\n" //
    + "  \"user\": \"user/-UPL8KaA0MHlEAnT\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"277d418MmlHTd2gv\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"object\": \"individual/EiCUMEboL7wnonL3\",\n" //
    + "  \"property\": \"individual_property/MSALf6ywLrvsXCgN\",\n" //
    + "  \"subject\": \"individual/Q53wTaRzT18GHI09\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"4ouYlpc5xmO9D_eH\",\n" //
    + "  \"name\": \"/guid/9202a8c04000641f8000000003e0963e\",\n" //
    + "  \"namespace\": \"f0\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"BXKvWIl23od1Z0GM\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"object\": \"individual/MD201NNwgD2obYAp\",\n" //
    + "  \"property\": \"individual_property/Vqn3zrk5MuGj6TbB\",\n" //
    + "  \"subject\": \"individual/Q53wTaRzT18GHI09\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"GP6kHMhA57BLwIN2\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"property\": \"string_property/gc-HvmZTuCK8Bmk_\",\n" //
    + "  \"subject\": \"individual/OftxyvmXmHOZcILM\",\n" //
    + "  \"value\": \"Fake Value\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"PxQ5Z4By24B3_PUa\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"object\": \"individual/jEboQ2zV6PnLsTlv\",\n" //
    + "  \"property\": \"individual_property/-OkHcavK5pcnAg_Q\",\n" //
    + "  \"subject\": \"individual/4ouYlpc5xmO9D_eH\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"Q53wTaRzT18GHI09\",\n" //
    + "  \"name\": \"/guid/9202a8c04000641f8000000008c75dd4\",\n" //
    + "  \"namespace\": \"f0\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"QdwuvitdVDBfLa0-\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"object\": \"individual/g45T_lsV1IA0MSNI\",\n" //
    + "  \"property\": \"individual_property/qB57aCR1M5_S-fAm\",\n" //
    + "  \"subject\": \"individual/4ouYlpc5xmO9D_eH\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"TJrwYJmf99esnbdB\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"object\": \"individual/PhcIsCWQkg552jem\",\n" //
    + "  \"property\": \"individual_property/qB57aCR1M5_S-fAm\",\n" //
    + "  \"subject\": \"individual/4ouYlpc5xmO9D_eH\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"WaoJ8sqflIL_G8oA\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"object\": \"individual/xf-NC34keILD3Qc4\",\n" //
    + "  \"property\": \"individual_property/-OkHcavK5pcnAg_Q\",\n" //
    + "  \"subject\": \"individual/4ouYlpc5xmO9D_eH\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"bWACfYchAbqWhVqC\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"object\": \"individual/OuErIQeIg4FbQdZI\",\n" //
    + "  \"property\": \"individual_property/-OkHcavK5pcnAg_Q\",\n" //
    + "  \"subject\": \"individual/4ouYlpc5xmO9D_eH\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"t44XYqK4hp0p-der\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"object\": \"individual/V48LMtaP6dwKdRzZ\",\n" //
    + "  \"property\": \"individual_property/-OkHcavK5pcnAg_Q\",\n" //
    + "  \"subject\": \"individual/4ouYlpc5xmO9D_eH\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"uNYbqgibUQFGLv5w\",\n" //
    + "  \"name\": \"/automotive/platform\",\n" //
    + "  \"namespace\": \"f0\"\n" //
    + "}", //
    "{\n" //
    + "  \"id\": \"ze209_SKzRfknQVa\",\n" //
    + "  \"namespace\": \"f0\",\n" //
    + "  \"object\": \"individual/fy0xv8YRsSroffbt\",\n" //
    + "  \"property\": \"individual_property/qB57aCR1M5_S-fAm\",\n" //
    + "  \"subject\": \"individual/4ouYlpc5xmO9D_eH\"\n" //
    + "}"};
        
    }

    public void testSimple() throws FatalException {
        RecordReader<String> reader = new ListRecordReader<String>(input());
        ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();
        int inLimit = 0; // no limit
        int outLimit = 0; // no limit

        FreebaseExtractor extractor = new FreebaseExtractor(reader, writer, inLimit, outLimit);
        extractor.setIso8601Date("fake date");
        extractor.run();

        verifyAll(writer.getList());
    }
}
