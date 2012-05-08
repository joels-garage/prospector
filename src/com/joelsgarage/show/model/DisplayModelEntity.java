/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Property;

import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.show.action.ModelEntityActionBean;
import com.joelsgarage.show.util.MultiList;
import com.joelsgarage.util.Dowser;
import com.joelsgarage.util.DowserFactory;
import com.joelsgarage.util.ForeignKey;
import com.joelsgarage.util.HbnSessionUtil;
import com.joelsgarage.util.HibernateProperty;
import com.joelsgarage.util.HibernateUtil;
import com.joelsgarage.util.NameUtil;
import com.joelsgarage.util.VisibleField;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;

/**
 * Self-contained (i.e. reference-resolved) Model Entity bean.
 * 
 * ok, i'm tired of making a zillion subclasses of this.
 * 
 * so instead of embedding the foreign type as a member, just use a list of members.
 * 
 * TODO: implement lazy dereferencing with active getters, so instead of Maps, use some other class
 * that lazily loads whatever it's pointing at.
 * 
 * @author joel
 */
public class DisplayModelEntity {
    Class<? extends ModelEntity> persistentClass;
    private String typeName;
    /** Is the subclass-specified model type something that Hibernate knows about? */
    private boolean valid = true;
    /** Also dereference the keys? Also does one-to-many joins. dereference if positive. */
    private int dereference = 0;

    /**
     * The key of this DisplayModelEntity. Now we populate the key without populating the instance
     * itself, and instead lazily populate the instance and the fields below.
     */
    private ExternalKey key = null;

    /** True if the entity can not be fetched, i.e. we tried once, shouldn't try again. */
    private boolean missing = false;

    /** Dynamically bound field here, so I don't need all the subclasses to keep restating it. */
    /** Accessor below double-dereferences this field, so use "instance" not "instance.instance" */
    private Instance<ModelEntity> instance;

    /** Fields on the entity itself */
    private Map<String, Object> fields;
    /** Dereferenced key fields of the instance, indexed by field name */
    private Map<String, DisplayModelEntity> manyToOne;
    /**
     * Other types, indexed by the field name in the foreign type. Because the foreign key could be
     * in a superclass, each join is actually done by multilist, i.e. splitting to the various final
     * types.
     */
    private Map<String, MultiList> oneToMany;
    // /** All ModelEntities have a creator field */
    // private DisplayUser creator;

    private Dowser dowser;

    /**
     * Constructor
     * 
     * @param dereference
     *            true if we should follow ExternalKey references to other entities
     */
    // temporary
    public DisplayModelEntity(boolean dereference) {
        this(null, 0);
    }

    // step 2
    public DisplayModelEntity(Class<? extends ModelEntity> persistentClass, int dereference) {
        setDowser(DowserFactory.newDowser());
        if (persistentClass == null) {
            setPersistentClass(ModelEntity.class);
        } else {
            setPersistentClass(persistentClass);
        }
        setTypeName(getDowser().getTypeNames().get(getPersistentClass()));
        this.instance = new Instance<ModelEntity>();
        setManyToOne(new HashMap<String, DisplayModelEntity>());
        setFields(new HashMap<String, Object>());
        setOneToMany(new HashMap<String, MultiList>());

        setDereference(dereference);

        Session session = HbnSessionUtil.getCurrentSession();
        if (session == null) {
            session = HbnSessionUtil.makeSession();
        }
        if (!HibernateUtil.isValidEntityType(session.getSessionFactory(), getPersistentClass())) {
            Logger.getLogger(ModelEntityActionBean.class).error("No Hibernate Config for type " //$NON-NLS-1$
                + getPersistentClass().getName());
            setValid(false);
            return;
        }

        // if (getDereference() > 0) {
        // setCreator(new DisplayUser(false));
        // }
    }

