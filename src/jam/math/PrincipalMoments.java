
package jam.math;

/**
 * Represents the principal moments of a gyration tensor.
 */
public final class PrincipalMoments {
    /**
     * The first (smallest) principal moment.
     */
    public final double pmX;

    /**
     * The second (middle) principal moment.
     */
    public final double pmY;

    /**
     * The third (greatest) principal moment.
     */
    public final double pmZ;

    /**
     * Creates a new principal moment.
     *
     * @param pmX the first (smallest) principal moment.
     * @param pmY the second (middle) principal moment.
     * @param pmZ the third (greatest) principal moment.
     *
     * @throws IllegalArgumentException unless the input arguments are
     * valid principal moments: {@code 0.0 <= pmX <= pmY <= pmZ}.
     */
    public PrincipalMoments(double pmX, double pmY, double pmZ) {
        validate(pmX, pmY, pmZ);

        this.pmX = pmX;
        this.pmY = pmY;
        this.pmZ = pmZ;
    }

    /**
     * Validates principal moment components.
     *
     * @param pmX the first (smallest) principal moment.
     * @param pmY the second (middle) principal moment.
     * @param pmZ the third (greatest) principal moment.
     *
     * @throws IllegalArgumentException unless the input arguments are
     * valid principal moments: {@code 0.0 <= pmX <= pmY <= pmZ}.
     */
    public static void validate(double pmX, double pmY, double pmZ) {
        DoubleComparator comparator = DoubleComparator.custom(pmX, pmY, pmZ);

        if (comparator.isNegative(pmX))
            throw new IllegalArgumentException("First principal moment is negative.");

        if (comparator.LT(pmY, pmX))
            throw new IllegalArgumentException("Second principal moment is less than the first.");

        if (comparator.LT(pmZ, pmY))
            throw new IllegalArgumentException("Third principal moment is less than the second.");
    }

    /**
     * Returns the asphericity of these principal moments.
     *
     * @return the asphericity of these principal moments.
     */
    public double asphericity() {
        return pmZ - 0.5 * (pmX + pmY);
    }

    /**
     * Returns the acylindricity of these principal moments.
     *
     * @return the acylindricity of these principal moments.
     */
    public double acylindricity() {
        return pmY - pmX;
    }

    /**
     * Returns the anisotropy of these principal moments.
     *
     * @return the anisotropy of these principal moments.
     */
    public double anisotropy() {
        double x2 = pmX;
        double y2 = pmY;
        double z2 = pmZ;

        double x4 = x2 * x2;
        double y4 = y2 * y2;
        double z4 = z2 * z2;

        return 1.5 * (x4 + y4 + z4) / Math.pow(x2 + y2 + z2, 2) - 0.5;
    }

    /**
     * Returns the scalar radius of gyration for these principal
     * moments.
     *
     * @return the scalar radius of gyration.
     */
    public double RG() {
        return Math.sqrt(pmX + pmY + pmZ);
    }
}
