/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import com.joelsgarage.callcenter.scenes.AddToRoster;
import com.joelsgarage.callcenter.scenes.Originate;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.User;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.HibernateProperty;
import com.joelsgarage.util.InitializationException;

/**
 * An operator is a user-visible account. Different operators have different behaviors.
 * 
 * I'm not sure this concept will be that useful -- the number of user-visible operators should be
 * small -- but it will be a good way for me to experiment with different behaviors.
 * 
 * Alternatively I could define a narrow domain of expertise for each operator. Dunno.
 * 
 * An operator handles many chat instances, and thus many chat flow and bridge instances.
 * 
 * The Operator concept (which holds username) may be redundant with the Bridge concept (which holds
 * chat state and maps messages to and from it). Dunno.
 * 
 * For now, it's one, just to move stuff out of CallCenter.
 * 
 * The chat initialization flow (in fact, the *whole* flow) is different for "originate" vs
 * "answer". I'd like both flows.
 * 
 * For an "originate" bot, there needs to be some reason for the call, and it would be nice to be
 * able to source that reason on the fly, i.e. not by starting up the app. And it would be nice for
 * the "originate" mode to use the very same userId ("nigel") as the more-common "answer" mode, so
 * that the roster can use the previous state (i.e. talk to whom you talked before).
 * 
 * So. Set it up to start in "answer" mode, and then write some "originate" code, separately.
 * 
 * Ah, maybe the way to do it is to write the originate/answer distinction into the state machine,
 * so you can switch between them if you're in "waiting to answer", and the event comes from outside
 * somewhere.
 * 
 * -----
 * 
 * OK, so, to do "answer" we just wait for someone to contact us.
 * 
 * To do "originate" we need to do the following:
 * 
 * 0. look at ALL USERS. for each user, run the scene chooser to find out whether we should play
 * with them. A SceneChooser is pretty easy to make, so we don't keep them around. There are four
 * cases:
 * 
 * 0.a. a scene is so important that we should attempt to chat with them for the first time, i.e.
 * add them to the roster. Roster additions may be accepted, ignored, or refused -- in this case,
 * the *only* thing we can do is try to add the user, and then return. When they appear in the
 * roster, we catch them in the case 0.b. below.
 * 
 * 0.b. less important but still worth creating a new chat, if they're in the roster.
 * 
 * 0.c. still less important, something we would say if we were already chatting with the person.
 * but if we are, then that bridge will discover the same scene, and play it. so we don't need to do
 * anything in this case.
 * 
 * 0.d. no scene is important enough to play at all.
 * 
 * 
 * 1. look at the list of people we know. this requires maintaining the roster (i.e. with a
 * listener)
 * 
 * 2. look at the scene history. this means we have to have the scene chooser, i think.
 * 
 * 3. if there's a scene, "joel wants to bug tammy" more recent than "bug tammy", then (if not
 * already connected) create a bridge and play the "bug tammy" scene.
 * 
 * Is there any way to avoid having the scene chooser? It's the only thing that can decide which
 * scenes to play, and it would be crazy for this class to have a *different* way to decide that.
 * You can't separate what-scene from make-connection because the connection is motivated by the
 * scene.
 * 
 * Of course, the scene chooser needs to know whether we're connected or not; the fatigue caused by
 * making a new chat is more than an existing one. Maybe it's just a threshold.
 * 
 * there may be userIds in the roster that are not users in the db. handle these with the roster
 * populator and listener.
 * 
 * The Operator listens to two kinds of updates to decide when to originate a chat: roster updates
 * and database updates. A roster update may be a newly-visible user. A database update may be a
 * user with a newly-relevant scene choice.
 * 
 * @author joel
 * 
 */
public class Operator implements ChatManagerListener, RosterListener, UpdateListener, Runnable {
    /** true if in dev mode; don't originate, don't add to roster. */
    private static final boolean DEV = true;
    /**
     * Each Operator has one connection.
     */
    private XMPPConnection connection;
    /**
     * ChatManager does not hold chat references, and so will recreate a Chat per Message unless we
     * hold a reference to each Chat it produces.
     */
    private ChatManager chatmanager;

    private String operatorUsername;
    private String operatorPassword;

    /**
     * Active bridges indexed by user key.
     */
    private Map<ExternalKey, Bridge> bridges = new HashMap<ExternalKey, Bridge>();

    // not sure i need this
    private Roster roster;

    private ConnectionConfiguration config;
    private Thread thread;

