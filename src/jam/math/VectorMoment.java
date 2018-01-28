
package jam.math;

import java.util.Collection;

import com.google.common.collect.Multiset;

import jam.lattice.Coord;
import jam.matrix.JamEigen;
import jam.matrix.JamMatrix;
import jam.matrix.MatrixView;
import jam.matrix.MatrixUtil;
import jam.vector.JamVector;
import jam.vector.VectorAggregator;
import jam.vector.VectorView;

/**
 * Computes the generalized center of mass and gyration tensor for
 * a collection of vector coordinates.
 */
public final class VectorMoment {
    private final JamVector CM;
    private final JamMatrix RG;

    // Eigenvalue decomposition of "RG", created on demand...
    private JamEigen eigen = null;

    // Principal moments of "RG", created on demand...
    private PrincipalMoments principal = null;

    private VectorMoment(JamVector CM, JamMatrix RG) {
        this.CM = CM;
        this.RG = RG;
    }
    
    /**
     * Computes the generalized center of mass and gyration tensor for
     * a collection of vector coordinates.
     *
     * @param vectors the vectors on which to operate.
     *
     * @return a {@code VectorMoment} instance containing the computed
     * center of mass and gyration tensor.
     *
     * @throws IllegalArgumentException unless there is at least one
     * vector and all vectors have the same dimensionality.
     */
    public static VectorMoment compute(Collection<? extends VectorView> vectors) {
        return compute(vectors.toArray(new VectorView[vectors.size()]));
    }
    
    /**
     * Computes the generalized center of mass and gyration tensor for
     * a collection of vector coordinates.
     *
     * @param vectors the vectors on which to operate.
     *
     * @return a {@code VectorMoment} instance containing the computed
     * center of mass and gyration tensor.
     *
     * @throws IllegalArgumentException unless there is at least one
     * vector and all vectors have the same dimensionality.
     */
    public static VectorMoment compute(VectorView... vectors) {
        if (vectors.length < 1)
            throw new IllegalArgumentException("At least one vector is required.");

        JamVector CM = computeCM(vectors);
        JamMatrix RG = computeRG(vectors, CM);

        return new VectorMoment(CM, RG);
    }

    private static JamVector computeCM(VectorView[] vectors) {
        int D = vectors[0].length();
        JamVector CM = JamVector.zeros(D);

        for (VectorView vector : vectors)
            CM.add(vector);

        CM.divide(vectors.length);
        return CM;
    }

    private static JamMatrix computeRG(VectorView[] vectors, VectorView CM) {
        int D = CM.length();
        JamMatrix RG = JamMatrix.zeros(D, D);

        for (VectorView vector : vectors)
            RG.add(JamMatrix.dyad(centered(vector, CM)));

        RG.divide(vectors.length);
        return RG;
    }

    private static JamVector centered(VectorView vector, VectorView CM) {
        JamVector result = new JamVector(vector);
        result.subtract(CM);
        return result;
    }

    /**
     * Computes the generalized center of mass and gyration tensor for
     * a collection of lattice coordinates.
     *
     * @param coords the coordinates on which to operate, specified as
     * a multiset counting the number of times each coordinate appears.
     *
     * @return a {@code VectorMoment} instance containing the computed
     * center of mass and gyration tensor.
     *
     * @throws IllegalArgumentException unless there is at least one
     * coordinate.
     */
    public static VectorMoment compute(Multiset<Coord> coords) {
        if (coords.isEmpty())
            throw new IllegalArgumentException("At least one coordinate is required.");

        JamVector CM = computeCM(coords);
        JamMatrix RG = computeRG(coords, CM);

        return new VectorMoment(CM, RG);
    }

    private static JamVector computeCM(Multiset<Coord> coords) {
        int      Nc = coords.size();
        double[] CM = new double[3];

        for (Multiset.Entry<Coord> entry : coords.entrySet()) {
            double weight = DoubleUtil.ratio(entry.getCount(), Nc);

            CM[0] += weight * entry.getElement().x;
            CM[1] += weight * entry.getElement().y;
            CM[2] += weight * entry.getElement().z;
        }

        return new JamVector(CM);
    }

