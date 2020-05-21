
package jam.time;

import java.time.LocalDate;
import java.util.function.Predicate;

/**
 * Defines an interface for date-range comparisons and provides
 * greater and less than.
 */
public abstract class DateRangePredicate implements Predicate<LocalDate> {
    /**
     * The bounding date.
     */
    protected final LocalDate bound;

    /**
     * Creates a new predicate with a fixed bounding date.
     *
     * @param bound the bounding date.
     */
    protected DateRangePredicate(LocalDate bound) {
        this.bound = bound;
    }

    /**
     * Returns a "less than" predicate for the upper bound of an open
     * range.
     *
     * @param bound the upper bound.
     *
     * @return a predicate for which {@code test(date) == true}
     * iff {@code date.isBefore(bound)}.
     */
    public static DateRangePredicate LT(LocalDate bound) {
	return new PredicateLT(bound);
    }

    /**
     * Returns a "less than or equal to" predicate for the upper bound
     * of a closed range.
     *
     * @param bound the upper bound.
     *
     * @return a predicate for which {@code test(date) == true}
     * iff {@code date.equals(bound) || date.isBefore(bound)}.
     */
    public static DateRangePredicate LE(LocalDate bound) {
	return new PredicateLE(bound);
    }

    /**
     * Returns a "greater than or equal to" predicate for the lower
     * bound of a closed range.
     *
     * @param bound the upper bound.
     *
     * @return a predicate for which {@code test(date) == true}
     * iff {@code date.equals(bound) || date.isAfter(bound)}.
     */
    public static DateRangePredicate GE(LocalDate bound) {
	return new PredicateGE(bound);
    }

    /**
     * Returns a "greater than" predicate for the lower bound of an
     * open range.
     *
     * @param bound the upper bound.
     *
     * @return a predicate for which {@code test(date) == true}
     * iff {@code date.isAfter(bound)}.
     */
    public static DateRangePredicate GT(LocalDate bound) {
	return new PredicateGT(bound);
    }
}

final class PredicateLT extends DateRangePredicate {
    PredicateLT(LocalDate bound) {
	super(bound);
    }

    @Override public boolean test(LocalDate date) {
	return date.isBefore(bound);
    }
}

final class PredicateLE extends DateRangePredicate {
    PredicateLE(LocalDate bound) {
	super(bound);
    }

    @Override public boolean test(LocalDate date) {
	return date.equals(bound) || date.isBefore(bound);
    }
}

final class PredicateGE extends DateRangePredicate {
    PredicateGE(LocalDate bound) {
	super(bound);
    }

    @Override public boolean test(LocalDate date) {
	return date.equals(bound) || date.isAfter(bound);
    }
}

final class PredicateGT extends DateRangePredicate {
    PredicateGT(LocalDate bound) {
	super(bound);
    }

    @Override public boolean test(LocalDate date) {
	return date.isAfter(bound);
    }
}
