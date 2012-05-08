/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SessionScope;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.action.Wizard;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.writers.HibernateRecordWriterFactory;
import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.User;
import com.joelsgarage.show.LoginActionBean;
import com.joelsgarage.show.ShowActionBean;
import com.joelsgarage.show.util.MultiList;
import com.joelsgarage.show.util.PaginatedList;
import com.joelsgarage.util.DateUtil;
import com.joelsgarage.util.Dowser;
import com.joelsgarage.util.DowserFactory;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.WizardField;

/**
 * Create a new instance using a form. Uses a Wizard to manage a sequence of pages that are used to
 * look up foreign entities.
 * 
 * It would be nice to use the DisplayModelEntity for this, i.e. the JSP walks over the maps that
 * the DisplayModelEntity has in it: fields and manyToOne. The oneToMany fields are not used in the
 * create or edit views -- they involve edits to the *other* entities. :-)
 * 
 * TODO: guard this bean with the security filter that forwards to login/registration if there is no
 * user in context.
 * 
 * maybe session scoping means i won't need to declare all the form fields.
 * 
 * The states here are not very clear. TODO: clean up the transitions
 * 
 * @author joel
 */
@SessionScope
@Wizard(startEvents = { "start", "update" })
@UrlBinding(value = "/create/{type}")
public class CreateActionBean extends ShowActionBean {
    /**
     * The type of the instance, e.g. "individual". Determines which form fields to show. If an
     * abstract type is specified, then just barf for now.
     */
    private String type;

    /** Governs "allowed type" */
    private Dowser dowser;

    /** How many records per page */
    @Validate(minvalue = 1)
    private int pageSize = -1;

    /** Zero-based page index */
    @Validate(minvalue = 0)
    private int page = -1;

    /** For pages that involve picklists, this is the list set. */
    private MultiList multiList;

    /**
     * For each position, the item in the list the user chose. It's just the loop index, because
     * it's small. :-) I could have used the serialized key itself, but the serialized key is big,
     * and there are a lot of them on the page. It's a map because the wizard remembers this value
     * between submits. It's a map to make it obvious that there will be holes.
     */
    private Map<Integer, Integer> choice;

    /** For each position, the type name of the multilist classes we actually picked */
    private Map<Integer, String> typeName;

    /** Primitive field values indexed by field name. */
    private Map<String, Object> fields;

    /** Key field values indexed by field name. */
    private Map<String, ExternalKey> manyToOne;

    /** Required fields by field name. Used in validation. */
    private Set<String> required;

    /** Writes the completed instance. */
    private RecordWriter<ModelEntity> writer;

    /**
     * Look up the current position to find the fields that should be displayed. For each position
     * there can be a collection of fields or a single key. These maps don't use the same keys as
     * the annotated positions; they are consecutive.
     */
    private Map<Integer, List<String>> fieldPosition;
    /** The field name for the key at the specified position */
    private Map<Integer, String> keyPosition;
    /** The class of the key at the specified position */
    private Map<Integer, Class<? extends ModelEntity>> keyClass;

    /**
     * The zero-based page position in the wizard. Fields marked with positions matching this one
     * will be rendered on the page.
     */
    private int position;
    /** The maximum valid position. If the submitted position is more than this, we're done. */
    private int maxPosition;

    /**
     * Optional restrict on name field
     * 
     * TODO: figure out why this is here
     */
    private String nameQuery;

    /** Where to go back to at the end */
    private String targetUrl;

    public CreateActionBean() {
        this(new HibernateRecordWriterFactory(null).newInstance(new ReaderConstraint(User.class)));
    }

    public CreateActionBean(RecordWriter<ModelEntity> writer) {
        Logger.getLogger(CreateActionBean.class).info("Construct"); //$NON-NLS-1$

        setMultiList(new MultiList());
        setChoice(new HashMap<Integer, Integer>());
        setTypeName(new HashMap<Integer, String>());
        setFields(new HashMap<String, Object>());
        setManyToOne(new HashMap<String, ExternalKey>());
        setRequired(new HashSet<String>());
        setWriter(writer);
        setType(new String());
        setDowser(DowserFactory.newDowser());
        setFieldPosition(new HashMap<Integer, List<String>>());
        setKeyPosition(new HashMap<Integer, String>());
        setKeyClass(new HashMap<Integer, Class<? extends ModelEntity>>());
    }