    private static JamMatrix computeRG(Multiset<Coord> coords, JamVector CM) {
        int        Nc = coords.size();
        double[][] RG = MatrixUtil.create(3, 3, 0.0);

        for (Multiset.Entry<Coord> entry : coords.entrySet()) {
            double dx = entry.getElement().x - CM.get(0);
            double dy = entry.getElement().y - CM.get(1);
            double dz = entry.getElement().z - CM.get(2);

            double weight = DoubleUtil.ratio(entry.getCount(), Nc);

            // Just accumulate the upper right triangle...
            RG[0][0] += weight * dx * dx;
            RG[0][1] += weight * dx * dy;
            RG[0][2] += weight * dx * dz;
            RG[1][1] += weight * dy * dy;
            RG[1][2] += weight * dy * dz;
            RG[2][2] += weight * dz * dz;
        }

        // Fill in the lower left...
        RG[1][0] = RG[0][1];
        RG[2][0] = RG[0][2];
        RG[2][1] = RG[1][2];

        return new JamMatrix(RG);
    }

    /**
     * Returns the center of mass vector.
     *
     * @return the center of mass vector.
     */
    public VectorView getCM() {
        return CM;
    }

    /**
     * Returns the gyration tensor.
     *
     * @return the gyration tensor.
     */
    public MatrixView getRG() {
        return RG;
    }

    /**
     * Returns the eigenvalue decomposition of the gyration tensor.
     *
     * @return the eigenvalue decomposition of the gyration tensor.
     */
    public JamEigen getEigen() {
        if (eigen == null)
            eigen = new JamEigen(RG);

        return eigen;
    }

    /**
     * Returns the principal moments of the (3D) gyration tensor.
     *
     * @return the principal moments of the (3D) gyration tensor.
     *
     * @throws IllegalStateException unless the gyration tensor has
     * dimensions of {@code 3 x 3}.
     */
    public PrincipalMoments getPrincipalMoments() {
        if (principal == null) {
            //
            // Eigenvalues are sorted in descending order (from
            // greatest to least)...
            //
            VectorView eigval = getEigen().viewValues();

            if (eigval.length() != 3)
                throw new IllegalStateException("Principal moments are defined only for 3 x 3 matrices.");

            principal = 
                new PrincipalMoments(eigval.getDouble(2),
                                     eigval.getDouble(1),
                                     eigval.getDouble(0));
        }

        return principal;
    }

    /**
     * Returns the asphericity of the (3D) gyration tensor.
     *
     * @return the asphericity of the (3D) gyration tensor.
     *
     * @throws IllegalStateException unless the gyration tensor has
     * dimensions of {@code 3 x 3}.
     */
    public double asphericity() {
        return getPrincipalMoments().asphericity();
    }

    /**
     * Returns the acylindricity of the (3D) gyration tensor.
     *
     * @return the acylindricity of the (3D) gyration tensor.
     *
     * @throws IllegalStateException unless the gyration tensor has
     * dimensions of {@code 3 x 3}.
     */
    public double acylindricity() {
        return getPrincipalMoments().acylindricity();
    }

    /**
     * Returns the anisotropy of the (3D) gyration tensor.
     *
     * @return the anisotropy of the (3D) gyration tensor.
     *
     * @throws IllegalStateException unless the gyration tensor has
     * dimensions of {@code 3 x 3}.
     */
    public double anisotropy() {
        return getPrincipalMoments().anisotropy();
    }

    /**
     * Returns the scalar radius of gyration: the square root of the
     * sum of squares of the diagonal values.
     *
     * @return the scalar radius of gyration.
     */
    public double scalar() {
        return Math.sqrt(VectorAggregator.norm1(RG.getDiagonal()));
    }
}
