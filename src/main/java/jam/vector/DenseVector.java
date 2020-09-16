
package jam.vector;

// Package-scope implementation of a dense floating-point vector.
//
final class DenseVector extends VectorImpl {
    private final double[] elements;

    DenseVector(double[] elements) {
        this.elements = elements;
    }

    @Override public VectorImpl copy() {
        return new DenseVector(VectorUtil.copy(elements));
    }

    @Override public VectorImpl like(int length) {
        return new DenseVector(new double[length]);
    }

    @Override public int length() {
        return elements.length;
    }

    @Override public double getDouble(int index) {
        return elements[index];
    }
    
    @Override public VectorImpl set(int index, double value) {
        elements[index] = value;
        return this;
    }
}
