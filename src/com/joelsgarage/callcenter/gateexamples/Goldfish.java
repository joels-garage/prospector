package com.joelsgarage.callcenter.gateexamples;

/**
 * Counts the number of times the word "Goldfish" appears in a sentence. That total is added as a
 * feature to every sentence annotation.
 * 
 * Also adds summary information to each document, namely: - total number of characters in document -
 * total number of tokens in document - total number of words in document - total number of
 * sentences - total "Goldfish" count
 * 
 * @author Andrew Golightly (acg4@cs.waikato.ac.nz) -- last updated 18/05/2003
 */

import gate.Annotation;
import gate.AnnotationSet;
import gate.creole.ANNIEConstants;
import gate.creole.ExecutionException;

import java.util.Iterator;

public class Goldfish extends gate.creole.AbstractLanguageAnalyser {
    /** Output (doc feature): number of occurrences of the word "Goldfish." */
    public static final String TOTAL_GOLDFISH_COUNT = "Total \"Goldfish\" count"; //$NON-NLS-1$
    /** Output (doc feature): number of tokens. */
    public static final String NUMBER_OF_WORDS = "Number of words"; //$NON-NLS-1$

    /** A token "kind". See plugins/ANNIE/tokeniser/postprocess.jape */
    private static final String WORD = "word"; //$NON-NLS-1$
    /** The magic word */
    private static final String GOLDFISH = "Goldfish"; //$NON-NLS-1$

    private static final long serialVersionUID = 1L;

    private String inputASname, outputASname;

    public String getinputASname() {
        return this.inputASname;
    }

    public void setinputASname(String inputASname) {
        this.inputASname = inputASname;
    }

    public String getoutputASname() {
        return this.outputASname;
    }

    public void setoutputASname(String outputASname) {
        this.outputASname = outputASname;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute() throws ExecutionException {
        gate.Document doc = getDocument();
        int totalGoldfishCount = 0;

        doc.getFeatures().clear();

        AnnotationSet inputAnnSet =
            (this.inputASname == null || this.inputASname.length() == 0) ? doc.getAnnotations()
                : doc.getAnnotations(this.inputASname);

        AnnotationSet outputAnnSet =
            (this.outputASname == null || this.outputASname.length() == 0) ? doc.getAnnotations()
                : doc.getAnnotations(this.outputASname);

        doc.getFeatures().put("Number of characters", //$NON-NLS-1$
            new Integer(doc.getContent().toString().length()).toString());
        try {
            doc.getFeatures().put(
                "Number of tokens", //$NON-NLS-1$
                new Integer(inputAnnSet.get(ANNIEConstants.TOKEN_ANNOTATION_TYPE).size())
                    .toString());
        } catch (NullPointerException e) {
            throw new ExecutionException("You need to run the English Tokenizer first!"); //$NON-NLS-1$
        }
        try {
            doc.getFeatures().put(
                "Number of sentences", //$NON-NLS-1$
                new Integer(inputAnnSet.get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE).size())
                    .toString());
        } catch (NullPointerException e) {
            throw new ExecutionException("You need to run the Sentence Splitter first!"); //$NON-NLS-1$
        }

        // iterate through the sentences
        Iterator sentenceIterator =
            inputAnnSet.get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE).iterator(), tokenIterator;
        int wordCount = 0;
        while (sentenceIterator.hasNext()) {
            Annotation sentenceAnnotation = (Annotation) sentenceIterator.next();
            tokenIterator =
                inputAnnSet.get(ANNIEConstants.TOKEN_ANNOTATION_TYPE,
                    sentenceAnnotation.getStartNode().getOffset(),
                    sentenceAnnotation.getEndNode().getOffset()).iterator();

            // iterate through the tokens in the current sentence
            int sentenceGoldfishCount = 0;

            while (tokenIterator.hasNext()) {
                Annotation tokenAnnotation = (Annotation) tokenIterator.next();
                if (tokenAnnotation.getFeatures().get(ANNIEConstants.TOKEN_KIND_FEATURE_NAME)
                    .equals(WORD))
                    wordCount++;

                // Is this token "Goldfish"?
                if (((String) tokenAnnotation.getFeatures().get(
                    ANNIEConstants.TOKEN_STRING_FEATURE_NAME)).equals(GOLDFISH)) {
                    try {
                        outputAnnSet.add(tokenAnnotation.getStartNode().getOffset(),
                            tokenAnnotation.getEndNode().getOffset(), GOLDFISH, gate.Factory
                                .newFeatureMap());
                    } catch (gate.util.InvalidOffsetException ioe) {
                        throw new ExecutionException(ioe);
                    }
                    sentenceGoldfishCount++;
                }
            }

            sentenceAnnotation.getFeatures().put(new String("Goldfish Count"), //$NON-NLS-1$
                new Integer(sentenceGoldfishCount));

            totalGoldfishCount += sentenceGoldfishCount;
        }

        doc.getFeatures().put(NUMBER_OF_WORDS, new Integer(wordCount).toString());
        doc.getFeatures().put(TOTAL_GOLDFISH_COUNT, new Integer(totalGoldfishCount).toString());
    }
}
