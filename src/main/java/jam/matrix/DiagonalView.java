
package jam.matrix;

import jam.vector.AbstractVector;

final class DiagonalView extends AbstractVector {
    private final MatrixView matrix;

    DiagonalView(MatrixView matrix) {
        this.matrix = matrix;
    }

    @Override public int length() {
        return matrix.diagonalLength();
    }

    @Override public double getDouble(int index) {
        return matrix.get(index, index);
    }
}
