/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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
import com.joelsgarage.model.IndividualProperty;
import com.joelsgarage.model.IndividualPropertyUtility;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.HibernateProperty;
import com.joelsgarage.util.HibernateUtil;
import com.joelsgarage.util.XMLUtil;

/**
 * Provide an IndividualPropertyUtility. Hide this for now; admins only.
 * 
 * @author joel
 * 
 */
public class RateIndividualProperty extends HistoryBasedScenePlayer {
    private EventListener listener;

    public RateIndividualProperty(EventListener listener) {
        setListener(listener);
    }

    @Override
    protected String sceneName() {
        return Scenes.RATE_INDIVIDUAL_PROPERTY;
    }

    /** This scene has zero min recency */
    @Override
    public NextScene getNextScene() {
        if (getUser().isAdmin()) {
            Decision decision = getCurrentDecision();
            double score;

            Document input = makeRateIndividualPropertyInput(decision);
            if (input == null) {
                // there was nothing to rate
                score = Double.NEGATIVE_INFINITY;
            } else {
                // there's something to do
                score = 1.0;
            }
            return new NextScene(Scenes.RATE_INDIVIDUAL_PROPERTY, input, Double.valueOf(score));
        }
        return null; // admin only
    }

    /**
     * Generate candidate individuals to rate in the context of a specific property. If no
     * individual is appropriate, return null.
     * 
     * First, get the list of all IndividualPropertyUtility ratings already made by this user.
     * Actually all we need is the set of <Property, Individual> pairs. For each property, there
     * will be a set of individuals.
     * 
     * Next we iterate through individual properties whose domain class is the decision class.
     * 
     * For each property, find classmember records whose class is the property range class.
     * 
     * For each class member, try to find the (property, individual) pair, i.e. have we already
     * rated it.
     * 
     * If not, this is the answer.
     * 
     * TODO: better ranking
     * 
     * TODO: break up this long function.
     * 
     * @param sceneList
     * @return
     */
    protected Document makeRateIndividualPropertyInput(Decision decision) {
        if (decision == null) {
            Logger.getLogger(RateIndividualProperty.class).info("null decision"); //$NON-NLS-1$
            return null;
        }
        Map<String, Object> queryTerms = new HashMap<String, Object>();
        queryTerms.put("decisionKey", decision.makeKey()); //$NON-NLS-1$
        queryTerms.put("userKey", getUser().makeKey()); //$NON-NLS-1$
        Stakeholder stakeholder = Util.getCompound(Stakeholder.class, queryTerms);
        if (stakeholder == null) {
            Logger.getLogger(RateIndividualProperty.class).info("null stakeholder"); //$NON-NLS-1$
            return null;
        }
        Logger.getLogger(RateIndividualProperty.class).info(
            "got stakeholder: " + stakeholder.makeKey().toString()); //$NON-NLS-1$

        SessionFactory factory = HibernateUtil.createSessionFactory(null, null);
        Session session = factory.openSession();

        Logger.getLogger(RateIndividualProperty.class)
            .info("Looking for IndividualPropertyUtility"); //$NON-NLS-1$

        Map<String, Object> queryConstraints = new HashMap<String, Object>();
        queryConstraints.put(HibernateProperty.STAKEHOLDER_KEY, stakeholder.makeKey());
        ReaderConstraint constraint =
            new ReaderConstraint(IndividualPropertyUtility.class, queryConstraints);
        HibernateRecordReader<ModelEntity> recordReader =
            new ModelEntityHibernateRecordReader(session, constraint);
        // TODO: remove this
        recordReader.setPersistentClass(IndividualPropertyUtility.class);
        ModelEntity modelEntity;

        // property ==> {individual}
        Multimap<ExternalKey, ExternalKey> rated = new HashMultimap<ExternalKey, ExternalKey>();

        try {
            // Scan all the ratings (there aren't that many for one person)
            recordReader.open();
            Logger.getLogger(RateIndividualProperty.class).info("opened."); //$NON-NLS-1$
            while ((modelEntity = recordReader.read()) != null) {
                Logger.getLogger(RateIndividualProperty.class).info("read something."); //$NON-NLS-1$
                if (modelEntity instanceof IndividualPropertyUtility) {
                    Logger.getLogger(RateIndividualProperty.class).info(
                        "adding IndividualPropertyUtility: " + modelEntity.makeKey().toString()); //$NON-NLS-1$
                    IndividualPropertyUtility ipu = (IndividualPropertyUtility) modelEntity;
                    rated.put(ipu.getPropertyKey(), ipu.getIndividualKey());
                } else {
                    Logger.getLogger(RateIndividualProperty.class).info(
                        "Weird type: " + modelEntity.getClass().getName()); //$NON-NLS-1$
                }
            }
            recordReader.close();
            // session.close();

            // new session for new reader
            session = factory.openSession();

            Logger.getLogger(RateIndividualProperty.class).info(
                "number of ratings: " + String.valueOf(rated.size())); //$NON-NLS-1$

            // Then iterate through the individual properties
            queryConstraints.clear();
            queryConstraints.put(HibernateProperty.DOMAIN_CLASS_KEY, decision.getClassKey());
            constraint = new ReaderConstraint(IndividualProperty.class, queryConstraints);
            recordReader = new ModelEntityHibernateRecordReader(session, constraint);
            // TODO: remove this
            recordReader.setPersistentClass(IndividualProperty.class);
            recordReader.open();
            Individual individual = null;
            ModelEntity individualPropertyModelEntity;
            while ((individualPropertyModelEntity = recordReader.read()) != null) {
                if (!(individualPropertyModelEntity instanceof IndividualProperty)) {
                    Logger.getLogger(RateIndividualProperty.class).info(
                        "Weird type: " + individualPropertyModelEntity.getClass().getName()); //$NON-NLS-1$
                    continue;
                }
                IndividualProperty individualProperty =
                    (IndividualProperty) individualPropertyModelEntity;
                ExternalKey propertyKey = individualProperty.makeKey();
                ExternalKey rangeClassKey = individualProperty.getRangeClassKey();
                if (rangeClassKey == null) {
                    Logger.getLogger(RateIndividualProperty.class).info("null range class"); //$NON-NLS-1$
                    continue;
                }

                Session aSession = factory.openSession();
                Logger.getLogger(RateIndividualProperty.class).info(
                    "got property: " + propertyKey.toString()); //$NON-NLS-1$

                // for each range class, iterate through the classmembers
                Map<String, Object> classMemberQueryConstraint = new HashMap<String, Object>();
                classMemberQueryConstraint.put(HibernateProperty.CLASS_KEY, rangeClassKey);
                ReaderConstraint classMemberConstraint =
                    new ReaderConstraint(ClassMember.class, classMemberQueryConstraint);

                HibernateRecordReader<ModelEntity> classMemberRecordReader =
                    new ModelEntityHibernateRecordReader(aSession, classMemberConstraint);
                classMemberRecordReader.setPersistentClass(ClassMember.class);
                ModelEntity classMemberModelEntity;
                classMemberRecordReader.open();

                while ((classMemberModelEntity = classMemberRecordReader.read()) != null) {
                    if (!(classMemberModelEntity instanceof ClassMember)) {
                        Logger.getLogger(RateIndividualProperty.class).info(
                            "Weird type: " + classMemberModelEntity.getClass().getName()); //$NON-NLS-1$
                        continue;
                    }
                    ClassMember classMember = (ClassMember) classMemberModelEntity;
                    ExternalKey individualKey = classMember.getIndividualKey();

                    Logger.getLogger(RateIndividualProperty.class).info(
                        "got classmember: " + classMember.toString()); //$NON-NLS-1$

                    if (!rated.containsEntry(propertyKey, individualKey)) {
                        // no rating for this property, individual pair, so this is the one we want

                        Session anotherSession = factory.openSession();

                        // try to fetch the individual
                        ReaderConstraint readerConstraint = new ReaderConstraint(Individual.class);
                        RecordFetcher<ExternalKey, ModelEntity> recordFetcher =
                            new HibernateRecordFetcher(anotherSession, readerConstraint);
                        Logger.getLogger(RateIndividualProperty.class).info(
                            "fetching individual: " + individualKey.toString()); //$NON-NLS-1$
                        ModelEntity individualModelEntity = recordFetcher.get(individualKey);
                        // anotherSession.close();
                        if (individualModelEntity == null) {
                            Logger.getLogger(RateIndividualProperty.class).warn(
                                "No Individual for individualKey: " + individualKey.toString()); //$NON-NLS-1$
                            // but don't give up, look for the next classMember.
                            continue;
                        }

                        if (!(individualModelEntity instanceof Individual)) {
                            Logger.getLogger(RateIndividualProperty.class).info(
                                "Weird type: " + individualModelEntity.getClass().getName()); //$NON-NLS-1$
                            continue;
                        }

                        // finally we're done, we have all the fields
                        individual = (Individual) individualModelEntity;
                        Map<String, Node> inputs = new HashMap<String, Node>();
                        inputs.put("decision", makeNode(decision)); //$NON-NLS-1$
                        inputs.put("stakeholder", makeNode(stakeholder)); //$NON-NLS-1$
                        inputs.put("individual", makeNode(individual)); //$NON-NLS-1$
                        inputs.put("property", makeNode(individualProperty)); //$NON-NLS-1$
                        classMemberRecordReader.close();
                        // aSession.close();
                        recordReader.close();
                        // session.close();
                        return XMLUtil.makeDocFromNodes(
                            CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                            HistoryBasedScenePlayer.INPUT, inputs);
                    }
                }
                classMemberRecordReader.close();
                // aSession.close();
            }
            recordReader.close();
            // session.close();
            Logger.getLogger(RateIndividualProperty.class).info(
                "couldn't find any properties to rate"); //$NON-NLS-1$
            return null;
        } catch (InitializationException e) {
            // session.close();
            Logger.getLogger(RateIndividualProperty.class).error("DB issue: " + e.getMessage()); //$NON-NLS-1$
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
