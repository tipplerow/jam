
package jam.math;

import java.util.Arrays;

import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import jam.vector.JamVector;
import jam.vector.VectorView;

/**
 * Provides static utility methods providing statistical operations.
 */
public final class StatUtil {
    /**
     * Multiplying the median absolute deviation by this constant
     * value ensures that the MAD and standard deviation converge
     * to the same value for large, normally distributed data sets.
     */
    public static final double MAD_CONSTANT = 1.4826;

    /**
     * Computes the linear (Pearson) correlation coefficient between
     * two data sets.
     *
     * @param x the first data array.
     *
     * @param y the second data array.
     *
     * @return the linear (Pearson) correlation coefficient between
     * the two data sets.
     *
     * @throws IllegalArgumentException if the array lenghts are not
     * equal or if their lengths are less than two.
     */
    public static double cor(VectorView x, VectorView y) {
        return new PearsonsCorrelation().correlation(x.toNumeric(), y.toNumeric());
    }

    /**
     * Computes the covariance between two data sets.
     *
     * @param x the first data array.
     *
     * @param y the second data array.
     *
     * @param biasCorrected if {@code true}, the denominator of the
     * covariance calculation will be {@code N - 1}, otherwise it will
     * be {@code N}, where {@code N} is the number of observations.
     *
     * @return the covariance between the two data sets.
     *
     * @throws IllegalArgumentException if the array lenghts are not
     * equal or if their lengths are less than two.
     */
    public static double cov(VectorView x, VectorView y, boolean biasCorrected) {
        return new Covariance().covariance(x.toNumeric(), y.toNumeric(), biasCorrected);
    }

    /**
     * Computes the cumulative sum of a numerical sequence, ignoring
     * missing values.
     *
     * @param values the values to examine.
     *
     * @return a vector {@code x} of the same length as the input
     * vector with {@code x.get(k)} equal to the sum of all
     * non-missing values from elements {@code 0, 1, ..., k}.  If the
     * first {@code N} elements of the input vector are missing (e.g.,
     * {@code Double.NaN}) then the first {@code N} elements of the
     * result will be {@code 0.0}.
     */
    public static JamVector cumsum(VectorView values) {
        double replace = 0.0;
        JamVector result = new JamVector(values.length());

        result.set(0, getSafe(values, 0, replace));

        for (int k = 1; k < values.length(); k++)
            result.set(k, result.get(k - 1) + getSafe(values, k, replace));

        return result;
    }
    
    private static double getSafe(VectorView values, int index, double replace) {
        //
        // Returns the non-missing value at the given index, or the replacement if
        // the value is missing (Double.NaN)...
        //
        return DoubleUtil.replaceNaN(values.getDouble(index), replace);
    }

    /**
     * Computes the cumulative product of a numerical sequence,
     * ignoring missing values.
     *
     * @param values the values to examine.
     *
     * @return a vector {@code x} of the same length as the input
     * vector with {@code x.get(k)} equal to the product of all
     * non-missing values from elements {@code 0, 1, ..., k}.  If the
     * first {@code N} elements of the input vector are missing (e.g.,
     * {@code Double.NaN}) then the first {@code N} elements of the
     * result will be {@code 1.0}.
     */
    public static JamVector cumprod(VectorView values) {
        double replace = 1.0;
        JamVector result = new JamVector(values.length());

        result.set(0, getSafe(values, 0, replace));

        for (int k = 1; k < values.length(); k++)
            result.set(k, result.get(k - 1) * getSafe(values, k, replace));

        return result;
    }

    /**
     * Finds the maximum value in a numerical sequence, ignoring
     * missing values.
     *
     * @param values the values to examine.
     *
     * @return the greatest value in the sequence, or {@code Double.NEGATIVE_INFINITY}
     * if there are no non-missing values in the sequence.
     */
    public static double max(VectorView values) {
        double result = Double.NEGATIVE_INFINITY;

        for (double value : values.elements())
            if (value > result)
                result = value;

        return result;
    }

