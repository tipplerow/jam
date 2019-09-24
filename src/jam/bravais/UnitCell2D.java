
package jam.bravais;

import java.util.Collections;
import java.util.List;

import jam.math.DoubleComparator;
import jam.math.Point;
import jam.matrix.JamMatrix;
import jam.vector.VectorView;

/**
 * Provides a base class for two-dimensional unit cells.
 */
public abstract class UnitCell2D extends AbstractUnitCell {
    //
    // Expand both the COLUMN matrix of primitive vectors and its
    // inverse for the most efficient point-index translations...
    //
    private final double p11;
    private final double p12;
    private final double p21;
    private final double p22;

    private final double inv11;
    private final double inv12;
    private final double inv21;
    private final double inv22;

    /**
     * Creates a new two-dimensional unit cell with fixed primitive
     * (basis) vectors.
     *
     * @param basis the primitive (basis) vectors for the unit cell.
     *
     * @throws IllegalArgumentException unless the primitive (basis)
     * vectors are valid.
     */
    protected UnitCell2D(List<VectorView> basis) {
        super(basis);
        validateBasis(basis, 2);

        // The basis vectors form the COLUMNS of the basis matrix...
        this.p11 = basis.get(0).getDouble(0);
        this.p12 = basis.get(1).getDouble(0);
        this.p21 = basis.get(0).getDouble(1);
        this.p22 = basis.get(1).getDouble(1);

        double det = p11 * p22 - p12 * p21;

        if (DoubleComparator.DEFAULT.isZero(det))
            throw new IllegalArgumentException("Degenerate basis vectors.");

        this.inv11 =  p22 / det;
        this.inv12 = -p12 / det;
        this.inv21 = -p21 / det;
        this.inv22 =  p11 / det;

        validatePrimitiveMatrix();
    }

    private void validatePrimitiveMatrix() {
        JamMatrix pmat = JamMatrix.byrow(2, 2, p11, p12, p21, p22);
        JamMatrix pinv = JamMatrix.byrow(2, 2, inv11, inv12, inv21, inv22);

        if (!pmat.inverse().equals(pinv))
            throw new IllegalStateException("Invalid primitive matrix inverse.");
    }

    @Override public int dimensionality() {
        return 2;
    }

    @Override public UnitIndex indexOf(Point point) {
        validateDimensionality(point);

        return UnitIndex.at((int) Math.round(inv11 * point.coord(0) + inv12 * point.coord(1)),
                            (int) Math.round(inv21 * point.coord(0) + inv22 * point.coord(1)));
    }

    @Override public Point pointAt(UnitIndex index) {
        validateDimensionality(index);

        return Point.at(p11 * index.coord(0) + p12 * index.coord(1),
                        p21 * index.coord(0) + p22 * index.coord(1));
    }
}
