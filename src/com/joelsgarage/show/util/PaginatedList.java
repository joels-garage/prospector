/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.show.model.DisplayModelEntity;
import com.joelsgarage.util.Dowser;
import com.joelsgarage.util.DowserFactory;
import com.joelsgarage.util.ForeignKey;
import com.joelsgarage.util.HbnSessionUtil;

/**
 * Database-backed paginated list.
 * 
 * Each row has an entity and a name (the name field of the entity.
 * 
 * The idea is that this would be accessed directly by the JSP fragment.
 * 
 * @author joel
 * 
 */
public class PaginatedList {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE = 0;
    /** How many records per page */
    private int pageSize = -1;
    /** Zero-based page index */
    private int page = -1;

    // TODO: pull out the list, result count, etc, as a separate object.
    /** True if there are more pages */
    private boolean more;
    /** Total results, per type */
    private Long resultCount;
    /** One list for each final type */
    private List<DisplayModelEntity> list;
    /** Name for the type reflected here */
    private String typeName;
    /** the join reflected here, if present */
    private ForeignKey foreignKey;
    /** Optional restrict on name field */
    private String nameQuery;

    /** Provides the subclass map for polymorphic queries */
    Dowser dowser;

    public PaginatedList() {
        setResultCount(new Long(0));
        setList(new ArrayList<DisplayModelEntity>());
        setDowser(DowserFactory.newDowser());
    }

    /** Total pages. I would think JSP could do this, but it doesn't seem to be rounding correctly */
    public Long getTotalPages() {
        long pages = 1 + ((getResultCount().longValue() - 1) / getPageSize());
        return Long.valueOf(pages);
    }

