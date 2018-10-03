
package jam.vector;

import java.util.Collection;

import org.apache.commons.collections.primitives.ArrayDoubleList;

import jam.math.DoubleUtil;
import jam.math.IntSequence;
import jam.math.JamRandom;
import jam.math.StatUtil;
import jam.matrix.MatrixView;

/**
 * Fixed-size real-valued vector with basic algebraic operations.
 *
 * <p>Vector indexing is zero-based, e.g., {@code get(0} returns the
 * first element in the vector.
 *
 * <p>Although not explicitly indicated by the method comments, most
 * methods throw {@code RuntimeException}s when passed illegal
 * arguments, e.g., non-positive dimensions or out-of-bounds indexes.
 */
public final class JamVector extends AbstractVector {
    private VectorImpl impl;

    private JamVector(VectorImpl impl) {
        this.impl = impl;
    }

    /**
     * Fixed unit vector along the x-axis.
     */
    public static final VectorView UNIT_X = valueOf(1.0, 0.0, 0.0);

    /**
     * Fixed unit vector along the y-axis.
     */
    public static final VectorView UNIT_Y = valueOf(0.0, 1.0, 0.0);

    /**
     * Fixed unit vector along the z-axis.
     */
    public static final VectorView UNIT_Z = valueOf(0.0, 0.0, 1.0);

    /**
     * Creates a vector of specified length with all elements
     * initialized to zero.
     *
     * @param length the number of elements.
     */
    public JamVector(int length) {
        this(length, 0.0);
    }

    /**
     * Creates a vector of specified length with all elements
     * initialized to a specified value.
     *
     * @param length the number of elements.
     *
     * @param fill the value to assign to all elements.
     */
    public JamVector(int length, double fill) {
        this(new DenseVector(VectorUtil.create(length, fill)));
    }

    /**
     * Creates a vector from a raw array.
     *
     * @param elements the elements to initialize the vector.
     *
     * @return a new vector with elements copied from the input
     * array.
     */
    public static JamVector copyOf(double[] elements) {
        return new JamVector(new DenseVector(VectorUtil.copy(elements)));
    }

    /**
     * Creates a vector as a copy of another view.
     *
     * @param view the vector view to copy.
     *
     * @return a new vector with elements copied from the input view.
     */
    public static JamVector copyOf(VectorView view) {
        return new JamVector(new DenseVector(view.toNumeric()));
    }

    /**
     * Creates a vector with elements copied from an Apache 
     * {@code ArrayDoubleList}.
     *
     * @param list the list to copy.
     *
     * @return a new vector with elements copied from the input list.
     */
    public static JamVector copyOf(ArrayDoubleList list) {
        return new JamVector(new DenseVector(list.toArray()));
    }

    /**
     * Creates a vector with elements copied from a collection of
     * {@code Double} values, in the order returned by the iterator.
     *
     * @param collection the collection to copy.
     *
     * @return a new vector with elements copied from the input
     * collection.
     */
    public static JamVector copyOf(Collection<Double> collection) {
        return copyOf(DoubleUtil.toArray(collection));
    }

    /**
     * Creates a vector from a raw floating-point sequence.
     *
     * @param elements the elements to initialize the vector.
     *
     * @return a new vector with elements copied from the input
     * sequence.
     */
    public static JamVector valueOf(double... elements) {
        return new JamVector(new DenseVector(VectorUtil.copy(elements)));
    }

    /**
     * Creates a vector of specified length with all elements
     * initialized to zero.
     *
     * @param length the number of elements.
     *
     * @return the vector of zeros.
     */
    public static JamVector zeros(int length) {
        return new JamVector(length, 0.0);
    }

    /**
     * Creates a vector of specified length with all elements
     * initialized to one.
     *
     * @param length the number of elements.
     *
     * @return the vector of ones.
     */
    public static JamVector ones(int length) {
        return new JamVector(length, 1.0);
    }

