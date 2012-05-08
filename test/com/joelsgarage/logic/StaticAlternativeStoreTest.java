package com.joelsgarage.logic;

import java.util.List;

import junit.framework.TestCase;

import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Individual;
import com.joelsgarage.util.FatalException;

@SuppressWarnings("nls")
public class StaticAlternativeStoreTest extends TestCase {
	private static final int PAGE_SIZE = 1000;
	AlternativeStore store;

	@Override
	public void setUp() throws FatalException {
		this.store = new StaticAlternativeStore();
	}

	protected void assertKeyFields(ExternalKey test, String type, String key) {

		assertEquals(type, test.getType());
		assertEquals(key, test.getKey());
	}

	public void testGetDecisions() {
		final List<Decision> dList = this.store.getDecisions(PAGE_SIZE);
		assertEquals(2, dList.size());
	}

	public void testGetAlternatives() {
		final List<Decision> dList = this.store.getDecisions(PAGE_SIZE);

		final Decision d1 = dList.get(0);
		final List<Individual> iList1 = this.store.getAlternatives(d1, PAGE_SIZE);
		assertEquals(8, iList1.size());

		final Decision d2 = dList.get(1);
		final List<Individual> iList2 = this.store.getAlternatives(d2, PAGE_SIZE);
		assertEquals(2, iList2.size());
	}

    // public void testGetIndividualPreferences() {
    // final List<Decision> dList = this.store.getDecisions(PAGE_SIZE);
    // final Decision d1 = dList.get(0);
    // final List<Individual> iList1 = this.store.getAlternatives(d1, PAGE_SIZE);
    //
    // final Individual i = iList1.get(0);
    // // assertEquals(21, i.getId().longValue());
    // assertTrue(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21").equals(i.getKey()));
    // final List<IndividualPreference> iPList = this.store.getIndividualPreferences(d1, i,
    // PAGE_SIZE);
    // assertEquals(2, iPList.size());
    // // The individual we got is the one we asked for.
    // assertKeyFields(iPList.get(0).getPrimaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE,
    // "21");
    // }
    //
    // public void testGetStakeholders() {
    // final List<Decision> dList = this.store.getDecisions(PAGE_SIZE);
    // final Decision d = dList.get(0);
    // final List<Stakeholder> eList = this.store.getStakeholders(d, PAGE_SIZE);
    // assertEquals(1, eList.size());
    // // assertEquals(41, eList.get(0).getId().longValue());
    // assertTrue(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41").equals(eList.get(0)
    // .getKey()));
    //
    // }
    //
    // public void testGetIndividualPreferencesWithTwoArgs() {
    // final Stakeholder e = new Stakeholder();
    // // e.setId(Long.valueOf(41));
    // e.setKey(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"));
    // final Individual i = new Individual();
    // // i.setId(Long.valueOf(21));
    // i.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    // final Individual j = new Individual();
    // // j.setId(Long.valueOf(23));
    // j.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "23"));
    //
    // final List<IndividualPreference> rList = this.store.getIndividualPreferences(e, i, j,
    // PAGE_SIZE);
    //
    // assertEquals(1, rList.size());
    // final IndividualPreference r = rList.get(0);
    // assertKeyFields(r.getStakeholderKey(), "", ExternalKey.STAKEHOLDER_TYPE, "41");
    // assertKeyFields(r.getPrimaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "21");
    // assertKeyFields(r.getSecondaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "23");
    // }
    //
    // public void testGetIndividualUtilities() {
    // final Stakeholder e = new Stakeholder();
    // // e.setId(Long.valueOf(41));
    // e.setKey(new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "41"));
    // final Individual i = new Individual();
    // // i.setId(Long.valueOf(21));
    // i.setKey(new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "21"));
    //
    // final List<IndividualUtility> uList = this.store.getIndividualUtilities(e, i, PAGE_SIZE);
    //
    // assertNotNull(uList);
    // assertEquals(1, uList.size());
    // final IndividualUtility u = uList.get(0);
    // assertKeyFields(u.getStakeholderKey(), "", ExternalKey.STAKEHOLDER_TYPE, "41");
    // assertKeyFields(u.getIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "21");
    // }
    //
    // public void testGetFacts() {
    // List<IndividualFact> fList = this.store.getIndividualFacts(new ExternalKey("",
    // ExternalKey.INDIVIDUAL_TYPE, "22"), PAGE_SIZE);
    // assertNotNull(fList);
    // assertEquals(0, fList.size());
    //
    // fList = this.store.getIndividualFacts(
    // new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "25"), PAGE_SIZE);
    // assertNotNull(fList);
    // assertEquals(2, fList.size());
    //
    // Fact f = fList.get(0);
    // assertTrue(f instanceof IndividualFact);
    // IndividualFact iF = (IndividualFact) f;
    // assertKeyFields(iF.getObjectKey(), "", ExternalKey.INDIVIDUAL_TYPE, "27");
    // assertKeyFields(iF.getPropertyKey(), "", ExternalKey.PROPERTY_TYPE, "51");
    //
    // f = fList.get(1);
    // assertTrue(f instanceof IndividualFact);
    // iF = (IndividualFact) f;
    // assertKeyFields(iF.getObjectKey(), "", ExternalKey.INDIVIDUAL_TYPE, "21");
    // assertKeyFields(iF.getPropertyKey(), "", ExternalKey.PROPERTY_TYPE, "a");
    //
    // fList = this.store.getIndividualFacts(
    // new ExternalKey("", ExternalKey.INDIVIDUAL_TYPE, "26"), PAGE_SIZE);
    // assertNotNull(fList);
    // assertEquals(2, fList.size());
    // f = fList.get(0);
    // assertNotNull(f);
    // assertTrue(f instanceof IndividualFact);
    // iF = (IndividualFact) f;
    // assertKeyFields(iF.getObjectKey(), "", ExternalKey.INDIVIDUAL_TYPE, "28");
    // assertKeyFields(iF.getPropertyKey(), "", ExternalKey.PROPERTY_TYPE, "51");
    //
    // f = fList.get(1);
    // assertNotNull(f);
    // assertTrue(f instanceof IndividualFact);
    // iF = (IndividualFact) f;
    // assertKeyFields(iF.getObjectKey(), "", ExternalKey.INDIVIDUAL_TYPE, "21");
    // assertKeyFields(iF.getPropertyKey(), "", ExternalKey.PROPERTY_TYPE, "a");
    // }
    //
    // public void testGetIndividualPropertyPreference() {
    // List<IndividualPropertyPreference> iPPList = this.store.getIndividualPropertyPreferences(
    // new ExternalKey("", ExternalKey.STAKEHOLDER_TYPE, "42"), PAGE_SIZE);
    // assertNotNull(iPPList);
    // assertEquals(0, iPPList.size());
    //
    // iPPList = this.store.getIndividualPropertyPreferences(new ExternalKey("",
    // ExternalKey.STAKEHOLDER_TYPE, "41"), PAGE_SIZE);
    // assertNotNull(iPPList);
    // assertEquals(3, iPPList.size());
    // final IndividualPropertyPreference iPP = iPPList.get(0);
    // assertNotNull(iPP);
    // assertKeyFields(iPP.getPrimaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "28");
    // assertKeyFields(iPP.getSecondaryIndividualKey(), "", ExternalKey.INDIVIDUAL_TYPE, "27");
    // assertKeyFields(iPP.getPropertyKey(), "", ExternalKey.PROPERTY_TYPE, "51");
    // }
}