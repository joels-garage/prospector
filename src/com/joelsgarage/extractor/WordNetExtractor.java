/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.Description;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.Log;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Subclass;
import com.joelsgarage.model.WriteEvent;
import com.joelsgarage.model.WordSense;
import com.joelsgarage.util.FatalException;

/**
 * Transforms WordNet prolog data into ModelEntites.
 * 
 * Sample input:
 * 
 * import com.joelsgarage.util.KeyUtil;
 * 
 * <pre>
 * s(100001740,1,'entity',n,1,11).
 * s(100001930,1,'physical entity',n,1,0).
 * s(100002137,1,'abstraction',n,6,0).
 * g(100001740,'that which is perceived or known or inferred to have its own distinct existence (living or nonliving)').
 * g(100001930,'an entity that has physical existence').
 * g(100002137,'a general concept formed by extracting common features from specific examples').
 * hyp(100001930,100001740).
 * hyp(100002137,100001740).
 * hyp(100002452,100001930).
 * ins(100060548,100058743).
 * ins(100060817,100058743).
 * ins(100208141,100207761).
 * </pre>
 * 
 * note there are embedded, escaped quotes and commas. Bleah.
 * 
 * @author joel
 * 
 */
public class WordNetExtractor extends ModelEntityExtractor<String> {
    /** The namespace everything goes in w0 = wordnet3-20070123 */
    private static final String NAMESPACE = "w0"; //$NON-NLS-1$
    public static final char QUOTE = '\'';
    public static final char INPUT_SEPARATOR = ',';
    private WriteEvent update = null;

    /** This is a somewhat overdesigned way to handle different row types */
    public abstract static class RecordHandler {
        private static final String EN_US = "en-US"; //$NON-NLS-1$
        // /** separate the synset_id from w_num with this */
        // private static final String OUTPUT_SEPARATOR = ":"; //$NON-NLS-1$
        /** if a word sense record has a zero for the w_num field, it's the canonical word sense */
        private static final String CANONICAL_W_NUM = "0"; //$NON-NLS-1$

        public static final String GLOSS = "g"; //$NON-NLS-1$
        public static final RecordHandler GLOSS_HANDLER = new RecordHandler() {
            @Override
            public void handle(String body, WordNetExtractor extractor) throws FatalException {
                List<String> fields = parseBody(body);
                String synset_id = fields.get(0);
                String gloss = fields.get(1);

                Description description =
                    new Description(makeClass(extractor, synset_id).makeKey(), gloss, extractor
                        .namespace());
                extractor.output(description);
            }
        };
        public static final String HYPERNYM = "hyp"; //$NON-NLS-1$
        public static final RecordHandler HYPERNYM_HANDLER = new RecordHandler() {
            @Override
            public void handle(String body, WordNetExtractor extractor) throws FatalException {
                List<String> fields = parseBody(body);
                String child_synset_id = fields.get(0);
                String parent_synset_id = fields.get(1);

                com.joelsgarage.model.Class parentClass = makeClass(extractor, parent_synset_id);

                com.joelsgarage.model.Class childClass = makeClass(extractor, child_synset_id);

                Subclass subclass =
                    new Subclass(childClass.makeKey(), parentClass.makeKey(), extractor.namespace());
                extractor.output(subclass);
            }
        };
        public static final String INSTANCE = "ins"; //$NON-NLS-1$
        public static final RecordHandler INSTANCE_HANDLER = new RecordHandler() {
            // this produces many copies of the referent, which are filtered out in the loading
            // stage.
            // TODO: consider keeping a set of produced referents to avoid this duplication.
            @Override
            public void handle(String body, WordNetExtractor extractor) throws FatalException {
                List<String> fields = parseBody(body);
                String ind_synset_id = fields.get(0);
                String class_synset_id = fields.get(1);

                Individual individual = new Individual(ind_synset_id, extractor.namespace());
                // we don't actually write this, we just want its key.
                com.joelsgarage.model.Class clazz = makeClass(extractor, class_synset_id);

                ClassMember classMember =
                    new ClassMember(individual.makeKey(), clazz.makeKey(), extractor.namespace());

                extractor.output(individual);
                extractor.output(classMember);
            }
        };
        public static final String SENSE = "s"; //$NON-NLS-1$
        public static final RecordHandler SENSE_HANDLER = new RecordHandler() {
            @Override
            public void handle(String body, WordNetExtractor extractor) throws FatalException {
                List<String> fields = parseBody(body);

                // these noncompliant variable names are from the docs

                String synset_id = fields.get(0);
                String w_num = fields.get(1);
                String word = fields.get(2);

                com.joelsgarage.model.Class referent = makeClass(extractor, synset_id);

                boolean canonical = false;
                if (w_num != null && w_num.equals(CANONICAL_W_NUM)) {
                    canonical = true;
                }

                // I think i don't need w_num but i'm not sure.
                // String name = synset_id + OUTPUT_SEPARATOR + w_num;

                WordSense wordSense =
                    new WordSense(EN_US, word, canonical, referent.makeKey(), extractor.namespace());

                extractor.output(referent);
                extractor.output(wordSense);
            }
        };

