/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.Log;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.model.WriteEvent;
import com.joelsgarage.model.User;
import com.joelsgarage.model.WordSense;
import com.joelsgarage.util.DateUtil;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.HibernateUtil;
import com.joelsgarage.util.XMLUtil;

/**
 * Handles most things the FSM needs -- does mid-level logic between the DB and the FSM, so you
 * don't have to, e.g., specify a whole join query somehow in XML to the low-level DB interface, or
 * put a mid-level method *in* the DB interface (which would be inappropriate).
 * 
 * Maybe this will actually contain state, but it's hard to get at it, I think -- if the FSM wants
 * to say "thanks for X" and X is in here, you're fucked. The only way to get it is to pass the
 * output *through* this class, which I think is bad.
 * 
 * Any method annotated as a Registrant, with the correct arguments (like SendTarget.send()) will be
 * registered for send's. These are expected to return an event of the same name, with "Result"
 * appended. That's stupid btw; multiple return "events" are possible. Guards? Maybe use a number of
 * suffixes.
 * 
 * @author joel
 */
public class Cogitator extends SendTargetBase {
    // HACK locale
    private static final String EN_US = "en-US"; //$NON-NLS-1$

    public static class DecisionAndStakeholder {
        public Decision d;
        public Stakeholder s;

        public DecisionAndStakeholder(Decision d, Stakeholder s) {
            this.d = d;
            this.s = s;
        }
    }

    /** Append to called method name to construct return event name */
    private static final String RESULT = "Result"; //$NON-NLS-1$

    /** The route to the FSM */
    private EventListener listener;

    /**
     * The user we're talking to. At the moment this is faked in the ctor.
     * 
     * TODO: really assign the correct user here.
     */
    private ExternalKey userKey;

    /**
     * Hibernate session factory.
     * 
     * TODO: factor it out, into some sort of data-access layer, for testing.
     */
    private SessionFactory factory;

    // TODO: consider moving these fields into some separate class.
    /** Decision and Stakeholder records that the user might be interested in */
    private List<DecisionAndStakeholder> decisionAndStakeholderList;
    /** Iterator on the above list */
    private Iterator<DecisionAndStakeholder> decisionAndStakeholderIterator;

