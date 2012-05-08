/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.joelsgarage.callcenter.scenes.AskSceneChoice;
import com.joelsgarage.callcenter.scenes.Chain;
import com.joelsgarage.callcenter.scenes.GetDecision;
import com.joelsgarage.callcenter.scenes.GetDecisionLite;
import com.joelsgarage.callcenter.scenes.GetIndividualPreference;
import com.joelsgarage.callcenter.scenes.GetIndividualPropertyPreference;
import com.joelsgarage.callcenter.scenes.GetStakeholder;
import com.joelsgarage.callcenter.scenes.HistoryBasedScenePlayer;
import com.joelsgarage.callcenter.scenes.Intro;
import com.joelsgarage.callcenter.scenes.Joke;
import com.joelsgarage.callcenter.scenes.RateIndividual;
import com.joelsgarage.callcenter.scenes.RateIndividualProperty;
import com.joelsgarage.callcenter.scenes.ShowAlternatives;
import com.joelsgarage.callcenter.scenes.ShowUrl;
import com.joelsgarage.callcenter.scenes.StakeholderNews;
import com.joelsgarage.callcenter.scenes.VerifyDecision;
import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.writers.ModelEntityHibernateRecordWriter;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.Scene;
import com.joelsgarage.model.User;
import com.joelsgarage.util.DateUtil;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.HibernateProperty;
import com.joelsgarage.util.HibernateUtil;
import com.joelsgarage.util.XMLUtil;

/**
 * A "Scene" is a short dialog intended to accomplish a specific goal. For example, a scene could be
 * "find out what decision the user wants to work on". Another scene could be "get some
 * PropertyUtility data from the user". A scene could even be "ask the user what scene they want to
 * play."
 * 
 * This class chooses a scene.
 * 
 * The plan is for there to be a Scene table in the db, which records the scenes actually played
 * with a particular user. Fields include:
 * <ul>
 * <li> the timestamp of the last time the scene was played with this user (so we don't play the
 * same one too often)
 * <li>its outcome: terminated, completed, in-progress
 * </ul>
 * So this class would pick up the entire scene history for a user, and, using heuristics and
 * ranking, decide what scene to play next. For example, if there's an in-progress scene, a likely
 * outcome would be to restart that scene. If there's no in-progress scene, then new scenes could be
 * derived through some simple rules (e.g. no scenes at all => do the get-decision scene), or some
 * kind of scoring, involving recency, value of information, etc. It's the last bit that pushes this
 * logic into Java rather than just making the heuristics into some states.
 * 
 * This class also has a role in origination, since the greeting is a kind of scene. So the Operator
 * consults this class for new originations to do. The origination SCXML also asks this class what
 * scene to play once connected. Reasons to originate include:
 * <ul>
 * <li>it's been awhile since we've talked
 * <li>a peer stakeholder just updated something; we're just informing you of it
 * <li>you're a stakeholder in a relatively new decision but your impact on that decision is low,
 * because you have few relevant preferences
 * <li>you're a stakeholder in a relatively new decision but your alternative ranking is poorly
 * correlated with the consensus
 * </ul>
 * An origination is a scene like any other scene.
 * 
 * TODO: factor out the scene history into some contained thing.
 * 
 * @author joel
 * 
 */
public class SceneChooser extends SendTargetBase {
    /** Never play a scene with score lower than this */
    private static final Double MIN_SCORE = Double.valueOf(0.0);
    /** Originate only if the scene scores better than this */
    private static final Double MIN_ORIGINATION_SCORE = Double.valueOf(1.0);
    /** Add to roster only if the scene scores better than this */
    private static final Double MIN_ADD_TO_ROSTER_SCORE = Double.valueOf(2.0);
    /** Sort scenes by score. Don't need to break ties. */
    private static Comparator<NextScene> scoreComparator = new Comparator<NextScene>() {
        @Override
        public int compare(NextScene o1, NextScene o2) {
            Double o1Score = Double.valueOf(0.0);
            Double o2Score = Double.valueOf(0.0);
            if (o1 != null)
                o1Score = o1.score;
            if (o2 != null)
                o2Score = o2.score;
            return o1Score.compareTo(o2Score);
        }
    };
    /** Events to the FSM */
    private EventListener listener;
    /** The user we're talking to? */
    private User user;
    /** The current scene */
    private Scene scene;

