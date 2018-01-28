
package jam.math;

/**
 * Distinguishes open and closed ranges.
 */
public enum RangeType {
    /**
     * Both left and right limits are open: {@code (a, b)}.
     */
    OPEN {
        @Override public DoublePredicate lowerPredicate(double lowerBound) {
            return DoublePredicate.GT(lowerBound);
        }

        @Override public DoublePredicate upperPredicate(double upperBound) {
            return DoublePredicate.LT(upperBound);
        }

        @Override public String format(double lowerBound, double upperBound) {
            return String.format("(%f, %f)", lowerBound, upperBound);
        }
    },

    /**
     * Left limit is open, right is closed: {@code (a, b]}.
     */
    LEFT_OPEN {
        @Override public DoublePredicate lowerPredicate(double lowerBound) {
            return DoublePredicate.GT(lowerBound);
        }

        @Override public DoublePredicate upperPredicate(double upperBound) {
            return DoublePredicate.LE(upperBound);
        }

        @Override public String format(double lowerBound, double upperBound) {
            return String.format("(%f, %f]", lowerBound, upperBound);
        }
    },

    /**
     * Left limit is closed, right is open: {@code [a, b)}.
     */
    LEFT_CLOSED {
        @Override public DoublePredicate lowerPredicate(double lowerBound) {
            return DoublePredicate.GE(lowerBound);
        }

        @Override public DoublePredicate upperPredicate(double upperBound) {
            return DoublePredicate.LT(upperBound);
        }

        @Override public String format(double lowerBound, double upperBound) {
            return String.format("[%f, %f)", lowerBound, upperBound);
        }
    },

    /**
     * Both left and right limits are closed: {@code [a, b]}.
     */
    CLOSED {
        @Override public DoublePredicate lowerPredicate(double lowerBound) {
            return DoublePredicate.GE(lowerBound);
        }

        @Override public DoublePredicate upperPredicate(double upperBound) {
            return DoublePredicate.LE(upperBound);
        }

        @Override public String format(double lowerBound, double upperBound) {
            return String.format("[%f, %f]", lowerBound, upperBound);
        }
    };

    /**
     * Returns a predicate to determine whether a real value satisfies
     * the lower bound for a range of this type.
     *
     * @param lowerBound location of the lower (left) boundary.
     *
     * @return a predicate to determine whether a real value satisfies
     * the given lower bound.
     */
    public abstract DoublePredicate lowerPredicate(double lowerBound);

    /**
     * Returns a predicate to determine whether a real value satisfies
     * the upper bound for a range of this type.
     *
     * @param upperBound location of the upper (right) boundary.
     *
     * @return a predicate to determine whether a real value satisfies
     * the given upper bound.
     */
    public abstract DoublePredicate upperPredicate(double upperBound);

    /**
     * Formats boundary limits according to this range type.
     *
     * @param lowerBound location of the lower (left) boundary.
     *
     * @param upperBound location of the upper (right) boundary.
     *
     * @return the bounds formatted appropriately.
     */
    public abstract String format(double lowerBound, double upperBound);
}
