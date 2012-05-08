/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.nodes.FromXMLProcessNode;
import com.joelsgarage.dataprocessing.nodes.ModelEntityToGazetteerProcessNode;
import com.joelsgarage.dataprocessing.nodes.PassThroughProcessNode;
import com.joelsgarage.dataprocessing.nodes.ToXMLProcessNode;
import com.joelsgarage.dataprocessing.readers.HibernateRecordReader;
import com.joelsgarage.dataprocessing.readers.ModelEntityHibernateRecordReader;
import com.joelsgarage.dataprocessing.readers.TSVFileRecordReader;
import com.joelsgarage.dataprocessing.readers.WordSenseHibernateRecordReader;
import com.joelsgarage.dataprocessing.readers.XMLFileRecordReader;
import com.joelsgarage.dataprocessing.writers.ModelEntityHibernateRecordWriter;
import com.joelsgarage.dataprocessing.writers.StringRecordWriter;
import com.joelsgarage.dataprocessing.writers.XMLFileRecordWriter;
import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualFact;
import com.joelsgarage.model.IndividualProperty;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.QuantityProperty;
import com.joelsgarage.model.StringFact;
import com.joelsgarage.model.StringProperty;
import com.joelsgarage.model.Subclass;
import com.joelsgarage.model.User;
import com.joelsgarage.model.WordSense;
import com.joelsgarage.model.WriteEvent;
import com.joelsgarage.skiploader.SkiploaderOptions.InputFormat;
import com.joelsgarage.skiploader.SkiploaderOptions.Task;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.HibernateUtil;
import com.joelsgarage.util.RealStreamFactory;
import com.joelsgarage.util.StreamFactory;

/**
 * Skiploader is the XML dumper/loader application. It goes into its own JAR.
 * 
 * Does several things:
 * <ul>
 * <li>Load entity data from an XML file into the database.
 * <li>Dump entity data from the database to an XML file
 * <li>Diff an XML file with the database, producing three files
 * </ul>
 * 
 * TODO: find out how to do a root element for dumping. At worst, just do it the dumb way, but
 * appending a string to the file.
 * 
 * OK, this isn't good enough. The dataflow for update is more complicated -- the source may have
 * changed, and we need to keep track of different kinds of change: new rows, new key function,
 * missing rows.
 * 
 * So, a big TODO for today:
 * 
 * Reimplement update with the following flow.
 * 
 * First, a dry run:
 * 
 * <pre>
 * 1. Select rows from the DB by namespace.  For now, select into RAM.
 * 2. Sort them by external-key (means we need an external key column, e.g. URL).  For now, do it in RAM.
 * 3. Slurp the entire XML file into RAM
 * 4. Sort it by external-key.
 * 5. Merge the two lists, producing three outputs:
 * 5.a. XML rows missing from the db
 * 5.b. Db rows missing from the XML
 * 5.c. Matching-key rows with mismatched NAME records
 * 6. Spit out all those three types into three files.
 * </pre>
 * 
 * Then, you can do two things (in separate runs); you can delete everything in the namespace, and
 * you can apply the update.
 * 
 * For now, deletion is unimplemented. It's dangerous, and not necessary; restrict by timestamp
 * instead (e.g. last_modified = max(last_modified)).
 * 
 * TODO: add a "check" task that simply transforms an input XML file into a set of serialized keys,
 * so that i can do "sort | uniq" on it.
 * 
 * TODO: add back the notion of namespace as an option -- at the moment this option is ignored, so
 * we always get the whole DB.
 * 
 * @author joel
 */
@SuppressWarnings("nls")
public class Skiploader {
    public static final int PROGRESS_COUNT = 10000;
    static final String XML_PROLOG = "<?xml";
    /** Same externalKey, different name */
    private static final String CONFLICT = ".diff.conflict";
    /** The externalKey is in the file but not the db */
    private static final String NEW = ".diff.new";
    /** The externalKey is in the db but not the file */
    private static final String MISSING = ".diff.missing";

    /** The external key field of ModelEntity */
    static final String EXTERNAL_KEY = "key";

    private Logger logger = null;
    private DataAccess dataAccess = null;
    private SAXReader reader = null;

