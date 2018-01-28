
package jam.vector;

import jam.math.IntSequence;

// Package-scope base class for concrete implementations of vector
// storage and arithmetic operations.
//
abstract class VectorImpl extends AbstractVector {
    // Creates a deep copy of this vector.
    abstract VectorImpl copy();

    // Creates a new implementation instance with the same run-time
    // type and length as this, with all elements initialized to zero.
    VectorImpl like() {
        return like(length());
    }

    // Creates a new implementation instance with the same run-time
    // type as this, with all elements initialized to zero.
    abstract VectorImpl like(int length);

    // Assigns a value to the element at location [index].
    //
    // Returns the implementation, usually this same vector, but
    // potentially a different implementation if the underlying
    // storage scheme should change, e.g., after assigning many
    // non-zero values to a sparse vector.
    //
    abstract VectorImpl set(int index, double value);

    // Computes the sum of this vector and a scalar multiple of
    // another (this + scalar * that) and returns the result in
    // a new vector; this vector is unchanged.
    //
    // The method name comes from the BLAS library function which
    // performs the same operation.
    //
    VectorImpl daxpy(double scalar, VectorView that) {
        validateOperand(that);
        VectorImpl result = like();

        for (int index : IntSequence.along(this))
            result = result.set(index, this.getDouble(index) + scalar * that.getDouble(index));

        return result;
    }

    double dot(VectorView that) {
        validateOperand(that);
        double result = 0.0;

        for (int index : IntSequence.along(this))
            result += this.getDouble(index) * that.getDouble(index);

        return result;
    }

    VectorImpl plus(double scalar) {
        VectorImpl result = like();

        for (int index : IntSequence.along(this))
            result = result.set(index, scalar + this.getDouble(index));

        return result;
    }

    VectorImpl times(double scalar) {
        VectorImpl result = like();

        for (int index : IntSequence.along(this))
            result = result.set(index, scalar * this.getDouble(index));

        return result;
    }
}
