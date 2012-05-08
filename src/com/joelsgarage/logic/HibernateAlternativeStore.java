/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.readers.HibernateRecordReader;
import com.joelsgarage.dataprocessing.readers.QuantityFactHibernateRecordReader;
import com.joelsgarage.model.ClassMember;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.model.IndividualFact;
import com.joelsgarage.model.IndividualPreference;
import com.joelsgarage.model.IndividualPropertyPreference;
import com.joelsgarage.model.IndividualPropertyUtility;
import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.MaximizerQuantityPropertyUtility;
import com.joelsgarage.model.MeasurementQuantity;
import com.joelsgarage.model.MeasurementUnit;
import com.joelsgarage.model.MinimizerQuantityPropertyUtility;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.OptimizerQuantityPropertyUtility;
import com.joelsgarage.model.PropertyWeight;
import com.joelsgarage.model.QuantityFact;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.HibernateProperty;
import com.joelsgarage.util.HibernateUtil;

/**
 * Supplies all the data needed for ranking. Everything is cold; fetched as needed using Hibernate.
 * 
 * TODO: make it faster, do the join, use indexes. :-)
 * 
 * @author joel
 * 
 */
public class HibernateAlternativeStore extends AlternativeStoreImpl {
    private Logger logger = null;
    private final HibernateModelAccess modelAccess;
    private SessionFactory factory;

    public HibernateAlternativeStore(String configResource) {
        this(new HibernateModelAccess(configResource));
        setFactory(HibernateUtil.createSessionFactory(configResource, null));
    }

    // for testing
    public HibernateAlternativeStore(final HibernateModelAccess modelAccess) {
        setLogger(Logger.getLogger(HibernateAlternativeStore.class));
        this.modelAccess = modelAccess;
    }

    @Override
    public List<Decision> getDecisions(int pageSize) {
        final List<Decision> result = new ArrayList<Decision>();
        final List<ModelEntity> dList =
            getModelAccess().findByCriteria(Decision.class.getName(), 0, pageSize);
        if (dList != null) {
            for (final ModelEntity d : dList) {
                if (d instanceof Decision) {
                    result.add((Decision) d);
                }
            }
        }
        return result;
    }

    @Override
    public List<Individual> getAlternatives(final ExternalKey decisionKey, int pageSize) {
        Logger.getLogger(HibernateAlternativeStore.class).info(
            "getAlternatives for decision: " + decisionKey.toString()); //$NON-NLS-1$
        List<Individual> result = null;
        final List<ModelEntity> dList =
            getModelAccess().findByKey(decisionKey, Decision.class.getName(), 0, pageSize);
        if ((dList != null) && (dList.size() > 0)) {
            // if multiple, just return the first one.
            final ModelEntity e = dList.get(0);
            if (e instanceof Decision) {
                final Decision d = (Decision) e;
                result = getAlternatives(d, pageSize);
            }
        }
        return result;
    }

