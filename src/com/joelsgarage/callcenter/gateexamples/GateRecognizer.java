package com.joelsgarage.callcenter.gateexamples;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.corpora.DocumentImpl;
import gate.creole.ANNIETransducer;
import gate.creole.POSTagger;
import gate.creole.SerialAnalyserController;
import gate.creole.annotdelete.AnnotationDeletePR;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.orthomatcher.OrthoMatcher;
import gate.creole.splitter.SentenceSplitter;
import gate.creole.tokeniser.DefaultTokeniser;
import gate.util.GateException;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.EngineManager;
import com.joelsgarage.callcenter.Recognizer;
import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.IndividualProperty;
import com.joelsgarage.skiploader.DataAccess;
import com.joelsgarage.skiploader.HibernateDataAccess;
import com.joelsgarage.skiploader.Skiploader;
import com.joelsgarage.util.InitializationException;
import com.joelsgarage.util.XMLUtil;

/**
 * Runs ANNIE plus a custom Gazetteer lexicon and a custom JAPE grammar. The expectation is that
 * something else will pick up the annotations thus created.
 */
public class GateRecognizer {

    /** The output payload root element name */
    public static final String USER_INPUT = "UserInput"; //$NON-NLS-1$
    /** The element containing the verbatim text */
    public static final String UTTERANCE = "utterance"; //$NON-NLS-1$

    private static final String ANNIE_ = "ANNIE_"; //$NON-NLS-1$
    /** The creole.xml herein specifies the extra gazetteer list */
    private static final String GATEEXAMPLES_PLUGINS_GATE_RECOGNIZER =
        "/gateexamples/plugins/GateRecognizer/"; //$NON-NLS-1$
    /** generic analyzer controller */
    private SerialAnalyserController annieController;
    /** The current run's corpus */
    private Corpus corpus;

    private ExternalKey filterClassKey = null;
    /** If not null, find only property words whose domainClass is the specified class */
    private ExternalKey propertyFilterClassKey = null;

