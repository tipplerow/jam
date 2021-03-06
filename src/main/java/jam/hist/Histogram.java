
package jam.hist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import jam.math.DoubleRange;
import jam.math.Point2D;
import jam.math.JamUnivariateFunction;
import jam.math.SplineFunction;
import jam.util.ListUtil;
import jam.vector.VectorUtil;

public final class Histogram {
    private final List<Bin> bins;
    private final long total;

    // On-demand calculation of the cumulative distribution and
    // density functions...
    private JamUnivariateFunction cdf = null;
    private JamUnivariateFunction pdf = null;

    private Histogram(List<Bin> bins) {
        Bin.freeze(bins);
        this.bins  = Collections.unmodifiableList(bins);
        this.total = Bin.computeTotalCount(bins);
    }

    // Use a linear rather than binary search when there are fewer
    // than this number of bins...
    private static final int LINEAR_SEARCH_LIMIT = 32;

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
     * @throws IllegalArgumentException if there are fewer than two
     * breakpoints.
     */
    public static Histogram compute(double[] brkpts, double[] data) {
        List<Bin> bins = createBins(brkpts);

        for (double obs : data)
            binObservation(bins, obs);
        
        return new Histogram(bins);
    }

    private static List<Bin> createBins(double[] brkpts) {
        int nbin = brkpts.length - 1;

        if (nbin < 1)
            throw new IllegalArgumentException("At least one bin is required.");

        List<Bin> bins = new ArrayList<Bin>(nbin);

        // The first range is fully closed...
        bins.add(Bin.empty(DoubleRange.closed(brkpts[0], brkpts[1])));

        // All other bins are left open to correspond with the
        // definition of the cumulative distribution function...
        for (int k = 1; k < nbin; ++k)
            bins.add(Bin.empty(DoubleRange.leftOpen(brkpts[k], brkpts[k + 1])));

        return bins;
    }

    private static void binObservation(List<Bin> bins, double obs) {
        Bin bin = findBin(bins, obs);

        if (bin != null)
            bin.increment();
    }

    private static Bin findBin(List<Bin> bins, double obs) {
        int nbin = bins.size();

        if (nbin <= LINEAR_SEARCH_LIMIT)
            return findBinLinear(bins, obs);

        int ind = nbin / 2;
        Bin bin = bins.get(ind);
        int cmp = bin.compare(obs);

        if (cmp == 0)
            return bin;

        if (cmp < 0)
            return findBin(bins.subList(0, ind), obs);

        return findBin(bins.subList(ind + 1, nbin), obs);
    }

    private static Bin findBinLinear(List<Bin> bins, double obs) {
        //
        // Linear search for short lists...
        //
        for (Bin bin : bins)
            if (bin.contains(obs))
                return bin;

        return null;
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
        List<Bin> bins = createBins(brkpts);

        for (double obs : data)
            binObservation(bins, obs);
        
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
     * Returns the cumulative distribution function described by this
     * histogram.
     *
     * <p>Let {@code L} be the returned list and {@code pk = L.get(k)}
     * be the kth point.  Then {@code pk.x} is the upper bound of the
     * kth bin and {@code pk.y} is the cumulative distribution function 
     * at that that point: the fraction of observations falling in the
     * kth bin or any bin to its left (with a lesser observation value).
     *
     * @return the cumulative distribution function described by this
     * histogram.
     */
    public JamUnivariateFunction getCDF() {
        if (cdf == null)
            cdf = computeCDF();

        return cdf;
    }

    private JamUnivariateFunction computeCDF() {
        List<Point2D> knots = new ArrayList<Point2D>(bins.size() + 3);

        double x = bins.get(0).getRange().getUpperBound();
        double y = bins.get(0).getFrequency(total);

        knots.add(Point2D.at(x, y));

        for (int k = 1; k < bins.size(); ++k) {
            x = bins.get(k).getRange().getUpperBound();
            y = bins.get(k).getFrequency(total) + knots.get(k - 1).y;

            knots.add(Point2D.at(x, y));
        }

        // Add one knot point with zero probability at the lower bound
        // of the first bin to truncate the distribution, and another
        // knot point with zero probability somewhere to the left so
        // that zero probability is extrapolated to negative infinity.
        double x0 = ListUtil.first(bins).getRange().getLowerBound();
        double w0 = ListUtil.first(bins).getRange().getWidth();

        knots.add(Point2D.at(x0 - w0, 0.0));
        knots.add(Point2D.at(x0,      0.0));

        // Now add one more knot above the last bin so that unit
        // probability is extrapolated to positive infinity...
        double xN = ListUtil.last(bins).getRange().getUpperBound();
        double wN = ListUtil.last(bins).getRange().getWidth();

        knots.add(Point2D.at(xN + wN, 1.0));
        return SplineFunction.linear(knots);
    }

    /**
     * Returns the probability density function described by this
     * histogram.
     *
     * <p>Let {@code L} be the returned list and {@code pk = L.get(k)}
     * be the kth point.  Then {@code pk.x} is the midpoint of the kth
     * bin and {@code pk.y} is the estimated probability density at
     * that point: the fraction of observations falling in the kth bin
     * divided by its width.
     *
     * @return the probability density function described by this
     * histogram.
     */
    public JamUnivariateFunction getPDF() {
        if (pdf == null)
            pdf = computePDF();

        return pdf;
    }

    private JamUnivariateFunction computePDF() {
        List<Point2D> knots = new ArrayList<Point2D>(bins.size() + 4);

        // Let "m0" and "w0" be the midpoint and width of the first
        // bin.  We add one knot point with zero density at (m0 - w0)
        // so that the PDF is normalized under linear interpolation.
        // (A small amount of probability density will "leak out"
        // below the lower bound of the first bin, but this is typical
        // of discrete kernal estimation.)  We then add a second bin
        // with zero density at (m0 - 2 * w0) so that zero probability
        // density is extrapolated to negative infinity.
        double m0 = ListUtil.first(bins).getMidPoint();
        double w0 = ListUtil.first(bins).getRange().getWidth();

        knots.add(Point2D.at(m0 - 2.0 * w0, 0.0));
        knots.add(Point2D.at(m0 - w0,       0.0));

        for (Bin bin : bins) {
            double x = bin.getMidPoint();
            double y = bin.getFrequency(total) / bin.getRange().getWidth();

            knots.add(Point2D.at(x, y));
        }

        // Now repeat the appropriate treatment for the upper boundary...
        double mN = ListUtil.last(bins).getMidPoint();
        double wN = ListUtil.last(bins).getRange().getWidth();

        knots.add(Point2D.at(mN + wN,       0.0));
        knots.add(Point2D.at(mN + 2.0 * wN, 0.0));

        return SplineFunction.linear(knots);
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
