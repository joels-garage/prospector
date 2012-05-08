/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.EventListener;
import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.callcenter.Util;
import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.fetchers.HibernateRecordFetcher;
import com.joelsgarage.dataprocessing.readers.HibernateRecordReader;
import com.joelsgarage.dataprocessing.readers.ModelEntityHibernateRecordReader;
import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.HibernateProperty;
import com.joelsgarage.util.HibernateUtil;
import com.joelsgarage.util.XMLUtil;

/**
 * Provide an Individual Utility for a known Individual.
 * 
 * TODO: accompanying comment.
 * 
 * @author joel
 * 
 */
public class RateIndividual extends HistoryBasedScenePlayer {
    private EventListener listener;

    public RateIndividual(EventListener listener) {
        setListener(listener);
    }

    @Override
    protected String sceneName() {
        return Scenes.RATE_INDIVIDUAL;
    }

    /** This scene can be reiterated instantly, i.e. has min recency of zero */
    @Override
    public NextScene getNextScene() {
        Decision decision = getCurrentDecision();
        double score;

        Document input = makeRateIndividualInput(decision);
        if (input == null) {
            score = Double.NEGATIVE_INFINITY;
        } else {
            if (decision == null) {
                score = Double.NEGATIVE_INFINITY;
            } else {
                score = 1.0;
            }
        }
        return new NextScene(Scenes.RATE_INDIVIDUAL, input, Double.valueOf(score));
    }

    /**
     * rate_individual wants the current decision and stakeholder, and the individual to rate.
     * 
     * So this input generator has to select an individual. To do that, make a list of some eligible
     * individuals, and select one of the unrated ones.
     * 
     * It is impossible to efficiently select unrated individuals, using the normalized schema.
     * 
     * So, for now (:-), select a pile of individuals, ordered in no particular way, iterate through
     * them, looking for the first for which no IndividualUtility record from this user exists.
     * 
     * Because the number of user ratings involved is strictly less than the number of individuals,
     * the way to do this with the fewest queries is to first fetch all the IndividualUtilities in
     * existence for this stakeholder, and then to iterate through the Individuals until one is
     * found that has no corresponding IndividualUtility. Oh right, the class restriction.
     * 
     * TODO: do this query more efficiently.
     * 
     * TODO: order the entities more optimally, e.g. look at the sensitivity of the rating.
     * 
     * TODO: implement a recency limit, i.e. don't ask about 'skipped' entities for N minutes, or
     * something
     * 
     * If no appropriate individual is available, return null.
     * 
     * @param sceneList
     * @return
     */
    protected Document makeRateIndividualInput(Decision decision) {
        // TODO: clean up the handling of null decision
        if (decision == null)
            return null;

        Map<String, Object> queryTerms = new HashMap<String, Object>();
        queryTerms.put("decisionKey", decision.makeKey()); //$NON-NLS-1$
        queryTerms.put("userKey", getUser().makeKey()); //$NON-NLS-1$
        Stakeholder stakeholder = Util.getCompound(Stakeholder.class, queryTerms);

        SessionFactory factory = HibernateUtil.createSessionFactory(null, null);
        Session session = factory.openSession();

        Map<String, Object> queryConstraints = new HashMap<String, Object>();
        queryConstraints.put(HibernateProperty.STAKEHOLDER_KEY, stakeholder.makeKey());
        ReaderConstraint constraint =
            new ReaderConstraint(IndividualUtility.class, queryConstraints);

        // First get all the Individuals mentioned by the user

        HibernateRecordReader<ModelEntity> recordReader =
            new ModelEntityHibernateRecordReader(session, constraint);
        // TODO: this is extraneous.
        recordReader.setPersistentClass(IndividualUtility.class);
        ModelEntity modelEntity;
        Set<ExternalKey> individualKeys = new HashSet<ExternalKey>();
        try {
            recordReader.open();
            while ((modelEntity = recordReader.read()) != null) {
                if (modelEntity instanceof IndividualUtility) {
                    individualKeys.add(((IndividualUtility) modelEntity).getIndividualKey());
                } else {
                    Logger.getLogger(RateIndividual.class).info(
                        "Weird type: " + modelEntity.getClass().getName()); //$NON-NLS-1$
                }
            }
            recordReader.close();
            // TODO: why did I do this?
            // session.close();

            // need a new session for a new transaciton
            session = factory.openSession();

            // Then get the relevant ClassMembers
            queryConstraints.clear();
            queryConstraints.put(HibernateProperty.CLASS_KEY, decision.getClassKey());
            constraint = new ReaderConstraint(ClassMember.class, queryConstraints);

            recordReader = new ModelEntityHibernateRecordReader(session, constraint);
            recordReader.setPersistentClass(ClassMember.class);
            recordReader.open();
            Individual individual = null;
            ModelEntity classMemberModelEntity;
            while ((classMemberModelEntity = recordReader.read()) != null) {
                if (classMemberModelEntity instanceof ClassMember) {
                    ClassMember classMember = (ClassMember) classMemberModelEntity;
                    // if the individual is not present in the set of ratings, then use it.
                    if (!(individualKeys.contains(classMember.getIndividualKey()))) {
                        // the first one is good enough :-)
                        ExternalKey individualKey = classMember.getIndividualKey();

                        if (individualKey == null) {
                            Logger.getLogger(RateIndividual.class).error(
                                "null individualkey for classmember: " + classMember.makeKey()); //$NON-NLS-1$
                            continue;
                        }

                        Session subSession = factory.openSession();
                        // try to fetch it
                        ReaderConstraint readerConstraint = new ReaderConstraint(Individual.class);
                        RecordFetcher<ExternalKey, ModelEntity> recordFetcher =
                            new HibernateRecordFetcher(subSession, readerConstraint);
                        Logger.getLogger(RateIndividual.class).info(
                            "fetching individual: " + individualKey.toString()); //$NON-NLS-1$
                        ModelEntity individualModelEntity = recordFetcher.get(individualKey);
                        // subSession.close();

                        if (individualModelEntity == null) {
                            Logger.getLogger(RateIndividual.class).error(
                                "No Individual for individualKey: " + individualKey.toString()); //$NON-NLS-1$
                            // but don't give up, look for the next classMember.
                            continue;
                        }

                        if (!(individualModelEntity instanceof Individual)) {
                            Logger.getLogger(RateIndividual.class).info(
                                "Weird type: " + individualModelEntity.getClass().getName()); //$NON-NLS-1$
                            continue;
                        }
                        individual = (Individual) individualModelEntity;

                        break;
                    }
                } else {
                    Logger.getLogger(RateIndividual.class).info(
                        "Weird type: " + classMemberModelEntity.getClass().getName()); //$NON-NLS-1$
                }
            }
            recordReader.close();
            // session.close();

            if (individual == null) {
                Logger.getLogger(RateIndividual.class).info("no individual available"); //$NON-NLS-1$
                return null;
            }

            Map<String, Node> inputs = new HashMap<String, Node>();
            inputs.put("decision", makeNode(decision)); //$NON-NLS-1$
            inputs.put("stakeholder", makeNode(stakeholder)); //$NON-NLS-1$
            inputs.put("individual", makeNode(individual)); //$NON-NLS-1$
            return XMLUtil.makeDocFromNodes(
                CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                HistoryBasedScenePlayer.INPUT, inputs);
        } catch (InitializationException e) {
            // session.close();
            Logger.getLogger(RateIndividual.class).error("DB issue: " + e.getMessage()); //$NON-NLS-1$
            return null;
        }
    }

    //

    public EventListener getListener() {
        return this.listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }
}
