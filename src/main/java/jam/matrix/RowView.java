
package jam.matrix;

import jam.vector.AbstractVector;

final class RowView extends AbstractVector {
    private final MatrixView matrix;
    private final int row;

    RowView(MatrixView matrix, int row) {
        matrix.validateRow(row);

        this.matrix = matrix;
        this.row = row;
    }

    @Override public int length() {
        return matrix.ncol();
    }

    @Override public double getDouble(int index) {
        return matrix.get(row, index);
    }
}