    /**
     * Initialize members but don't make any connections. See run() for that.
     * 
     * @param config
     * @param username
     * @param password
     */
    public Operator(ConnectionConfiguration config, String username, String password) {
        Logger.getLogger(Operator.class).info("ctor username: " + username); //$NON-NLS-1$
        setOperatorUsername(username);
        setOperatorPassword(password);
        setConfig(config);
    }

    /** Run the operator in a new thread */
    public void start() {
        Logger.getLogger(Operator.class).info("start"); //$NON-NLS-1$
        setThread(new Thread(this));
        getThread().start();
    }

    /**
     * Make a connection, and register a listener for new chats. This listener calls newChat.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        setConnection(new XMPPConnection(getConfig()));

        if (getConnection() == null) {
            Logger.getLogger(Operator.class).error("weird null connection"); //$NON-NLS-1$
            return;
        }
        try {
            getConnection().connect();
        } catch (XMPPException e) {
            Logger.getLogger(Operator.class).error("Could not connect."); //$NON-NLS-1$
            return;
        }

        try {
            getConnection().login(getOperatorUsername(), getOperatorPassword());
        } catch (XMPPException e) {
            Logger.getLogger(Operator.class).error("Could not login."); //$NON-NLS-1$
            return;
        }

        setChatmanager(getConnection().getChatManager());

        // This accepts "answer-mode" chats from users.
        getChatmanager().addChatListener(this);

        // For "originate" mode, there are two steps:
        // 1. add the user to the roster
        // 2. create a chat with them

        // Here is some old code that does it; resurrect it at some point.

        // if (this.roster.contains(USER_EMAIL)) {
        // hello(Collections.singleton(USER_EMAIL));
        // } else {
        // // Could be our friend is already on the roster, which the server remembers.
        //
        // // but we have to wait until this is accepted, before trying to chat.
        // // so we need to wait (maybe forever) for a roster entry to be accepted; need
        // // to have a list of pending roster entries?

        //
        // // Gtalk requires subscription (?) before allowing chats. So try to add a roster entry.
        // The listener picks up the "OK" for this entry attempt.
        // try {
        // getRoster().createEntry(USER_EMAIL, USER_ALIAS, null);
        // } catch (XMPPException e) {
        // getLogger().info("Error adding to roster."); //$NON-NLS-1$
        // }
        // }

        // try {
        // this.chat.sendMessage("Howdy!");
        // } catch (XMPPException e) {
        // System.out.println("Error Delivering block.");
        // }

        sendPresence();

        // This does nothing but wait to populate the roster. We don't care about
        // synchronizing the first roster update, so I don't think we need it; we can just
        // listen for the updates.
        setRoster(this.connection.getRoster());
        // Accept all subscriptions
        getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        // Get roster updates, so that we can follow up immediately to newly added roster entries
        getRoster().addRosterListener(this);

        // Log the roster
        Collection<RosterEntry> entries = this.roster.getEntries();
        for (RosterEntry entry : entries) {
            Logger.getLogger(Operator.class).info(entry.getName() + " (" + entry.getUser() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

    /**
     * Set up a new chat.
     * 
     * We extract the user id from the chat, and look up the user. If no matching user can be found,
     * we create a new user.
     * 
     * @param chat
     * @param createdLocally
     *            means "originate" mode
     * @throws FatalException
     */
    protected void newChat(Chat newChat, boolean createdLocally) throws FatalException {
        Logger.getLogger(Operator.class).info("new chat."); //$NON-NLS-1$
        if (newChat == null) {
            Logger.getLogger(Operator.class).error("new chat is null."); //$NON-NLS-1$
            return;
        }
        Logger.getLogger(Operator.class).info("new chat with: " + newChat.getParticipant()); //$NON-NLS-1$
        // The Bridge handles incoming and outgoing chat messages.
        try {
            String userId = StringUtils.parseBareAddress(newChat.getParticipant());

            User theUser = findOrCreateUser(userId);
            if (theUser == null) {
                Logger.getLogger(Operator.class).error(
                    "Failed to make default user record for userid: " + userId); //$NON-NLS-1$
                return;
            }

            Bridge bridge = new Bridge();
            getBridges().put(theUser.makeKey(), bridge);
            Logger.getLogger(Operator.class).info("foo2"); //$NON-NLS-1$

            SceneChooser sceneChooser = new SceneChooser(bridge, theUser);
            Logger.getLogger(Operator.class).info("foo3"); //$NON-NLS-1$

            bridge.run(newChat, createdLocally, sceneChooser);
            Logger.getLogger(Operator.class).info("foo4"); //$NON-NLS-1$

        } catch (InitializationException e) {
            Logger.getLogger(Operator.class).error(e.getMessage());
            // swallow the error, continue.
        }
    }

