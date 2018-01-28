
package jam.matrix;

/**
 * Identifies data structures that have a numeric matrix
 * ({@code double[][]}) representation.
 */
public interface NumericMatrix {
    /**
     * Returns a numeric matrix representation of this object.
     *
     * @return a numeric matrix representation of this object.
     */
    public abstract double[][] toNumeric();
}
