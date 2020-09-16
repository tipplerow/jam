
package jam.bravais;

import java.util.Collections;
import java.util.List;

import jam.math.DoubleComparator;
import jam.math.Point;
import jam.matrix.JamMatrix;
import jam.vector.VectorView;

/**
 * Provides a base class for three-dimensional unit cells.
 */
public abstract class UnitCell3D extends AbstractUnitCell {
    //
    // Expand both the COLUMN matrix of primitive vectors and its
    // inverse for the most efficient point-index translations...
    //
    private final double p11;
    private final double p12;
    private final double p13;
    private final double p21;
    private final double p22;
    private final double p23;
    private final double p31;
    private final double p32;
    private final double p33;

    private final double inv11;
    private final double inv12;
    private final double inv13;
    private final double inv21;
    private final double inv22;
    private final double inv23;
    private final double inv31;
    private final double inv32;
    private final double inv33;

    /**
     * Creates a new three-dimensional unit cell with fixed primitive
     * (basis) vectors.
     *
     * @param basis the primitive (basis) vectors for the unit cell.
     *
     * @throws IllegalArgumentException unless the primitive (basis)
     * vectors are valid.
     */
    protected UnitCell3D(List<VectorView> basis) {
        super(basis);
        validateBasis(basis, 3);

        // The basis vectors form the COLUMNS of the basis matrix...
        JamMatrix basisMat = JamMatrix.cbind(basis);
        JamMatrix basisInv = basisMat.inverse();

        this.p11 = basisMat.get(0, 0);
        this.p12 = basisMat.get(0, 1);
        this.p13 = basisMat.get(0, 2);
        this.p21 = basisMat.get(1, 0);
        this.p22 = basisMat.get(1, 1);
        this.p23 = basisMat.get(1, 2);
        this.p31 = basisMat.get(2, 0);
        this.p32 = basisMat.get(2, 1);
        this.p33 = basisMat.get(2, 2);

        this.inv11 = basisInv.get(0, 0);
        this.inv12 = basisInv.get(0, 1);
        this.inv13 = basisInv.get(0, 2);
        this.inv21 = basisInv.get(1, 0);
        this.inv22 = basisInv.get(1, 1);
        this.inv23 = basisInv.get(1, 2);
        this.inv31 = basisInv.get(2, 0);
        this.inv32 = basisInv.get(2, 1);
        this.inv33 = basisInv.get(2, 2);
    }

    @Override public int dimensionality() {
        return 3;
    }

    @Override public UnitIndex indexOf(Point point) {
        validateDimensionality(point);

        return UnitIndex.at((int) Math.round(inv11 * point.coord(0) + inv12 * point.coord(1) + inv13 * point.coord(2)),
                            (int) Math.round(inv21 * point.coord(0) + inv22 * point.coord(1) + inv23 * point.coord(2)),
                            (int) Math.round(inv31 * point.coord(0) + inv32 * point.coord(1) + inv33 * point.coord(2)));
    }

    @Override public Point pointAt(UnitIndex index) {
        validateDimensionality(index);

        return Point.at(p11 * index.coord(0) + p12 * index.coord(1) + p13 * index.coord(2),
                        p21 * index.coord(0) + p22 * index.coord(1) + p23 * index.coord(2),
                        p31 * index.coord(0) + p32 * index.coord(1) + p33 * index.coord(2));
    }
}