    /**
     * Fetch a single list.
     * 
     * @param specifiedClass
     *            the type of entity to fetch. Must be final.
     * @param namespace
     *            optional constraint on the returned namespace. Use for lists.
     * @param key
     *            optional key constraint. if property is null, apply to primary key, to get an
     *            instance. if property is not null, apply to the named property, to get a list.
     * @param property
     *            if not null, the key constraint are applied to this field rather than the 'key'
     *            field
     * @param dereferenceDepth
     *            if zero, don't dereference. instances get this decremented.
     */
    @SuppressWarnings("nls")
    public void populateList(Class<? extends ModelEntity> specifiedClass, String namespace,
        ExternalKey key, String property, int dereferenceDepth, ForeignKey specifiedForeignKey) {
        setForeignKey(specifiedForeignKey);

        Logger.getLogger(PaginatedList.class).info(
            "populating list for class: " + specifiedClass.getName() + " with dereference "
                + String.valueOf(dereferenceDepth));
        Logger.getLogger(PaginatedList.class).info(
            "page: " + getPage() + " pagesize: " + getPageSize());

        Logger.getLogger(PaginatedList.class).info("populating list");

        setList(new ArrayList<DisplayModelEntity>());

        // If the user has not supplied pagination parameters, set the defaults:
        if (getPageSize() < 0)
            setPageSize(DEFAULT_PAGE_SIZE);
        if (getPage() < 0)
            setPage(DEFAULT_PAGE);

        Logger.getLogger(PaginatedList.class).info(
            "page: " + getPage() + " pagesize: " + getPageSize());

        // Verify the type is allowed.
        // TODO: use the Dowser to find the subclasses of disallowed types.
        if (Dowser.isAllowed(specifiedClass)) {
            Logger.getLogger(PaginatedList.class).error(
                "Suicidal polymorphic query for class: " + specifiedClass.getName()); //$NON-NLS-1$
            return;
        }

        setTypeName(getDowser().getTypeNames().get(specifiedClass));

        // TODO: remove HbnSession here?
        Session session = HbnSessionUtil.getCurrentSession();

        Map<String, Object> properties = new HashMap<String, Object>();

        List<String> wheres = new ArrayList<String>();

        if (namespace != null && namespace.length() > 0) {
            wheres.add("c.key.namespace = :namespace");
            properties.put("namespace", namespace);
            Logger.getLogger(PaginatedList.class).info("Namespace: " + namespace);
        }

        if (key != null) {
            String field = "key";
            if (property != null) {
                field = property;
            }
            wheres.add("c." + field + " = :key");
            properties.put("key", key);
            Logger.getLogger(PaginatedList.class).info("Key: " + key);
        }

        if (getNameQuery() != null && getNameQuery() != "") {
            wheres.add("c.name like :nameQuery");
            // Note leading and trailing "%" ... no index for you!
            properties.put("nameQuery", "%" + getNameQuery() + "%");
        }

        String queryString = "from " + specifiedClass.getName() + " as c";
        for (int i = 0; i < wheres.size(); ++i) {
            if (i == 0)
                queryString += " where ";
            if (i > 0)
                queryString += " and ";
            queryString += wheres.get(i);
        }

        String orderBy = " order by c.name";

        Logger.getLogger(PaginatedList.class).info("My Query: " + queryString);

        Query query = session.createQuery(queryString + orderBy);

        query.setProperties(properties);
        query.setReadOnly(true);

        query.setFirstResult(getPageSize() * getPage());
        query.setMaxResults(getPageSize() + 1);
        query.setFetchSize(getPageSize() + 1);

        Logger.getLogger(PaginatedList.class).info(
            "pagesize: " + getPageSize() + " page: " + getPage());

        List<?> result = query.list();

        Logger.getLogger(PaginatedList.class).info("got this many rows: " + result.size());

        for (Object item : result) {
            if (specifiedClass.isInstance(item)) {
                if (item instanceof ModelEntity) {
                    Logger.getLogger(PaginatedList.class).info(
                        "item name: " + ((ModelEntity) item).getName());
                }
                DisplayModelEntity displayEntity = specifiedClass.cast(item).newDisplayEntity();
                displayEntity.setDereference(dereferenceDepth);
                Logger.getLogger(PaginatedList.class)
                    .info("dereference depth: " + dereferenceDepth);
                if (dereferenceDepth > 0) {
                    Logger.getLogger(PaginatedList.class).info(
                        "dereferencing: " + displayEntity.getInstance().getName());
                    // TODO: remove this, replace with lazy loader
                    displayEntity.dereference(getForeignKey(), getPage(), getPageSize());
                } else {
                    Logger.getLogger(PaginatedList.class).info(
                        "not dereferencing: " + displayEntity.getInstance().getName());
                }
                getList().add(displayEntity);
            } else {
                Logger.getLogger(PaginatedList.class).info(
                    "wrong class returned.  asked for " + specifiedClass.getName() + " but got "
                        + item.getClass().getName());
            }
        }

        if (getList().size() > getPageSize()) {
            // extra one says "more available" so set the bit and remove the instance
            setMore(true);
            getList().remove(getList().size() - 1);
        } else {
            // no more to be seen.
            setMore(false);
        }

        String aggregate = "select count(*) ";
        query = session.createQuery(aggregate + queryString);
        query.setProperties(properties);
        query.setReadOnly(true);
        Long count = (Long) query.uniqueResult();

        Logger.getLogger(PaginatedList.class).info("number of results: " + String.valueOf(count));

        if (count == null) {
            // this is some kind of weird error.
            setResultCount(Long.valueOf(0));
        } else {
            setResultCount(count);
        }
    }

    //

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

    public boolean isMore() {
        return this.more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public Dowser getDowser() {
        return this.dowser;
    }

    public void setDowser(Dowser dowser) {
        this.dowser = dowser;
    }

    public Long getResultCount() {
        return this.resultCount;
    }

    public void setResultCount(Long resultCount) {
        this.resultCount = resultCount;
    }

    public List<DisplayModelEntity> getList() {
        return this.list;
    }

    public void setList(List<DisplayModelEntity> list) {
        this.list = list;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public ForeignKey getForeignKey() {
        return this.foreignKey;
    }

    public void setForeignKey(ForeignKey foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getNameQuery() {
        return this.nameQuery;
    }

    public void setNameQuery(String nameQuery) {
        this.nameQuery = nameQuery;
    }
}