    /**
     * Creates a vector with elements drawn from a Gaussian
     * distribution with a specified mean and standard deviation.
     *
     * @param length the dimensionality of the structure.
     *
     * @param mean the mean of the Gaussian distribution from which
     * the elements are drawn.
     *
     * @param stdev the standard deviation of the Gaussian
     * distribution from which the elements are drawn.
     *
     * @return a new vector with elements drawn from a Gaussian
     * distribution (using the global random number generator).
     */
    public static JamVector gaussian(int length, double mean, double stdev) {
        JamVector vector = new JamVector(length);

        for (int k = 0; k < length; ++k)
            vector.set(k, JamRandom.global().nextGaussian(mean, stdev));

        return vector;
    }

    /**
     * Creates a copy of this vector.
     *
     * @return a new vector with the same elements as this vector.
     */
    public JamVector copy() {
	return new JamVector(this.impl.copy());
    }

    /**
     * Assigns a single value to all elements.
     *
     * @param value the value to assign.
     *
     * @return {@code this} vector (for operator chaining).
     */
    public JamVector fill(double value) {
        for (int index : IntSequence.along(this))
            set(index, value);

        return this;
    }

    /**
     * Returns the first element in this vector.
     *
     * @return the first element in this vector.
     *
     * @throws RuntimeException if this vector is empty.
     */
    public double first() {
        return get(0);
    }

    /**
     * Returns the element at a specified location.
     *
     * @param index the index of the element to return.
     *
     * @return the element at the specified location.
     *
     * @throws RuntimeException unless the specified index is a valid
     * location.
     */
    public double get(int index) {
        return impl.getDouble(index);
    }

    /**
     * Returns the last element in this vector.
     *
     * @return the last element in this vector.
     *
     * @throws RuntimeException if this vector is empty.
     */
    public double last() {
        return get(length() - 1);
    }

    /**
     * Assigns the value of a specified element.
     *
     * @param index the (zero-offset) element index.
     *
     * @param value the value to assign.
     */
    public void set(int index, double value) {
        //
        // Must update the implementation object, since the underlying
        // storage might change, e.g., from sparse to dense...
        // 
        impl = impl.set(index, value);
    }

    /**
     * Adds <em>in place</em> a scalar value to each element in this
     * vector.
     *
     * @param scalar the scalar value to add.
     *
     * @return {@code this} vector (for operator chaining).
     */
    public JamVector add(double scalar) {
        impl = impl.plus(scalar);
        return this;
    }

    /**
     * Adds another vector to this vector <em>in place</em>.
     *
     * @param that the vector to add.
     *
     * @throws IllegalArgumentException unless the input vector has
     * the same length as this vector.
     *
     * @return {@code this} vector (for operator chaining).
     */
    public JamVector add(VectorView that) {
        impl = impl.daxpy(1.0, that);
        return this;
    }

    /**
     * Computes the cosine of the angle between this vector and
     * another.
     *
     * @param that the vector in relation to this.
     *
     * @return the cosine of the angle between this vector and the
     * input vector.
     *
     * @throws IllegalArgumentException unless the input vector has
     * the same length as this vector.
     */
    public double cosine(VectorView that) {
        return cosine(this, that);
    }

    /**
     * Computes the dot product of two vectors.
     *
     * @param v1 the first vector factor.
     *
     * @param v2 the second vector factor.
     *
     * @return the dot product of the two vectors.
     *
     * @throws IllegalArgumentException unless the vectors have the
     * same length.
     */
    public static double cosine(VectorView v1, VectorView v2) {
        return unit(v1).dot(unit(v2));
    }

    /**
     * Computes the vector cross product of this vector and another
     * and returns the result in a new {@code JamVector}; this vector
     * is unchanged.
     *
     * <p>Note that the cross product is defined only for vectors of
     * length 3.
     *
     * @param that the vector factor.
     *
     * @return the cross product of this vector and the input vector.
     *
     * @throws IllegalArgumentException unless this vector and the
     * input vector have length 3.
     */
    public JamVector cross(VectorView that) {
        return cross(this, that);
    }

