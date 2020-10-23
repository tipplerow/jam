
package jam.math;

import java.util.ArrayList;
import java.util.List;

import jam.lang.JamException;

/**
 * Defines a regularly spaced sequence of floating-point values with
 * identical intervals between each point in the sequence.
 */
public final class RegularSequence {
    private final int size;
    private final double first;
    private final double last;
    private final double step;

    private RegularSequence(double first, double last, double step) {
        this.first = first;
        this.last = last;
        this.step = step;
        this.size = computeSize();
    }

    private int computeSize() {
        if (!DoubleComparator.DEFAULT.isPositive(step))
            throw JamException.runtime("Regular sequence step size must be positive.");

        // The sequence size is one greater than the number of steps
        // between points...
        double stepCount = (last - first) / step;

        if (DoubleComparator.DEFAULT.LT(stepCount, 1.0))
            throw JamException.runtime("A regular sequence must contain at least two points.");

        // The step count must be an integral value (within the
        // standard floating-point tolerance)...
        if (!DoubleUtil.isInt(stepCount))
            throw JamException.runtime("The last value and step size are inconsistent.");

        return 1 + (int) Math.round(stepCount);
    }

    /**
     * Creates a new regular sequence with fixed attributes.
     *
     * @param first the first point in the sequence.
     *
     * @param last the last point in the sequence.
     *
     * @param step the distance between each point in the sequence.
     *
     * @return a new regular sequence with the specified parameters.
     *
     * @throws RuntimeException unless the step size is positive and
     * the last point is greater than the first by an integer multiple
     * of the step size.
     */
    public static RegularSequence create(double first, double last, double step) {
        return new RegularSequence(first, last, step);
    }

    /**
     * Returns the first point in this sequence.
     *
     * @return the first point in this sequence.
     */
    public double first() {
        return first;
    }

    /**
     * Returns the last point in this sequence.
     *
     * @return the last point in this sequence.
     */
    public double last() {
        return last;
    }

    /**
     * Returns the distance between each point in this sequence.
     *
     * @return the distance between each point in this sequence.
     */
    public double step() {
        return step;
    }

    /**
     * Returns the number of points in this sequence.
     *
     * @return the number of points in this sequence.
     */
    public int size() {
        return size;
    }

    /**
     * Returns the points in this sequence in an (ordered) array.
     *
     * @return the points in this sequence in an (ordered) array.
     */
    public double[] toArray() {
        double[] points = new double[size];

        for (int index = 0; index < size; ++index)
            points[index] = first + index * step;

        return points;
    }

    /**
     * Returns the points in this sequence in an (ordered) list.
     *
     * @return the points in this sequence in an (ordered) list.
     */
    public List<Double> toList() {
        List<Double> points = new ArrayList<Double>(size);

        for (int index = 0; index < size; ++index)
            points.add(first + index * step);

        return points;
    }
}