    /**
     * Find the specified user, or create a new one, or return null if everything failed.
     * 
     * @throws FatalException
     */
    protected static User findOrCreateUser(String userId) throws FatalException {
        if (userId == null) {
            Logger.getLogger(Operator.class).error("Can't chat with null userId, so give up."); //$NON-NLS-1$
            return null;
        }

        User theUser = Util.getByField(User.class, HibernateProperty.EMAIL_ADDRESS, userId);

        if (theUser == null) {
            Logger.getLogger(Operator.class).info(
                "Making default user record for userid: " + userId); //$NON-NLS-1$

            User newUser = newUser(userId);

            try {
                Util.write(newUser);
            } catch (com.joelsgarage.dataprocessing.InitializationException e) {
                Logger.getLogger(Operator.class).error("write error: " + e.getMessage()); //$NON-NLS-1$
                e.printStackTrace();
            }

            // now confirm that we can find it.
            theUser = Util.getByField(User.class, HibernateProperty.EMAIL_ADDRESS, userId);
            if (theUser == null) {
                Logger.getLogger(Operator.class).error(
                    "Failed to make default user record for userid: " + userId); //$NON-NLS-1$
                return null;
            }
        }
        return theUser;

    }

    /**
     * Given a chat userId (bare address), create a new user with that id as name and email.
     * 
     * TODO: write this to the db, together with an update record and a log record.
     * 
     * @throws FatalException
     */
    protected static User newUser(String userId) throws FatalException {
        User user = new User(userId, Util.CALLCENTER_NAMESPACE);
        return user;
    }

    protected void sendPresence() {
        Presence p = new Presence(Presence.Type.available);
        p.setStatus("Alive"); //$NON-NLS-1$
        getConnection().sendPacket(p);
    }

    //
    // CHAT MANAGER LISTENER METHODS
    //