    /**
     * Computes the vector cross product of two 3-dimensional vectors.
     *
     * @param u the first vector factor.
     *
     * @param v the second vector factor.
     *
     * @return the cross product of the input vectors.
     *
     * @throws IllegalArgumentException unless both input vectors have
     * length 3.
     */
    public static JamVector cross(VectorView u, VectorView v) {
	if (u.length() != 3 || v.length() != 3)
	    throw new IllegalArgumentException("Vector product is defined only for 3-vectors.");

	double ux = u.getDouble(0);
	double uy = u.getDouble(1);
	double uz = u.getDouble(2);

	double vx = v.getDouble(0);
	double vy = v.getDouble(1);
	double vz = v.getDouble(2);

	double rx = uy * vz - uz * vy;
	double ry = uz * vx - ux * vz;
	double rz = ux * vy - uy * vx;

	return JamVector.valueOf(rx, ry, rz);
    }

    /**
     * Computes the sum of this vector and the scalar multiple of
     * another vector {@code (this + scalar * that)} and returns the
     * result in a new {@code JamVector}; this vector is unchanged.
     *
     * <p>The method name comes from the BLAS library function which
     * performs the same operation.
     *
     * @param scalar the scalar factor to multiply the input vector.
     *
     * @param that the vector operand to add to this vector.
     *
     * @return a new vector {@code v} with {@code v.get(k) == this.get(k) + scalar * that.getDouble(k)}.
     */
    public JamVector daxpy(double scalar, VectorView that) {
        return new JamVector(impl.daxpy(scalar, that));
    }

    /**
     * Computes the dot product of this vector and another.
     *
     * @param that the vector to multiply this.
     *
     * @return the dot product of this vector and the input vector.
     *
     * @throws IllegalArgumentException unless the input vector has
     * the same length as this vector.
     */
    public double dot(VectorView that) {
        return impl.dot(that);
    }

    /**
     * Computes the dot product of two vectors.
     *
     * @param v1 the first vector factor.
     *
     * @param v2 the second vector factor.
     *
     * @return the dot product of the two vectors.
     *
     * @throws IllegalArgumentException unless the vectors have the
     * same length.
     */
    public static double dot(VectorView v1, VectorView v2) {
        return JamVector.copyOf(v1).dot(v2);
    }

    /**
     * Divide <em>in place</em> all elements in this vector by a
     * scalar value.
     *
     * @param scalar the scalar value to divide each element.
     *
     * @return {@code this} vector (for operator chaining).
     */
    public JamVector divide(double scalar) {
        return multiply(1.0 / scalar);
    }

    /**
     * Divides two vectors element-by-element; the input vectors
     * are unchanged.
     *
     * @param v1 the first vector factor.
     *
     * @param v2 the first vector factor.
     *
     * @return a new vector {@code u} with the same common length as
     * the input vectors having {@code u.get(k) == v1.getDouble(k) / v2.getDouble(k)}.
     */
    public static JamVector divideEBE(VectorView v1, VectorView v2) {
        v1.validateOperand(v2);
        JamVector result = new JamVector(v1.length());

        for (int index : IntSequence.along(result))
            result.set(index, v1.getDouble(index) / v2.getDouble(index));

        return result;
    }

    /**
     * Computes the difference between this vector and a scalar value
     * and returns the result in a new {@code JamVector}; this vector
     * is unchanged.
     *
     * @param scalar the scalar value to subtract.
     *
     * @return the difference between this vector and the input value.
     */
    public JamVector minus(double scalar) {
        return plus(-scalar);
    }

    /**
     * Computes the difference between this vector and a read-only
     * vector view and returns the result in a new {@code JamVector};
     * this vector is unchanged.
     *
     * @param that a read-only view of the vector to subtract.
     *
     * @return the difference between this vector and the input view.
     *
     * @throws IllegalArgumentException unless the view has the same
     * length as this vector.
     */
    public JamVector minus(VectorView that) {
        return daxpy(-1.0, that);
    }