        /** TODO: make this exception more informative */
        public abstract void handle(String body, WordNetExtractor extractor) throws FatalException;

        protected static com.joelsgarage.model.Class makeClass(WordNetExtractor extractor,
            String name) throws FatalException {
            return new com.joelsgarage.model.Class(name, extractor.namespace());
        }

        /**
         * CSV parser that handles escaping
         * 
         * TODO: extract this to a util class.
         */
        protected static List<String> parseBody(String body) {
            List<String> fields = new ArrayList<String>();
            StringBuilder builder = new StringBuilder();
            boolean quoted = false;
            for (int index = 0; index < body.length(); ++index) {
                char cc = body.charAt(index);
                if (cc == QUOTE) {
                    if (quoted && body.length() > (index + 1) && (body.charAt(index + 1) == QUOTE)) {
                        builder.append(QUOTE);
                        ++index;
                    } else {
                        quoted = !quoted;
                    }
                } else if (cc == INPUT_SEPARATOR && !quoted) {
                    fields.add(builder.toString());
                    builder = new StringBuilder();
                } else {
                    builder.append(cc);
                }
            }
            fields.add(builder.toString());
            return fields;
        }
    }

    private static Map<String, RecordHandler> handlers = new HashMap<String, RecordHandler>() {
        private static final long serialVersionUID = 1L;
        {
            put(RecordHandler.SENSE, RecordHandler.SENSE_HANDLER);
            put(RecordHandler.GLOSS, RecordHandler.GLOSS_HANDLER);
            put(RecordHandler.HYPERNYM, RecordHandler.HYPERNYM_HANDLER);
            put(RecordHandler.INSTANCE, RecordHandler.INSTANCE_HANDLER);
        }
    };

    public static class ParsedRecord {
        public String type;
        public String body;

        public ParsedRecord(String type, String body) {
            this.type = type;
            this.body = body;
        }
    }

    public static class ParseError extends Exception {
        private static final long serialVersionUID = 1L;

        public ParseError(Exception ex) {
            super(ex);
        }

        public ParseError(String message) {
            super(message);
        }
    }

    public WordNetExtractor(RecordReader<String> reader, RecordWriter<ModelEntity> writer,
        int inLimit, int outLimit) throws FatalException {
        super(reader, writer, inLimit, outLimit);
    }

    @Override
    protected void start() {
        try {
            // output() relies on update being set.
            setUpdate(new WriteEvent(getIso8601Date(), getCreatorKey(), namespace()));
            output(getUpdate());
        } catch (FatalException e) {
            e.printStackTrace();
        }
        super.start();
    }

    @Override
    protected boolean handleRecord(String record) {
        try {
            if (record == null)
                return true;

            ParsedRecord parsedRecord = parseRecord(record);

            RecordHandler handler = getHandler(parsedRecord.type);
            if (handler == null)
                return true;
            handler.handle(parsedRecord.body, this);

            return true;
        } catch (ParseError ex) {
            Logger.getLogger(WordNetExtractor.class).error(ex.getMessage());
            return true;
        } catch (FatalException ex) {
            Logger.getLogger(WordNetExtractor.class).error(ex.getMessage());
            return false;
        }
    }

    /** Decide which handler should handle the body */
    protected RecordHandler getHandler(String type) {
        return handlers.get(type);
    }

    /**
     * Increases the visibility of output for our nested class's benefit, and also adds a "log" row
     * for each output entity.
     */
    @Override
    public void output(ModelEntity entity) {
        if (entity == null) {
            Logger.getLogger(WordNetExtractor.class).error("tried to output null entity"); //$NON-NLS-1$
            return;
        }
        super.output(entity);
        try {
            super.output(new Log(entity.makeKey(), getUpdate().makeKey(), namespace()));
        } catch (FatalException e) {
            e.printStackTrace();
        }
    }

    /** Separate the record into type and body */
    protected static ParsedRecord parseRecord(String record) throws ParseError {
        if (record == null)
            throw new ParseError("null record"); //$NON-NLS-1$
        try {
            int startOffset = record.indexOf('(');
            String type = record.substring(0, startOffset);
            int endOffset = record.lastIndexOf(')');
            String body = record.substring(startOffset + 1, endOffset);
            return new ParsedRecord(type, body);
        } catch (IndexOutOfBoundsException ex) {
            Logger.getLogger(WordNetExtractor.class).error(
                "something dumb happened with record: " + record); //$NON-NLS-1$
            Logger.getLogger(WordNetExtractor.class).error(ex.getMessage());
            throw new ParseError(ex);
        }
    }

    @Override
    protected String namespace() {
        return NAMESPACE;
    }

    public WriteEvent getUpdate() {
        return this.update;
    }

    public void setUpdate(WriteEvent update) {
        this.update = update;
    }

}