    public GateRecognizer() throws InitializationException {
        Logger.getLogger(GateRecognizer.class).info("Initialising ANNIE..."); //$NON-NLS-1$

        try {
            Logger.getLogger(GateRecognizer.class)
                .info("Setting GateHome to: " + Util.GATEEXAMPLES); //$NON-NLS-1$

            String filename = "/gateexamples/user-gate.xml"; //$NON-NLS-1$
            Logger.getLogger(GateRecognizer.class).info("trying a file: " + filename); //$NON-NLS-1$
            URL fileURL = this.getClass().getResource(filename);
            Logger.getLogger(GateRecognizer.class).info("succeeded: " + fileURL.toString()); //$NON-NLS-1$

            String dirname = "/gateexamples"; //$NON-NLS-1$
            Logger.getLogger(GateRecognizer.class).info("trying a dir: " + dirname); //$NON-NLS-1$
            URL dirURL = this.getClass().getResource(dirname);
            Logger.getLogger(GateRecognizer.class).info("succeeded: " + dirURL.toString()); //$NON-NLS-1$

            String className = "GateRecognizer.class"; //$NON-NLS-1$
            Logger.getLogger(GateRecognizer.class).info("trying a classname: " + className); //$NON-NLS-1$
            URL classURL = this.getClass().getResource(className);
            Logger.getLogger(GateRecognizer.class).info("succeeded: " + classURL.toString()); //$NON-NLS-1$

            if (classURL.getProtocol().equals("jar")) { //$NON-NLS-1$

                Logger.getLogger(GateRecognizer.class).info("and a one"); //$NON-NLS-1$
                // in the jar, gateHome is the root.
                Logger.getLogger(GateRecognizer.class).info("foo 1"); //$NON-NLS-1$

                String classURLStr = classURL.getFile();
                Logger.getLogger(GateRecognizer.class).info("foo 2: " + classURLStr); //$NON-NLS-1$

                File classJarFile =
                    new File(new URI(classURLStr.substring(0, classURLStr.indexOf('!'))));
                Logger.getLogger(GateRecognizer.class).info("foo 3: " + classJarFile.toString()); //$NON-NLS-1$

                // This is the directory containing the jar.
                File finalFile = classJarFile.getParentFile();
                Logger.getLogger(GateRecognizer.class).info("foo 4: " + finalFile.toString()); //$NON-NLS-1$

                String base = finalFile.toString();

                String dumpFileStr =
                    base + Util.GATEEXAMPLES
                        + "/plugins/GateRecognizer/resources/gazetteer/dumpgate.lst"; //$NON-NLS-1$

                //
                //
                // WARNING // WARNING // WARNING // WARNING // WARNING // WARNING
                // WARNING // WARNING // WARNING // WARNING // WARNING // WARNING
                //
                // This is where I write the gate lexicon on the fly. It is not the right place to
                // put it.

                // NOTE because I write this file on the fly, I can't store it in the jar.
                //

                File dumpFile = new File(dumpFileStr);

                dumpGate(dumpFile);

                Logger.getLogger(GateRecognizer.class).info(
                    "got dumpgate file: " + dumpFile.toString()); //$NON-NLS-1$

                try {
                    InputStream inputStream = new FileInputStream(dumpFile);
                    Logger.getLogger(GateRecognizer.class).info("got inputstream."); //$NON-NLS-1$
                    int foo;
                    while ((foo = inputStream.read()) != -1) {
                        char c = (char) foo;
                        System.out.print(c);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Logger.getLogger(GateRecognizer.class).info("failed to find dumpgate file"); //$NON-NLS-1$
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.getLogger(GateRecognizer.class).info("failed to read dumpgate file"); //$NON-NLS-1$
                }

                // /////////////////////////////////////
                // ///////////////////////////////////// (end hack)
                // /////////////////////////////////////
                // /////////////////////////////////////

                if (Gate.getGateHome() == null) {

                    String gateHomeStr = base + Util.GATEEXAMPLES;
                    Logger.getLogger(GateRecognizer.class).info("foo 5: " + gateHomeStr); //$NON-NLS-1$

                    File gateHomeFile = new File(gateHomeStr);

                    Logger.getLogger(GateRecognizer.class)
                        .info("foo 6: " + gateHomeFile.toString()); //$NON-NLS-1$

                    Gate.setGateHome(gateHomeFile);
                } else {
                    Logger.getLogger(GateRecognizer.class)
                        .error("GateHome set early; pressing on."); //$NON-NLS-1$
                }

                Logger.getLogger(GateRecognizer.class).info(
                    "Setting UserConfigFile to: " + Util.GATEEXAMPLES_USER_GATE_XML); //$NON-NLS-1$
                if (Gate.getUserConfigFile() == null) {
                    String userConfigFileStr = base + Util.GATEEXAMPLES_USER_GATE_XML;

                    Logger.getLogger(GateRecognizer.class).info("bar 1: " + userConfigFileStr); //$NON-NLS-1$

                    File userConfigFile = new File(userConfigFileStr);

                    Logger.getLogger(GateRecognizer.class).info(
                        "bar 2: " + userConfigFile.toString()); //$NON-NLS-1$

                    Gate.setUserConfigFile(userConfigFile);
                } else {
                    Logger.getLogger(GateRecognizer.class).error(
                        "UserConfigFile set early; pressing on."); //$NON-NLS-1$
                }
                Logger.getLogger(GateRecognizer.class).info("Initialising GATE..."); //$NON-NLS-1$

                Gate.init();

                Logger.getLogger(GateRecognizer.class).info("...GATE initialised"); //$NON-NLS-1$

                // these are no longer in the jar, they're just files.

                // Gate.getCreoleRegister().registerDirectories(
                // GateRecognizer.class.getResource(Util.GATEEXAMPLES_PLUGINS_ANNIE));
                //
                // Gate.getCreoleRegister().registerDirectories(
                // this.getClass().getResource(GATEEXAMPLES_PLUGINS_GATE_RECOGNIZER));

                try {
                    Gate.getCreoleRegister().registerDirectories(
                        new File(base + Util.GATEEXAMPLES_PLUGINS_ANNIE).toURI().toURL());

                    Gate.getCreoleRegister().registerDirectories(
                        new File(base + GATEEXAMPLES_PLUGINS_GATE_RECOGNIZER).toURI().toURL());
                } catch (MalformedURLException e) {
                    Logger.getLogger(GateRecognizer.class).error("registerDirectories failed"); //$NON-NLS-1$

                    e.printStackTrace();
                }
            } else {
                if (Gate.getGateHome() == null) {
                    Logger.getLogger(GateRecognizer.class).info("and a one"); //$NON-NLS-1$

                    // it's a normal file, a subdir
                    URL gateHomeURL = this.getClass().getResource(Util.GATEEXAMPLES);
                    Logger.getLogger(GateRecognizer.class).info("and a two"); //$NON-NLS-1$

                    URI gateHomeURI = gateHomeURL.toURI();
                    Logger.getLogger(GateRecognizer.class).info("and a three"); //$NON-NLS-1$

                    File gateHomeFile = new File(gateHomeURI);
                    Logger.getLogger(GateRecognizer.class).info("and a four"); //$NON-NLS-1$

                    Gate.setGateHome(gateHomeFile);
                    Logger.getLogger(GateRecognizer.class).info("ba da dee dee dop doo bop"); //$NON-NLS-1$
                } else {
                    Logger.getLogger(GateRecognizer.class).info("GateHome set early; pressing on."); //$NON-NLS-1$
                }

                Logger.getLogger(GateRecognizer.class).info(
                    "Setting UserConfigFile to: " + Util.GATEEXAMPLES_USER_GATE_XML); //$NON-NLS-1$
                if (Gate.getUserConfigFile() == null) {
                    Gate.setUserConfigFile(new File(GateRecognizer.class.getResource(
                        Util.GATEEXAMPLES_USER_GATE_XML).toURI()));
                } else {
                    Logger.getLogger(GateRecognizer.class).info(
                        "UserConfigFile set early; pressing on."); //$NON-NLS-1$
                }
                Logger.getLogger(GateRecognizer.class).info("Initialising GATE..."); //$NON-NLS-1$

                Gate.init();

                Logger.getLogger(GateRecognizer.class).info("...GATE initialised"); //$NON-NLS-1$

                // these are no longer in the jar, they're just files.

                // Gate.getCreoleRegister().registerDirectories(
                // GateRecognizer.class.getResource(Util.GATEEXAMPLES_PLUGINS_ANNIE));
                //
                // Gate.getCreoleRegister().registerDirectories(
                // this.getClass().getResource(GATEEXAMPLES_PLUGINS_GATE_RECOGNIZER));

                Gate.getCreoleRegister().registerDirectories(
                    GateRecognizer.class.getResource(Util.GATEEXAMPLES_PLUGINS_ANNIE));

                Gate.getCreoleRegister().registerDirectories(
                    this.getClass().getResource(GATEEXAMPLES_PLUGINS_GATE_RECOGNIZER));
            }

            Logger.getLogger(GateRecognizer.class).info(
                "Creating ANNIE (this may take several minutes) ..."); //$NON-NLS-1$

            this.annieController =
                (SerialAnalyserController) Factory.createResource(SerialAnalyserController.class
                    .getName(), Factory.newFeatureMap(), // no parameters
                    Factory.newFeatureMap(), // no features
                    ANNIE_ + Gate.genSym());

            String[] PRNames =
                {
                    AnnotationDeletePR.class.getName(),
                    DefaultTokeniser.class.getName(),
                    DefaultGazetteer.class.getName(),
                    Gazetteer.class.getName(),
                    SentenceSplitter.class.getName(),
                    POSTagger.class.getName(),
                    ANNIETransducer.class.getName(),
                    Transducer.class.getName(),
                    OrthoMatcher.class.getName() };

            for (int i = 0; i < PRNames.length; i++) {
                Logger.getLogger(GateRecognizer.class).info("loading PR " + PRNames[i]); //$NON-NLS-1$

                FeatureMap params = Factory.newFeatureMap(); // use default parameters
                ProcessingResource pr =
                    (ProcessingResource) Factory.createResource(PRNames[i], params);

                // TODO: keep the gazetteer handy so I can add and remove (mostly add) new lexicon
                // to it.
                
                Logger.getLogger(GateRecognizer.class).info("PR class: " + pr.getClass().getName()); //$NON-NLS-1$
                if (pr instanceof Gazetteer) {
                    Gazetteer gaz = (Gazetteer) pr;
                    gaz.logAllLists();
                    Logger.getLogger(GateRecognizer.class).info(
                        "Gazetteer url: " + gaz.getListsURL().toString()); //$NON-NLS-1$
                    Logger.getLogger(GateRecognizer.class).info(
                        "LD url: " + gaz.getLinearDefinition().getURL().toString()); //$NON-NLS-1$
                }

                this.annieController.add(pr);
            }

            Logger.getLogger(GateRecognizer.class).info("...ANNIE loaded"); //$NON-NLS-1$

        } catch (GateException e) {
            Logger.getLogger(GateRecognizer.class).error("Gate exception: " + e.getMessage()); //$NON-NLS-1$
            throw new InitializationException(e);
        } catch (URISyntaxException e) {
            Logger.getLogger(GateRecognizer.class).error("URI syntax exception: " + e.getMessage()); //$NON-NLS-1$
            throw new InitializationException(e);
        } catch (IllegalArgumentException e) {
            Logger.getLogger(GateRecognizer.class).error(
                "illegal argument exception: " + e.getMessage()); //$NON-NLS-1$
            throw new InitializationException(e);
        }
    }

    /**
     * Dump the contents of the database so that the GateRecognizer can see it.
     * 
     * HACK: This is not the right place to put this function.
     * 
     * TODO: allow the lexicon to be dynamically updated somehow
     * 
     * TODO: think about using the "ontology aware" thing that GATE does.
     */
    protected void dumpGate(File dumpFile) {
        Logger.getLogger(GateRecognizer.class).info("dumpGate1"); //$NON-NLS-1$
        DataAccess dataAccess = new HibernateDataAccess(null, null);
        Logger.getLogger(GateRecognizer.class).info("dumpGate2"); //$NON-NLS-1$

        Skiploader skiploader = new Skiploader(dataAccess);
        Logger.getLogger(GateRecognizer.class).info("dumpGate3"); //$NON-NLS-1$

        skiploader.dumpGateQuietly(dumpFile);
        Logger.getLogger(GateRecognizer.class).info("dumpGate4"); //$NON-NLS-1$

    }

    /** Executes the pipeline on the specified input, leaving the annotations in the corpus */
    @SuppressWarnings("unchecked")
    public void run(String input) throws GateException, InitializationException {
        if (this.annieController == null)
            throw new InitializationException("Must initialize Annie first"); //$NON-NLS-1$
        if (input == null || input.length() == 0)
            return;

        this.corpus = Factory.newCorpus("Foo Corpus Label"); //$NON-NLS-1$

        FeatureMap params = Factory.newFeatureMap();
        params.put(Document.DOCUMENT_PRESERVE_CONTENT_PARAMETER_NAME, new Boolean(true));
        params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, input);

        Logger.getLogger(GateRecognizer.class).info("Creating doc for " + String.valueOf(input)); //$NON-NLS-1$

        Document doc = (Document) Factory.createResource(DocumentImpl.class.getName(), params);

        this.corpus.add(doc);

        this.annieController.setCorpus(this.corpus);
        Logger.getLogger(GateRecognizer.class).info("Running ANNIE..."); //$NON-NLS-1$
        this.annieController.execute();
        Logger.getLogger(GateRecognizer.class).info("...ANNIE complete"); //$NON-NLS-1$
    }