    /**
     * Computes the arithmetic mean of a numerical sequence, ignoring
     * missing values.
     *
     * @param values the values to examine.
     *
     * @return the arithmetic mean of the non-missing values, or
     * {@code Double.NaN} if the sequence contains no non-missing
     * values.
     */
    public static double mean(VectorView values) {
        int count = 0;
        double sumval = 0.0;

        for (double value : values.elements())
            if (!Double.isNaN(value)) {
                count++;
                sumval += value;
            }

        return sumval / count;
    }

    /**
     * Computes the arithmetic mean of the squared values in a
     * numerical sequence, ignoring missing values.
     *
     * @param values the values to examine.
     *
     * @return the arithmetic mean of the squares of the non-missing
     * values, or {@code Double.NaN} if the sequence contains no
     * non-missing values.
     */
    public static double meansqr(VectorView values) {
        int count = 0;
        double sumsqr = 0.0;

        for (double value : values.elements())
            if (!Double.isNaN(value)) {
                count++;
                sumsqr += value * value;
            }

        return sumsqr / count;
    }

    /**
     * Computes the weighted mean of a data set, ignoring missing
     * values and weights.
     *
     * <p>Note that the weights must be non-negative but need not sum
     * to unity.
     *
     * @param values the observation values.
     *
     * @param weights the relative observation weights.
     *
     * @return the weighted mean of the observations with non-missing
     * values and weights, or {@code Double.NaN} if there are none.
     *
     * @throws IllegalArgumentException unless the values and weights
     * have the same length and all weights are non-negative.
     */
    public static double mean(VectorView values, VectorView weights) {
        if (values.length() != weights.length())
            throw new IllegalArgumentException("Length mismatch.");

        double totalValue = 0.0;
        double totalWeight = 0.0;

        for (int k : IntSequence.along(values)) {
            double value = values.getDouble(k);
            double weight = weights.getDouble(k);

            if (weight < 0.0)
                throw new IllegalArgumentException("Non-positive observation weight.");

            if (!Double.isNaN(value) && !Double.isNaN(weight)) {
                totalWeight += weight;
                totalValue  += weight * value;
            }
        }

        if (totalWeight < DoubleComparator.EPSILON)
            throw new IllegalArgumentException("Total weight is not positive.");

        return totalValue / totalWeight;
    }

    /**
     * Computes the median of a numerical sequence, ignoring missing
     * values.
     *
     * @param values the values to examine.
     *
     * @return the median of the non-missing values, or {@code
     * Double.NaN} if the sequence contains no non-missing values.
     */
    public static double median(VectorView values) {
        double[] sorted = values.toNumeric();
        Arrays.sort(sorted);

        // The Double comparator considers NaN values to be larger
        // than any other value, including Double.POSITIVE_INFINITY,
        // so all NaNs will be at the end of the sorted array. Find
        // them and exclude them from the calculation.
        int upper = sorted.length - 1;

        while (upper >= 0 && Double.isNaN(sorted[upper]))
            upper--;

        if (upper < 0)
            return Double.NaN;

        int mid = upper / 2;

        if (upper % 2 == 0)
            return sorted[mid];
        else
            return 0.5 * (sorted[mid] + sorted[mid + 1]);
    }

    /**
     * Computes the median absolute deviation (MAD) of a numerical
     * sequence, ignoring missing values.
     *
     * <p>The raw MAD is multiplied by 1.4826 so that the MAD
     * converges to the standard deviation for large normally
     * distributed data sets.
     *
     * @param center the center around which to measure deviation
     * (usually the median).
     *
     * @param values the values to examine.
     *
     * @return the median absolute deviation (MAD) of the non-missing
     * values (scaled for consistency with the standard deviation of
     * normally distributed values), or {@code Double.NaN} if the
     * sequence contains no non-missing values.
     */
    public static double MAD(double center, VectorView values) {
        JamVector absdev = new JamVector(values.length());

        for (int k : IntSequence.along(values))
            absdev.set(k, Math.abs(values.getDouble(k) - center));

        return MAD_CONSTANT * median(absdev);
    }

