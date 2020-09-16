
package jam.matrix;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Allows iteration over matrix elements in row-major order.
//
final class MatrixCursorIterator implements Iterator<MatrixCursor> {
    private final MatrixView matrix;
    private int row;
    private int col;

    MatrixCursorIterator(MatrixView matrix) {
        this.matrix = matrix;

        // The "hasNext" and "next" implementations are simplest when
        // we initialize the row and column indexes to point to the
        // end of the hypothetical row immediately before the first
        // row...
        this.row = -1;
        this.col = matrix.ncol() - 1;
    }

    @Override public boolean hasNext() {
        return hasNextRow() || hasNextCol();
    }

    private boolean hasNextCol() {
        //
        // Prior to incrementing the column index...
        //
        return (col + 1) < matrix.ncol();
    }

    private boolean hasNextRow() {
        //
        // Prior to incrementing the row index...
        //
        return (row + 1) < matrix.nrow();
    }

    @Override public MatrixCursor next() {
        if (hasNextCol()) {
            //
            // Move to the next column in the same row...
            //
            col++;
        }
        else if (hasNextRow()) {
            //
            // Move to the first column in the next row...
            //
            col = 0;
            row++;
        }
        else {
            //
            // Gone past the last column in the last row...
            //
            throw new NoSuchElementException();
        }

        return new MatrixCursor(row, col);
    }
}