    /**
     * True if the specified individual is a member of the specified class.
     * 
     * TODO: move this to some Util class.
     * 
     * @param individualKey
     * @param classKey
     * @return
     */
    protected static boolean classMemberExists(ExternalKey individualKey, ExternalKey classKey) {
        // Session session = HibernateUtil.createSessionFactory(null, null).openSession();
        Logger.getLogger(GateRecognizer.class).info(
            "testing classmember existence for individual: " + individualKey.toString() // //$NON-NLS-1$
                + " class: " + classKey.toString()); //$NON-NLS-1$

        Map<String, Object> queryTerms = new HashMap<String, Object>();
        queryTerms.put("individualKey", individualKey);//$NON-NLS-1$
        queryTerms.put("classKey", classKey); //$NON-NLS-1$

        ClassMember result =
            com.joelsgarage.callcenter.Util.getCompound(ClassMember.class, queryTerms);

        if (result == null) {
            Logger.getLogger(GateRecognizer.class).info("nope!"); //$NON-NLS-1$
            return false;
        }
        Logger.getLogger(GateRecognizer.class).info("got it!"); //$NON-NLS-1$
        return true;
    }

    /**
     * Collect UserInput annotations from the corpus, producing a UserInput XML Document appropriate
     * for the Recognizer function.
     * 
     * We take the FIRST UserInput annotation only.
     * 
     * We take *ALL* the features on the UserInput annotation.
     * 
     * This is where the filtering needs to go.
     * 
     * But since this has no understanding at all of the payload, it's a hard place to put it.
     * 
     * But JAPE isn't any better. I'd have to figure out how to inject a document feature (easy) and
     * then use it in the pattern (a mystery), and it involves a DB lookup anyway so, not such a
     * great place to put it.
     * 
     * so record event=preference only if exists(classmember(classkey, individualkey)
     * 
     */
    @SuppressWarnings("unchecked")
    public org.w3c.dom.Document getUserInput() {
        Logger.getLogger(GateRecognizer.class).info("getUserInput"); //$NON-NLS-1$
        // The response document is a bag of text elements.
        Map<String, String> elements = new HashMap<String, String>();

        AnnotationSet annotationSet = getAnnotations(USER_INPUT);
        if (annotationSet != null) {
            Iterator annotationIter = annotationSet.iterator();
            while (annotationIter.hasNext()) {
                Annotation currAnnot = (Annotation) annotationIter.next();
                // this should be UserInput, since that's what we asked for.
                Logger.getLogger(GateRecognizer.class).info(
                    "Got annotation: " + currAnnot.getType()); //$NON-NLS-1$

                FeatureMap featureMap = currAnnot.getFeatures();
                // TODO: test this filter

                String event = (String) featureMap.get("event"); //$NON-NLS-1$
                String type = (String) featureMap.get("type");//$NON-NLS-1$

                // Accept only individuals of the specified class.
                if (this.filterClassKey != null && type != null && event != null) {
                    // filter applies to these 'individual' type annotations
                    if (type.equals("individual") && //$NON-NLS-1$
                        (event.equals("preference") || //$NON-NLS-1$
                        event.equals("model_entity"))) { //$NON-NLS-1$
                        String individualKeyString = (String) featureMap.get("key"); //$NON-NLS-1$
                        if (!classMemberExists(new ExternalKey(individualKeyString),
                            this.filterClassKey)) {
                            Logger.getLogger(GateRecognizer.class).info(
                                "skipping filtered annotation " + currAnnot.getType()); //$NON-NLS-1$
                            continue;
                        }
                    }
                }

                // Accept only properties whose domain is as specified.
                if (this.propertyFilterClassKey != null && type != null && event != null) {
                    if (type.equals("individual_property")) { //$NON-NLS-1$
                        String propertyKeyString = (String) featureMap.get("key"); //$NON-NLS-1$
                        ExternalKey propertyKey = new ExternalKey(propertyKeyString);
                        IndividualProperty property =
                            com.joelsgarage.callcenter.Util.getPrimary(IndividualProperty.class,
                                propertyKey);
                        // This shouldn't happen; skip it if it does.
                        if (property == null) {
                            Logger.getLogger(GateRecognizer.class).info(
                                "Can't dereference key: " + propertyKey.toString()); //$NON-NLS-1$
                            continue;
                        }
                        ExternalKey domainClassKey = property.getDomainClassKey();
                        if (domainClassKey == null) {
                            Logger.getLogger(GateRecognizer.class).info(
                                "Domainless property: " + property.getName()); //$NON-NLS-1$
                            continue;
                        }
                        // Skip annotations of a different class
                        if (!domainClassKey.equals(this.propertyFilterClassKey)) {
                            Logger.getLogger(GateRecognizer.class).info(
                                "skipping filtered annotation " + currAnnot.getType()); //$NON-NLS-1$
                            continue;
                        }
                    }
                }

                // OK, we got one we like, grab its feature map.
                for (Map.Entry<Object, Object> entry : featureMap.entrySet()) {
                    Logger.getLogger(GateRecognizer.class).info(
                        "Got feature: " + String.valueOf(entry.getKey()) //$NON-NLS-1$
                            + " == " + String.valueOf(entry.getValue())); //$NON-NLS-1$
                    elements.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                }

                // Also add a feature for the annotated text.
                Long startOffset = currAnnot.getStartNode().getOffset();
                Long endOffset = currAnnot.getEndNode().getOffset();
                String annotatedContent;
                try {
                    annotatedContent =
                        getDocument().getContent().getContent(startOffset, endOffset).toString();
                    elements.put("text", annotatedContent); //$NON-NLS-1$
                } catch (InvalidOffsetException e) {
                    e.printStackTrace();
                    Logger.getLogger(GateRecognizer.class)
                        .error("Failed to get text of annotation"); //$NON-NLS-1$
                }
                break;
            }
        }

        if (elements.size() == 0) {
            // No rules fired, i.e. nothing recognized. So this is unrecognized input.
            elements.put(EngineManager.EVENT, Recognizer.UNRECOGNIZED_INPUT);
        }
        // reset the filters
        this.filterClassKey = null;
        this.propertyFilterClassKey = null;

        // UserInput always contains the verbatim utterance.
        elements.put(UTTERANCE, getDocument().getContent().toString());
        return XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
            USER_INPUT, elements);
    }

    public void setPreferenceFilter(ExternalKey classKey) {
        this.filterClassKey = classKey;
    }

    public void setPropertyFilterClassKey(ExternalKey classKey) {
        this.propertyFilterClassKey = classKey;
    }

    /** Return the one GATE document in the current GATE corpus */
    @SuppressWarnings("unchecked")
    public Document getDocument() {
        if (this.corpus == null)
            return null;
        Iterator corpusIter = this.corpus.iterator();

        if (!corpusIter.hasNext()) {
            Logger.getLogger(GateRecognizer.class).error("No documents in corpus."); //$NON-NLS-1$
            return null;
        }

        Document doc = (Document) corpusIter.next();

        if (corpusIter.hasNext()) {
            Logger.getLogger(GateRecognizer.class).error("Ignoring extra document in corpus."); //$NON-NLS-1$
        }
        return doc;
    }

    /** Fetch the specified annotations. */
    @SuppressWarnings("unchecked")
    public AnnotationSet getAnnotations(String annotationName) {
        Document inputDoc = getDocument();
        if (inputDoc == null)
            return null;
        AnnotationSet defaultAnnotSet = inputDoc.getAnnotations();

        Logger.getLogger(GateRecognizer.class).info(
            "annotation set size: " + String.valueOf(defaultAnnotSet.size())); //$NON-NLS-1$

        Set annotTypesRequired = new HashSet();
        annotTypesRequired.add(annotationName);
        return defaultAnnotSet.get(annotTypesRequired);
    }

    /** Fetch the specified annotations. */
    @SuppressWarnings("unchecked")
    public AnnotationSet getMultipleAnnotations(Collection<String> annotationNames) {
        Document inputDoc = getDocument();
        if (inputDoc == null)
            return null;
        AnnotationSet defaultAnnotSet = inputDoc.getAnnotations();

        Logger.getLogger(GateRecognizer.class).info(
            "annotation set size: " + String.valueOf(defaultAnnotSet.size())); //$NON-NLS-1$

        Set annotTypesRequired = new HashSet();
        annotTypesRequired.addAll(annotationNames);
        return defaultAnnotSet.get(annotTypesRequired);
    }

    /** Produce a pretty print of the results */
    @SuppressWarnings("unchecked")
    public String toXML() {
        Document inputDoc = getDocument();
        if (inputDoc == null)
            return null;
        return inputDoc.toXml(inputDoc.getAnnotations(), true);
    }
}