    /**
     * Finds the minimum value in a numerical sequence, ignoring
     * missing values.
     *
     * @param values the values to examine.
     *
     * @return the least value in the sequence, or {@code Double.POSITIVE_INFINITY}
     * if there are no non-missing values in the sequence.
     */
    public static double min(VectorView values) {
        double result = Double.POSITIVE_INFINITY;

        for (double value : values.elements())
            if (value < result)
                result = value;

        return result;
    }

    /**
     * Computes the p-norm of a numerical sequence, ignoring missing
     * values.
     *
     * @param values the values to examine.
     * @param p the norm index.
     *
     * @return the p-norm of the non-missing values, or {@code 0.0} if
     * the sequence contains no non-missing values.
     */
    public static double normp(VectorView values, int p) {
        double sump = 0.0;

        for (double value : values.elements())
            if (!Double.isNaN(value))
                sump += Math.pow(Math.abs(value), p);

        return Math.pow(sump, 1.0 / p);
    }

    /**
     * Computes the 1-norm (sum of absolute values) of a numerical
     * sequence, ignoring missing values.
     *
     * @param values the values to examine.
     *
     * @return the 1-norm of the non-missing values, or {@code 0.0} if
     * the sequence contains no non-missing values.
     */
    public static double norm1(VectorView values) {
        double sumabs = 0.0;

        for (double value : values.elements())
            if (!Double.isNaN(value))
                sumabs += Math.abs(value);

        return sumabs;
    }

    /**
     * Computes the 2-norm (square root of sum of squared values) of a
     * numerical sequence, ignoring missing values.
     *
     * @param values the values to examine.
     *
     * @return the 2-norm of the non-missing values, or {@code 0.0} if
     * the sequence contains no non-missing values.
     */
    public static double norm2(VectorView values) {
        double sumsqr = 0.0;

        for (double value : values.elements())
            if (!Double.isNaN(value))
                sumsqr += (value * value);

        return Math.sqrt(sumsqr);
    }

    /**
     * Computes the infinity norm (maximum absolute value) of a
     * numerical sequence, ignoring missing values.
     *
     * @param values the values to examine.
     *
     * @return the maximum absolute value of the non-missing values,
     * or {@code Double.NEGATIVE_INFINITY} if the sequence contains
     * no non-missing values.
     */
    public static double normInf(VectorView values) {
        double result = Double.NEGATIVE_INFINITY;

        for (double value : values.elements())
            if (!Double.isNaN(value))
                result = Math.max(result, Math.abs(value));

        return result;
    }

    /**
     * Computes the product of a numerical sequence, ignoring missing
     * values.
     *
     * @param values the sequence to examine.
     *
     * @return the product of non-missing values in the sequence, or
     * {@code 1.0} if there are no non-missing values in the sequence.
     */
    public static double prod(VectorView values) {
        double result = 1.0;

        for (double value : values.elements())
            if (!Double.isNaN(value))
                result *= value;

        return result;
    }

    /**
     * Removes {@code Double.NaN} values from a numerical array.
     *
     * @param values a numerical array.
     *
     * @return a new numerical array with all {@code Double.NaN}
     * values from the input array removed.
     */
    public static JamVector removeNaN(VectorView values) {
        ArrayDoubleList notnan = new ArrayDoubleList(values.length());

        for (double value : values.elements())
            if (!Double.isNaN(value))
                notnan.add(value);

        return new JamVector(notnan);
    }

    /**
     * Computes the standard deviation of a numerical sequence, excluding
     * {@code NaN} values.
     *
     * @param values the sequence to examine.
     *
     * @return the standard deviation of the non-missing values, or
     * {@code Double.NaN} if there are fewer than two non-missing
     * values in the sequence.
     */
    public static double stdev(VectorView values) {
        return Math.sqrt(variance(values));
    }

