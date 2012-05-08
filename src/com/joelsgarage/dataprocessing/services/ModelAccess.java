/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;

/**
 * Accessor for model entities. Supports CRUD operations and one bulk operation, which is just to
 * fetch everything in the type.
 * 
 * TODO(joel): add exceptions to this API, e.g. missing key etc.
 * 
 * @author joel
 * 
 */
public interface ModelAccess extends RemoteService {
    /**
     * Persist the instance, possibly destructively, if an instance with the same key exists. Could
     * be that "save" is the right delegate for this; dunno.
     * 
     * @param instance
     *            an instance of ModelEntity without a key
     */
    public ModelEntity persist(ModelEntity instance);

    /**
     * Fetch by ID. Use a classname if you know what it is.
     * 
     * @param id
     *            the one you want
     * @param className
     *            optional, narrows the query.  FULL class name.
     * @return the thing itself, or maybe null if it doesn't exist.
     */

    public ModelEntity read(ExternalKey key, String className);

    /**
     * Remove instance from the DB.
     * 
     * @param instance
     *            the one you want to delete
     */
    public void delete(ModelEntity instance);

    /**
     * Retrieve a list of instances.. If you specify a name, you get the instances matching that
     * name (exactly). If not, you get all the instances. If you specify a class, you get only
     * instances of that class (and its subclasses).
     * 
     * TODO(joel): paginate this operation.
     * 
     * If passing the class doesn't work, I can just pass the hibernate entity name.
     * 
     * Note: querying a non-final class directly is suicide, and will always return an empty list.
     * See
     * 
     * @param name
     *            exact match on ModelEntity.Name
     * @param className
     *            subclass you want
     * @param page
     *            page index (0 based)
     * @param pageSize
     *            records per page
     * @return the list you want
     */
    public List<ModelEntity> findByName(String name, String className, int page, int pageSize);

    /**
     * Find entities by name. This method, unlike the above, allows nonfinal class names, and
     * returns a map of lists, indexed by the allowed subclasses of the nonfinal class.
     * 
     * @param name
     * @param className
     * @param page
     * @param pageSize
     * @return one map entry per final subtype
     */
    public Map<String, List<ModelEntity>> findByNameMultiType(String name, String className,
        int page, int pageSize);

    /**
     * It is possible to specify an id that exists, but not within the type specified.
     * 
     * @param id
     * @param className
     * @return
     */
    public List<ModelEntity> findByKey(ExternalKey key, String className, int page, int pageSize);
}