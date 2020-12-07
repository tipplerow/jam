
package jam.collect;

import jam.lang.JamException;

/**
 * Provides a matrix of objects with fixed row and column dimensions.
 */
public final class ObjectMatrix<E> {
    private final E[][] elements;

    @SuppressWarnings("unchecked")
    private ObjectMatrix(int nrow, int ncol) {
        validateDim(nrow, ncol);

        this.elements = (E[][]) new Object[nrow][];

        for (int i = 0; i < nrow; ++i)
            this.elements[i] = (E[]) new Object[ncol];
    }

    private static void validateDim(int nrow, int ncol) {
        if (nrow < 1)
            throw JamException.runtime("Number of rows must be positive.");

        if (ncol < 1)
            throw JamException.runtime("Number of columns must be positive.");
    }

    /**
     * Creates a new matrix with fixed row and column dimensions.
     *
     * @param nrow the number of rows in the matrix.
     *
     * @param ncol the number of columns in the matrix.
     *
     * @return a new matrix with the specified dimensions (and all
     * elements set to {@code null}).
     */
    public static <E> ObjectMatrix<E> create(int nrow, int ncol) {
        return new ObjectMatrix<E>(nrow, ncol);
    }

    /**
     * Retrieves an element from this matrix.
     *
     * @param row the zero-offset row index of the element.
     *
     * @param col the zero-offset column index of the element.
     *
     * @return the element at the specified location.
     *
     * @throws IndexOutOfBoundsException unless the row and column
     * indexes are valid.
     */
    public E get(int row, int col) {
        return elements[row][col];
    }

    /**
     * Returns the number of rows in this matrix.
     *
     * @return the number of rows in this matrix.
     */
    public int nrow() {
        return elements.length;
    }

    /**
     * Returns the number of columns in this matrix.
     *
     * @return the number of columns in this matrix.
     */
    public int ncol() {
        return elements[0].length;
    }

    /**
     * Assigns an element to this matrix.
     *
     * @param row the zero-offset row index of the element.
     *
     * @param col the zero-offset column index of the element.
     *
     * @param element the element to assign at the specified location.
     *
     * @throws IndexOutOfBoundsException unless the row and column
     * indexes are valid.
     */
    public void set(int row, int col, E element) {
        elements[row][col] = element;
    }
}