    /** All the possible scenes */
    private Map<String, HistoryBasedScenePlayer> allScenes =
        new HashMap<String, HistoryBasedScenePlayer>();

    /**
     * SceneChooser chooses the next scene to play, based on the history of scenes played so far,
     * and their outcomes.
     * 
     * @param listener
     *            Provides access for FSM events
     * @param theUser
     *            The user we're talking to.
     */
    public SceneChooser(EventListener listener, User theUser) {
        setListener(listener);
        setUser(theUser);
        if (theUser == null) {
            Logger.getLogger(SceneChooser.class).info("null user"); //$NON-NLS-1$
        } else {
            Logger.getLogger(SceneChooser.class).info("non null user"); //$NON-NLS-1$

        }

        getAllScenes().put(Scenes.ASK_SCENE_CHOICE, new AskSceneChoice(listener, this));
        getAllScenes().put(Scenes.CHAIN, new Chain(this));
        getAllScenes().put(Scenes.GET_DECISION, new GetDecision());
        getAllScenes().put(Scenes.GET_DECISION_LITE, new GetDecisionLite());

        getAllScenes().put(Scenes.GET_INDIVIDUAL_PREFERENCE, new GetIndividualPreference(listener));
        getAllScenes().put(Scenes.GET_INDIVIDUAL_PROPERTY_PREFERENCE,
            new GetIndividualPropertyPreference());
        getAllScenes().put(Scenes.GET_STAKEHOLDER, new GetStakeholder(listener));
        getAllScenes().put(Scenes.INTRO, new Intro());
        getAllScenes().put(Scenes.JOKE, new Joke());
        getAllScenes().put(Scenes.RATE_INDIVIDUAL_PROPERTY, new RateIndividualProperty(listener));
        getAllScenes().put(Scenes.RATE_INDIVIDUAL, new RateIndividual(listener));
        getAllScenes().put(Scenes.SHOW_ALTERNATIVES, new ShowAlternatives(listener));
        getAllScenes().put(Scenes.SHOW_URL, new ShowUrl());
        getAllScenes().put(Scenes.STAKEHOLDER_NEWS, new StakeholderNews());
        getAllScenes().put(Scenes.VERIFY_DECISION, new VerifyDecision());

        for (HistoryBasedScenePlayer target : getAllScenes().values()) {
            target.setUser(getUser());
        }
    }

    /** Register ourselves and also all the component targets. */
    @Override
    public void registerThyself(SendTargetRegistry registry) {
        // register our own methods
        super.registerThyself(registry);

        // register methods of each scene
        for (HistoryBasedScenePlayer target : getAllScenes().values()) {
            target.registerThyself(registry);
        }
    }

    /**
     * Choose which scene to play next. Sets the chosen scene as the "current" scene, and logs it.
     * This assumes that the caller "obeys" the choice of scene, which is not guaranteed.
     * 
     * For now the scenes are the "apps" in the main.sc.xml.
     * 
     * Inputs: none
     * 
     * @throws FatalException
     *             if key population fails
     */
    @SendTarget.Registrant
    public void chooseScene(@SuppressWarnings("unused")
    String data, @SuppressWarnings("unused")
    Element element) throws FatalException {
        NextScene chosenScene = getNextScene(fetchSceneHistory());
        Logger.getLogger(SceneChooser.class).info("Chose scene: " + chosenScene.name); //$NON-NLS-1$
        Logger.getLogger(SceneChooser.class).info(
            "Scene payload: " + XMLUtil.writeXML(chosenScene.input)); //$NON-NLS-1$

        beginScene(chosenScene);
        // The scene name is the event name, and the input document is the eventdata.

        // Don't use null, use an empty input doc.
        if (chosenScene.input == null) {
            Map<String, Node> inputs = new HashMap<String, Node>();
            chosenScene.input =
                XMLUtil.makeDocFromNodes(
                    CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                    HistoryBasedScenePlayer.INPUT, inputs);
        }

        getListener().handleMessage(chosenScene.name, chosenScene.input);
    }

