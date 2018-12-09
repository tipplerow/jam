
package jam.math;

import java.util.function.Predicate;

/**
 * Represents an interval on the real number line with some finite
 * tolerance for imprecision in floating-point comparisons.
 */
public interface DoublePredicate extends Predicate<Double> {
    /**
     * Evaluates the predicate for a specific double value.
     *
     * @param value a value to examine.
     *
     * @return the logical value at the specified value.
     */
    public abstract boolean test(double value);

    /**
     * Evaluates the predicate for a specific double value.
     *
     * @param value a value to examine.
     *
     * @return the logical value at the specified value.
     */
     @Override public boolean test(Double value) {
	 return test(value.doubleValue());
     }

    /**
     * Returns a "less than" predicate representing a strict upper
     * bound (with comparisons conducted by the default comparator).
     *
     * @param bound the upper bound.
     *
     * @return a predicate for which {@code test(value) == true}
     * iff {@code value &lt; bound} (subject to the default floating
     * point tolerance).
     */
    public static DoublePredicate LT(double bound) {
	return new PredicateLT(bound);
    }

    /**
     * Returns a "less than or equal to" predicate representing a
     * strict upper bound (with comparisons conducted by the default
     * comparator).
     *
     * @param bound the upper bound.
     *
     * @return a predicate for which {@code test(value) == true}
     * iff {@code value &le; bound} (subject to the default floating
     * point tolerance).
     */
    public static DoublePredicate LE(double bound) {
	return new PredicateLE(bound);
    }

    /**
     * Returns a "greater than or equal to" predicate representing a
     * strict upper bound (with comparisons conducted by the default
     * comparator).
     *
     * @param bound the upper bound.
     *
     * @return a predicate for which {@code test(value) == true}
     * iff {@code value &ge; bound} (subject to the default floating
     * point tolerance).
     */
    public static DoublePredicate GE(double bound) {
	return new PredicateGE(bound);
    }

    /**
     * Returns a "greater than" predicate representing a strict upper
     * bound (with comparisons conducted by the default comparator).
     *
     * @param bound the upper bound.
     *
     * @return a predicate for which {@code test(value) == true}
     * iff {@code value &gt; bound} (subject to the default floating
     * point tolerance).
     */
    public static DoublePredicate GT(double bound) {
	return new PredicateGT(bound);
    }
}

abstract class ComparisonPredicate implements DoublePredicate {
    protected final double bound;
    protected final DoubleComparator comparator;

    protected ComparisonPredicate(double bound) {
	this(bound, DoubleComparator.DEFAULT);
    }

    protected ComparisonPredicate(double bound, double tolerance) {
	this(bound, new DoubleComparator(tolerance));
    }

    protected ComparisonPredicate(double bound, DoubleComparator comparator) {
	this.bound = bound;
	this.comparator = comparator;
    }
}

final class PredicateLT extends ComparisonPredicate {
    PredicateLT(double bound) {
	super(bound);
    }

    PredicateLT(double bound, double tolerance) {
	super(bound, tolerance);
    }

    PredicateLT(double bound, DoubleComparator comparator) {
	super(bound, comparator);
    }

    @Override public boolean test(double value) {
	return comparator.LT(value, bound);
    }
}

final class PredicateLE extends ComparisonPredicate {
    PredicateLE(double bound) {
	super(bound);
    }

    PredicateLE(double bound, double tolerance) {
	super(bound, tolerance);
    }

    PredicateLE(double bound, DoubleComparator comparator) {
	super(bound, comparator);
    }

    @Override public boolean test(double value) {
	return comparator.LE(value, bound);
    }
}

final class PredicateGE extends ComparisonPredicate {
    PredicateGE(double bound) {
	super(bound);
    }

    PredicateGE(double bound, double tolerance) {
	super(bound, tolerance);
    }

    PredicateGE(double bound, DoubleComparator comparator) {
	super(bound, comparator);
    }

    @Override public boolean test(double value) {
	return comparator.GE(value, bound);
    }
}

final class PredicateGT extends ComparisonPredicate {
    PredicateGT(double bound) {
	super(bound);
    }

    PredicateGT(double bound, double tolerance) {
	super(bound, tolerance);
    }

    PredicateGT(double bound, DoubleComparator comparator) {
	super(bound, comparator);
    }

    @Override public boolean test(double value) {
	return comparator.GT(value, bound);
    }
}
