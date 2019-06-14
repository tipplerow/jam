
package jam.junit;

import jam.math.DoubleUtil;
import jam.math.JamRandom;
import jam.math.Probability;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

public class ProbabilityTest extends NumericTestBase {
    private void assertProbability(double expected, Probability actual) {
        assertDouble(expected, actual.doubleValue());
    }

    private void assertProbability(double expected, Probability actual, double tolerance) {
        assertEquals(expected, actual.doubleValue(), tolerance);
    }

    private void assertProbability(Probability expected, Probability actual, double tolerance) {
        assertEquals(expected.doubleValue(), actual.doubleValue(), tolerance);
    }

    @Test public void testAccept() {
        int accepted = 0;
        int numTrial = 100000;
        Probability prob = Probability.parse("0.3");

        for (int index = 0; index < numTrial; index++)
            if (prob.accept())
                ++accepted;

        assertEquals(prob.doubleValue(), DoubleUtil.ratio(accepted, numTrial), 0.0002);
    }

    @Test public void testAllOccur() {
        Probability p1 = Probability.valueOf(0.2);

        assertProbability(0.2,    p1.allOccur(1));
        assertProbability(0.04,   p1.allOccur(2));
        assertProbability(0.008,  p1.allOccur(3));
        assertProbability(0.0016, p1.allOccur(4));
    }

    @Test public void testAllOccurStatic() {
        Probability p1 = Probability.valueOf(0.8);
        Probability p2 = Probability.valueOf(0.6);
        Probability p3 = Probability.valueOf(0.5);
        Probability p4 = Probability.valueOf(0.8 * 0.6 * 0.5);

        assertEquals(p4, Probability.allOccur(p1, p2, p3));
        assertEquals(p4, Probability.allOccur(p3, p2, p1));
    }

    @Test public void testAndInstance() {
        Probability p1 = Probability.valueOf(0.8);
        Probability p2 = Probability.valueOf(0.6);
        Probability p3 = Probability.valueOf(0.8 * 0.6);

        assertEquals(p3, p1.and(p2));
        assertEquals(p3, p2.and(p1));
    }

    @Test public void testAnyOccurStatic() {
        Probability p1 = Probability.valueOf(0.1);
        Probability p2 = Probability.valueOf(0.2);
        Probability p3 = Probability.valueOf(0.4);
        Probability p4 = Probability.valueOf(1.0 - 0.9 * 0.8 * 0.6);

        assertEquals(p4, Probability.anyOccur(p1, p2, p3));
        assertEquals(p4, Probability.anyOccur(p3, p2, p1));
    }

    @Test public void testDivide() {
        assertEquals(Probability.valueOf(0.125), Probability.valueOf(0.5).divide(4.0));
    }

    @Test public void testIfAllOccured() {
        Probability pN = Probability.valueOf(0.0001);

        assertProbability(0.0001, pN.ifAllOccurred(1));
        assertProbability(0.01,   pN.ifAllOccurred(2));
        assertProbability(0.1,    pN.ifAllOccurred(4));

        for (int index = 0; index < 100; index++) {
            Probability single = Probability.valueOf(JamRandom.global().nextDouble());

            for (int trialCount = 1; trialCount <= 6; trialCount++) {
		Probability joint = single.allOccur(trialCount);
		assertEquals(single, joint.ifAllOccurred(trialCount));
	    }
        }
    }

    @Test public void testIfNoneOccured() {
        Probability pN = Probability.valueOf(0.25);
	
        assertProbability(0.75, pN.ifNoneOccurred(1));
        assertProbability(0.50, pN.ifNoneOccurred(2));
        assertProbability(0.3700395, pN.ifNoneOccurred(3), 0.0000001);
        assertProbability(0.2928932, pN.ifNoneOccurred(4), 0.0000001);

        for (int index = 0; index < 100; index++) {
            Probability single = Probability.valueOf(JamRandom.global().nextDouble());

            for (int trialCount = 1; trialCount <= 6; trialCount++) {
		Probability joint = single.noneOccur(trialCount);
                assertEquals(single, joint.ifNoneOccurred(trialCount));
            }
        }
    }

    @Test public void testIfOneOrMoreOccured() {
        Probability pN = Probability.valueOf(0.25);
	
        assertProbability(0.25, pN.ifOneOrMoreOccurred(1));
        assertProbability(0.1339746, pN.ifOneOrMoreOccurred(2), 0.0000001);
        assertProbability(0.0914397, pN.ifOneOrMoreOccurred(3), 0.0000001);
        assertProbability(0.0693951, pN.ifOneOrMoreOccurred(4), 0.0000001);

        for (int index = 0; index < 100; index++) {
            Probability single = Probability.valueOf(JamRandom.global().nextDouble());

            for (int trialCount = 1; trialCount <= 4; trialCount++) {
		Probability joint = single.oneOrMoreOccur(trialCount);
                assertProbability(single, joint.ifOneOrMoreOccurred(trialCount), 1.0e-08);
            }
        }
    }

    @Test public void testIsNormalized() {
        Probability p1 = Probability.valueOf(0.2);
        Probability p2 = Probability.valueOf(0.3);
        Probability p3 = Probability.valueOf(0.5);

        assertFalse(Probability.isNormalized(p1, p2));
        assertFalse(Probability.isNormalized(p2, p3));

        assertTrue(Probability.isNormalized(p1, p2, p3));
        assertTrue(Probability.isNormalized(p3, p2, p1));
    }