    /**
     * Handles new incoming chats.
     * 
     * @see org.jivesoftware.smack.ChatManagerListener#chatCreated(org.jivesoftware.smack.Chat,
     *      boolean)
     */
    public void chatCreated(Chat newChat, boolean createdLocally) {
        try {
            newChat(newChat, createdLocally);
        } catch (FatalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //
    // UPDATE LISTENER METHODS
    //

    /**
     * This user might have newly interesting scenes to play. There are two cases we need to deal
     * with: those with users on our roster, and those with users not yet on our roster.
     * 
     * @see com.joelsgarage.callcenter.UpdateListener#update(com.joelsgarage.model.User)
     */
    @Override
    public void update(User user) {
        if (user == null) {
            Logger.getLogger(Operator.class).error("Got update for null user"); //$NON-NLS-1$
            return;
        }
        Logger.getLogger(Operator.class).info("Got update for user: " + user.getEmailAddress()); //$NON-NLS-1$

        if (getRoster() == null) {
            Logger.getLogger(Operator.class).error("no roster, can't proceed"); //$NON-NLS-1$
            return;
        }

        if (getBridges().containsKey(user.makeKey())) {
            Logger.getLogger(Operator.class).info(
                "Already chatting with: " + user.getEmailAddress()); //$NON-NLS-1$
            return;
        }

        if (getRoster().contains(user.getEmailAddress())) {
            Logger.getLogger(Operator.class)
                .info("found user in roster: " + user.getEmailAddress()); //$NON-NLS-1$
            // Now we have to decide whether to originate.
            // If a user is rostered but unavailable, we never ever talk to them.
            if (!(getRoster().getPresence(user.getEmailAddress()).isAvailable())) {
                Logger.getLogger(Operator.class).info(
                    "No available presence for: " + user.getEmailAddress()); //$NON-NLS-1$
                return;
            }
            try {
                originate(user);
            } catch (FatalException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Logger.getLogger(Operator.class).info(
                "user is not in roster: " + user.getEmailAddress()); //$NON-NLS-1$
            // so now we have to decide whether to add them.
            try {
                addToRoster(user);
            } catch (FatalException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * If we should add this user to the roster, do so.
     * 
     * TODO: log the add-to-roster attempt, in order to frequency-limit it.
     * 
     * @throws FatalException
     *             if key population fails
     */
    protected void addToRoster(User user) throws FatalException {
        Logger.getLogger(Operator.class).info("addToRoster?"); //$NON-NLS-1$

        /** don't add to roster in dev mode */
        if (DEV) {
            Logger.getLogger(Operator.class).info("no, in dev mode"); //$NON-NLS-1$
            return;
        }

        if (user == null) {
            Logger.getLogger(Operator.class).error("can't originate with null user"); //$NON-NLS-1$
            return;
        }
        String userId = user.getEmailAddress();
        if (userId == null) {
            Logger.getLogger(Operator.class).error("can't originate with null user id"); //$NON-NLS-1$
            return;
        }
        // String userName = user.getName();
        // if (userName == null) {
        // Logger.getLogger(Operator.class).error("can't originate with null user name");
        // //$NON-NLS-1$
        // return;
        // }

        // TODO: remove this extra check.
        if (userId.indexOf("truher") == -1) { //$NON-NLS-1$
            Logger.getLogger(Operator.class).info("ignoring non-truher name: " + userId); //$NON-NLS-1$
            return;
        }

        SceneChooser chooser = new SceneChooser(null, user);
        NextScene addToRosterScene = chooser.getAddToRoster();
        if (addToRosterScene != null) {
            // we should add the user.
            // Note we ignore the actual scene.

            // First check the frequency of origination.
            AddToRoster addToRoster = new AddToRoster();
            SceneHistory history = chooser.fetchSceneHistory();
            addToRoster.setHistory(history);
            if (!addToRoster.isNovel()) {
                Logger.getLogger(Operator.class).info("origination scene played too recently"); //$NON-NLS-1$
                return;
            }

            // It's not been recently played, so OK to play again.
            addToRosterScene = addToRoster.getNextScene();
            try {
                chooser.beginScene(addToRosterScene);
                chooser.endScene(null);
                getRoster().createEntry(userId, userId, null);
            } catch (XMPPException e) {
                Logger.getLogger(Operator.class).info("Error adding to roster: " + userId); //$NON-NLS-1$
            }
        }
    }

    /**
     * If we should originate a chat with this user, do so.
     * 
     * TODO: log the origination attempt.
     * 
     * Note that some originations are just what the server needs to do to continue conversations in
     * progress, during a server restart. These shouldn't involve any particular utterance upon
     * creation.
     * 
     * TODO: remove the origination utterance altogether.
     * 
     * So this origination event isn't really important to frequency-cap. The min frequency limit
     * can be quite low. More important to cap the really annoying things, i.e. non sequiturs, which
     * can happen in a single chat or across chats.
     * 
     * @throws FatalException
     */
    protected void originate(User user) throws FatalException {
        Logger.getLogger(Operator.class).info("originate?"); //$NON-NLS-1$
        if (DEV) {
            Logger.getLogger(Operator.class).info("no, in dev mode"); //$NON-NLS-1$
            return;
        }

        // null listener because this is just to decide the next scene
        // TODO: some refactoring
        SceneChooser chooser = new SceneChooser(null, user);
        // The only choice we have to make here is whether to create a new chat.
        NextScene originationScene = chooser.getOrigination();

        if (originationScene != null) {
            // got a good scene, we can create a new chat!
            // Note we ignore the actual scene here; the SceneChooser gets to
            // figure it out again. We don't do that many originations so it's
            // not that much extra work.

            // First check the frequency of origination.
            Originate originate = new Originate();
            SceneHistory history = chooser.fetchSceneHistory();
            originate.setHistory(history);
            if (!originate.isNovel()) {
                Logger.getLogger(Operator.class).info("origination scene played too recently"); //$NON-NLS-1$
                return;
            }

            // It's not been recently played, so OK to play again.

            Logger.getLogger(Operator.class)
                .info("got origination scene: " + originationScene.name); //$NON-NLS-1$

            Bridge bridge = new Bridge();
            getBridges().put(user.makeKey(), bridge);
            Logger.getLogger(Operator.class).info("foo2"); //$NON-NLS-1$

            // This is a different one, with a bridge.
            // TODO: some refactoring
            SceneChooser sceneChooser = new SceneChooser(bridge, user);
            Logger.getLogger(Operator.class).info("foo3"); //$NON-NLS-1$

            // The listener is null because the Bridge adds it later, and there's
            // no harm (apparently) in not having one.
            Chat newChat = getChatmanager().createChat(user.getEmailAddress(), null);

            originationScene = originate.getNextScene();
            try {
                // This returns pretty quickly, since it just fires up the engine until
                // quiescence.
                chooser.beginScene(originationScene);
                // end it right away to avoid leaving the scene open.
                chooser.endScene(null);
                bridge.run(newChat, true, sceneChooser);
            } catch (InitializationException e) {
                Logger.getLogger(Operator.class).error("bad bridge run"); //$NON-NLS-1$
                e.printStackTrace();
            }
            Logger.getLogger(Operator.class).info("foo4"); //$NON-NLS-1$
        }
    }

    //
    // ROSTER LISTENER METHODS
    //

    /**
     * This means a user has allowed chats from us. Perhaps for the very first time, perhaps a long
     * time after we invited them. So we should try to decide if we have anything to say, using the
     * SceneChooser.
     * 
     * @see org.jivesoftware.smack.RosterListener#entriesAdded(java.util.Collection)
     */
    @Override
    public void entriesAdded(Collection<String> addresses) {
        Logger.getLogger(Operator.class).info("entries added"); //$NON-NLS-1$
        for (String entry : addresses) {
            Logger.getLogger(Operator.class).info("entry added: " + entry); //$NON-NLS-1$

            // First make sure this user is really on the roster.
            // This should not happen.
            if (!(getRoster().contains(entry))) {
                Logger.getLogger(Operator.class).error("No roster entry for: " + entry); //$NON-NLS-1$
                continue;
            }

            // Then make sure they're "available".
            // This is normal.
            if (!(getRoster().getPresence(entry).isAvailable())) {
                Logger.getLogger(Operator.class).info("No available presence for: " + entry); //$NON-NLS-1$
                continue;
            }

            User theUser;
            try {
                theUser = findOrCreateUser(entry);

                if (theUser == null) {
                    Logger.getLogger(Operator.class).error(
                        "Failed to make find or make user record for userid: " + entry); //$NON-NLS-1$
                    continue;
                }

                // Then make sure we're not already chatting with this user
                // This can happen all the time, so it's not an error.
                if (getBridges().containsKey(theUser.makeKey())) {
                    Logger.getLogger(Operator.class).info("Already chatting with: " + entry); //$NON-NLS-1$
                    continue;
                }

                // Now we have a user who's on the roster, available, and not already chatting with
                // us.
                // Should we bug them?

                originate(theUser);
            } catch (FatalException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * This entry is removed from the roster. We ignore this.
     * 
     * @see org.jivesoftware.smack.RosterListener#entriesDeleted(java.util.Collection)
     */
    @Override
    public void entriesDeleted(Collection<String> addresses) {
        Logger.getLogger(Operator.class).info("entries deleted"); //$NON-NLS-1$
        for (String entry : addresses) {
            Logger.getLogger(Operator.class).info("entry deleted: " + entry); //$NON-NLS-1$
        }
    }

    /**
     * Dunno what kind of update this is. It appears not to occur anyway.
     * 
     * @see org.jivesoftware.smack.RosterListener#entriesUpdated(java.util.Collection)
     */
    @Override
    public void entriesUpdated(Collection<String> addresses) {
        Logger.getLogger(Operator.class).info("entries updated"); //$NON-NLS-1$
        for (String entry : addresses) {
            Logger.getLogger(Operator.class).info("entry updated: " + entry); //$NON-NLS-1$
        }
    }

    /**
     * Gtalk seems to produce these presence messages every so often, even if there's been no recent
     * change. So we shouldn't trigger any action on the message itself.
     * 
     * @see org.jivesoftware.smack.RosterListener#presenceChanged(org.jivesoftware.smack.packet.Presence)
     */
    @Override
    public void presenceChanged(Presence presence) {
        Logger.getLogger(Operator.class).info("presence changed"); //$NON-NLS-1$
        Logger.getLogger(Operator.class).info("presence from: " + presence.getFrom() //$NON-NLS-1$
            + " changed: " + presence.getType().toString()); //$NON-NLS-1$
    }

    //
    //
    //

    public ChatManager getChatmanager() {
        return this.chatmanager;
    }

    public void setChatmanager(ChatManager chatmanager) {
        this.chatmanager = chatmanager;
    }

    public XMPPConnection getConnection() {
        return this.connection;
    }

    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }

    public Roster getRoster() {
        return this.roster;
    }

    public void setRoster(Roster roster) {
        this.roster = roster;
    }

    public String getOperatorUsername() {
        return this.operatorUsername;
    }

    public void setOperatorUsername(String operatorUsername) {
        this.operatorUsername = operatorUsername;
    }

    public String getOperatorPassword() {
        return this.operatorPassword;
    }

    public void setOperatorPassword(String operatorPassword) {
        this.operatorPassword = operatorPassword;
    }

    public ConnectionConfiguration getConfig() {
        return this.config;
    }

    public void setConfig(ConnectionConfiguration config) {
        this.config = config;
    }

    public Map<ExternalKey, Bridge> getBridges() {
        return this.bridges;
    }

    public void setBridges(Map<ExternalKey, Bridge> bridges) {
        this.bridges = bridges;
    }

    public Thread getThread() {
        return this.thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
