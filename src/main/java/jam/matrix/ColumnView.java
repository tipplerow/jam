
package jam.matrix;

import jam.vector.AbstractVector;

final class ColumnView extends AbstractVector {
    private final MatrixView matrix;
    private final int column;

    ColumnView(MatrixView matrix, int column) {
        matrix.validateColumn(column);

        this.matrix = matrix;
        this.column = column;
    }

    @Override public int length() {
        return matrix.nrow();
    }

    @Override public double getDouble(int index) {
        return matrix.get(index, column);
    }
}