    /**
     * Return a scene that's important enough to solicit a previously-unknown user to join the
     * roster. (really important!). I think the caller only cares if this is null or not, but
     * there's no harm in returning the actual scene.
     */
    public NextScene getAddToRoster() {
        return getNextSceneThreshold(MIN_ADD_TO_ROSTER_SCORE.doubleValue());
    }

    /**
     * Return a scene that's important enough to create a new chat with a user already in the
     * roster.
     */
    public NextScene getOrigination() {
        return getNextSceneThreshold(MIN_ORIGINATION_SCORE.doubleValue());
    }

    /** Return the next scene with score equal to or more than the specified minimum score */
    protected NextScene getNextSceneThreshold(double minScore) {
        SceneHistory sceneHistory = fetchSceneHistory();
        // For now, all we do is select the best scene from getNextScene(),
        // and filter it with a higher threshhold.
        NextScene nextScene = getNextScene(sceneHistory);
        if (nextScene.score == null)
            return null;
        if (nextScene.score.doubleValue() >= minScore) {
            return nextScene;
        }
        return null;
    }

    /**
     * Chooses the next scene by asking the various scenes to supply suggestions and scores.
     * 
     * TODO: look for scene "extended_intro" in the history. if not present (or very old?), do it
     * again.
     * 
     * TODO: factor out the min score
     * 
     * @param history
     *            the full scene history for this user.
     * @return the next scene
     */
    protected NextScene getNextScene(SceneHistory history) {
        Logger.getLogger(SceneChooser.class)
            .info("working on user: " + getUser().getEmailAddress()); //$NON-NLS-1$
        List<NextScene> nextScenes = new ArrayList<NextScene>();

        Logger.getLogger(SceneChooser.class).info("===AVAILABLE SCENES===");//$NON-NLS-1$

        for (HistoryBasedScenePlayer candidateScene : getAllScenes().values()) {
            Logger.getLogger(SceneChooser.class).info(
                "name: " + candidateScene.getClass().getName()); //$NON-NLS-1$
        }

        for (HistoryBasedScenePlayer candidateScene : getAllScenes().values()) {
            candidateScene.setHistory(history);
            Logger.getLogger(SceneChooser.class).info(
                "looking at scene: " + candidateScene.getClass().getName()); //$NON-NLS-1$
            NextScene nextScene = candidateScene.getNextScene();
            if (nextScene == null) {
                Logger.getLogger(SceneChooser.class).info(
                    "null scene from: " + candidateScene.getClass().getName()); //$NON-NLS-1$
            } else {
                Logger.getLogger(SceneChooser.class).info("name: " + nextScene.name); //$NON-NLS-1$
                Logger.getLogger(SceneChooser.class).info(
                    "payload: " + XMLUtil.writeXML(nextScene.input)); //$NON-NLS-1$
                nextScenes.add(nextScene);
            }
        }

        Collections.sort(nextScenes, scoreComparator);

        Logger.getLogger(SceneChooser.class).info("===SCENE SCORES===");//$NON-NLS-1$
        for (NextScene nextScene : nextScenes) {
            Logger.getLogger(SceneChooser.class).info("name: " + nextScene.name //$NON-NLS-1$
                + " score: " + String.valueOf(nextScene.score)); //$NON-NLS-1$
        }

        if (nextScenes.size() > 0) {
            NextScene maxScene = nextScenes.get(nextScenes.size() - 1);
            double maxScore = maxScene.score.doubleValue();
            List<NextScene> maxGroup = new ArrayList<NextScene>();
            maxGroup.add(maxScene);

            // add the rest of them, if any
            for (int index = nextScenes.size() - 2; index >= 0; --index) {
                NextScene aScene = nextScenes.get(index);
                double aScore = aScene.score.doubleValue();
                if (aScore < maxScore)
                    break;
                maxGroup.add(aScene);
            }

            // Select a random scene from the class.
            Collections.shuffle(maxGroup);
            NextScene nextScene = maxGroup.get(0);
            if (nextScene.score.doubleValue() >= MIN_SCORE.doubleValue()) {
                Logger.getLogger(SceneChooser.class).info("chose scene: " + nextScene.name); //$NON-NLS-1$
                Logger.getLogger(SceneChooser.class).info(
                    "payload: " + XMLUtil.writeXML(nextScene.input)); //$NON-NLS-1$
                Logger.getLogger(SceneChooser.class).info(
                    "score: " + String.valueOf(nextScene.score)); //$NON-NLS-1$
                return maxGroup.get(0);
            }
            Logger.getLogger(SceneChooser.class).info(
                "Max score is too low: " + String.valueOf(nextScene.score)); //$NON-NLS-1$
        }

        // Last resort
        Logger.getLogger(SceneChooser.class).info("last resort scene (ask)"); //$NON-NLS-1$
        return new NextScene(Scenes.ASK_SCENE_CHOICE, null, null);
    }

