/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents a little conversation we had with a user. The dialog experience consists of sequences
 * of scenes, each of which has a goal, and some small exchange. For example, the first scene is to
 * find out what the user's goal is (i.e. what class).
 * 
 * This is not a big table -- it's the number of users times the number of scenes each has played,
 * so it's in the millions, for awhile.
 * 
 * The SceneChooser uses the entire scene history for a user to decide what scene to play next.
 * 
 * Nowhere in the DB are the types of scenes described -- they're in the CallCenter somewhere, just
 * referred to here by their "descriptions".
 * 
 * @author joel
 */
@VisibleType(ExternalKey.SCENE_TYPE)
public final class Scene extends ModelEntity {
    /**
     * Machine-readable identifier for the KIND of scene played (since the "name" field applies to
     * this particular playing). Could be as simple as "answer" or more complicated, if a scene has
     * some parameters worth remembering.
     */
    private String description = new String();
    /** The user we talked to. */
    private ExternalKey actorKey = new ExternalKey();
    /** ISO 8601 date when the scene began. Same format as LastModified. */
    private String start = new String();
    /** ISO 8601 date when the scene ended. */
    private String end = new String();
    /**
     * Machine-readable state input for the scene. For example, most scenes need to know what the
     * active decision key is. It's a serialized XML thing, e.g.
     * 
     * <foo><decisionKey>a/b/c</decisionKey></foo>
     */
    private String input = new String();

    /**
     * Machine-readable identifier for the outcome of the scene. Could be the literal "outcome" of
     * the scene subflow (which presumably will include something like 'terminated by user'). Also
     * could be the special outcome, 'timeout'.
     * 
     * OK actually this is a serialized (XML) thing with multiple fields. For example:
     * 
     * <foo> <outcome>bar</outcome> </foo>
     */
    private String output = new String();

    protected Scene() {
        super();
    }

    /**
     * minimal key fields here. same actor is pretty unlikely to have exactly the same start
     * timestamp for the same scene. When we "update" the scene upon completion, it needs to have
     * the same key, so of course "end" and "output" are not key fields. "input" is the only
     * question, and I think it's not necessary.
     * 
     * @param description
     *            keyfield
     * @param actorKey
     *            keyfield
     * @param start
     *            keyfield
     * @param end
     * @param input
     * @param output
     * @param namespace
     * @throws FatalException
     */
    public Scene(String description, ExternalKey actorKey, String start, String end, String input,
        String output, String namespace) throws FatalException {
        super(namespace);
        setDescription(description);
        setActorKey(actorKey);
        setStart(start);
        setEnd(end);
        setInput(input);
        setOutput(output);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getDescription());
        u.update(getActorKey());
        u.update(getStart());
    }

    // // TODO: fix this before using it
    // @Deprecated
    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayDecision d = new DisplayDecision(true);
    // d.setInstance(this);
    // return d;
    // }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " description : " + String.valueOf(getDescription()) //
    // + " actorKey: " + String.valueOf(getActorKey()) //
    // + " start: " + String.valueOf(getStart()) //
    // + " end: " + String.valueOf(getEnd()) //
    // + " input: " + String.valueOf(getInput()) //
    // + " output: " + String.valueOf(getOutput());
    // return result;
    // }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.encode(getName());
    // }

    //

    @VisibleField("description")
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @VisibleJoin(value = User.class, name = "actor")
    public ExternalKey getActorKey() {
        return this.actorKey;
    }

    public void setActorKey(ExternalKey actorKey) {
        this.actorKey = actorKey;
    }

    @VisibleField("start")
    public String getStart() {
        return this.start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    @VisibleField("end")
    public String getEnd() {
        return this.end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @VisibleField("outcome")
    public String getOutput() {
        return this.output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @VisibleField("input")
    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
