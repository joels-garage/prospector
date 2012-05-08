/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * A user is an online persona.
 * 
 * Represents the account of someone who either uses our system, or who has written content that the
 * system knows about, i.e. has an account on some other system.
 * 
 * The login id is the inherited "name" field.
 * 
 * There are a lot of personas on the web; maybe ten per real user, and maybe a billion real users,
 * so it's eventually 10B records.
 * 
 * TODO(joel): multiple email addresses per person, multiple names per person, etc, using separate
 * tables. note most of the time we can only *guess* about these equivalences, with some confidence.
 * if a user confirms two email addresses, then we're 100% confident, but the rest of the time, it's
 * less.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.USER_TYPE)
public final class User extends ModelEntity {
    public static final String PASSWORD = "password"; //$NON-NLS-1$
    public static final String REAL_NAME = "real_name"; //$NON-NLS-1$
    public static final String EMAIL_ADDRESS = "email_address"; //$NON-NLS-1$
    public static final String COOKIE = "cookie"; //$NON-NLS-1$
    public static final String ADMIN = "admin"; //$NON-NLS-1$
    /**
     * Crypt(password) (string nullable) -- most users don't have accounts. e.g. "fpasdf"
     * 
     * TODO(joel): make this a list, i.e. it can change over time.
     */
    private String password = new String();
    /**
     * Name, for display (no markup) (string nullable) -- maybe we guessed this. In particular, for
     * chat users, we fill in the name with the "bare address" from the user id.
     * 
     * TODO(joel): make this a list, i.e. it can change over time, aliases, etc.
     */
    private String realName = new String();
    /**
     * email address (string nullable). e.g. "joel.truher@gmail.com" For chat users, this is the
     * "bare address" from the userid.
     * 
     * TODO(joel): make this a list; multiple can be simultaneously valid.
     */
    private String emailAddress = new String();

    // TODO: implement the fields below:
    // /**
    // * Some sort of location information, maybe see
    // * http://microformats.org/wiki/location-formats
    // */
    // private Location location;
    // /**
    // * The real person who owns this account. In reality, accounts may be
    // * shared, so maybe this should be represented differently.
    // */
    // private Long personId;

    /**
     * The permanent cookie associated with this user.
     * 
     * TODO(joel): make this a list, i.e. it can change over time.
     */
    private String cookie = new String();

    /**
     * Does this user have admin privileges?
     * 
     * TODO: this seems like the wrong way to represent this concept.
     */
    private boolean admin = false;

    protected User() {
        super();
    }

    public User(String emailAddress, String namespace) throws FatalException {
        setNamespace(namespace);
        setEmailAddress(emailAddress);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getEmailAddress());
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setPassword(input.get(PASSWORD));
        setRealName(input.get(REAL_NAME));
        setEmailAddress(input.get(EMAIL_ADDRESS));
        setCookie(input.get(COOKIE));
        setAdmin(Boolean.valueOf(input.get(ADMIN)).booleanValue());
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(PASSWORD, getPassword());
        output.put(REAL_NAME, getRealName());
        output.put(EMAIL_ADDRESS, getEmailAddress());
        output.put(COOKIE, getCookie());
        output.put(ADMIN, String.valueOf(getAdmin()));
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " password: " + String.valueOf(getPassword()) + //
    // " realname: " + String.valueOf(getRealName()) + //
    // " email: " + String.valueOf(getEmailAddress()) + //
    // " cookie: " + String.valueOf(getCookie()) + //
    // " admin: " + String.valueOf(isAdmin());
    //        return result;
    //    }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.encode(getName());
    // }

    /**
     * This user is a guest user.
     */
    public boolean isGuest() {
        if ((getEmailAddress() == null) || (getEmailAddress().length() == 0)) {
            return true;
        }
        return false;
    }

    /**
     * This user has admin privileges.
     * 
     * @return
     */
    public boolean isAdmin() {
        if (this.admin)
            return true;
        return false;
    }

    // Note password is not a visible field !
    // TODO: remove this entirely, save the crypted version only
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @VisibleField(REAL_NAME)
    @WizardField(type = WizardField.Type.REQUIRED, position = 1)
    public String getRealName() {
        return this.realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @VisibleField(EMAIL_ADDRESS)
    @WizardField(type = WizardField.Type.OPTIONAL, position = 1)
    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCookie() {
        return this.cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean getAdmin() {
        return this.admin;
    }
}