    public Cogitator(EventListener listener) throws FatalException {
        setListener(listener);
        this.factory = HibernateUtil.createSessionFactory(null, // default config
            null); // default database

        // TODO: use the real user on the other end
        setUserKey(new User("joel", "a1").makeKey()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** Who are we talking to? */
    @SendTarget.Registrant
    public void findUser(String data, Element element) {
        Logger.getLogger(Cogitator.class).info("data: " + data); //$NON-NLS-1$
        Logger.getLogger(Cogitator.class).info("payload: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
        // TODO: implement it
    }

    /**
     * Populate the decision list according to the specified class and user. This involves a
     * client-side join, which I find annoying, but not nearly as annoying as SQL joins. (for now
     * ignore the user). The callback event is a kind of iterator, i.e. it returns the number of
     * matches, and you can get the instances with a different function. So the flow is:
     * 
     * find decisions. returns number of matches, resets the iterator.
     * 
     * fetch a decision. returns the entity, or a no-more event
     * 
     * TODO: implement this using a RecordFetcher rather than Hibernate directly, i.e. some sort of
     * JoinedRecordFetcher.
     */
    @SuppressWarnings("nls")
    @SendTarget.Registrant
    public void findDecisionByClassAndUser(String data, Element element) {
        String classKey =
            XMLUtil.getNodeValueByPath(namespaceContext, element, "scxml:data/cc:classKey/text()"); //$NON-NLS-1$
        String userId = null;

        Logger.getLogger(Cogitator.class).info("using classKey: " + classKey); //$NON-NLS-1$
        Logger.getLogger(Cogitator.class).info("ignoring userId: " + userId); //$NON-NLS-1$

        Session session = this.factory.openSession();

        if (session == null) {
            Logger.getLogger(Cogitator.class).error("null session!"); //$NON-NLS-1$
            getListener().handleMessage(data + "Fail", null);
            return;
        }

        // Here we do a kinda crazy client-side join. I'm pretty unhappy about it. But still
        // resistant to actually committing to MySQL joins, since they won't work at scale without
        // a lot more work than this silly join. It's not big.

        this.decisionAndStakeholderList = new ArrayList<DecisionAndStakeholder>();

        // named query properties
        Map<String, Object> properties = new HashMap<String, Object>();

        // First get (all) the stakeholders corresponding to this user
        Query query =
            session.createQuery("from " + Stakeholder.class.getName()
                + " as c where c.userKey = :userKey");

        // TODO: use the real user
        properties.put("userKey", getUserKey());
        query.setProperties(properties);
        query.setReadOnly(true);
        query.setFirstResult(0);
        query.setMaxResults(1000);
        query.setFetchSize(1000);

        List<?> stakeholderList = query.list();

        // Now for each stakeholder, fetch the corresponding decision.
        for (int index = 0; index < stakeholderList.size(); ++index) {
            Object obj = stakeholderList.get(index);
            if (!(obj instanceof Stakeholder)) {
                Logger.getLogger(Cogitator.class).info(
                    "got weird type: " + obj.getClass().getName()); //$NON-NLS-1$
                continue;
            }
            Stakeholder stakeholder = (Stakeholder) obj;
            // Logger.getLogger(Cogitator.class).info("got stakeholder: " + stakeholder.getName());
            // //$NON-NLS-1$

            Query decisionQuery =
                session
                    .createQuery("from " + Decision.class.getName() + " as c where c.key = :key");
            Map<String, Object> decisionProperties = new HashMap<String, Object>();
            decisionProperties.put("key", stakeholder.getDecisionKey());
            decisionQuery.setProperties(decisionProperties);
            decisionQuery.setReadOnly(true);
            decisionQuery.setFirstResult(0);
            decisionQuery.setMaxResults(1000);
            decisionQuery.setFetchSize(1000);
            List<?> decisionList = decisionQuery.list();
            Logger.getLogger(Cogitator.class).info(
                "decision count: " + String.valueOf(decisionList.size())); //$NON-NLS-1$

            for (int jindex = 0; jindex < decisionList.size(); ++jindex) {
                Object dObj = decisionList.get(jindex);
                if (!(dObj instanceof Decision)) {
                    continue;
                }
                Decision decision = (Decision) dObj;
                if (decision.getClassKey().toString().equals(classKey)) {
                    Logger.getLogger(Cogitator.class).info(
                        "got classkey: " + decision.getClassKey().toString()); //$NON-NLS-1$
                    // this is one we want
                    DecisionAndStakeholder decisionAndStakeholder =
                        new DecisionAndStakeholder(decision, stakeholder);
                    this.decisionAndStakeholderList.add(decisionAndStakeholder);
                } else {
                    Logger.getLogger(Cogitator.class).info(
                        "bad classkey: " + decision.getClassKey().toString()); //$NON-NLS-1$
                }
            }
        }

        this.decisionAndStakeholderIterator = this.decisionAndStakeholderList.iterator();

        Map<String, String> elements = new HashMap<String, String>();
        elements.put("count", String.valueOf(this.decisionAndStakeholderList.size()));
        Document payload =
            XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "Result", elements);
        getListener().handleMessage(data + RESULT, payload);
    }

    /**
     * Iterate through decisionAndStakeholderIterator. Assumes that findDecisionByClassAndUser has
     * already been called.
     */
    @SuppressWarnings("nls")
    @SendTarget.Registrant
    public void fetchDecision(String data, @SuppressWarnings("unused")
    Element element) {
        if (this.decisionAndStakeholderIterator.hasNext()) {
            DecisionAndStakeholder currentDecisionAndStakeholder =
                this.decisionAndStakeholderIterator.next();
            if (currentDecisionAndStakeholder == null) {
                getListener().handleMessage(data + "Done", null);
                return;
            }
            Map<String, Object> elements = new HashMap<String, Object>();
            elements.put("decision", currentDecisionAndStakeholder.d);
            Document payload =
                XMLUtil.makeDocFromAnything(
                    CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER, "Result",
                    CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_PROSPECTOR, elements);
            getListener().handleMessage(data + "Next", payload);
        } else {
            // No more to look at
            getListener().handleMessage(data + "Done", null);
        }
    }

    /** Reset the iterator */
    @SuppressWarnings("nls")
    @SendTarget.Registrant
    public void resetIterator(String data, @SuppressWarnings("unused")
    Element element) {
        this.decisionAndStakeholderIterator = this.decisionAndStakeholderList.iterator();
        getListener().handleMessage(data + "Done", null);
    }

    // /**
    // * create a new Decision with the specified name, classKey, description.
    // *
    // * also creates a new Stakeholder record.
    // *
    // * creatorKey is filled in because we know who we're talking to.
    // *
    // * Args:
    // * <ul>
    // * <li> cc:name == name for the decision
    // * <li> cc:classKey == serialized key for the class
    // * </ul>
    // *
    // * returns: cc:key == key of the newly created decision
    // *
    // * Note this method does not check if the specified name (thus key, thus entity) already
    // exists.
    // *
    // * Is there a hibernate way to do that, that doesn't involve the obvious race condition, or
    // * locks all the way out here?
    // *
    // * TODO: return an error if it already exists.
    // *
    // * TODO: remove this method
    // */
    // @SendTarget.Registrant
    // public void createDecision(String data, Element element) {
    // Node nameNode = XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:name");
    // //$NON-NLS-1$
    // Node classKeyNode =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:classKey"); //$NON-NLS-1$
    //
    // if (nameNode == null || classKeyNode == null) {
    // Logger.getLogger(Cogitator.class).info(
    // "Call failed for element: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
    // return;
    // }
    // String name = nameNode.getTextContent();
    // String classKeyString = classKeyNode.getTextContent();
    // ExternalKey classKey = new ExternalKey(classKeyString);
    //
    // String nowString = DateUtil.nowString();
    //
    // Decision decision = new Decision();
    // decision.setCreatorKey(getUserKey());
    // decision.setName(name);
    // KeyUtil.setKey(Util.CALLCENTER_NAMESPACE, decision);
    // decision.setLastModified(nowString);
    // decision.setClassKey(classKey);
    // // TODO: add solicitation for description
    // // decision.setDescription(description);
    //
    // // stakeholder == this user + the above decision.
    // Stakeholder stakeholder = new Stakeholder();
    // stakeholder.setCreatorKey(getUserKey());
    // stakeholder.setLastModified(nowString);
    // stakeholder.setDecisionKey(decision.getKey());
    // stakeholder.setUserKey(getUserKey());
    // stakeholder.setDefaultName();
    // KeyUtil.setKey(Util.CALLCENTER_NAMESPACE, stakeholder);
    //
    // Map<String, Object> elements = new HashMap<String, Object>();
    // elements.put("cc:key", String.valueOf(decision.getKey())); //$NON-NLS-1$
    //
    // writeEntities(data, elements, new ModelEntity[] { decision, stakeholder });
    // }

    /** Just logs whatever you send it */
    @SendTarget.Registrant
    public void echo(@SuppressWarnings("unused")
    String data, Element element) {
        Logger.getLogger(Cogitator.class).info("echo payload: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
    }

    /**
     * Create a new Individual with the specified name, and a ClassMember indicating the new
     * Individual and the specified Class.
     * 
     * creatorKey is filled in because we know who we're talking to.
     * 
     * args:
     * <ul>
     * <li> cc:name == individual name
     * <li> cc:classKey == a class of which it is a member
     * </ul>
     * 
     * returns: cc:key == individual key just created
     * 
     * TODO: remove this method
     * 
     * @throws FatalException
     * 
     */
    @SendTarget.Registrant
    public void createAlternative(String data, Element element) throws FatalException {
        String name =
            XMLUtil.getNodeValueByPath(namespaceContext, element, "/scxml:data/cc:name/text()"); //$NON-NLS-1$
        Node classKeyNode =
            XMLUtil.getNodeByPath(namespaceContext, element,
                "/scxml:data/cc:classKey/p:ExternalKey"); //$NON-NLS-1$

        Logger.getLogger(Cogitator.class).info("creating alternative for name: " + name); //$NON-NLS-1$

        ExternalKey classKey = XMLUtil.makeKeyFromNode(classKeyNode);

        String now = DateUtil.nowString();
        WriteEvent update = new WriteEvent(now, getUserKey(), Util.CALLCENTER_NAMESPACE);
        Individual individual = new Individual(name, Util.CALLCENTER_NAMESPACE);
        Log iLog = new Log(individual.makeKey(), update.makeKey(), Util.CALLCENTER_NAMESPACE);
        WordSense wordSense =
            new WordSense(EN_US, name, true, individual.makeKey(), Util.CALLCENTER_NAMESPACE);
        ClassMember classMember =
            new ClassMember(individual.makeKey(), classKey, Util.CALLCENTER_NAMESPACE);
        Log cLog = new Log(classMember.makeKey(), update.makeKey(), Util.CALLCENTER_NAMESPACE);

        Map<String, Object> elements = new HashMap<String, Object>();
        elements.put("cc:key", individual.makeKey().toString()); //$NON-NLS-1$

        writeEntities(data, elements, new ModelEntity[] {
            update,
            individual,
            iLog,
            wordSense,
            classMember,
            cLog });
    }

    /**
     * Create a new ClassMember with the specified Individual and Class.
     * 
     * creatorKey is filled in because we know who we're talking to.
     * 
     * args:
     * <ul>
     * <li> cc:individualKey == individual key string
     * <li> cc:classKey == the class (ExternalKey) of which it is a member
     * </ul>
     * 
     * returns: nothing
     * 
     * TODO: remove this method
     * 
     */
    // @SendTarget.Registrant
    // public void createClassMember(String data, Element element) {
    // String individualKeyString =
    // XMLUtil.getNodeValueByPath(namespaceContext, element,
    // "scxml:data/cc:individualKey/text()"); //$NON-NLS-1$
    // Node classKeyNode =
    // XMLUtil
    // .getNodeByPath(namespaceContext, element, "scxml:data/cc:classKey/p:ExternalKey");
    // //$NON-NLS-1$
    //
    // ExternalKey individualKey = new ExternalKey(individualKeyString);
    // ExternalKey classKey = XMLUtil.makeKeyFromNode(classKeyNode);
    // if (classKey == null)
    // return;
    //
    // Update update = new Update(DateUtil.nowString(), getUserKey(), Util.CALLCENTER_NAMESPACE);
    // ClassMember classMember =
    // new ClassMember(individualKey, classKey, Util.CALLCENTER_NAMESPACE);
    // Log cLog = new Log(classMember.getKey(), update.getKey(), Util.CALLCENTER_NAMESPACE);
    // Map<String, Object> elements = new HashMap<String, Object>();
    //
    // writeEntities(data, elements, new ModelEntity[] { update, classMember, cLog });
    // }
    /**
     * Create a new IndividualUtility. The payload partially specifies an IndividualUtility object;
     * this is an attempt to somewhat-generically instantiate and write it.
     * 
     * creatorKey is filled in because we know who we're talking to.
     * 
     * args:
     * <ul>
     * <li> cc:entity/p:IndividualUtility == the payload to write
     * </ul>
     * 
     * returns: success or fail
     * 
     * TODO: remove this method
     * 
     * @throws FatalException
     *             if any keypopulations fail
     * 
     */
    @SendTarget.Registrant
    public void createIndividualUtility(String data, Element element) throws FatalException {
        // The notion of deserializing and writing whatever is provided can be reused.
        // TODO: factor it out.
        Node individualUtilityNode =
            XMLUtil.getNodeByPath(namespaceContext, element,
                "scxml:data/cc:entity/p:IndividualUtility"); //$NON-NLS-1$

        Object object = XMLUtil.fromXMLNode(individualUtilityNode);
        if (object == null) {
            Logger.getLogger(Cogitator.class).error(
                "null object for: " + XMLUtil.writeXML(individualUtilityNode)); //$NON-NLS-1$
            completeFetch(data, null);
            return;
        }
        if (!(object instanceof ModelEntity)) {
            Logger.getLogger(Cogitator.class).error("bad type: " + object.getClass().getName()); //$NON-NLS-1$
            completeFetch(data, null);
            return;
        }
        ModelEntity entity = (ModelEntity) object;

        writeEntity(entity, data);
    }

    /** Dereference the supplied key */
    @SendTarget.Registrant
    public void fetchModelEntity(String data, Element element) {
        Node node = XMLUtil.getNodeByPath(namespaceContext, element, "scxml:data/cc:key/*[1]"); //$NON-NLS-1$
        ExternalKey key = XMLUtil.makeKeyFromNode(node);
        // don't use the serialized key anymore.
        // if (key == null) {
        // // maybe it's the serialized key.
        // String keyString =
        // XMLUtil.getNodeValueByPath(namespaceContext, element,
        // "scxml:data/cc:keyString/text()"); //$NON-NLS-1$
        // if (keyString == null)
        // completeFetch(data, null);
        // key = new ExternalKey(keyString);
        // }
        ModelEntity entity = Util.getPrimary(key);
        completeFetch(data, entity);
    }

    /** Non-primary constraints may be specified. */
    @SendTarget.Registrant
    public void fetchModelEntityByField(String data, Element element) {
        String type =
            XMLUtil.getNodeValueByPath(namespaceContext, element,
                "scxml:data/cc:payload/cc:type/text()"); //$NON-NLS-1$
        String fieldName =
            XMLUtil.getNodeValueByPath(namespaceContext, element,
                "scxml:data/cc:payload/cc:fieldName/text()"); //$NON-NLS-1$
        // The value is either a subtree, which produces an Object, or it's a text node, which
        // produces a String.
        // First try the subtree.
        Node valueNode =
            XMLUtil.getNodeByPath(namespaceContext, element, "scxml:data/cc:payload/cc:value/*[1]"); //$NON-NLS-1$
        Object value = XMLUtil.fromXMLNode(valueNode);
        if (value == null) {
            // then try the string
            String valueString =
                XMLUtil.getNodeValueByPath(namespaceContext, element,
                    "scxml:data/cc:payload/cc:value/text()"); //$NON-NLS-1$
            if (valueString == null) {
                Logger.getLogger(Cogitator.class).error("couldn't get value from payload"); //$NON-NLS-1$
                completeFetch(data, null);
            }
            value = valueString;
        }

        ModelEntity entity = Util.getByFieldAndType(type, fieldName, value);
        completeFetch(data, entity);
    }

    /** Because the Data() function doesn't allow child appending, we have this silly two-arg method. */
    @SendTarget.Registrant
    public void fetchModelEntityByTwoFields(String data, Element element) {
        String type =
            XMLUtil.getNodeValueByPath(namespaceContext, element,
                "scxml:data/cc:payload/cc:type/text()"); //$NON-NLS-1$

        String field1Name =
            XMLUtil.getNodeValueByPath(namespaceContext, element,
                "scxml:data/cc:payload/cc:term[1]/cc:fieldName/text()"); //$NON-NLS-1$
        if (field1Name == null) {
            Logger.getLogger(Cogitator.class).error("field1 null"); //$NON-NLS-1$
            completeFetch(data, null);
        }
        Object value1 =
            XMLUtil.fromXMLNode(XMLUtil.getNodeByPath(namespaceContext, element,
                "scxml:data/cc:payload/cc:term[1]/cc:value/*[1]")); //$NON-NLS-1$
        if (value1 == null) {
            Logger.getLogger(Cogitator.class).error("value1 null"); //$NON-NLS-1$
            String valueString1 =
                XMLUtil.getNodeValueByPath(namespaceContext, element,
                    "scxml:data/cc:payload/cc:term[1]/cc:value/text()"); //$NON-NLS-1$
            if (valueString1 == null) {
                Logger.getLogger(Cogitator.class).error("couldn't get value from payload"); //$NON-NLS-1$
                completeFetch(data, null);
            }
            value1 = valueString1;
        }
        String field2Name =
            XMLUtil.getNodeValueByPath(namespaceContext, element,
                "scxml:data/cc:payload/cc:term[2]/cc:fieldName/text()"); //$NON-NLS-1$
        if (field2Name == null) {
            Logger.getLogger(Cogitator.class).error("field2 null"); //$NON-NLS-1$
            completeFetch(data, null);
        }
        Object value2 =
            XMLUtil.fromXMLNode(XMLUtil.getNodeByPath(namespaceContext, element,
                "scxml:data/cc:payload/cc:term[2]/cc:value/*[1]")); //$NON-NLS-1$
        if (value2 == null) {
            Logger.getLogger(Cogitator.class).error("value2 null"); //$NON-NLS-1$
            String valueString2 =
                XMLUtil.getNodeValueByPath(namespaceContext, element,
                    "scxml:data/cc:payload/cc:term[2]/cc:value/text()"); //$NON-NLS-1$
            if (valueString2 == null) {
                Logger.getLogger(Cogitator.class).error("couldn't get value from payload"); //$NON-NLS-1$
                completeFetch(data, null);
            }
            value2 = valueString2;
        }

        Map<String, Object> queryTerms = new HashMap<String, Object>();
        queryTerms.put(field1Name, value1);
        queryTerms.put(field2Name, value2);

        ModelEntity entity = Util.getCompoundByType(type, queryTerms);
        completeFetch(data, entity);
    }

    /**
     * Send the response message for a fetch
     * 
     * @param entity
     *            the entity returned (or null if none), which goes in the payload
     * @param data
     *            the name of the method called, to which we append Success or Fail.
     */
    protected void completeFetch(String data, ModelEntity entity) {
        Map<String, Object> elements = new HashMap<String, Object>();
        String result = null;
        if (entity != null) {
            elements.put("entity", entity); //$NON-NLS-1$
            result = "Success"; //$NON-NLS-1$

        } else {
            result = "Fail"; //$NON-NLS-1$
        }
        getListener()
            .handleMessage(
                data + result,
                XMLUtil
                    .makeDocFromAnything(
                        CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                        "Result", CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_PROSPECTOR, elements)); //$NON-NLS-1$
    }

    /**
     * Create a new ModelEntity (probably actually a subclass). The payload tag denotes the actual
     * type.
     * 
     * This method fills in some deterministic fields, e.g. creatorKey.
     * 
     * args:
     * <ul>
     * <li> cc:entity == the payload to write
     * </ul>
     * 
     * returns: success or fail. also returns the entire entity in the return payload.
     * 
     * @throws FatalException
     *             if any key populations fail
     * 
     */
    @SendTarget.Registrant
    public void createModelEntity(String data, Element element) throws FatalException {
        // select the first (should be only) child of cc:entity
        Node entityNode =
            XMLUtil.getNodeByPath(namespaceContext, element, "scxml:data/cc:entity/*[1]"); //$NON-NLS-1$

        Object object = XMLUtil.fromXMLNode(entityNode);
        if (!(object instanceof ModelEntity))
            return; // BUG BUG BUG this will cause the FSM not to proceed
        ModelEntity entity = (ModelEntity) object;

        writeEntity(entity, data);
    }

    /**
     * Write the specified entity to the db and return it in the "success" payload
     * 
     * @throws FatalException
     *             if any key populations fail
     */
    protected void writeEntity(ModelEntity entity, String data) throws FatalException {
        WriteEvent update = new WriteEvent(DateUtil.nowString(), getUserKey(), Util.CALLCENTER_NAMESPACE);

        // // These fields need not be specified in the input, since they are overridden here.
        // entity.setCreatorKey(getUserKey());
        // if (entity.getName() == null || entity.getName().isEmpty())
        // entity.setDefaultName();
        // this entity might not have a name.

        // HACK
        // I have no idea what to put in the key here. Use the entire entity I guess.

        // TODO: set the key somewhere.
        //        
        // entity.setKey(entity.compositeKey(Util.CALLCENTER_NAMESPACE, new String(KeyUtil
        // .generateHashKey(entity.toString(), entity.getClass(), Util.CALLCENTER_NAMESPACE))));
        // // KeyUtil.setKey(Util.CALLCENTER_NAMESPACE, entity);
        // DateUtil.setLastModifiedNow(entity);

        Log cLog = new Log(entity.makeKey(), update.makeKey(), Util.CALLCENTER_NAMESPACE);

        Map<String, Object> elements = new HashMap<String, Object>();
        elements.put("entity", entity); //$NON-NLS-1$
        writeEntities(data, elements, new ModelEntity[] { update, entity, cLog });
    }

    /**
     * Write the specified entities to the DB, and fire a message according to the result, either
     * <methodname>Success or <methodname>Fail.
     * 
     * @param methodName
     *            used as part of the return message
     * @param elements
     *            payload for the return message
     * @param entities
     *            ModelEntity records to write
     */
    protected void writeEntities(String methodName, Map<String, Object> elements,
        ModelEntity[] entities) {
        try {
            Util.write(entities);
            getListener()
                .handleMessage(
                    methodName + "Success", //$NON-NLS-1$
                    XMLUtil
                        .makeDocFromAnything(
                            CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                            "Result", CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_PROSPECTOR, elements)); //$NON-NLS-1$
        } catch (InitializationException e) {
            e.printStackTrace();
            elements.put("cc:error", e.getMessage());//$NON-NLS-1$
            getListener()
                .handleMessage(
                    methodName + "Fail", //$NON-NLS-1$
                    XMLUtil
                        .makeDocFromAnything(
                            CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                            "Result", CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_PROSPECTOR, elements)); //$NON-NLS-1$
        }

    }

    /** Fetch properties of the decision class, so we can solicit preferences about them */
    @SuppressWarnings("nls")
    @SendTarget.Registrant
    public void getIndividualProperties(String data, @SuppressWarnings("unused")
    Element element) {
        Session session = this.factory.openSession();
        if (session == null) {
            Logger.getLogger(Cogitator.class).error("null session!"); //$NON-NLS-1$
            getListener().handleMessage(data + "Fail", null);
            return;
        }
    }

    //

    public EventListener getListener() {
        return this.listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public ExternalKey getUserKey() {
        return this.userKey;
    }

    public void setUserKey(ExternalKey userKey) {
        this.userKey = userKey;
    }
}