    /**
     * Computes the weighted standard deviation of a data set,
     * ignoring missing values and weights.
     *
     * <p>Note that the weights must be non-negative but need not sum
     * to unity.
     *
     * <p>Note that the variance is normalized by the total weight,
     * not {@code values.length() - 1} as in the equally-weighted case.
     *
     * @param values the observation values.
     *
     * @param weights the relative observation weights.
     *
     * @return the weighted standard deviation of the observations
     * with non-missing values and weights.
     *
     * @throws IllegalArgumentException unless the values and weights
     * have the same length and all weights are non-negative.
     */
    public static double stdev(VectorView values, VectorView weights) {
        return Math.sqrt(variance(values, weights));
    }

    /**
     * Removes non-finite values from a numerical array.
     *
     * @param values a numerical array.
     *
     * @return a new numerical array with all non-finite values
     * from the input array removed.
     */
    public static JamVector selectFinite(VectorView values) {
        ArrayDoubleList finite = new ArrayDoubleList(values.length());

        for (double value : values.elements())
            if (DoubleUtil.isFinite(value))
                finite.add(value);

        return new JamVector(finite);
    }

    /**
     * Computes the sum of a numerical sequence, ignoring missing
     * values.
     *
     * @param values the values to examine.
     *
     * @return the sum of non-missing values in the sequence, or
     * {@code 0.0} if there are no non-missing values in the sequence.
     */
    public static double sum(VectorView values) {
        double result = 0.0;

        for (double value : values.elements())
            if (!Double.isNaN(value))
                result += value;

        return result;
    }

    /**
     * Computes the sum of the squared values in a numerical sequence,
     * ignoring missing values.
     *
     * @param values the values to examine.
     *
     * @return the sum of the squared non-missing values in the sequence,
     * or {@code 0.0} if there are no non-missing values in the sequence.
     */
    public static double sumsqr(VectorView values) {
        double result = 0.0;

        for (double value : values.elements())
            if (!Double.isNaN(value))
                result += value * value;

        return result;
    }

    /**
     * Computes the variance of a numerical sequence, excluding
     * {@code NaN} values.
     *
     * @param values the sequence to examine.
     *
     * @return the variance of the non-missing values, or {@code
     * Double.NaN} if there are fewer than two non-missing values in
     * the sequence.
     */
    public static double variance(VectorView values) {
        int count = 0;
        double sumsqr = 0.0;
        double meanval = mean(values);

        for (double value : values.elements())
            if (!Double.isNaN(value)) {
                count++;
                sumsqr += DoubleUtil.square(value - meanval);
            }

        if (count < 2)
            return Double.NaN;
        else
            return sumsqr / (count - 1);
    }

    /**
     * Computes the weighted variance of a data set, ignoring missing
     * values and weights.
     *
     * <p>Note that the weights must be non-negative but need not sum
     * to unity.
     *
     * <p>Note that the variance is normalized by the total weight,
     * not {@code values.length() - 1} as in the equally-weighted case.
     *
     * @param values the observation values.
     *
     * @param weights the relative observation weights.
     *
     * @return the weighted variance of the observations with non-missing
     * values and weights.
     *
     * @throws IllegalArgumentException unless the values and weights
     * have the same length and all weights are non-negative.
     */
    public static double variance(VectorView values, VectorView weights) {
        if (values.length() != weights.length())
            throw new IllegalArgumentException("Length mismatch.");

        double totwt = 0.0;
        double sumsqr = 0.0;
        double wtmean = mean(values, weights);

        for (int k : IntSequence.along(values)) {
            double value = values.getDouble(k);
            double weight = weights.getDouble(k);

            if (weight < 0.0)
                throw new IllegalArgumentException("Non-positive observation weight.");

            if (!Double.isNaN(value) && !Double.isNaN(weight)) {
                totwt += weight;
                sumsqr += weight * DoubleUtil.square(value - wtmean);
            }
        }

        return sumsqr / totwt;
    }
}