    /**
     * Fetch the instance whose key is as specified Try lazy, i.e. don't do the fetch.
     */
    public void fetch(ExternalKey instanceKey) {
        setKey(instanceKey);
        Logger.getLogger(DisplayModelEntity.class).info(
            "fetch == lazy fetch for key: " + instanceKey.toString()); //$NON-NLS-1$
    }

    /** Really do the fetch */
    protected void fetchInstance(ExternalKey instanceKey) {
        if (instanceKey == null) {
            Logger.getLogger(DisplayModelEntity.class).info("null key in fetchInstance()"); //$NON-NLS-1$
        } else {
            Logger
                .getLogger(DisplayModelEntity.class)
                .info(
                    "Fetching class:" + getPersistentClass().getName() + " id: " + instanceKey.toString()); //$NON-NLS-1$//$NON-NLS-2$
        }
        fetch(Property.forName(HibernateProperty.KEY).eq(instanceKey));
    }

    /**
     * Fetch the instance whose specified property has the specified key value. If multiple match,
     * we just take the first one. See ListFetcher if you want more.
     */
    public void fetch(String property, ExternalKey foreignKey) {
        fetch(Property.forName(property).eq((foreignKey)));
    }

    /**
     * Fetch the instance of type T given the criterion list specified, and dereference if required.
     * 
     * @param criterion
     */
    public void fetch(Criterion... criterion) {
        // Fetch the base instance
        fetchByCriteria(criterion);
        // if (getDereference() > 0)
        // dereference(null, 0, 0);
    }

    /**
     * Given the current instance, follow its references. The idea is that the instance can be "set"
     * as in a list, and the list details filled in using this method, without refetching the base
     * instance.
     * 
     * OK, so the many-to-one dereferencing can happen at a higher "depth" than the many-to-one. I
     * guess.
     * 
     * @param foreignKey
     *            if not null, restrict one-to-many joins to this one, and paginate as specified.
     * @param page
     * @param pageSize
     */
    @SuppressWarnings("nls")
    public void dereference() {
        this.dereference(null, 0, 0);
    }

    public void dereference(ForeignKey specifiedForeignKey, int page, int pageSize) {
        if (getInstance() == null) {
            Logger.getLogger(DisplayModelEntity.class).info(
                "dereference got null for class: " + getPersistentClass().getName()); //$NON-NLS-1$
            return;
        }
        Logger.getLogger(DisplayModelEntity.class).info(
            "dereferencing class: " + getInstance().getClass().getName()); //$NON-NLS-1$
        // First find the many-to-one relations. the foreign keys are in us or a subclass,
        // so we can reflect to get them.
        if (getDereference() < 1) {
            Logger.getLogger(DisplayModelEntity.class).info(
                "dereference < 1 for class " + getPersistentClass().getName()); //$NON-NLS-1$
            return;
        }

        populateManyToOne();
        // OK now go the other way, dereference the one-to-many relations into little lists.

        if (getDereference() < 1) {
            Logger.getLogger(DisplayModelEntity.class).info(
                "dereference < 1 for class " + getPersistentClass().getName()); //$NON-NLS-1$
            return;
        }

        populateOneToMany(specifiedForeignKey, page, pageSize);
        Logger.getLogger(DisplayModelEntity.class).info(
            "done dereferencing class: " + getInstance().getClass().getName()); //$NON-NLS-1$
    }

