/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.util.InitializationException;
import com.joelsgarage.util.XMLUtil;

/**
 * Manages communication with the user, and holds the Chat instance.
 * 
 * It handles NLS recognition and generation.
 * 
 * TODO: add key=>name dereferencing, i.e. FSM specifies an entity key, we dereference the key on
 * the way out.
 * 
 * TODO: add URL escaping.
 * 
 * @author joel
 * 
 */
public class ChatFacilitator extends SendTargetBase implements MessageListener {

    /**
     * Number of messages we'll send (across all users) before shutting up. Prevents crazy spam.
     * TODO: do this differently, maybe some sort of flag that is reset by user input?
     */
    private static final int UTTERANCE_LIMIT = 40;

    /**
     * Everything the user says will go here, so that we don't have to update this enum every time
     * we add a statement to the transducer
     */
    private static final String USER_INPUT = "user_input"; //$NON-NLS-1$

    private EventListener listener;
    private Recognizer recognizer;
    private Generator generator;
    /** The chat corresponding (1:1) with this facilitator. */
    private ChatAdapter chatAdapter;
    /** the number of messages we send before shutting up */
    private int counter;

    public ChatFacilitator(EventListener listener, ChatAdapter chatAdapter) {
        Logger.getLogger(ChatFacilitator.class).info("ctor"); //$NON-NLS-1$
        setListener(listener);
        try {
            Logger.getLogger(ChatFacilitator.class).info("foo1"); //$NON-NLS-1$

            setRecognizer(new Recognizer());
            Logger.getLogger(ChatFacilitator.class).info("foo2"); //$NON-NLS-1$

        } catch (InitializationException e) {
            Logger.getLogger(ChatFacilitator.class).info("foo3"); //$NON-NLS-1$

            // We survive but we can't recognize anything
            e.printStackTrace();
            setRecognizer(null);
        }
        Logger.getLogger(ChatFacilitator.class).info("foo4"); //$NON-NLS-1$

        setGenerator(new Generator());
        Logger.getLogger(ChatFacilitator.class).info("foo5"); //$NON-NLS-1$

        setChatAdapter(chatAdapter);
        getChatAdapter().addMessageListener(this);
        setCounter(UTTERANCE_LIMIT);
    }

    /**
     * All outgoing messages pass through this method.
     * 
     * @param text
     */
    public void sendMessage(String text) {
        Logger.getLogger(ChatFacilitator.class).info("sendMessage: " + String.valueOf(text)); //$NON-NLS-1$

        if (getCounter() < 0) {
            Logger.getLogger(ChatFacilitator.class).info("Utterance limit exceeded"); //$NON-NLS-1$
            return;
        }
        setCounter(getCounter() - 1);
        try {
            getChatAdapter().sendMessage(text);
        } catch (XMPPException e) {
            Logger.getLogger(ChatFacilitator.class).error("Error uttering " + text); //$NON-NLS-1$
        }
    }

    /**
     * Handle an incoming message. Called by Smack.
     * 
     * @see MessageListener#processMessage(Chat, Message)
     */
    @Override
    public void processMessage(Chat newChat, Message message) {
        if (newChat == null) {
            Logger.getLogger(ChatFacilitator.class).error("null chat"); //$NON-NLS-1$
            return;
        }
        if (!getChatAdapter().chatEquals(newChat)) {
            // It's weird to get a different chat here, but not fatal.
            Logger.getLogger(ChatFacilitator.class).error("different chat"); //$NON-NLS-1$
        }

        if (message == null)
            return;

        processMessageInternal(message);
    }

    /** Actually do the processing work */
    public void processMessageInternal(Message message) {
        String verbatim = message.getBody();

        if (verbatim == null)
            return;

        if (getListener() == null) {
            Logger.getLogger(ChatFacilitator.class).error(
                "null listener, can't handle incoming message"); //$NON-NLS-1$
            return;
        }

        // This forces the recognizer to choose an interpretation for the chat, i.e.
        // one chat == one event. if a chat could be several events, mmm, that could be
        // good but could be really confusing, since they'd queue up. so never mind, don't do that.
        if (getRecognizer() != null) {
            Document document = getRecognizer().recognize(verbatim);
            Logger.getLogger(ChatFacilitator.class).info(XMLUtil.writeXML(document));
            getListener().handleMessage(USER_INPUT, document);
        }
    }

