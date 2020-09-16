
package jam.matrix;

final class MatrixWrapper extends MatrixView {
    private double[][] matrix;

    MatrixWrapper(double[][] matrix) {
        MatrixUtil.validate(matrix);
        this.matrix = matrix;
    }

    @Override public int nrow() {
        return MatrixUtil.nrow(matrix);
    }

    @Override public int ncol() {
        return MatrixUtil.ncol(matrix);
    }

    @Override public double get(int row, int col) {
        return matrix[row][col];
    }
}