    private static final List<Class<? extends ModelEntity>> typesToRead =
        new ArrayList<Class<? extends ModelEntity>>() {
            private static final long serialVersionUID = 1L;

            {
                add(com.joelsgarage.model.Class.class);
                add(ClassMember.class);
                add(Individual.class);
                add(IndividualFact.class);
                add(IndividualProperty.class);
                // Log is very large and not really necessary
                // add(Log.class);
                add(QuantityFact.class);
                add(QuantityProperty.class);
                add(StringFact.class);
                add(StringProperty.class);
                add(Subclass.class);
                add(User.class);
                add(WordSense.class);
                add(WriteEvent.class);
            }
        };

    public Skiploader(DataAccess dataAccess) {
        setLogger(Logger.getLogger(Skiploader.class));
        setDataAccess(dataAccess);
        if (getLogger() == null) {
            System.out.println("gah");
            return;
        }
    }

    /**
     * This is the main method.
     * 
     * @param task
     * @param filename
     * @param database
     * @param inputFormat
     */
    public void doWork(Task task, String filename, String database, InputFormat inputFormat) {
        try {
            getDataAccess().setup();
            switch (task) {
                case DUMP:
                    // Dump everything in the namespace to one big XML file
                    getLogger().info("DUMP using output file: " + filename);
                    // dump includes transaction
                    dump(new FileOutputStream(filename));

                    break;
                case LOAD:
                    if (inputFormat.equals(InputFormat.XML)) {
                        // Load everything from one XML file into the DB
                        getLogger().info("LOAD using input file: " + filename);
                        // maybe put this in the Main rather than here?
                        // load includes transaction begin/commit
                        load(database, new FileInputStream(filename));
                    } else if (inputFormat.equals(InputFormat.TSV)) {
                        loadTSV(database, filename);
                    } else {
                        getLogger().error("Strange input format: " + inputFormat.toString());
                        return;
                    }
                    break;
                case DIFF:
                    // Compute differences, construct three output files
                    getLogger().info("DIFF using input file: " + filename);
                    // it would be nice if the differ didn't know anything about the files.
                    getLogger().info("using input file: " + filename);
                    // diff includes transaction
                    diff(new FileInputStream(filename), // maybe put this in the Main rather than
                        // here?
                        new FileOutputStream(filename + MISSING), // Records in the DB
                        // namespace, new FileOutputStream(filename + MISSING), // Records in the DB

                        // that are missing from
                        // the file
                        new FileOutputStream(filename + NEW), // Records in the file that are
                        // missing
                        // from the db, i.e. that are new.
                        new FileOutputStream(filename + CONFLICT)); // Records with the same
                    // externalKeys but different names.

                    break;
                case LOADARC:
                    getLogger().info("LOADARC using input file: " + filename);
                    // loadarc does NOT include the transaction -- load does.
                    loadArc(database, new FileInputStream(filename));
                    break;
                case DUMPGATE:
                    // Dump everything in the database to one big GATE Gazetteer file
                    getLogger().info("DUMPGATE using input file: " + filename);
                    dumpGate(new FileOutputStream(filename));
                    break;
                default:
                    getLogger().error("strange task: " + task.toString());
            }
        } catch (FileNotFoundException e) {
            // Maybe there's stuff to clean up, maybe not.
            getLogger().error("File Not Found Exception: " + e.getMessage());
            e.printStackTrace();
        } catch (FatalException e) {
            // Maybe there's stuff to clean up, maybe not.
            getLogger().error("Fatal Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // roll back if the transaction is still open.
            getDataAccess().cleanup();
        }
    }

    /**
     * Load the data from the DB, in the specified namespace, into RAM, and then dump it out as an
     * XML file. Sets up input and output and calls the stream dumper.
     * 
     * TODO; put namespace back in
     */
    protected void dump(OutputStream output) throws FatalException {
        SessionFactory factory = HibernateUtil.createSessionFactory(null, // default config
            null); // configured database
        if (factory == null)
            throw new FatalException("can't make session factory");

        Session readerSession = factory.openSession();

        RecordReader<ModelEntity> recordReader =
            new ModelEntityHibernateRecordReader(readerSession, null);
        RecordWriter<Element> writer = new XMLFileRecordWriter(output);
        dumpProcess(recordReader, writer);
        readerSession.close();
    }

    /** Stream based dumper */
    protected void dumpProcess(RecordReader<ModelEntity> recordReader, RecordWriter<Element> writer) {
        ProcessNode<ModelEntity, Element> node = new ToXMLProcessNode(recordReader, writer, 0, 0);
        node.run();
    }

    public void dumpGateQuietly(File file) {
        Logger.getLogger(Skiploader.class).info("dumpGateQuietly"); //$NON-NLS-1$

        try {
            dumpGate(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            Logger.getLogger(Skiploader.class).error(e.getMessage());
            e.printStackTrace();
        } catch (FatalException e) {
            Logger.getLogger(Skiploader.class).error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets up input and output and calls the stream based gate dumper.
     * 
     * Dumps all records for all namespaces of type:
     * 
     * <ul>
     * <li>Individual
     * <li>Class
     * <li>IndividualProperty
     * </ul>
     * 
     * TODO: dump some other types too. Note that many of them (e.g. facts) have meaningless names,
     * i.e. they are collections of other entities, not really detectable entities in themselves, so
     * we'll probably never dump facts.
     */
    protected void dumpGate(OutputStream output) throws FatalException {
        Logger.getLogger(Skiploader.class).info("dumpGate"); //$NON-NLS-1$

        StringRecordWriter writer = new StringRecordWriter(output);

        writer.open();

        dumpGate(writer);

        writer.close();
    }

    /**
     * Dump all the WordSense records.
     * 
     * TODO: also do UnitSynonym, or make those refer to WordSenses.
     */
    protected void dumpGate(RecordWriter<String> writer) throws FatalException {
        if (writer == null) {
            Logger.getLogger(Skiploader.class).info("null writer"); //$NON-NLS-1$
            return;
        }

        SessionFactory factory = HibernateUtil.createSessionFactory(null, // default config
            null); // configured database
        if (factory == null)
            throw new FatalException("can't make session factory");

        Session readerSession = factory.openSession();
        HibernateRecordReader<WordSense> recordReader =
            new WordSenseHibernateRecordReader(readerSession, null);
        try {
            recordReader.open();
            Logger.getLogger(Skiploader.class).info("dumpGate foo2"); //$NON-NLS-1$
            dumpGateProcess(recordReader, writer);
            Logger.getLogger(Skiploader.class).info("dumpGate foo3"); //$NON-NLS-1$
            recordReader.close();
        } catch (InitializationException e) {
            Logger.getLogger(Skiploader.class).error("Couldn't open reader.");
        }
        Logger.getLogger(Skiploader.class).info("dumpGate done"); //$NON-NLS-1$
        // TODO: find out why i used to do this
        // readerSession.close();
        Logger.getLogger(Skiploader.class).info("dumpGate done"); //$NON-NLS-1$

    }

    /** Dumps all the NLS (WordSense) records to a GATE gazetteer */
    protected void dumpGateProcess(RecordReader<WordSense> recordReader, RecordWriter<String> writer) {
        ProcessNode<WordSense, String> node =
            new ModelEntityToGazetteerProcessNode(recordReader, writer, 0, 0);
        node.work();
    }

    /**
     * Load the configured XML file into RAM, and then load it into the database. Sets up input and
     * output and calls the stream loader.
     */
    protected void load(String database, InputStream input) throws FatalException {
        SessionFactory factory = HibernateUtil.createSessionFactory(null, // default config
            database); // configured database
        if (factory == null)
            throw new FatalException("can't make session factory");

        Session session = factory.openSession();
        RecordReader<Element> recordReader = new XMLFileRecordReader(input);
        try {
            RecordWriter<ModelEntity> writer = new ModelEntityHibernateRecordWriter(session);
            loadProcess(recordReader, writer);
        } catch (InitializationException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    protected void loadTSV(String database, String filename) throws FatalException {
        SessionFactory factory = HibernateUtil.createSessionFactory(null, // default config
            database); // configured database
        if (factory == null)
            throw new FatalException("can't make session factory");

        Session session = factory.openSession();
        StreamFactory streamFactory = new RealStreamFactory();
        RecordReader<ModelEntity> recordReader =
            new TSVFileRecordReader(streamFactory, filename, typesToRead);
        try {
            RecordWriter<ModelEntity> writer = new ModelEntityHibernateRecordWriter(session);
            loadProcessPassthrough(recordReader, writer);
        } catch (InitializationException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /** The stream based loader, easier to test. */
    protected void loadProcess(RecordReader<Element> recordReader, RecordWriter<ModelEntity> writer) {
        ProcessNode<Element, ModelEntity> node = new FromXMLProcessNode(recordReader, writer, 0, 0);
        node.setProgressCount(PROGRESS_COUNT);
        node.run();
    }

    protected void loadProcessPassthrough(RecordReader<ModelEntity> recordReader,
        RecordWriter<ModelEntity> writer) {
        ProcessNode<ModelEntity, ModelEntity> node =
            new PassThroughProcessNode<ModelEntity>(recordReader, writer, 0, 0);
        node.setProgressCount(PROGRESS_COUNT);
        node.run();
    }

    /**
     * Compare the input stream to the database, and construct three output streams.
     * 
     * TODO: replace this with a stream join.
     * 
     * @param input
     * @param missingOutput
     *            records in the DB that are not in the file
     * @param newOutput
     *            records in the file that are not in the DB
     * @param conflictOutput
     *            records with matching externalKeys but mismatched names
     * @throws FatalException
     */
    protected void diff(InputStream input, OutputStream missingOutput, OutputStream newOutput,
        OutputStream conflictOutput) throws FatalException {
        Comparator<Element> externalKeyComparator = new SkiploaderUtil.CompareExternalKey();

        XMLWriter missingWriter = SkiploaderUtil.makeXMLWriter(missingOutput);
        XMLWriter newWriter = SkiploaderUtil.makeXMLWriter(newOutput);
        XMLWriter conflictWriter = SkiploaderUtil.makeXMLWriter(conflictOutput);

        List<Element> fileElements = SkiploaderUtil.readElementsFromStream(input);

        getDataAccess().beginTransaction();
        List<Element> dbElements = getElementsFromDB();
        getDataAccess().commit();

        // TODO: externalize these sorts; the streams must be pre-sorted.
        Collections.sort(fileElements, externalKeyComparator);
        Collections.sort(dbElements, externalKeyComparator);

        ListIterator<Element> fileIterator = fileElements.listIterator();
        ListIterator<Element> dbIterator = dbElements.listIterator();

        SkiploaderUtil.startDoc(missingWriter);
        SkiploaderUtil.startDoc(newWriter);
        SkiploaderUtil.startDoc(conflictWriter);

        int missingElementCount = 0;
        int newElementCount = 0;
        int conflictElementCount = 0;
        int matchingElementCount = 0;

        if (fileIterator.hasNext() && dbIterator.hasNext()) {
            // now we know both iterators are valid.
            Element fileElement = fileIterator.next();
            Element dbElement = dbIterator.next();

            while (true) {
                int comparison = externalKeyComparator.compare(fileElement, dbElement);

                if (comparison < 0) {
                    // fileElement < dbElement
                    SkiploaderUtil.writeElement(newWriter, fileElement);

                    ++newElementCount;
                    if (fileIterator.hasNext()) {
                        fileElement = fileIterator.next();
                    } else {
                        break;
                    }
                } else if (comparison > 0) {
                    // fileElement > dbElement
                    SkiploaderUtil.writeElement(missingWriter, dbElement);
                    ++missingElementCount;
                    if (dbIterator.hasNext()) {
                        dbElement = dbIterator.next();
                    } else {
                        break;
                    }
                } else {
                    // fileElement == dbElement
                    // Check for conflict.
                    if (SkiploaderUtil.elementNameEquals(fileElement, dbElement)) {
                        // The two are identical and can be ignored.
                        ++matchingElementCount;
                    } else {
                        // Write the conflicting elements inside a little pair container
                        Element conflictElement = DocumentHelper.createElement("conflict");
                        conflictElement.add(fileElement.createCopy());
                        conflictElement.add(dbElement.createCopy());
                        SkiploaderUtil.writeElement(conflictWriter, conflictElement);
                        ++conflictElementCount;
                    }
                    // increment both iterators if possible
                    if (fileIterator.hasNext() && dbIterator.hasNext()) {
                        fileElement = fileIterator.next();
                        dbElement = dbIterator.next();
                    } else {
                        break;
                    }
                }
            }
        }

        // write the leftovers.

        while (fileIterator.hasNext()) {
            Element fileElement = fileIterator.next();
            SkiploaderUtil.writeElement(newWriter, fileElement);
            getLogger().info("new leftover: " + fileElement.element(EXTERNAL_KEY).asXML());
            ++newElementCount;
        }
        while (dbIterator.hasNext()) {
            Element dbElement = dbIterator.next();
            if (dbElement == null) {
                Logger.getLogger(Skiploader.class).error("weird null element");
                continue;
            }
            SkiploaderUtil.writeElement(missingWriter, dbElement);
            Element el = dbElement.element(EXTERNAL_KEY);
            if (el == null) {
                Logger.getLogger(Skiploader.class).error(
                    "weird null key for element: " + dbElement.asXML());
                continue;
            }
            getLogger().info("missing leftover: " + dbElement.element(EXTERNAL_KEY).asXML());
            ++missingElementCount;
        }

        SkiploaderUtil.endDoc(missingWriter);
        SkiploaderUtil.endDoc(newWriter);
        SkiploaderUtil.endDoc(conflictWriter);

        getLogger().info("fileElementCount: " + fileElements.size());
        getLogger().info("dbElementCount: " + dbElements.size());
        getLogger().info("matchingElementCount: " + String.valueOf(matchingElementCount));
        getLogger().info("missingElementCount: " + String.valueOf(missingElementCount));
        getLogger().info("newElementCount: " + String.valueOf(newElementCount));
        getLogger().info("conflictElementCount: " + String.valueOf(conflictElementCount));
    }

    /**
     * Load a set of concatenated XML files, as produced by the extractor.
     * 
     * It works by copying the input to a buffer, until it finds a prolog indicating a second file.
     * Then it constructs an inputstream from the buffer, and loads it.
     * 
     * There's probably some smarter way to do this.
     * 
     * @param input
     */
    protected void loadArc(String database, InputStream input) throws FatalException {
        int size = XML_PROLOG.length();
        byte[] circularBuffer = new byte[size];
        int pos = 0; // position in buffer
        StringBuilder arcBuilder = new StringBuilder();
        // start by filling the buffer
        try {
            int startBytesRead = input.read(circularBuffer);
            if (startBytesRead < size) {
                // pathologically short file.
                throw new FatalException("input file too short");
            }
            if (!SkiploaderUtil.matchBuffer(circularBuffer, pos)) {
                // file that doesn't start with the prolog
                throw new FatalException("file should start with prolog");
            }
            // ok if we got here, then the buffer is full with the prolog at zero, so we can start
            // looking for the next one, one byte at a time. I'm sure there's a better way.
            byte[] b = new byte[1];
            while (input.read(b) > 0) {
                // read the buffer at pos
                byte p = circularBuffer[pos];
                // push the byte on the result
                arcBuilder.append((char) p); // casting byte -> char is ok (reverse is not)
                // put the new byte in the buffer
                circularBuffer[pos] = b[0];
                // increment pos
                pos = (pos + 1) % circularBuffer.length;
                if (SkiploaderUtil.matchBuffer(circularBuffer, pos)) {
                    // if the buffer has a prolog in it, then the stringbuilder contains
                    // the whole document we want to process.
                    // so process it...
                    // TODO: jeezus, some sort of pipe stream here, with more than one thread,
                    // so i don't have to make a big copy of it.
                    String doc = arcBuilder.toString();
                    // load handles the transaction
                    load(database, new ByteArrayInputStream(doc.getBytes()));
                    // and then purge the stringbuilder.
                    arcBuilder = new StringBuilder();
                }
            }
            // Last doc needs to be flushed.
            if (arcBuilder.length() > 0) { // this is maybe extraneous.
                // get the last few chars from the buffer, starting at pos
                arcBuilder.append(new String(circularBuffer, pos, circularBuffer.length - pos));
                // and the last of them, ending at pos-1
                arcBuilder.append(new String(circularBuffer, 0, pos));
                load(database, new ByteArrayInputStream(arcBuilder.toString().getBytes()));
            }
        } catch (IOException e) {
            throw new FatalException(e);
        }

    }

    /**
     * Load the elements from the database, into a generic List.
     * 
     * TODO: some sort of iterator interface instead?
     * 
     * TODO: add namespace constraint.
     * 
     * @return
     * @throws FatalException
     */
    protected List<Element> getElementsFromDB() throws FatalException {
        return getDataAccess().fetchElements();
    }

    /**
     * Save the specified element to the database, or barf, kinda like WriteOrDie, but for the
     * database.
     * 
     * @param element
     * @throws FatalException
     */
    protected void saveElement(Element element) throws FatalException {
        getDataAccess().saveElement(element);
    }

    /**
     * Save a list of elements to the database.
     * 
     * @param elements
     */
    protected void saveElementsToDB(Collection<Element> elements) throws FatalException {
        // this set of elements is a transaction. dunno if that's the right size, but
        // it's better than one row, and better than a whole ARC.
        for (Element element : elements) {
            saveElement(element);
        }
    }

    public DataAccess getDataAccess() {
        return this.dataAccess;
    }

    public void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public SAXReader getReader() {
        return this.reader;
    }

    public void setReader(SAXReader reader) {
        this.reader = reader;
    }
}
