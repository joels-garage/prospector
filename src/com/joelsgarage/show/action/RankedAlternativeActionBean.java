/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

import org.apache.log4j.Logger;

import com.joelsgarage.prospector.client.model.AnnotatedAlternative;
import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.RankedAlternativeCollection;
import com.joelsgarage.prospector.server.AlternativeRanker;
import com.joelsgarage.prospector.server.AlternativeStore;
import com.joelsgarage.prospector.server.HibernateAlternativeStore;
import com.joelsgarage.prospector.server.PreferenceAlternativeRanker;
import com.joelsgarage.show.ShowActionBean;

/**
 * Displays ranked alternatives for the specified decision.
 * 
 * TODO: make it actually use the decision argument
 * 
 * @author joel
 */
// This might be better as e.g. /browse/decision/foo/alternatives
// TODO: shove the two things together that way, maybe
@UrlBinding(value = "/alternatives/{key.type}/{key.namespace}/{key.key}")
public class RankedAlternativeActionBean extends ShowActionBean {
    // TODO: extract these to some superclass (see MultiList)
    private static final int DEFAULT_PAGE_SIZE = 10;
    @SuppressWarnings("unused")
    private static final int DEFAULT_PAGE = 0;
    /** The key of the decision, or foreign key of the list. */
    private ExternalKey key;

    /** How many records per page */
    @Validate(minvalue = 1)
    private int pageSize = -1;
    /** Zero-based page index */
    @Validate(minvalue = 0)
    private int page = -1;

    // Encapsulates data access
    private AlternativeStore store;
    private AlternativeRanker ranker;

    // A list of lists of alternatives.
    private List<RankedAlternativeCollection> alternatives;

    public RankedAlternativeActionBean() {
        setKey(new ExternalKey());
        setStore(new HibernateAlternativeStore("")); //$NON-NLS-1$
        setRanker(new PreferenceAlternativeRanker(getStore()));
    }

    protected String getJSP() {
        return "/alternatives.jsp"; //$NON-NLS-1$
    }

    protected String getErrorJSP() {
        return "/error/error.jsp"; //$NON-NLS-1$
    }

    // TODO: pagination
    @SuppressWarnings("nls")
    @DefaultHandler
    public Resolution view() {
        String specifiedType = getKey().getType();
        Logger.getLogger(RankedAlternativeActionBean.class).info("got type: " + specifiedType);

        Logger.getLogger(RankedAlternativeActionBean.class).info(
            "view for decision key: " + getKey().getKey());

        int actualPageSize = getPageSize();
        if (actualPageSize < 0)
            actualPageSize = DEFAULT_PAGE_SIZE;

        if (getKey().equals(new ExternalKey())) {
            // Show them all.
            setAlternatives(getRanker().getRankedList(actualPageSize));
        } else {
            // Restrict to the specified one.
            setAlternatives(getRanker().getRankedList(actualPageSize, getKey()));
        }

        if (getAlternatives() == null || getAlternatives().size() == 0) {
            Logger.getLogger(RankedAlternativeActionBean.class).info("no alternative collection");
        } else {
            RankedAlternativeCollection col = getAlternatives().get(0);
            if (col == null || col.getAlternatives().size() == 0) {
                Logger.getLogger(RankedAlternativeActionBean.class).info("no alternatives");
            } else {
                AnnotatedAlternative anno = col.getAlternatives().get(0);
                Logger.getLogger(RankedAlternativeActionBean.class).info(
                    "returned example: " + anno.getIndividual().getName());
            }
        }

        return new ForwardResolution(getJSP());
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

    public ExternalKey getKey() {
        return this.key;
    }

    public void setKey(ExternalKey key) {
        this.key = key;
    }

    public AlternativeStore getStore() {
        return this.store;
    }

    public void setStore(AlternativeStore store) {
        this.store = store;
    }

    public AlternativeRanker getRanker() {
        return this.ranker;
    }

    public void setRanker(AlternativeRanker ranker) {
        this.ranker = ranker;
    }

    public List<RankedAlternativeCollection> getAlternatives() {
        return this.alternatives;
    }

    public void setAlternatives(List<RankedAlternativeCollection> alternatives) {
        this.alternatives = alternatives;
    }

}
