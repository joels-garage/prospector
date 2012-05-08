package com.joelsgarage.logic;

import java.util.List;

import junit.framework.TestCase;

import com.joelsgarage.logic.differential.PreferenceAlternativeRanker;
import com.joelsgarage.model.AnnotatedAlternative;
import com.joelsgarage.model.RankedAlternativeCollection;
import com.joelsgarage.util.FatalException;

public class AlternativeRankerTest extends TestCase {
    private static final int PAGE_SIZE = 1000;

    public void testSimple() throws FatalException {
        final AlternativeStore store = new StaticAlternativeStore();

        final AlternativeRanker ranker = new PreferenceAlternativeRanker(store);

        final List<RankedAlternativeCollection> list = ranker.getRankedList(PAGE_SIZE);

        /*
         * There are two decisions in the store.
         */
        assertEquals(2, list.size());

        /*
         * This decision has 8 alternatives.
         */
        List<AnnotatedAlternative> d0Alternatives = list.get(0).getAlternatives();
        assertNotNull(d0Alternatives);
        assertEquals(8, d0Alternatives.size());

        /*
         * This decision has two alternatives.
         */
        List<AnnotatedAlternative> d1Alternatives = list.get(1).getAlternatives();
        assertNotNull(d1Alternatives);
        assertEquals(2, d1Alternatives.size());

        /*
         * Both sets of alternatives are correctly sorted.
         */
        verifyOrdering(d0Alternatives);
        verifyOrdering(d1Alternatives);
    }

    /** Verify that the list is sorted IN REVERSE ORDER */
    @SuppressWarnings("nls")
    private void verifyOrdering(List<AnnotatedAlternative> list) {
        for (int i = 0; i < list.size() - 1; ++i) {
            AnnotatedAlternative l = list.get(i);
            AnnotatedAlternative h = list.get(i + 1);
            assertNotNull(l);
            assertNotNull(h);
            Double lScore = l.getScore();
            Double hScore = h.getScore();
            assertNotNull(lScore);
            assertNotNull(hScore);
            assertTrue("i: " + String.valueOf(i) + " l: " + lScore.toString() + " h: "
                + hScore.toString(), lScore.compareTo(hScore) >= 0);
        }
    }
}