    /**
     * Computes the difference between a vector and a scalar value and
     * returns the result in a new {@code JamVector}.
     *
     * @param vector the vector minuend.
     *
     * @param scalar the scalar subtrahend.
     *
     * @return the difference between the vector and scalar values.
     */
    public static JamVector minus(VectorView vector, double scalar) {
        return JamVector.copyOf(vector).minus(scalar);
    }

    /**
     * Computes the difference between two vectors and returns the
     * result in a new {@code JamVector}.
     *
     * @param v1 the vector minuend.
     *
     * @param v2 the scalar subtrahend.
     *
     * @return the difference between the two vectors.
     *
     * @throws IllegalArgumentException unless the vectors have the
     * same length.
     */
    public static JamVector minus(VectorView v1, VectorView v2) {
        return JamVector.copyOf(v1).minus(v2);
    }

    /**
     * Multiplies <em>in place</em> all elements in this vector by a
     * scalar value.
     *
     * @param scalar the scalar value to multiply each element.
     *
     * @return {@code this} vector (for operator chaining).
     */
    public JamVector multiply(double scalar) {
        impl = impl.times(scalar);
        return this;
    }

    /**
     * Multiplies two vectors element-by-element; the input vectors
     * are unchanged.
     *
     * @param v1 the first vector factor.
     *
     * @param v2 the first vector factor.
     *
     * @return a new vector {@code u} with the same common length as
     * the input vectors having {@code u.getDouble(k) == v1.getDouble(k) * v2.getDouble(k)}.
     */
    public static JamVector multiplyEBE(VectorView v1, VectorView v2) {
        v1.validateOperand(v2);
        JamVector result = new JamVector(v1.length());

        for (int index : IntSequence.along(result))
            result.set(index, v1.getDouble(index) * v2.getDouble(index));

        return result;
    }

    /**
     * Returns the 2-norm of this vector.
     *
     * @return the 2-norm of this vector.
     */
    public double norm() {
        return VectorAggregator.norm2(this);
    }

    /**
     * Returns the {@code p}-norm of this vector.
     *
     * @param p the index of the vector norm to compute.
     *
     * @return the {@code p}-norm of this vector.
     */
    public double norm(int p) {
        return StatUtil.normp(this, p);
    }

    /**
     * Normalizes this vector: after applying this operation, the
     * elements of this vector will sum to one.
     *
     * @return {@code this} vector (for operator chaining).
     */
    public JamVector normalize() {
        return divide(sum());
    }

    /**
     * Computes the sum of this vector and a scalar value and returns
     * the result in a new {@code JamVector}; this vector is unchanged.
     *
     * @param scalar the scalar value to add.
     *
     * @return the sum of this vector and the input value.
     */
    public JamVector plus(double scalar) {
        return new JamVector(impl.plus(scalar));
    }

    /**
     * Computes the sum of this vector and a read-only vector view and
     * returns the result in a new {@code JamVector}; this vector is
     * unchanged.
     *
     * @param that a read-only view of the vector to add.
     *
     * @return the sum of this vector and the input view.
     *
     * @throws IllegalArgumentException unless the view has the same
     * length as this vector.
     */
    public JamVector plus(VectorView that) {
        return daxpy(1.0, that);
    }

    /**
     * Computes the sum of a vector and a scalar value and returns the
     * result in a new {@code JamVector}.
     *
     * @param vector the vector addend.
     *
     * @param scalar the scalar addend.
     *
     * @return the sum of the vector and scalar values.
     */
    public static JamVector plus(VectorView vector, double scalar) {
        return JamVector.copyOf(vector).plus(scalar);
    }

    /**
     * Computes the sum of two vectors and returns the result in a new
     * {@code JamVector}.
     *
     * @param v1 the first vector addend.
     *
     * @param v2 the second vector addend.
     *
     * @return the sum of the two vectors.
     *
     * @throws IllegalArgumentException unless the vectors have the
     * same length.
     */
    public static JamVector plus(VectorView v1, VectorView v2) {
        return JamVector.copyOf(v1).plus(v2);
    }

    /**
     * Subtracts <em>in place</em> a scalar value from each element in
     * this vector.
     *
     * @param scalar the scalar value to subtract.
     *
     * @return {@code this} vector (for operator chaining).
     */
    public JamVector subtract(double scalar) {
        return add(-scalar);
    }

