
package jam.junit;

import jam.math.DoublePredicate;

import org.junit.*;
import static org.junit.Assert.*;

public class DoublePredicateTest {
    private static final double BOUND = 10.0;
    private static final double SMALL = 1.0E-08;
    private static final double TINY  = 1.0E-15;

    @Test public void testLT() {
        DoublePredicate predicate = DoublePredicate.LT(BOUND);

	assertTrue(predicate.test(BOUND - SMALL));
	assertFalse(predicate.test(BOUND - TINY));
	assertFalse(predicate.test(BOUND));
	assertFalse(predicate.test(BOUND + TINY));
	assertFalse(predicate.test(BOUND + SMALL));
    }

    @Test public void testLE() {
        DoublePredicate predicate = DoublePredicate.LE(BOUND);

	assertTrue(predicate.test(BOUND - SMALL));
	assertTrue(predicate.test(BOUND - TINY));
	assertTrue(predicate.test(BOUND));
	assertTrue(predicate.test(BOUND + TINY));
	assertFalse(predicate.test(BOUND + SMALL));
    }

    @Test public void testGE() {
        DoublePredicate predicate = DoublePredicate.GE(BOUND);

	assertFalse(predicate.test(BOUND - SMALL));
	assertTrue(predicate.test(BOUND - TINY));
	assertTrue(predicate.test(BOUND));
	assertTrue(predicate.test(BOUND + TINY));
	assertTrue(predicate.test(BOUND + SMALL));
    }

    @Test public void testGT() {
        DoublePredicate predicate = DoublePredicate.GT(BOUND);

	assertFalse(predicate.test(BOUND - SMALL));
	assertFalse(predicate.test(BOUND - TINY));
	assertFalse(predicate.test(BOUND));
	assertFalse(predicate.test(BOUND + TINY));
	assertTrue(predicate.test(BOUND + SMALL));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DoublePredicateTest");
    }
}