    /** Populate the one-to-many relations, i.e. the little lists */
    protected void populateOneToMany(ForeignKey specifiedForeignKey, int page, int pageSize) {
        Logger.getLogger(DisplayModelEntity.class).info(
            "Populating one to many for class: " + getInstance().getClass()); //$NON-NLS-1$
        Set<ForeignKey> foreignKeys = getDowser().getAllowedJoins().get(getInstance().getClass());
        if (foreignKeys == null) {
            // all done
            Logger.getLogger(DisplayModelEntity.class).info(
                "Could not find foreign keys for class: " + getInstance().getClass()); //$NON-NLS-1$
            return;
        }
        Logger.getLogger(DisplayModelEntity.class)
            .info("got this many keys: " + foreignKeys.size()); //$NON-NLS-1$

        for (ForeignKey foreignKey : foreignKeys) {
            Logger.getLogger(DisplayModelEntity.class).info(
                "working on fk: " + foreignKey.toString()); //$NON-NLS-1$

            // If the key is specified, skip all but the specified one.
            if (specifiedForeignKey != null && !foreignKey.equals(specifiedForeignKey))
                continue;
            if (foreignKey.getClas() == null || foreignKey.getMethod() == null
                || foreignKey.getLabel() == null)
                continue;
            // the label is not necessarily unique (though it would be pathological if it were)
            // so prepend it with the class name, to get, e.g. "string_fact/subject"
            String joinType = getDowser().getTypeNames().get(foreignKey.getClas());
            String joinField = foreignKey.getLabel();
            String joinLabel = joinType + "/" + joinField; //$NON-NLS-1$

            // This is the class of the list we want to fetch.
            Class<? extends ModelEntity> otherClass = foreignKey.getClas();
            // Hibernate always thinks the propertyname is the methodname minus "get", downcase the
            // first letter.
            String propertyName;
            Method method = foreignKey.getMethod();
            String methodName = method.getName();
            if (methodName == null)
                continue;
            if (!methodName.startsWith("get")) //$NON-NLS-1$
                continue;
            String leadingChar = methodName.substring(3, 4);
            methodName = methodName.substring(4); // remove "get"
            propertyName = leadingChar.toLowerCase() + methodName;
            // Just query our own namespace?
            // Save namespace constraints for later.
            // String namespace = getInstance().getKey().getNamespace();
            ExternalKey instanceKey = getInstance().getKey();
            MultiList multiList = new MultiList();
            if (specifiedForeignKey != null) {
                multiList.setPage(page);
                multiList.setPageSize(pageSize);
            }
            // fetch all final subtypes of otherclass, with key for property.
            // don't dereference these instances.
            // TODO: differentiate between forward and reverse dereferencing?
            multiList.setPrimary(getInstance().getKey());
            multiList.setJoinType(joinType);
            multiList.setJoinField(joinField);
            multiList.populateList(otherClass, null, instanceKey, propertyName,
                getDereference() - 1, null);

            Logger.getLogger(DisplayModelEntity.class).info("got one to many label: " + joinLabel); //$NON-NLS-1$

            getOneToMany().put(joinLabel, multiList);
        }
    }