    /**
     * Returns a subvector of this vector.
     *
     * <p>Similarly to {@code String.substring}, the subvector begins
     * at the specified index and extends to the end of this vector.
     *
     * @param beginIndex the beginning index, inclusive.
     *
     * @return the specified subvector (in a new object).
     *
     * @throws RuntimeException unless the specifed index is valid.
     */
    public JamVector subvector(int beginIndex) {
        return subvector(beginIndex, length());
    }

    /**
     * Returns a subvector of this vector.
     *
     * <p>Similarly to {@code String.substring}, the subvector
     * contains elements {@code (beginIndex, endIndex]}.
     *
     * @param beginIndex the beginning index, inclusive.
     *
     * @param endIndex the ending index, exclusive.
     *
     * @return the specified subvector (in a new object).
     *
     * @throws RuntimeException unless the specifed indexes are valid.
     */
    public JamVector subvector(int beginIndex, int endIndex) {
        return new JamVector(impl.subvector(beginIndex, endIndex));
    }

    /**
     * Subtracts another vector from this vector <em>in place</em>.
     *
     * @param that the vector to subtract.
     *
     * @throws IllegalArgumentException unless the input vector has
     * the same length as this vector.
     *
     * @return {@code this} vector (for operator chaining).
     */
    public JamVector subtract(VectorView that) {
        impl = impl.daxpy(-1.0, that);
        return this;
    }

    /**
     * Returns the sum of the elements in this vector.
     *
     * @return the sum of the elements in this vector.
     */
    public double sum() {
        return VectorAggregator.sum(this);
    }

    /**
     * Computes the product of this vector and a scalar value and returns
     * the result in a new {@code JamVector}; this vector is unchanged.
     *
     * @param scalar the scalar value to multiply each element.
     *
     * @return the product of this vector and the input value.
     */
    public JamVector times(double scalar) {
        return new JamVector(impl.times(scalar));
    }

    /**
     * Computes product sum of a vector and a scalar value and returns
     * the result in a new {@code JamVector}.
     *
     * @param vector the vector factor.
     *
     * @param scalar the scalar factor.
     *
     * @return the product of the vector and scalar values.
     */
    public static JamVector times(VectorView vector, double scalar) {
        return JamVector.copyOf(vector).times(scalar);
    }

    /**
     * Returns a new unit vector along the same direction as the input
     * vector.
     *
     * @param vector the reference vector.
     *
     * @return a new unit vector along the direction of the input vector.
     */
    public static JamVector unit(VectorView vector) {
        JamVector result = JamVector.copyOf(vector);
        result.unitize();
        return result;
    }
    
    /**
     * Rescales this vector into a unit vector.
     *
     * @return {@code this} vector (for operator chaining).
     */
    public JamVector unitize() {
        return divide(norm());
    }

    /**
     * Creates a vector by parsing a comma-delimited string.
     *
     * @param line a string contining the vector elements separated by
     * commas.
     *
     * @return a new vector with the elements assigned from the given
     * formatted string.
     *
     * @throws IllegalArgumentException unless the input string is
     * properly formatted.
     */
    public static JamVector parseCSV(String line) {
        return parse(line, ",");
    }

    /**
     * Creates a vector by parsing a delimited string.
     *
     * @param line a string contining the vector elements separated by
     * the given delimiter.
     *
     * @param delimiter the element delimiter.
     *
     * @return a new vector with the elements assigned from the given
     * formatted string.
     *
     * @throws IllegalArgumentException unless the input string is
     * properly formatted.
     */
    public static JamVector parse(String line, String delimiter) {
        String[]  fields = line.split(delimiter);
        JamVector vector = new JamVector(fields.length);

        for (int k = 0; k < fields.length; k++)
            vector.set(k, Double.parseDouble(fields[k]));

        return vector;
    }

    @Override public int length() {
        return impl.length();
    }

    @Override public double getDouble(int index) {
        return impl.getDouble(index);
    }
}
