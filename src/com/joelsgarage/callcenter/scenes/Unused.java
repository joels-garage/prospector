/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import com.joelsgarage.callcenter.SendTargetBase;

/**
 * A place to put stuff I don't want to delete. This is not used anywhere.
 * 
 * @author joel
 * 
 */
public class Unused extends SendTargetBase {
    // private EventListener listener;

    // public Unused(EventListener listener) {
    // setListener(listener);
    // }
    //
    // /**
    // * Dereference the specified decision key. No longer needed because we pass the entire
    // decision
    // * as input, if a scene needs it.
    // */
    // protected Decision getCurrentDecision(String decisionKeyString) {
    // Session session = HibernateUtil.createSessionFactory(null, null).openSession();
    // ReaderConstraint readerConstraint = new ReaderConstraint(Decision.class);
    // RecordFetcher<ExternalKey, ModelEntity> recordFetcher =
    // new HibernateRecordFetcher(session, readerConstraint);
    // ExternalKey decisionKey = new ExternalKey(decisionKeyString);
    // ModelEntity modelEntity = recordFetcher.get(decisionKey);
    // if (modelEntity == null) {
    // Logger.getLogger(GetIndividualPreference.class).info(
    // "No Decision for decisionKey: " + decisionKey.toString()); //$NON-NLS-1$
    // return null;
    // }
    // if (!(modelEntity instanceof Decision)) {
    // Logger.getLogger(GetIndividualPreference.class).info(
    // "Weird type: " + modelEntity.getClass().getName()); //$NON-NLS-1$
    // return null;
    // }
    // Decision decision = (Decision) modelEntity;
    // return decision;
    // }
    //
    // /**
    // * Verify the specified individual is a member of the class corresponding to the current
    // * decision. Fires "true" if any of the classMembers for this individual are in the correct
    // * class. Fires "false" otherwise.
    // *
    // * No longer needed because we filter individuals for class membership in the GateRecognizer.
    // *
    // * inputs are individualkey and classkey
    // */
    // @SuppressWarnings("nls")
    // @SendTarget.Registrant
    // public void verifyIndividualClass(String data, Element element) {
    // String individualKeyString =
    // XMLUtil.getNodeValueByPath(namespaceContext, element,
    // "scxml:data/cc:individualKey/text()"); //$NON-NLS-1$
    // Node classKeyNode =
    // XMLUtil
    // .getNodeByPath(namespaceContext, element, "scxml:data/cc:classKey/p:ExternalKey");
    // //$NON-NLS-1$
    //
    // ExternalKey classKey = XMLUtil.makeKeyFromNode(classKeyNode);
    // if (classKey == null)
    // return;
    //
    // Session session = HibernateUtil.createSessionFactory(null, null).openSession();
    //
    // // find the classMember linking the class to the individual.
    // Query query = session.createQuery("from " + ClassMember.class.getName() //
    // + " as c"//
    // + " where c.individualKey = :individualKey"//
    // + " and c.classKey = :classKey");
    //
    // // named query properties
    // Map<String, Object> properties = new HashMap<String, Object>();
    // ExternalKey individualKey = new ExternalKey(individualKeyString);
    // properties.put("individualKey", individualKey);
    // properties.put("classKey", classKey);
    //
    // query.setProperties(properties);
    // query.setReadOnly(true);
    // query.setFirstResult(0);
    // query.setMaxResults(1000);
    // query.setFetchSize(1000);
    //
    // List<?> list = query.list();
    // if (list.size() > 1) {
    // Logger.getLogger(GetIndividualPreference.class).info(
    // "Extra ClassMember records for individual: " + individualKey.toString() //$NON-NLS-1$
    // + " class: " + classKey.toString()); //$NON-NLS-1$
    // }
    // if (list.size() > 0) {
    // getListener().handleMessage(data + "True", null);
    // return;
    // }
    // getListener().handleMessage(data + "False", null);
    // }
    //
    // //
    //
    // public EventListener getListener() {
    // return this.listener;
    // }
    //
    // public void setListener(EventListener listener) {
    // this.listener = listener;
    // }
}