    @Override
    public List<Individual> getAlternatives(final Decision d, int pageSize) {
        Logger.getLogger(HibernateAlternativeStore.class).info(
            "getAlternatives for decision: " + d.makeKey().toString()); //$NON-NLS-1$
        // TODO: Do all this in SQL. the problem is that hibernate wants to know
        // about the associations in order to do the joins, and i've not told hibernate
        // about any associations.

        final List<Individual> result = new ArrayList<Individual>();

        // Fetch all the ClassMember records, so we can find the ones relevant
        // to this class.
        // TODO: do it in SQL.
        final List<ModelEntity> classMemberList =
            getModelAccess().findByCriteria(ClassMember.class.getName(), 0, pageSize,
                Restrictions.eq(HibernateProperty.CLASS_KEY, d.getClassKey()));
        if (classMemberList != null) {
            for (final ModelEntity c : classMemberList) {
                if (c instanceof ClassMember) {
                    final ClassMember classMember = (ClassMember) c;
                    // For each membership record, fetch and add the individual.
                    // TODO: should we guarantee uniqueness here?
                    final ModelEntity e =
                        this.modelAccess.read(classMember.getIndividualKey(), Individual.class
                            .getName());
                    if (e instanceof Individual) {
                        result.add((Individual) e);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Individual getIndividual(ExternalKey individualKey) {
        final ModelEntity i = getModelAccess().read(individualKey, Individual.class.getName());
        if (i instanceof Individual) {
            return (Individual) i;
        }
        return null;
    }

    @Override
    public List<IndividualPreference> getIndividualPreferences(final Decision d,
        final Individual i, int pageSize) {
        List<IndividualPreference> result = new ArrayList<IndividualPreference>();
        for (Stakeholder s : getStakeholders(d, pageSize)) {
            // Find IndividualPreferences wherein i is primary *or* secondary, and e is stakeholder.
            List<ModelEntity> iPList =
                getModelAccess().findByCriteria(
                    IndividualPreference.class.getName(),
                    0,
                    pageSize,
                    Restrictions.and( //
                        Restrictions.or( //
                            Restrictions.eq(HibernateProperty.PRIMARY_INDIVIDUAL_KEY, i.makeKey()), //
                            Restrictions
                                .eq(HibernateProperty.SECONDARY_INDIVIDUAL_KEY, i.makeKey())), //
                        Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, s.makeKey())));
            if (iPList != null) {
                for (ModelEntity f : iPList) {
                    if (f instanceof IndividualPreference) {
                        IndividualPreference iP = (IndividualPreference) f;
                        result.add(iP);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<Stakeholder> getStakeholders(final Decision d, int pageSize) {
        List<Stakeholder> result = new ArrayList<Stakeholder>();
        List<ModelEntity> stakeholderList =
            getModelAccess().findByCriteria(Stakeholder.class.getName(), 0, pageSize,
                Restrictions.eq(HibernateProperty.DECISION_KEY, d.makeKey()));
        if (stakeholderList != null) {
            for (ModelEntity e : stakeholderList) {
                if (e instanceof Stakeholder) {
                    result.add((Stakeholder) e);
                }
            }
        }
        return result;
    }

    @Override
    public List<IndividualPreference> getIndividualPreferences(final ExternalKey eKey,
        final ExternalKey iKey, final ExternalKey jKey, int pageSize) {
        List<IndividualPreference> result = new ArrayList<IndividualPreference>();
        List<ModelEntity> iPList =
            getModelAccess().findByCriteria(IndividualPreference.class.getName(), 0, pageSize,
                Restrictions.and( //
                    Restrictions.and( //
                        Restrictions.eq(HibernateProperty.PRIMARY_INDIVIDUAL_KEY, iKey), //
                        Restrictions.eq(HibernateProperty.SECONDARY_INDIVIDUAL_KEY, jKey)), //
                    Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, eKey)));
        if (iPList != null) {
            for (ModelEntity f : iPList) {
                if (f instanceof IndividualPreference) {
                    result.add((IndividualPreference) f);
                }
            }
        }
        return result;
    }

    @Override
    public List<IndividualPreference> getAllIndividualPreferences(ExternalKey stakeholderKey,
        int pageSize) {
        List<ModelEntity> list =
            getModelAccess().findByCriteria(IndividualPreference.class.getName(), 0, pageSize,
                Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, stakeholderKey));
        List<IndividualPreference> result = new ArrayList<IndividualPreference>();
        if (list != null) {
            for (ModelEntity f : list) {
                if (f instanceof IndividualPreference) {
                    result.add((IndividualPreference) f);
                }
            }
        }
        return result;
    }

    @Override
    public List<IndividualUtility> getIndividualUtilities(final Stakeholder e, final Individual i,
        int pageSize) {
        List<IndividualUtility> result = new ArrayList<IndividualUtility>();
        List<ModelEntity> list =
            getModelAccess().findByCriteria(IndividualUtility.class.getName(), 0, pageSize,
                Restrictions.and( //
                    Restrictions.eq(HibernateProperty.INDIVIDUAL_KEY, i.makeKey()), //
                    Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, e.makeKey())));
        if (list != null) {
            for (ModelEntity f : list) {
                if (f instanceof IndividualUtility) {
                    result.add((IndividualUtility) f);
                }
            }
        }
        return result;
    }

    @Override
    public List<IndividualFact> getIndividualFacts(final ExternalKey subjectKey, int pageSize) {
        List<IndividualFact> result = new ArrayList<IndividualFact>();
        final List<ModelEntity> list =
            getModelAccess().findByCriteria(IndividualFact.class.getName(), 0, pageSize,
                Restrictions.eq(HibernateProperty.SUBJECT_KEY, subjectKey));
        if (list != null) {
            for (ModelEntity e : list) {
                if (e instanceof IndividualFact) {
                    result.add((IndividualFact) e);
                }
            }
        }
        return result;
    }

    @Override
    public List<QuantityFact> getQuantityFacts(final ExternalKey subjectKey, int pageSize) {
        List<QuantityFact> result = new ArrayList<QuantityFact>();
        final List<ModelEntity> list =
            getModelAccess().findByCriteria(QuantityFact.class.getName(), 0, pageSize,
                Restrictions.eq(HibernateProperty.SUBJECT_KEY, subjectKey));
        if (list != null) {
            for (ModelEntity e : list) {
                if (e instanceof QuantityFact) {
                    result.add((QuantityFact) e);
                }
            }
        }
        return result;
    }

    @Override
    public List<IndividualPropertyPreference> getIndividualPropertyPreferences(
        final ExternalKey stakeholderKey, int pageSize) {
        List<IndividualPropertyPreference> result = new ArrayList<IndividualPropertyPreference>();
        final List<ModelEntity> list =
            getModelAccess().findByCriteria(IndividualPropertyPreference.class.getName(), 0,
                pageSize, Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, stakeholderKey));
        if (list != null) {
            for (ModelEntity e : list) {
                if (e instanceof IndividualPropertyPreference) {
                    result.add((IndividualPropertyPreference) e);
                }
            }
        }
        return result;
    }

    @Override
    public List<IndividualPropertyPreference> getIndividualPropertyPreferencesForProperty(
        ExternalKey stakeholderKey, ExternalKey propertyKey, int pageSize) {
        List<IndividualPropertyPreference> result = new ArrayList<IndividualPropertyPreference>();

        final List<ModelEntity> list =
            getModelAccess().findByCriteria(IndividualPropertyPreference.class.getName(), 0,
                pageSize, //
                Restrictions.and( //
                    Restrictions.eq(HibernateProperty.PROPERTY_KEY, propertyKey), //
                    Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, stakeholderKey)));
        if (list != null) {
            for (ModelEntity e : list) {
                if (e instanceof IndividualPropertyPreference) {
                    result.add((IndividualPropertyPreference) e);
                }
            }
        }
        return result;
    }

    @Override
    public List<PropertyWeight> getPropertyWeights(final ExternalKey stakeholderKey, int pageSize) {
        List<PropertyWeight> result = new ArrayList<PropertyWeight>();
        final List<ModelEntity> list =
            getModelAccess().findByCriteria(PropertyWeight.class.getName(), 0, pageSize,
                Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, stakeholderKey));
        if (list != null) {
            for (ModelEntity e : list) {
                if (e instanceof PropertyWeight) {
                    result.add((PropertyWeight) e);
                }
            }
        }
        return result;
    }

    @Override
    public List<PropertyWeight> getPropertyWeightsForProperty(ExternalKey stakeholderKey,
        ExternalKey propertyKey, int pageSize) {
        List<PropertyWeight> result = new ArrayList<PropertyWeight>();

        final List<ModelEntity> list =
            getModelAccess().findByCriteria(PropertyWeight.class.getName(), 0, pageSize, //
                Restrictions.and( //
                    Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, stakeholderKey), //
                    Restrictions.eq(HibernateProperty.PROPERTY_KEY, propertyKey)));
        if (list != null) {
            for (ModelEntity e : list) {
                if (e instanceof PropertyWeight) {
                    result.add((PropertyWeight) e);
                }
            }
        }
        return result;
    }

    @Override
    public List<IndividualPropertyUtility> getIndividualPropertyUtilityList(
        ExternalKey stakeholderKey, int pageSize) {
        List<IndividualPropertyUtility> result = new ArrayList<IndividualPropertyUtility>();
        final List<ModelEntity> list =
            getModelAccess().findByCriteria(IndividualPropertyUtility.class.getName(), 0, pageSize,
                Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, stakeholderKey));
        if (list != null) {
            for (ModelEntity e : list) {
                if (e instanceof IndividualPropertyUtility) {
                    result.add((IndividualPropertyUtility) e);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("nls")
    @Override
    public List<MaximizerQuantityPropertyUtility> getMaximizerQuantityPropertyUtilityList(
        ExternalKey stakeholderKey, int pageSize) {
        Logger.getLogger(HibernateAlternativeStore.class).info(
            "About to query the DB for maxQPUs, for stakeholder: " + stakeholderKey.toString());
        List<MaximizerQuantityPropertyUtility> result =
            new ArrayList<MaximizerQuantityPropertyUtility>();
        final List<ModelEntity> list =
            getModelAccess().findByCriteria(MaximizerQuantityPropertyUtility.class.getName(), 0,
                pageSize, Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, stakeholderKey));
        if (list != null) {
            Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info("Got nonnull...");

            for (ModelEntity e : list) {
                Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info("Got one...");

                if (e instanceof MaximizerQuantityPropertyUtility) {
                    Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                        "of the right type...");

                    result.add((MaximizerQuantityPropertyUtility) e);
                } else {
                    Logger.getLogger(PropertyDerivedPreferenceSummaryFactory.class).info(
                        "wrong type: " + e.getClass().getName());

                }
            }
        }
        return result;
    }

    @Override
    public List<MinimizerQuantityPropertyUtility> getMinimizerQuantityPropertyUtilityList(
        ExternalKey stakeholderKey, int pageSize) {
        List<MinimizerQuantityPropertyUtility> result =
            new ArrayList<MinimizerQuantityPropertyUtility>();
        Logger.getLogger(HibernateAlternativeStore.class).info(
            "About to query the DB for minQPUs, for stakeholder: " + stakeholderKey.toString()); //$NON-NLS-1$
        final List<ModelEntity> list =
            getModelAccess().findByCriteria(MinimizerQuantityPropertyUtility.class.getName(), 0,
                pageSize, Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, stakeholderKey));
        if (list != null) {
            for (ModelEntity e : list) {
                if (e instanceof MinimizerQuantityPropertyUtility) {
                    result.add((MinimizerQuantityPropertyUtility) e);
                }
            }
        }
        return result;
    }

    @Override
    public List<OptimizerQuantityPropertyUtility> getOptimizerQuantityPropertyUtilityList(
        ExternalKey stakeholderKey, int pageSize) {
        List<OptimizerQuantityPropertyUtility> result =
            new ArrayList<OptimizerQuantityPropertyUtility>();
        Logger.getLogger(HibernateAlternativeStore.class).info(
            "About to query the DB for optQPUs, for stakeholder: " + stakeholderKey.toString()); //$NON-NLS-1$
        final List<ModelEntity> list =
            getModelAccess().findByCriteria(OptimizerQuantityPropertyUtility.class.getName(), 0,
                pageSize, Restrictions.eq(HibernateProperty.STAKEHOLDER_KEY, stakeholderKey));
        if (list != null) {
            for (ModelEntity e : list) {
                if (e instanceof OptimizerQuantityPropertyUtility) {
                    result.add((OptimizerQuantityPropertyUtility) e);
                }
            }
        }
        return result;
    }

    @Override
    public MeasurementQuantity getMeasurementQuantity(ExternalKey measurementQuantityKey) {
        final ModelEntity i =
            getModelAccess().read(measurementQuantityKey, MeasurementQuantity.class.getName());
        if (i instanceof MeasurementQuantity) {
            return (MeasurementQuantity) i;
        }
        return null;
    }

    @Override
    public MeasurementUnit getMeasurementUnit(ExternalKey measurementUnitKey) {
        final ModelEntity i =
            getModelAccess().read(measurementUnitKey, MeasurementUnit.class.getName());
        if (i instanceof MeasurementUnit) {
            return (MeasurementUnit) i;
        }
        return null;
    }

    @Override
    public List<MeasurementUnit> getMeasurementUnitList(int pageSize) {
        List<MeasurementUnit> result = new ArrayList<MeasurementUnit>();
        final List<ModelEntity> list =
            getModelAccess().findByCriteria(MeasurementUnit.class.getName(), 0, pageSize);
        if (list != null) {
            for (ModelEntity e : list) {
                if (e instanceof MeasurementUnit) {
                    result.add((MeasurementUnit) e);
                }
            }
        }
        return result;
    }

    @Override
    public void visitQuantityFactsForProperty(ExternalKey propertyKey,
        ProcessNode<QuantityFact, ?> processNode) {
        Session readerSession = getFactory().openSession();
        if (readerSession == null)
            return;

        Map<String, Object> queryConstraints = new HashMap<String, Object>();
        queryConstraints.put(HibernateProperty.PROPERTY_KEY, propertyKey);
        ReaderConstraint constraint = new ReaderConstraint(QuantityFact.class, queryConstraints);

        HibernateRecordReader<QuantityFact> reader =
            new QuantityFactHibernateRecordReader(readerSession, constraint);
        processNode.setReader(reader);
        processNode.run();
        readerSession.close();

    }

    // Accessors

    public Logger getLogger() {
        return this.logger;
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    public HibernateModelAccess getModelAccess() {
        return this.modelAccess;
    }

    public SessionFactory getFactory() {
        return this.factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

}