    protected String getJSP() {
        return "/create.jsp"; //$NON-NLS-1$
    }

    protected String getErrorJSP() {
        return "/error/error.jsp"; //$NON-NLS-1$
    }

    /** Ensures that the specified type is allowed. */
    @ValidationMethod
    public void validateType(ValidationErrors errors) {
        Logger.getLogger(CreateActionBean.class).info("got type " + getType()); //$NON-NLS-1$
        Class<? extends ModelEntity> specifiedClass = getDowser().getAllowedTypes().get(getType());

        // If type is not specified, or if it's not an allowed type, it's a fatal error.
        if (specifiedClass == null) {
            errors.add("type", new LocalizableError("invalidType", getType())); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        // Accept only final types.
        if (!Modifier.isFinal(specifiedClass.getModifiers())) {
            errors.add("type", new LocalizableError("invalidType", getType())); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        // Hm, not sure I like this.
        Logger.getLogger(CreateActionBean.class).info("valid type " + getType()); //$NON-NLS-1$
    }

    @ValidationMethod
    public void debug(@SuppressWarnings("unused")
    ValidationErrors errors) {
        for (String s : getFields().keySet()) {
            Logger.getLogger(CreateActionBean.class).info(
                "DEBUG! field key: " + s + " value: " + String.valueOf(getFields().get(s))); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /** Ensures that fields that are required _for this position_ are, in fact, present */
    @ValidationMethod
    public void validateRequired(ValidationErrors errors) {
        Logger.getLogger(CreateActionBean.class).info("Validate required"); //$NON-NLS-1$
        for (String s : getFields().keySet()) {
            Logger.getLogger(CreateActionBean.class).info(
                "field key: " + s + " value: " + String.valueOf(getFields().get(s))); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Integer currentPosition = Integer.valueOf(getPosition());

        // The primitive fields are written directly...
        List<String> currentFields = getFieldPosition().get(currentPosition);
        if (currentFields != null) {
            for (String field : currentFields) {
                Logger.getLogger(CreateActionBean.class).info("map size: " + getFields().size()); //$NON-NLS-1$
                Logger.getLogger(CreateActionBean.class).info("validating: " + field); //$NON-NLS-1$
                validateField(errors, field, getFields());
            }
        }

        // But the keys are just submitted as choice and typename...
        String keyField = getKeyPosition().get(currentPosition);
        if (keyField != null) {
            if (getChoice().get(currentPosition) == null) {
                errors.add(keyField, new LocalizableError("missingRequiredField")); //$NON-NLS-1$
                Logger.getLogger(CreateActionBean.class).info("map required field: " + keyField); //$NON-NLS-1$
            }
            if (getTypeName().get(currentPosition) == null) {
                errors.add(keyField, new LocalizableError("missingRequiredField")); //$NON-NLS-1$
                Logger.getLogger(CreateActionBean.class).info("map required field: " + keyField); //$NON-NLS-1$
            }
        }
    }

    /** Adds an error if the specified field name is not present or if its value is blank */
    protected void validateField(ValidationErrors errors, String field, Map<String, ?> valueMap) {
        if (getRequired().contains(field)) {
            Logger.getLogger(CreateActionBean.class).info("map size: " + valueMap.size()); //$NON-NLS-1$
            for (String s : valueMap.keySet()) {
                Logger.getLogger(CreateActionBean.class).info(
                    "key: '" + s + "' value: '" + String.valueOf(valueMap.get(s)) + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            if (!valueMap.containsKey(field)) {
                errors.add(field, new LocalizableError("missingRequiredField")); //$NON-NLS-1$
                Logger.getLogger(CreateActionBean.class).info("map required field: " + field); //$NON-NLS-1$
                return;
            }
            if (valueMap.get(field).toString().equals("")) { //$NON-NLS-1$
                errors.add(field, new LocalizableError("missingRequiredField")); //$NON-NLS-1$
                Logger.getLogger(CreateActionBean.class).info("required field is blank: " + field //$NON-NLS-1$
                    + " value: " + String.valueOf(valueMap.get(field))); //$NON-NLS-1$
            }
        }
    }

    /**
     * Populate the position maps. Guarantee that the field positions and key positions don't
     * overlap, and that each key position has but a single key. The first key encountered wins.
     * 
     * First we run through the positions as they appear in the annotations, populating some
     * temporary maps. Then we compact the maps to a consecutive positioning.
     * 
     * TODO: look at superclasses to find wizard field annotations. This will keep me from having to
     * repeat the annotations on the covariant join fields, remembering to keep the position the
     * same.
     */
    @Before
    protected void populatePositions() {
        Logger.getLogger(CreateActionBean.class).info("populatePositions"); //$NON-NLS-1$
        Logger.getLogger(CreateActionBean.class).info("position: " + getPosition()); //$NON-NLS-1$

        Map<Integer, List<String>> annotatedFieldPosition = new HashMap<Integer, List<String>>();
        Map<Integer, String> annotatedKeyPosition = new HashMap<Integer, String>();
        Map<Integer, Class<? extends ModelEntity>> annotatedKeyClass =
            new HashMap<Integer, Class<? extends ModelEntity>>();
        SortedSet<Integer> validPositions = new TreeSet<Integer>();

        Class<? extends ModelEntity> specifiedClass = getDowser().getAllowedTypes().get(getType());
        Method[] methods = specifiedClass.getMethods();
        for (Method method : methods) {
            WizardField wizardField = method.getAnnotation(WizardField.class);
            if (wizardField == null)
                continue;
            int wizardPosition = wizardField.position();
            Integer positionKey = Integer.valueOf(wizardPosition);
            validPositions.add(positionKey);
            String label = null;

            VisibleField visibleField = method.getAnnotation(VisibleField.class);
            if (visibleField != null) {
                label = visibleField.value();
                if (label == null)
                    continue;
                if (!annotatedFieldPosition.containsKey(positionKey)) {
                    annotatedFieldPosition.put(positionKey, new ArrayList<String>());
                }
                Logger.getLogger(CreateActionBean.class).info(
                    "found label " + label + " at position " + String.valueOf(positionKey)); //$NON-NLS-1$ //$NON-NLS-2$
                annotatedFieldPosition.get(positionKey).add(label);
            } else {
                VisibleJoin visibleJoin = method.getAnnotation(VisibleJoin.class);
                if (visibleJoin != null) {
                    label = visibleJoin.name();
                    if (label == null)
                        continue;
                    if (annotatedKeyPosition.containsKey(positionKey)) {
                        Logger.getLogger(CreateActionBean.class).error(
                            "In type: " + specifiedClass.getName() //$NON-NLS-1$
                                + " ignoring overlapping key: " + label //$NON-NLS-1$
                                + " at position: " + String.valueOf(positionKey)); //$NON-NLS-1$
                        continue;
                    }
                    Logger.getLogger(CreateActionBean.class).info(
                        "found label " + label + " at position " + String.valueOf(positionKey)); //$NON-NLS-1$ //$NON-NLS-2$
                    annotatedKeyPosition.put(positionKey, label);
                    annotatedKeyClass.put(positionKey, visibleJoin.value());
                }
            }

            WizardField.Type wizardFieldType = wizardField.type();
            if (wizardFieldType == null)
                continue;
            if (wizardFieldType.equals(WizardField.Type.REQUIRED)) {
                Logger.getLogger(CreateActionBean.class).info("found required: " + label); //$NON-NLS-1$
                getRequired().add(label);
            }
        }
        // Verify that the maps don't overlap
        for (Integer key : annotatedFieldPosition.keySet()) {
            if (annotatedKeyPosition.containsKey(key)) {
                // obliterate the invalid key -- fields take precedence.
                Logger.getLogger(CreateActionBean.class).error(
                    "In type: " + specifiedClass.getName() //$NON-NLS-1$
                        + " overlapping position: " + String.valueOf(key)); //$NON-NLS-1$
                annotatedKeyPosition.remove(key);
            }
        }

        // Compact the annotated maps to consecutive positions.
        Iterator<Integer> iter = validPositions.iterator();
        setMaxPosition(0); // used as the loop counter
        while (iter.hasNext()) {
            Integer sourceKey = iter.next();
            Integer destKey = Integer.valueOf(getMaxPosition());
            if (annotatedFieldPosition.containsKey(sourceKey)) {
                Logger.getLogger(CreateActionBean.class).error(
                    "Compacted Field " + String.valueOf(destKey) //$NON-NLS-1$
                        + " is " + annotatedFieldPosition.get(sourceKey)); //$NON-NLS-1$
                getFieldPosition().put(destKey, annotatedFieldPosition.get(sourceKey));
            } else if (annotatedKeyPosition.containsKey(sourceKey)) {
                Logger.getLogger(CreateActionBean.class).error(
                    "Compacted Key " + String.valueOf(destKey) //$NON-NLS-1$
                        + " is " + annotatedKeyPosition.get(sourceKey)); //$NON-NLS-1$
                getKeyPosition().put(destKey, annotatedKeyPosition.get(sourceKey));
                getKeyClass().put(destKey, annotatedKeyClass.get(sourceKey));
            } else {
                Logger.getLogger(CreateActionBean.class).error(
                    "Weirdness: " + String.valueOf(sourceKey)); //$NON-NLS-1$
            }
            if (iter.hasNext())
                setMaxPosition(getMaxPosition() + 1);
        }
        Logger.getLogger(CreateActionBean.class).error(
            "maxposition " + String.valueOf(getMaxPosition())); //$NON-NLS-1$
        // Should exit the loop with max position correctly set.
    }

    /** Verifies the WizardField annotations, for the whole entity. */
    protected void verifyRequiredFields(ModelEntity entity, ValidationErrors errors) {
        // Walk over the entity methods, and make sure there's a value for any that are marked
        // REQUIRED.
        Logger.getLogger(CreateActionBean.class).info(
            "verifying required fields on entity: " + entity.toString()); //$NON-NLS-1$
        Method[] methods = entity.getClass().getMethods();
        for (Method method : methods) {
            Logger.getLogger(CreateActionBean.class).info("verifying method: " + method.getName()); //$NON-NLS-1$
            VisibleField visibleField = method.getAnnotation(VisibleField.class);
            VisibleJoin visibleJoin = method.getAnnotation(VisibleJoin.class);
            String name;
            if (visibleField != null) {
                name = visibleField.value();
            } else if (visibleJoin != null) {
                name = visibleJoin.name();
            } else {
                continue;
            }
            WizardField wizardField = method.getAnnotation(WizardField.class);
            if (wizardField == null)
                continue;
            WizardField.Type wizardFieldType = wizardField.type();
            if (wizardFieldType == null)
                continue;
            if (wizardFieldType.equals(WizardField.Type.REQUIRED)) {
                Logger.getLogger(CreateActionBean.class).info(
                    "verifying required field method: " + method.getName()); //$NON-NLS-1$
                // Check the value. It's never null, so we need to check some other way.
                try {
                    Object value = method.invoke(entity);
                    Logger.getLogger(CreateActionBean.class).info(
                        "verifying required field method: " + String.valueOf(value)); //$NON-NLS-1$
                    // TODO: better validation
                    if (value == null) {
                        errors.add(name, new LocalizableError("missingRequiredField")); //$NON-NLS-1$
                        Logger.getLogger(CreateActionBean.class).error(
                            "null result for field: " + name); //$NON-NLS-1$
                    } else if (value instanceof ExternalKey) {
                        if (value.equals(new ExternalKey())) {
                            errors.add(name, new LocalizableError("missingRequiredField")); //$NON-NLS-1$
                            Logger.getLogger(CreateActionBean.class).error(
                                "uninitialized field: " + name //$NON-NLS-1$
                                    + " value: " + String.valueOf(value)); //$NON-NLS-1$
                        }
                    } else if (value.toString().equals("")) { //$NON-NLS-1$
                        errors.add(name, new LocalizableError("missingRequiredField")); //$NON-NLS-1$
                        Logger.getLogger(CreateActionBean.class).error("missing field: " + name //$NON-NLS-1$
                            + " value: " + String.valueOf(value)); //$NON-NLS-1$
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** Entry point for the wizard. Forwards to the JSP that displays the first (blank) page. */
    @DontValidate
    public Resolution start() {
        // just validate the specified type (not the rest of the wizard validation)
        ValidationErrors errors = new ValidationErrors();
        validateType(errors);
        if (errors.size() != 0) {
            return new ForwardResolution(getErrorJSP());
        }
        setPosition(0);
        return display();
    }

    /**
     * The "middle" steps of the wizard use this method. It should not be called at the end, i.e.
     * there should be at least one more step after this. The JSP ensures that.
     */
    public Resolution more() {
        Logger.getLogger(CreateActionBean.class).info("position: " + getPosition()); //$NON-NLS-1$

        // Whoops. The position *may* be incremented depending on the outcome of the event.
        // Mmm actually i seem to have put all the recycling into the validator, so maybe this is
        // ok.
        setPosition(getPosition() + 1);
        Logger.getLogger(CreateActionBean.class).info("position: " + getPosition()); //$NON-NLS-1$

        return display();
    }

    /**
     * The multi-list renderer uses the default handler to update the list, an event which does not
     * progress the wizard state. So this is the default handler that just refreshes the list.
     */
    @DontValidate
    @DefaultHandler
    public Resolution update() {
        return display();
    }

    /** The event fired when a user chooses from the picklist */
    public Resolution choose() {
        chooseInternal();
        return more();
    }

    /** Event fired when a user chooses from the picklist and it's the last step */
    public Resolution chooseAndFinish() {
        chooseInternal();
        return finish();
    }

    /** Maps the users picklist choice to a populated manyToOne value */
    protected void chooseInternal() {
        Integer currentPosition = Integer.valueOf(getPosition());
        Logger.getLogger(CreateActionBean.class).info(
            "position: " + String.valueOf(currentPosition)); //$NON-NLS-1$
        Logger.getLogger(CreateActionBean.class).info(
            "typeName: " + getTypeName().get(currentPosition)); //$NON-NLS-1$
        Logger.getLogger(CreateActionBean.class).info(
            "choice: " + String.valueOf(getChoice().get(currentPosition))); //$NON-NLS-1$

        String fieldName = getKeyPosition().get(Integer.valueOf(getPosition()));
        for (Map.Entry<Integer, String> s : getKeyPosition().entrySet()) {
            Logger.getLogger(CreateActionBean.class).info("key: " + String.valueOf(s.getKey()) // //$NON-NLS-1$
                + " value: " + String.valueOf(s.getValue())); //$NON-NLS-1$

        }
        Logger.getLogger(CreateActionBean.class).info("fieldName: " + fieldName); //$NON-NLS-1$

        Class<? extends ModelEntity> chosenClass =
            getDowser().getAllowedTypes().get(getTypeName().get(currentPosition));
        Logger.getLogger(CreateActionBean.class).info("chose class: " + chosenClass.getName()); //$NON-NLS-1$

        PaginatedList pList = getMultiList().getPaginatedLists().get(chosenClass);

        ExternalKey key =
            pList.getList().get(getChoice().get(currentPosition).intValue()).getInstance().getKey();
        Logger.getLogger(CreateActionBean.class).info("got key: " + key.toString()); //$NON-NLS-1$

        getManyToOne().put(fieldName, key);
    }

    /** This does the real work; used by various event handlers. */
    protected Resolution display() {

        Logger.getLogger(CreateActionBean.class).info("attributes:"); //$NON-NLS-1$
        Enumeration<?> en = getContext().getRequest().getAttributeNames();
        while (en.hasMoreElements()) {
            Object o = en.nextElement();
            Logger.getLogger(CreateActionBean.class).info("name: " + String.valueOf(o) + // //$NON-NLS-1$
                " value: " //$NON-NLS-1$
                + String.valueOf(getContext().getRequest().getAttribute((String) o)));
        }

        Logger.getLogger(CreateActionBean.class).info("position: " + getPosition()); //$NON-NLS-1$

        // This shouldn't happen but just in case ...
        if (getPosition() > getMaxPosition())
            return finish();

        if (getKeyPosition().containsKey(Integer.valueOf(getPosition()))) {
            // String fieldName = getKeyPosition().get(Integer.valueOf(getPosition()));
            Class<? extends ModelEntity> specifiedClass =
                getKeyClass().get(Integer.valueOf(getPosition()));

            // it's a picklist, so populate it.
            getMultiList().setPage(getPage());
            getMultiList().setPageSize(getPageSize());
            getMultiList().setNameQuery(getNameQuery());
            getMultiList().populateList(specifiedClass, null, null, null, 1, null);
        } else {
            // it's a form
        }

        Logger.getLogger(CreateActionBean.class).info("position: " + getPosition()); //$NON-NLS-1$

        return new ForwardResolution(getJSP());
    }

    /** Last submit in the wizard. Instantiates the entity and writes it. */
    public Resolution finish() {
        // You don't need to be logged in until the very end, so check here.
        // TODO: somehow carry our state along
        if (getContext().getUser() == null) {
            Logger.getLogger(CreateActionBean.class).error("No user!"); //$NON-NLS-1$
            return new RedirectResolution(LoginActionBean.class);
        }

        Class<? extends ModelEntity> specifiedClass = getDowser().getAllowedTypes().get(getType());
        try {
            ModelEntity entity = specifiedClass.newInstance();
            Logger.getLogger(CreateActionBean.class).info("Populating!"); //$NON-NLS-1$

            Method[] methods = specifiedClass.getMethods();
            for (Method method : methods) {
                if (method.getAnnotation(WizardField.class) == null)
                    continue;
                String propertyName = method.getName().substring(3);
                String leadingChar = propertyName.substring(0, 1);
                propertyName = leadingChar.toLowerCase() + propertyName.substring(1);
                VisibleField visibleField = method.getAnnotation(VisibleField.class);
                if (visibleField != null) {
                    String label = visibleField.value();
                    Logger.getLogger(CreateActionBean.class).error(
                        "Setting field: " + label + " value: " + getFields().get(label) //$NON-NLS-1$ //$NON-NLS-2$
                            + " with propertyname: " + propertyName); //$NON-NLS-1$
                    BeanUtils.setProperty(entity, propertyName, getFields().get(label));
                    continue; // a method can't be both a field and a join, so proceed
                }
                VisibleJoin visibleJoin = method.getAnnotation(VisibleJoin.class);
                if (visibleJoin != null) {
                    String label = visibleJoin.name();
                    Logger.getLogger(CreateActionBean.class).error(
                        "Setting key: " + label + " value: " + getManyToOne().get(label)); //$NON-NLS-1$ //$NON-NLS-2$
                    BeanUtils.setProperty(entity, propertyName, getManyToOne().get(label));
                    continue;
                }
            }

            // Fill in the determined fields.
            entity.setCreatorKey(getContext().getUser().getKey());

            entity.setKey(entity.compositeKey(SHOW_NAMESPACE));
            DateUtil.setLastModifiedNow(entity);

            // Make sure the fields are all filled in.
            ValidationErrors errors = new ValidationErrors();
            verifyRequiredFields(entity, errors);
            if (errors.size() > 0) {
                getContext().getValidationErrors().putAll(errors);
                Logger.getLogger(CreateActionBean.class).error("Missing fields!"); //$NON-NLS-1$
                return getContext().getSourcePageResolution(); // show the submit page again
            }

            Logger.getLogger(CreateActionBean.class).info("done! writing!"); //$NON-NLS-1$

            // Write the instance. Note this could be done inside DisplayModelEntity too, since
            // it's certainly able to talk to the DB. Not that I like it that way.
            getWriter().open();
            getWriter().write(entity);
            getWriter().close();

            if (getTargetUrl() != null) {
                // now target url really is the url
                return new RedirectResolution(getTargetUrl(), false);
            }
            return new RedirectResolution(BrowseActionBean.class);

        } catch (InstantiationException e) {
            Logger.getLogger(CreateActionBean.class).error(
                "Instantiation: " + specifiedClass.getName()); //$NON-NLS-1$
            e.printStackTrace();
            return new ForwardResolution(getErrorJSP());
        } catch (IllegalAccessException e) {
            Logger.getLogger(CreateActionBean.class).error(
                "Illegal Access: " + specifiedClass.getName()); //$NON-NLS-1$
            e.printStackTrace();
            return new ForwardResolution(getErrorJSP());
        } catch (InvocationTargetException e) {
            Logger.getLogger(CreateActionBean.class).error(
                "Invocation Target: " + specifiedClass.getName()); //$NON-NLS-1$
            e.printStackTrace();
            return new ForwardResolution(getErrorJSP());
        }

    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Dowser getDowser() {
        return this.dowser;
    }

    public void setDowser(Dowser dowser) {
        this.dowser = dowser;
    }

    public MultiList getMultiList() {
        return this.multiList;
    }

    public void setMultiList(MultiList multiList) {
        this.multiList = multiList;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RecordWriter<ModelEntity> getWriter() {
        return this.writer;
    }

    public void setWriter(RecordWriter<ModelEntity> writer) {
        this.writer = writer;
    }

    public int getPosition() {
        Logger.getLogger(CreateActionBean.class).info(
            "GET POSITION: " + String.valueOf(this.position)); //$NON-NLS-1$
        return this.position;
    }

    public void setPosition(int position) {
        Logger.getLogger(CreateActionBean.class).info(
            "SET POSITION, was: " + String.valueOf(this.position) //$NON-NLS-1$
                + " now: " + String.valueOf(position)); //$NON-NLS-1$
        this.position = position;

    }

    public Map<Integer, List<String>> getFieldPosition() {
        return this.fieldPosition;
    }

    public void setFieldPosition(Map<Integer, List<String>> fieldPosition) {
        this.fieldPosition = fieldPosition;
    }

    public Map<Integer, String> getKeyPosition() {
        return this.keyPosition;
    }

    public void setKeyPosition(Map<Integer, String> keyPosition) {
        this.keyPosition = keyPosition;
    }

    public int getMaxPosition() {
        return this.maxPosition;
    }

    public void setMaxPosition(int maxPosition) {
        this.maxPosition = maxPosition;
    }

    public String getNameQuery() {
        return this.nameQuery;
    }

    public void setNameQuery(String nameQuery) {
        this.nameQuery = nameQuery;
    }

    public Map<String, Object> getFields() {
        return this.fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, ExternalKey> getManyToOne() {
        return this.manyToOne;
    }

    public void setManyToOne(Map<String, ExternalKey> manyToOne) {
        this.manyToOne = manyToOne;
    }

    public Set<String> getRequired() {
        return this.required;
    }

    public void setRequired(Set<String> required) {
        this.required = required;
    }

    public String getTargetUrl() {
        return this.targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public Map<Integer, Class<? extends ModelEntity>> getKeyClass() {
        return this.keyClass;
    }

    public void setKeyClass(Map<Integer, Class<? extends ModelEntity>> keyClass) {
        this.keyClass = keyClass;
    }

    public Map<Integer, Integer> getChoice() {
        return this.choice;
    }

    public void setChoice(Map<Integer, Integer> choice) {
        this.choice = choice;
    }

    public Map<Integer, String> getTypeName() {
        return this.typeName;
    }

    public void setTypeName(Map<Integer, String> typeName) {
        this.typeName = typeName;
    }
}