    /**
     * Look up the ID of the desired utterance, and speak it.
     */
    @SendTarget.Registrant
    public void speak0(@SuppressWarnings("unused")
    String data, Element element) {
        Node messageIdNode =
            XMLUtil.getNodeByPath(namespaceContext, element, "scxml:data/cc:messageId"); //$NON-NLS-1$
        if (messageIdNode == null) {
            Logger.getLogger(ChatFacilitator.class).error(
                "Speak0 failed for element: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
            return;
        }
        String messageId = messageIdNode.getTextContent();
        String nlsText = getGenerator().generate0(messageId);
        sendMessage(nlsText);
        Logger.getLogger(ChatFacilitator.class).info(
            "Uttered id: " + messageId + " nls: " + nlsText); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** Insert verbatim1 into the specified message and speak it */
    @SendTarget.Registrant
    public void speak1(@SuppressWarnings("unused")
    String data, Element element) {
        Node messageIdNode =
            XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:messageId"); //$NON-NLS-1$
        Node verbatim1Node =
            XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:verbatim1"); //$NON-NLS-1$
        if (messageIdNode == null || verbatim1Node == null) {
            Logger.getLogger(ChatFacilitator.class).error(
                "Speak1 failed for element: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
            return;
        }
        String messageId = messageIdNode.getTextContent();
        String verbatim1 = verbatim1Node.getTextContent();
        Logger.getLogger(ChatFacilitator.class).info(
            "speak1 id: " + messageId + " verbatim: " + verbatim1); //$NON-NLS-1$ //$NON-NLS-2$
        String nlsText = getGenerator().generate1(messageId, verbatim1);
        sendMessage(nlsText);
        Logger.getLogger(ChatFacilitator.class).info(
            "Uttered id: " + messageId + " nls: " + nlsText);//$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Construct a URL from a base part (which is not escaped) and a key (whose components *are*
     * escaped), then insert it into the specified message and speak it.
     * 
     * OK, this is a little different, it gets the key in XML.
     */
    @SendTarget.Registrant
    public void speakUrlFromKey(@SuppressWarnings("unused")
    String data, Element element) {
        Node messageIdNode =
            XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:messageId"); //$NON-NLS-1$
        Node baseUrlNode =
            XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:baseUrl"); //$NON-NLS-1$
        Node keyNode =
            XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:key/p:ExternalKey"); //$NON-NLS-1$

        if (messageIdNode == null || baseUrlNode == null || keyNode == null) {
            Logger.getLogger(ChatFacilitator.class).error(
                "SpeakUrl failed for element: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
            return;
        }
        String messageId = messageIdNode.getTextContent();
        String baseUrl = baseUrlNode.getTextContent();

        ExternalKey key = XMLUtil.makeKeyFromNode(keyNode);
        if (key == null)
            return;

        Logger.getLogger(ChatFacilitator.class).info("speak1Url id: " + messageId //$NON-NLS-1$
            + " baseUrl: " + baseUrl//$NON-NLS-1$
            + " keyString: " + key.toString()); //$NON-NLS-1$

        // I have no idea if this will work.
        String encodedUrl = baseUrl //
            + key.escapedString();
        String nlsText = getGenerator().generate1(messageId, encodedUrl);
        sendMessage(nlsText);
        Logger.getLogger(ChatFacilitator.class).info(
            "Uttered id: " + messageId + " nls: " + nlsText);//$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Look up the specified key and speak the corresponding name, inserted in the specified
     * message. I'm not super happy about mixing database access with this class; I could move this
     * code elsewhere. // * TODO: remove this method -- it uses the serialized key string, but I'd
     * rather use the whole key
     */
    // @SendTarget.Registrant
    // public void speakNameLookup(@SuppressWarnings("unused")
    // String data, Element element) {
    // Node messageIdNode =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:messageId"); //$NON-NLS-1$
    // Node nameNode =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:keyString"); //$NON-NLS-1$
    //
    // if (messageIdNode == null || nameNode == null) {
    // Logger.getLogger(ChatFacilitator.class).error(
    // "SpeakNameLookup failed for element: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
    // return;
    // }
    // String messageId = messageIdNode.getTextContent();
    // String name = nameLookup(nameNode.getTextContent());
    //
    // String nlsText = getGenerator().generate1(messageId, name);
    // sendMessage(nlsText);
    // Logger.getLogger(ChatFacilitator.class).info(
    // "Uttered id: " + messageId + " nls: " + nlsText);//$NON-NLS-1$ //$NON-NLS-2$
    // }
    // /** A two-arg message. zero-th is a lookup, first is verbatim */
    // @SendTarget.Registrant
    // public void speakNameAndVerbatim(@SuppressWarnings("unused")
    // String data, Element element) {
    // Node messageIdNode =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:messageId"); //$NON-NLS-1$
    // Node verbatim1Node =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:verbatim1"); //$NON-NLS-1$
    // Node nameNode =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:keyString"); //$NON-NLS-1$
    //
    // if (messageIdNode == null || verbatim1Node == null || nameNode == null) {
    // Logger.getLogger(ChatFacilitator.class).error(
    // "SpeakNameAndVerbatim failed for element: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
    // return;
    // }
    // String messageId = messageIdNode.getTextContent();
    // String verbatim1 = verbatim1Node.getTextContent();
    // String name = nameLookup(nameNode.getTextContent());
    //
    // String nlsText = getGenerator().generate2(messageId, name, verbatim1);
    // sendMessage(nlsText);
    // Logger.getLogger(ChatFacilitator.class).info(
    // "Uttered id: " + messageId + " nls: " + nlsText);//$NON-NLS-1$ //$NON-NLS-2$
    // }
    // /** A one-arg message. zero-th is a lookup (using the whole key) */
    // @SendTarget.Registrant
    // public void speakNameKey(@SuppressWarnings("unused")
    // String data, Element element) {
    // Node messageIdNode =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:messageId"); //$NON-NLS-1$
    // Node keyNode =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:key/p:ExternalKey");
    // //$NON-NLS-1$
    //
    // if (messageIdNode == null || keyNode == null) {
    // Logger.getLogger(ChatFacilitator.class).error(
    // "SpeakNameKey failed for element: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
    // return;
    // }
    // String messageId = messageIdNode.getTextContent();
    //
    // ExternalKey key = XMLUtil.makeKeyFromNode(keyNode);
    // if (key == null)
    // return;
    //
    // String name = nameLookupByKey(key);
    //
    // String nlsText = getGenerator().generate1(messageId, name);
    // sendMessage(nlsText);
    // Logger.getLogger(ChatFacilitator.class).info(
    // "Uttered id: " + messageId + " nls: " + nlsText);//$NON-NLS-1$ //$NON-NLS-2$
    // }
    /**
     * A three-arg message, all lookup (using the whole key).
     * 
     * TODO: parse the templates, allow them to specify 'lookup' as a modifier to the field
     * 
     * TODO: could do this with repeated elements instead of key0 etc, ah, except that the Data()
     * operator doesn't do that, I think.
     */

    // @SendTarget.Registrant
    // public void speakThreeKeys(@SuppressWarnings("unused")
    // String data, Element element) {
    // Node messageIdNode =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:messageId"); //$NON-NLS-1$
    // Node key0Node =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:key0/p:ExternalKey");
    // //$NON-NLS-1$
    // Node key1Node =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:key1/p:ExternalKey");
    // //$NON-NLS-1$
    // Node key2Node =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:key2/p:ExternalKey");
    // //$NON-NLS-1$
    //
    // if (messageIdNode == null || key0Node == null || key1Node == null || key2Node == null) {
    // Logger.getLogger(ChatFacilitator.class).error(
    // "SpeakThreeKeys failed for element: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
    // return;
    // }
    // String messageId = messageIdNode.getTextContent();
    //
    // ExternalKey key0 = XMLUtil.makeKeyFromNode(key0Node);
    // ExternalKey key1 = XMLUtil.makeKeyFromNode(key1Node);
    // ExternalKey key2 = XMLUtil.makeKeyFromNode(key2Node);
    //
    // String nlsText =
    // getGenerator().generate3(messageId, nameLookupByKey(key0), nameLookupByKey(key1),
    // nameLookupByKey(key2));
    // sendMessage(nlsText);
    // Logger.getLogger(ChatFacilitator.class).info(
    // "Uttered id: " + messageId + " nls: " + nlsText);//$NON-NLS-1$ //$NON-NLS-2$
    // }
    /** A two-arg message. zero-th is a lookup (using the whole key), first is verbatim */

    // crap! the name lookup no longer works because not every entity has a name field.
    //
    //
    // @SendTarget.Registrant
    // public void speakNameKeyAndVerbatim(@SuppressWarnings("unused")
    // String data, Element element) {
    // Node messageIdNode =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:messageId"); //$NON-NLS-1$
    // Node verbatim1Node =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:verbatim1"); //$NON-NLS-1$
    // Node keyNode =
    // XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:key/p:ExternalKey");
    // //$NON-NLS-1$
    //
    // if (messageIdNode == null || verbatim1Node == null || keyNode == null) {
    // Logger.getLogger(ChatFacilitator.class).error(
    // "SpeakNameAndVerbatim failed for element: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
    // return;
    // }
    // String messageId = messageIdNode.getTextContent();
    // String verbatim1 = verbatim1Node.getTextContent();
    //
    // ExternalKey key = XMLUtil.makeKeyFromNode(keyNode);
    // if (key == null)
    // return;
    //
    // String name = nameLookupByKey(key);
    //
    // String nlsText = getGenerator().generate2(messageId, name, verbatim1);
    // sendMessage(nlsText);
    // Logger.getLogger(ChatFacilitator.class).info(
    // "Uttered id: " + messageId + " nls: " + nlsText);//$NON-NLS-1$ //$NON-NLS-2$
    // }
    /** Insert verbatim1 and verbatim2 into the specified message and speak it */
    @SendTarget.Registrant
    public void speak2(@SuppressWarnings("unused")
    String data, Element element) {
        Node messageIdNode =
            XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:messageId"); //$NON-NLS-1$
        Node verbatim1Node =
            XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:verbatim1"); //$NON-NLS-1$
        Node verbatim2Node =
            XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:verbatim2"); //$NON-NLS-1$
        if (messageIdNode == null || verbatim1Node == null || verbatim2Node == null) {
            Logger.getLogger(ChatFacilitator.class).error(
                "Speak2 failed for element: " + XMLUtil.writeXML(element)); //$NON-NLS-1$
            return;
        }
        String messageId = messageIdNode.getTextContent();
        String verbatim1 = verbatim1Node.getTextContent();
        String verbatim2 = verbatim2Node.getTextContent();

        String nlsText = getGenerator().generate2(messageId, verbatim1, verbatim2);
        sendMessage(nlsText);
        Logger.getLogger(ChatFacilitator.class).info(
            "Uttered id: " + messageId + " nls: " + nlsText); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Set a recognizer filter, which is active until the next recognition event, after which it is
     * reset.
     * 
     * This filter ignores 'preference' events if the individual they describe is not a member of
     * the specified class (i.e. look for existence of the ClassMember record).
     * 
     * input: cc:classKey/p:ExternalKey == classKey
     * 
     * this method does not provide a "done" event; maybe it should, i dunno.
     * 
     */
    @SendTarget.Registrant
    public void setRecognizerPreferenceFilter(@SuppressWarnings("unused")
    String data, Element element) {
        getRecognizer().setPreferenceFilter(
            XMLUtil.makeKeyFromNode(XMLUtil.getNodeByPath(namespaceContext, element,
                "/scxml:data/cc:classKey/p:ExternalKey"))); //$NON-NLS-1$
    }

    /** Set the recognizer to accept only properties whose domain is the specified class */
    @SendTarget.Registrant
    public void setRecognizerPropertyFilter(@SuppressWarnings("unused")
    String data, Element element) {
        getRecognizer().setPropertyFilterClassKey(
            XMLUtil.makeKeyFromNode(XMLUtil.getNodeByPath(namespaceContext, element,
                "/scxml:data/cc:classKey/p:ExternalKey"))); //$NON-NLS-1$
    }

    //
    // Internals
    //
    // protected String nameLookupByKey(ExternalKey key) {
    // if (key == null)
    // return null;
    // Dowser dowser = DowserFactory.newDowser();
    // Class<?> entityClass = dowser.getAllowedTypes().get(key.getType());
    // if (entityClass == null)
    // return null;
    //
    // Logger.getLogger(ChatFacilitator.class).info("Looking for key: " + key.toString());
    // //$NON-NLS-1$
    // ModelEntity entity = Util.getPrimary(key);
    // if (entity == null) {
    // return null;
    // }
    // String name = entity.getName();
    // return name;
    // }

    /**
     * Look up the specified key in the DB, and return the name.
     * 
     * TODO: replace this DB code with a RecordFetcher; eliminate Hibernate dependency from this
     * class.
     * 
     * @param keyString
     * @return
     */
    // protected String nameLookup(String keyString) {
    // // which type is this key?
    // ExternalKey key = new ExternalKey(keyString);
    // return nameLookupByKey(key);
    // }
    //
    // accessors
    //
    public EventListener getListener() {
        return this.listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public ChatAdapter getChatAdapter() {
        return this.chatAdapter;
    }

    /**
     * Set the chat corresponding to this facilitator. This should not be changed, I think; if I
     * really believed that I'd make it final.
     */
    protected void setChatAdapter(ChatAdapter chatAdapter) {
        this.chatAdapter = chatAdapter;
    }

    public int getCounter() {
        return this.counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Generator getGenerator() {
        return this.generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public Recognizer getRecognizer() {
        return this.recognizer;
    }

    public void setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
    }
}
