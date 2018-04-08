
package jam.hist;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import jam.util.ListUtil;
import jam.vector.VectorUtil;

public final class Histogram {
    private final List<Bin> bins;
    private final long total;

    private Histogram(List<Bin> bins) {
        Bin.freeze(bins);
        this.bins  = Collections.unmodifiableList(bins);
        this.total = Bin.computeTotalCount(bins);
    }

    /**
     * Generates a histogram with contiguous bins defined by arbitrary
     * (ascending) breakpoints.
     *
     * @param brkpts the breakpoints for the bins, in ascending order.
     *
     * @param data the data to bin.
     *
     * @return the histogram of binned observations.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size and the number of bins is positive.
     */
    public static Histogram compute(double[] brkpts, double[] data) {
        List<Bin> bins = Bin.create(brkpts);

        for (double obs : data)
            binObservation(brkpts, bins, obs);
        
        return new Histogram(bins);
    }

    private static void binObservation(double[] brkpts, List<Bin> bins, double obs) {
        int index = VectorUtil.bracket(brkpts, obs);

        if (index < 0) {
            //
            // The observation is below the lower bound of the range...
            //
            return;
        }
        else if (index < bins.size()) {
            //
            // The observation falls in the range [lower, upper)...
            //
            bins.get(index).increment();
        }
        else if (index == bins.size()) {
            //
            // The observation is exactly equal to the upper bound of
            // the range...
            //
            bins.get(index - 1).increment();
        }
        else {
            //
            // The observation is above the upper bound of the range...
            //
            return;
        }
    }

    /**
     * Generates a histogram with equally-sized bins that span a
     * specified floating-point range.
     *
     * @param lower the lower bound of the range to span.
     *
     * @param upper the upper bound of the range to span.
     *
     * @param nbin the number of equally-sized bins to create.
     *
     * @param data the data to bin.
     *
     * @return the histogram of binned observations.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size and the number of bins is positive.
     */
    public static Histogram compute(double lower, double upper, int nbin, double[] data) {
        return compute(VectorUtil.sequence(lower, upper, nbin + 1), data);
    }

    /**
     * Generates a histogram with bins that span a floating-point
     * range uniformly <em>in logarithmic space</em>.
     *
     * @param lower the lower bound of the range to span.
     *
     * @param upper the upper bound of the range to span.
     *
     * @param nbin the number of bins to create.
     *
     * @param data the data to bin.
     *
     * @return the histogram of binned observations.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size, is strictly positive, and the number of bins is
     * positive.
     */
    public static Histogram computeLog(double lower, double upper, int nbin, double[] data) {
        return compute(VectorUtil.sequenceLog(lower, upper, nbin + 1), data);
    }

    /**
     * Generates a histogram with contiguous bins defined by arbitrary
     * (ascending) breakpoints.
     *
     * @param brkpts the breakpoints for the bins, in ascending order.
     *
     * @param data the data to bin.
     *
     * @return the histogram of binned observations.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size and the number of bins is positive.
     */
    public static Histogram compute(double[] brkpts, Collection<Double> data) {
        List<Bin> bins = Bin.create(brkpts);

        for (double obs : data)
            binObservation(brkpts, bins, obs);
        
        return new Histogram(bins);
    }

    /**
     * Generates a histogram with equally-sized bins that span a
     * specified floating-point range.
     *
     * @param lower the lower bound of the range to span.
     *
     * @param upper the upper bound of the range to span.
     *
     * @param nbin the number of equally-sized bins to create.
     *
     * @param data the data to bin.
     *
     * @return the histogram of binned observations.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size and the number of bins is positive.
     */
    public static Histogram compute(double lower, double upper, int nbin, Collection<Double> data) {
        return compute(VectorUtil.sequence(lower, upper, nbin + 1), data);
    }

    /**
     * Generates a histogram with bins that span a floating-point
     * range uniformly <em>in logarithmic space</em>.
     *
     * @param lower the lower bound of the range to span.
     *
     * @param upper the upper bound of the range to span.
     *
     * @param nbin the number of bins to create.
     *
     * @param data the data to bin.
     *
     * @return the histogram of binned observations.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size, is strictly positive, and the number of bins is
     * positive.
     */
    public static Histogram computeLog(double lower, double upper, int nbin, Collection<Double> data) {
        return compute(VectorUtil.sequenceLog(lower, upper, nbin + 1), data);
    }

    /**
     * Generates a histogram with contiguous bins defined by arbitrary
     * (ascending) breakpoints.
     *
     * @param <V> the runtime type of the data objects.
     *
     * @param brkpts the breakpoints for the bins, in ascending order.
     *
     * @param objects the objects from which numerical data is extracted.
     *
     * @param attribute a function to extract the numerical attribute.
     *
     * @return the histogram of binned observations.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size and the number of bins is positive.
     */
    public static <V> Histogram compute(double[] brkpts, Collection<V> objects, Function<V, Double> attribute) {
        return compute(brkpts, ListUtil.apply(objects, attribute));
    }

    /**
     * Generates a histogram with equally-sized bins that span a
     * specified floating-point range.
     *
     * @param <V> the runtime type of the data objects.
     *
     * @param lower the lower bound of the range to span.
     *
     * @param upper the upper bound of the range to span.
     *
     * @param nbin the number of equally-sized bins to create.
     *
     * @param objects the objects from which numerical data is extracted.
     *
     * @param attribute a function to extract the numerical attribute.
     *
     * @return the histogram of binned observations.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size and the number of bins is positive.
     */
    public static <V> Histogram compute(double lower, double upper, int nbin,
                                        Collection<V> objects, Function<V, Double> attribute) {
        return compute(lower, upper, nbin, ListUtil.apply(objects, attribute));
    }

    /**
     * Generates a histogram with bins that span a floating-point
     * range uniformly <em>in logarithmic space</em>.
     *
     * @param <V> the runtime type of the data objects.
     *
     * @param lower the lower bound of the range to span.
     *
     * @param upper the upper bound of the range to span.
     *
     * @param nbin the number of bins to create.
     *
     * @param objects the objects from which numerical data is extracted.
     *
     * @param attribute a function to extract the numerical attribute.
     *
     * @return the histogram of binned observations.
     *
     * @throws IllegalArgumentException unless the input range has
     * finite size, is strictly positive, and the number of bins is
     * positive.
     */
    public static <V> Histogram computeLog(double lower, double upper, int nbin,
                                           Collection<V> objects, Function<V, Double> attribute) {
        return computeLog(lower, upper, nbin, ListUtil.apply(objects, attribute));
    }

    /**
     * Returns the total number of binned observations.
     *
     * @return the total number of binned observations.
     */
    public long getTotalCount() {
        return total;
    }

    /**
     * Returns a read-only view of the binned observations.
     *
     * @return a read-only view of the binned observations.
     */
    public List<Bin> viewBins() {
        return bins;
    }
}