    @Test public void testMax() {
        Probability p1 = Probability.valueOf(0.2);
        Probability p2 = Probability.valueOf(0.3);
        Probability p3 = Probability.valueOf(0.5);

        // Want to make sure that the OBJECT is returned as the maximum...
        assertTrue(p1 == Probability.max(p1));
        assertTrue(p2 == Probability.max(p1, p2));
        assertTrue(p3 == Probability.max(p1, p2, p3));
    }

    @Test public void testMin() {
        Probability p1 = Probability.valueOf(0.2);
        Probability p2 = Probability.valueOf(0.3);
        Probability p3 = Probability.valueOf(0.5);

        // Want to make sure that the OBJECT is returned as the minimum...
        assertTrue(p3 == Probability.min(p3));
        assertTrue(p2 == Probability.min(p2, p3));
        assertTrue(p1 == Probability.min(p1, p2, p3));
    }

    @Test public void testNoneOccur() {
        Probability p1 = Probability.valueOf(0.2);

        assertProbability(0.8,    p1.noneOccur(1));
        assertProbability(0.64,   p1.noneOccur(2));
        assertProbability(0.512,  p1.noneOccur(3));
        assertProbability(0.4096, p1.noneOccur(4));
    }

    @Test public void testNoneOccurStatic() {
        Probability p1 = Probability.valueOf(0.1);
        Probability p2 = Probability.valueOf(0.2);
        Probability p3 = Probability.valueOf(0.4);
        Probability p4 = Probability.valueOf(0.9 * 0.8 * 0.6);

        assertEquals(p4, Probability.noneOccur(p1, p2, p3));
        assertEquals(p4, Probability.noneOccur(p3, p2, p1));
    }

    @Test public void testNot() {
        Probability p1 = Probability.valueOf(0.22);
        Probability p2 = Probability.valueOf(0.78);

        assertEquals(p2, Probability.not(p1));
        assertEquals(p1, Probability.not(p2));

        assertEquals(p2, p1.not());
        assertEquals(p1, p2.not());

        for (int index = 0; index < 100; index++) {
            Probability prob = Probability.valueOf(JamRandom.global().nextDouble());

            assertEquals(prob, prob.not().not());
            assertEquals(prob, Probability.other(prob.not()));
        }
    }

    @Test public void testOneOrMoreOccur() {
        Probability p1 = Probability.valueOf(0.2);

        assertProbability(0.2,    p1.oneOrMoreOccur(1));
        assertProbability(0.36,   p1.oneOrMoreOccur(2));
        assertProbability(0.488,  p1.oneOrMoreOccur(3));
        assertProbability(0.5904, p1.oneOrMoreOccur(4));
    }

    @Test public void testOrInstance() {
        Probability p1 = Probability.valueOf(0.1);
        Probability p2 = Probability.valueOf(0.2);
        Probability p3 = Probability.valueOf(0.3);

        assertEquals(p3, p1.or(p2));
        assertEquals(p3, p2.or(p1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrInstanceInvalid() {
        Probability p1 = Probability.valueOf(0.6);
        Probability p2 = Probability.valueOf(0.7);

        p1.or(p2);
    }

    @Test public void testOrStatic() {
        Probability p1 = Probability.valueOf(0.1);
        Probability p2 = Probability.valueOf(0.2);
        Probability p3 = Probability.valueOf(0.3);
        Probability p4 = Probability.valueOf(0.6);

        assertEquals(p4, Probability.or(p1, p2, p3));
        assertEquals(p4, Probability.or(p3, p2, p1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOrStaticInvalid() {
        Probability p1 = Probability.valueOf(0.3);
        Probability p2 = Probability.valueOf(0.4);
        Probability p3 = Probability.valueOf(0.5);

        Probability.or(p1, p2, p3);
    }

    @Test public void testOther() {
        Probability p1 = Probability.valueOf(0.1);
        Probability p2 = Probability.valueOf(0.2);
        Probability p3 = Probability.valueOf(0.3);
        Probability p4 = Probability.valueOf(0.4);

        assertEquals(p4, Probability.other(p1, p2, p3));
        assertEquals(p4, Probability.other(p3, p2, p1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOtherInvalid() {
        Probability p1 = Probability.valueOf(0.3);
        Probability p2 = Probability.valueOf(0.4);
        Probability p3 = Probability.valueOf(0.5);

        Probability.other(p1, p2, p3);
    }

    @Test public void testTimes() {
        assertEquals(Probability.valueOf(0.125), Probability.valueOf(0.5).times(0.25));
    }

    @Test(expected = RuntimeException.class)
    public void testTimesInvalid() {
        Probability.valueOf(0.5).times(3.0);
    }

    @Test public void testValidate1() {
        Probability.validate(0.0);
        Probability.validate(0.5);
        Probability.validate(1.0);

        Probability.valueOf(0.0);
        Probability.valueOf(0.5);
        Probability.valueOf(1.0);
    }

    @Test(expected = RuntimeException.class)
    public void testValidate2() {
        Probability.validate(-0.0000001);
    }

    @Test(expected = RuntimeException.class)
    public void testValidate3() {
        Probability.validate(1.0000001);
    }

    @Test public void testValidateSeries1() {
        Probability.validate(VectorView.wrap(0.1, 0.2, 0.3, 0.4));
    }

    @Test(expected = RuntimeException.class)
    public void testValidateSeries2() {
        Probability.validate(VectorView.wrap(0.1, 0.2, 0.3));
    }

    @Test(expected = RuntimeException.class)
    public void testValidateSeries3() {
        Probability.validate(VectorView.wrap(2.0, -1.0));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ProbabilityTest");
    }
}