    /** Populate the Many-to-one relations, i.e. the single lookup fields */
    protected void populateManyToOne() {
        Logger.getLogger(DisplayModelEntity.class).info(
            "Populating many to one for class: " + getInstance().getClass()); //$NON-NLS-1$

        Method[] methods = getInstance().getClass().getMethods();
        for (Method method : methods) {
            // e.g. getSubjectKey();
            // the only possible joins are ExternalKey getters.
            Class<?> returnType = method.getReturnType();
            if (returnType != ExternalKey.class)
                continue;

            VisibleJoin visibleJoin = method.getAnnotation(VisibleJoin.class);
            if (visibleJoin == null)
                continue;

            // it's a join field.
            // e.g. primaryClass = Individual.class
            // the "clas" is the "foreign" class.
            // primaryClass is actually the superclass, with which we typecheck the "type" field of
            // the key.
            Class<? extends ModelEntity> primaryClass = visibleJoin.value();

            // Check that the join class is visible
            VisibleType joinVisibleType = primaryClass.getAnnotation(VisibleType.class);
            if (joinVisibleType == null) {
                Logger.getLogger(DisplayModelEntity.class).info(
                    "primaryclass " + primaryClass.getName() + " not visibletype"); //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }

            Logger.getLogger(DisplayModelEntity.class).info(
                "primaryclass " + primaryClass.getName() + " is visibletype"); //$NON-NLS-1$ //$NON-NLS-2$

            // e.g. "subject"
            // joinLabel corresponds to the name of the *actual* class of the entity.
            String joinLabel = visibleJoin.name();

            if (joinLabel == null || joinLabel.length() == 0) {
                Logger.getLogger(DisplayModelEntity.class).info("found no label"); //$NON-NLS-1$
                continue;
            }
            Logger.getLogger(DisplayModelEntity.class).info("got label"); //$NON-NLS-1$
            // So now we have a class, primaryClass, a method (this one), and a label,
            // joinLabel.

            try {
                ExternalKey joinKey = (ExternalKey) method.invoke(getInstance());

                if (joinKey == null) {
                    Logger.getLogger(DisplayModelEntity.class).info("Null key."); //$NON-NLS-1$
                    continue;
                }
                // The type specified in the foreign key.
                Class<? extends ModelEntity> actualJoinClass =
                    getDowser().getAllowedTypes().get(joinKey.getType());
                if (actualJoinClass == null) {
                    Logger.getLogger(DisplayModelEntity.class).info(
                        "Could not find join class for key type " + joinKey.getType()); //$NON-NLS-1$
                    continue;
                }

                if (!primaryClass.isAssignableFrom(actualJoinClass)) {
                    Logger.getLogger(DisplayModelEntity.class).info(
                        "Type violation.  Annotated class" + primaryClass.getName() + " key class " //$NON-NLS-1$ //$NON-NLS-2$
                            + actualJoinClass.getName() + " for key " + joinKey.toString()); //$NON-NLS-1$
                    continue;
                }

                // now we have an allowed actual class, so instantiate the display entity for that
                // class.

                DisplayModelEntity joinEntity =
                    new DisplayModelEntity(actualJoinClass, getDereference() - 1);

                Logger.getLogger(DisplayModelEntity.class).info(
                    "trying to invoke method " + method.getName() + " on object " //$NON-NLS-1$ //$NON-NLS-2$
                        + getInstance().getClass().getName());

                joinEntity.fetch(joinKey);
                // I have no idea if this is the right thing to do.
                joinEntity.dereference();

                getManyToOne().put(joinLabel, joinEntity);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
                continue;
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                continue;
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
                continue;
            }
        }
    }

    /**
     * Fetch a single instance of getPersistentClass() type, with the specified additional criteria,
     * and set our instance to it.
     * 
     * @param criterion
     */
    protected void fetchByCriteria(Criterion... criterion) {
        Logger.getLogger(DisplayModelEntity.class).info("fetch."); //$NON-NLS-1$

        setInstance(null);
        if (!isValid()) {
            Logger.getLogger(DisplayModelEntity.class).error("invalid."); //$NON-NLS-1$
            return;
        }

        Logger.getLogger(DisplayModelEntity.class).info("foo."); //$NON-NLS-1$

        Session session = HbnSessionUtil.getCurrentSession();
        Criteria crit = session.createCriteria(getPersistentClass());

        for (Criterion c : criterion) {
            crit.add(c);
        }
        Logger.getLogger(DisplayModelEntity.class).info("foo."); //$NON-NLS-1$

        // We only get one record. If you want more records, use ListFetcher.
        crit.setFirstResult(0);
        crit.setMaxResults(1);
        crit.setFetchSize(1);

        Logger.getLogger(DisplayModelEntity.class).info("foo."); //$NON-NLS-1$

        List<?> result = crit.list();
        Logger.getLogger(DisplayModelEntity.class).info("result count: " + result.size()); //$NON-NLS-1$

        ModelEntity fetchedInstance = null;
        if (result.size() > 0) {
            Object o = result.get(0);
            if (getPersistentClass().isInstance(o)) {
                Logger.getLogger(DisplayModelEntity.class).info(
                    "setting instance : " + getPersistentClass().cast(o).getKey().toString()); //$NON-NLS-1$
                fetchedInstance = getPersistentClass().cast(o);
            } else {
                Logger.getLogger(DisplayModelEntity.class).error("not instance."); //$NON-NLS-1$
            }
        } else {
            Logger.getLogger(DisplayModelEntity.class).error("no result."); //$NON-NLS-1$
        }
        Logger.getLogger(DisplayModelEntity.class).info("foo."); //$NON-NLS-1$

        if (fetchedInstance == null) {
            Logger.getLogger(DisplayModelEntity.class).error("Could not find entity."); //$NON-NLS-1$
        } else {
            setInstance(fetchedInstance);
            Logger.getLogger(DisplayModelEntity.class).info("foo."); //$NON-NLS-1$
        }
    }

    // /** The model type to fetch */
    // public Class<? extends ModelEntity> getPersistentClass() {
    // return ModelEntity.class;
    // }

    /** Double-dereference the instance, fetching it if necessary. */
    public ModelEntity getInstance() {
        if (this.instance == null) {
            Logger.getLogger(DisplayModelEntity.class).info(
                "null instance for type " + this.typeName); //$NON-NLS-1$

            // Maybe we need to fetch it
            fetchInstance(getKey());
            if (this.instance == null) {
                Logger.getLogger(DisplayModelEntity.class).info(
                    "Failed to fetch instance for type " + this.typeName); //$NON-NLS-1$
                return null;
            }
        }

        if (this.instance.getInstance() == null) {
            Logger.getLogger(DisplayModelEntity.class).info(
                "null instance.instance for type " + this.typeName); //$NON-NLS-1$

            // Maybe we need to fetch it
            fetchInstance(getKey());
            if (this.instance.getInstance() == null) {
                Logger.getLogger(DisplayModelEntity.class).info(
                    "Failed to fetch instance for type " + this.typeName); //$NON-NLS-1$
                return null;
            }
        }

        return this.instance.getInstance();
    }

    /** Any time you set the instance, you also repopulate the map of fields */
    public void setInstance(ModelEntity instance) {
        if (instance == null) {
            Logger.getLogger(DisplayModelEntity.class).info("null arg for setInstance()"); //$NON-NLS-1$
            return;
        }

        this.instance.setInstance(instance);

        // For now just set the key from the instance.
        // TODO: reverse this; set the key and do nothing else, and then fetch lazily.
        setKey(instance.getKey());

        Method[] methods = instance.getClass().getMethods();
        for (Method method : methods) {
            VisibleField visibleField = method.getAnnotation(VisibleField.class);
            if (visibleField == null)
                continue;
            String label = visibleField.value();
            try {
                Object value = method.invoke(instance);
                getFields().put(label, value);

            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
                continue;
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                continue;
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
                continue;
            }
        }
    }

    public int getDereference() {
        return this.dereference;
    }

    public void setDereference(int dereference) {
        this.dereference = dereference;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Map<String, DisplayModelEntity> getManyToOne() {
        return this.manyToOne;
    }

    public void setManyToOne(Map<String, DisplayModelEntity> manyToOne) {
        this.manyToOne = manyToOne;
    }

    public Class<? extends ModelEntity> getPersistentClass() {
        return this.persistentClass;
    }

    public void setPersistentClass(Class<? extends ModelEntity> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public Dowser getDowser() {
        return this.dowser;
    }

    public void setDowser(Dowser dowser) {
        this.dowser = dowser;
    }

    public Map<String, Object> getFields() {
        return this.fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, MultiList> getOneToMany() {
        return this.oneToMany;
    }

    public void setOneToMany(Map<String, MultiList> oneToMany) {
        this.oneToMany = oneToMany;
    }

    public boolean isDereference() {
        return getDereference() > 0;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public ExternalKey getKey() {
        return this.key;
    }

    public void setKey(ExternalKey key) {
        this.key = key;
    }

    // These are used to double-encode stuff that Tomcat decodes. I dunno if it will work.

    public String getEncodedKey() {
        return NameUtil.encode(getKey().getKey());
    }

    public String getEncodedType() {
        return NameUtil.encode(getKey().getType());
    }

    // public String getEncodedNamespace() {
    // return NameUtil.encode(getKey().getNamespace());
    //    }

    public boolean isMissing() {
        return this.missing;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

}