    /**
     * Make a w3c node in the specified namespace containing the serialized input.
     */
    protected static Node makeNode(Object input) {
        return XMLUtil.toXML(input, CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_PROSPECTOR)
            .getDocumentElement();
    }

    /**
     * Log the end of a Scene. Updates the member scene with the specified outcome. Fires
     * logEndSceneDone when finished. The SCXML should wait for this event because the very next
     * state is scene selection, and we need to make sure that the scene history is correctly
     * updated first.
     * 
     * OK, so, the thing is that the scene table is the ONLY record of the ENTIRE outcome of a
     * scene. so it's not just the outcome state that matters, it's whatever data it manipulates.
     * for example, in the pick-a-decision scene, the outcome is "picked one" but there's also the
     * data, i.e. the decision key itself.
     * 
     * So, one option for that kind of thing is for the scene to stash its output data somewhere
     * scene-specific, e.g. create some sort of "current decision" table, but that's so icky i can't
     * even describe it.
     * 
     * I think it's better for the distilled content of the scene to go in the scene table.
     * 
     * So then the "outcome" is some serialized form of that state, interpretable by, i think, just
     * the SceneChooser. Maybe "outcome" should be called "output" to denote its complex nature.
     * 
     * Oh also there needs to be some "input" data for the scene. For example, once you've picked a
     * decision to talk about (the "output" of "ask_scene_choice"), you need to provide that
     * decision key as the "input" of something else like "show_alternatives", so that it knows what
     * to do.
     * 
     * And, to make the log useful, you need to log the input to a scene just like the output.
     * 
     * So, then, I could make an <onexit> for the main chooser that puts the "input" eventdata into
     * the main input datamodel, and i could also make some outside state that logs the "output"
     * eventdata.
     * 
     * so the routine would be that the scene has an 'output' datamodel node, e.g. 'jokeOuput', and
     * copies it to logEndScene, so the main can log it.
     * 
     * @param data
     * @param element
     *            the element is scxml:data, and contains cc:output, which is what we want.
     * @throws FatalException
     */
    @SendTarget.Registrant
    public void logEndScene(String data, Element element) throws FatalException {
        Logger.getLogger(SceneChooser.class)
            .info("element namespace: " + element.getNamespaceURI()); //$NON-NLS-1$

        // we only want the 'output' node, not the containing 'data' node.
        Node outputNode =
            XMLUtil.getNodeByPath(new CallCenterNamespaceContext(), element,
                "/scxml:data/cc:output/."); //$NON-NLS-1$
        String serializedOutput = null;
        if (outputNode == null) {
            Logger.getLogger(SceneChooser.class).info("no matching node"); //$NON-NLS-1$
        } else {
            serializedOutput = XMLUtil.writeXML(outputNode);
        }
        Logger.getLogger(SceneChooser.class).info("Log End scene: " + getScene().getDescription() //$NON-NLS-1$
            + " Payload: " + serializedOutput); //$NON-NLS-1$

        endScene(serializedOutput);
        getListener().handleMessage(data + "Done", null); //$NON-NLS-1$
    }

