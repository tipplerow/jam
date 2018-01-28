
package jam.matrix;

/**
 * Identifies a unique element within a matrix during iteration over
 * all elements.
 */
public final class MatrixCursor {
    private final int row;
    private final int col;

    /**
     * Creates a new cursor pointing to a current element in an
     * iteration.
     *
     * @param row the current row index.
     *
     * @param col the current column index.
     */
    public MatrixCursor(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the current row index.
     *
     * @return the current row index.
     */
    public int row() {
        return row;
    }

    /**
     * Returns the current column index.
     *
     * @return the current column index.
     */
    public int col() {
        return col;
    }

    /**
     * Returns the cursor pointing to the same element in the
     * transpose of the parent matrix.
     *
     * @return the cursor pointing to the same element in the
     * transpose of the parent matrix.
     */
    public MatrixCursor transpose() {
        return new MatrixCursor(col, row);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof MatrixCursor) && equalsCursor((MatrixCursor) that);
    }

    private boolean equalsCursor(MatrixCursor that) {
        return this.row == that.row && this.col == that.col;
    }

    @Override public String toString() {
        return String.format("MatrixCursor(%d, %d)", row, col);
    }
}
