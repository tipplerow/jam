
package jam.bravais;

/**
 * Provides are partial implementation of the {@code Period}
 * interface.
 */
public abstract class AbstractPeriod implements Period {
    /**
     * Ensures that a period length is valid.
     *
     * @param period the period length to validate.
     *
     * @throws IllegalArgumentException unless the period length is
     * positive.
     */
    public static void validateDimension(int period) {
	if (period < 1)
	    throw new IllegalArgumentException("Non-positive periodic dimension.");
    }

    /**
     * Ensures that the dimensionality of a discrete unit index
     * matches the dimensionality of this lattice period.
     *
     * @param index the index to validate.
     *
     * @throws IllegalArgumentException unless the dimensionality of
     * the input index matches the dimensionality of this period.
     */
    public void validateDimensionality(UnitIndex index) {
        if (index.dimensionality() != this.dimensionality())
            throw new IllegalArgumentException("Inconsistent index dimensionality.");
    }

    @Override public boolean contains(UnitIndex index) {
        validateDimensionality(index);

        for (int dim = 0; dim < dimensionality(); ++dim)
            if (!Period.contains(index.coord(dim), period(dim)))
                return false;

        return true;
    }

    @Override public long countSites() {
        long count = 1;

        for (int dim = 0; dim < dimensionality(); ++dim)
            count *= period(dim);

        return count;
    }
}