    /**
     * Create, remember as a member, and log a new scene with the specified name. A new scene has a
     * "start" timestamp of "now", a null "end", and a null "outcome".
     * 
     * For now the sceneName is used as the description.
     * 
     * @param sceneName
     *            name of the scene
     * @param input
     *            serialized (XML) input state
     * @throws FatalException
     *             if key population fails
     */
    protected void beginScene(NextScene nextScene) throws FatalException {
        String now = DateUtil.nowString();
        setScene(new Scene(nextScene.name, getUser().makeKey(), now, null, XMLUtil
            .writeXML(nextScene.input), null, Util.CALLCENTER_NAMESPACE));
        Logger.getLogger(SceneChooser.class).info("Begin scene " + nextScene.name); //$NON-NLS-1$
        logScene();
    }

    /**
     * Logs the completed scene. Takes the member scene, updates its end and outcome, logs it, and
     * nulls the member.
     * 
     * @param output
     *            serialized output state
     * @throws FatalException
     *             if key population fails
     */
    protected void endScene(String output) throws FatalException {
        Logger.getLogger(SceneChooser.class).info("End scene " + getScene().getDescription()); //$NON-NLS-1$
        Logger.getLogger(SceneChooser.class).info("Output " + output); //$NON-NLS-1$
        Scene newScene =
            new Scene(getScene().getDescription(), getScene().getActorKey(), getScene().getStart(),
                DateUtil.nowString(), getScene().getInput(), output, getScene().getNamespace());
        setScene(newScene);

        logScene();
        setScene(null);
    }

    /** Writes (destructively) the member scene to the DB. */
    protected void logScene() {
        Logger.getLogger(SceneChooser.class).info("Logging scene " + getScene().getDescription()); //$NON-NLS-1$

        SessionFactory factory = HibernateUtil.createSessionFactory(null, null);
        Session session = factory.openSession();
        try {
            RecordWriter<ModelEntity> recordWriter = new ModelEntityHibernateRecordWriter(session);
            recordWriter.open();
            recordWriter.write(getScene());
            recordWriter.close();
        } catch (InitializationException e) {
            Logger.getLogger(SceneChooser.class).error("Can't log scene: " + e.getMessage()); //$NON-NLS-1$
        } finally {
            session.close();
        }
    }

    /** Get all the scenes for the current user. Might return an empty list, never null. */
    protected SceneHistory fetchSceneHistory() {
        Logger.getLogger(SceneChooser.class).info("Fetching Scene History"); //$NON-NLS-1$
        SceneHistory history = new SceneHistory();
        Map<String, Object> constraints = new HashMap<String, Object>();
        constraints.put(HibernateProperty.ACTOR_KEY, getUser().makeKey());
        ReaderConstraint readerConstraint = new ReaderConstraint(Scene.class, constraints);
        Util.fill(Scene.class, readerConstraint, history.getHistory());
        return history;
    }

    //
    //
    //

    public EventListener getListener() {
        return this.listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Scene getScene() {
        return this.scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Map<String, HistoryBasedScenePlayer> getAllScenes() {
        return this.allScenes;
    }

    public void setAllScenes(Map<String, HistoryBasedScenePlayer> allScenes) {
        this.allScenes = allScenes;
    }
}
