
package jam.math;

import java.util.Collection;
import java.util.function.Function;

import com.google.common.collect.Multiset;

import jam.util.ListUtil;
import jam.util.RegexUtil;
import jam.vector.VectorUtil;
import jam.vector.VectorView;

/**
 * Computes and stores summary statistics for univariate data.
 */
public final class StatSummary {
    private final int size;

    private final double min;
    private final double max;

    private final double mean;
    private final double SD;

    private final double median;
    private final double MAD;

    private final double quartile1;
    private final double quartile3;

    private StatSummary(int size,
                        double min,
                        double max,
                        double mean,
                        double SD,
                        double median,
                        double MAD,
                        double quartile1,
                        double quartile3) {
        this.size = size;

        this.min = min;
        this.max = max;

        this.mean = mean;
        this.SD   = SD;

        this.median = median;
        this.MAD    = MAD;

        this.quartile1 = quartile1;
        this.quartile3 = quartile3;
    }

    /**
     * Creates univariate summary statistics for a given data set.
     *
     * @param values the univariate sample data.
     *
     * @return the new summary object.
     *
     * @throws IllegalArgumentException unless there is at least one
     * data value.
     */
    public static StatSummary compute(double... values) {
        return compute(VectorView.wrap(values));
    }

    /**
     * Creates univariate summary statistics for a given data set.
     *
     * @param values the univariate sample data.
     *
     * @return the new summary object.
     *
     * @throws IllegalArgumentException unless there is at least one
     * data value.
     */
    public static StatSummary compute(Collection<Double> values) {
        return compute(VectorUtil.toArray(values));
    }

    /**
     * Creates univariate summary statistics for a given data set.
     *
     * @param <V> the runtime type of the data objects.
     *
     * @param objects the objects from which a numerical attribute can
     * be extracted.
     *
     * @param attribute a function to extract the numerical attribute
     * from each object.
     *
     * @return the new summary object.
     *
     * @throws IllegalArgumentException unless there is at least one
     * data object.
     */
    public static <V> StatSummary compute(Collection<V> objects, Function<V, Double> attribute) {
        return compute(ListUtil.apply(objects, attribute));
    }

    /**
     * Creates univariate summary statistics for a given data set.
     *
     * @param values the univariate sample data.
     *
     * @return the new summary object.
     *
     * @throws IllegalArgumentException unless there is at least one
     * data value.
     */
    public static StatSummary compute(Multiset<Integer> values) {
        return compute(values, k -> Double.valueOf(k));
    }

    /**
     * Creates univariate summary statistics for a given data set.
     *
     * @param values the univariate sample data.
     *
     * @return the new summary object.
     *
     * @throws IllegalArgumentException unless there is at least one
     * data value.
     */
    public static StatSummary compute(VectorView values) {
        if (values.length() < 1)
            throw new IllegalArgumentException("At least one value required.");

        Quantile quantile = new Quantile(values);

        int    size      = values.length();
        double min       = StatUtil.min(values);
        double max       = StatUtil.max(values);
        double mean      = StatUtil.mean(values);
        double SD        = StatUtil.stdev(values);
        double median    = quantile.evaluate(0.5);
        double MAD       = StatUtil.MAD(median, values);
        double quartile1 = quantile.evaluate(0.25);
        double quartile3 = quantile.evaluate(0.75);

        return new StatSummary(size, min, max, mean, SD, median, MAD, quartile1, quartile3);
    }

    /**
     * Formats this summary into a comma-delimited string.
     *
     * @return a comma-delimited string containing the items in this
     * summary.
     */
    public String format() {
        StringBuilder builder = new StringBuilder();

        builder.append(size);
        builder.append(",");
        builder.append(min);
        builder.append(",");
        builder.append(max);
        builder.append(",");
        builder.append(mean);
        builder.append(",");
        builder.append(SD);
        builder.append(",");
        builder.append(median);
        builder.append(",");
        builder.append(MAD);
        builder.append(",");
        builder.append(quartile1);
        builder.append(",");
        builder.append(quartile3);

        return builder.toString();
    }

    /**
     * Returns a string to be used as the header line in
     * comma-delimited files.
     *
     * @return a string to be used as the header line in
     * comma-delimited files.
     */
    public static String header() {
        StringBuilder builder = new StringBuilder();

        builder.append("size");
        builder.append(",");
        builder.append("min");
        builder.append(",");
        builder.append("max");
        builder.append(",");
        builder.append("mean");
        builder.append(",");
        builder.append("SD");
        builder.append(",");
        builder.append("median");
        builder.append(",");
        builder.append("MAD");
        builder.append(",");
        builder.append("quartile1");
        builder.append(",");
        builder.append("quartile3");

        return builder.toString();
    }

    /**
     * Parses a comma-delimited string containing the items in a
     * summary object.
     *
     * @param s the comma-delimited string.
     *
     * @return a summary object with items assigned from the input
     * string.
     *
     * @throws IllegalArgumentException unless the string is properly
     * formatted.
     */
    public static StatSummary parse(String s) {
        String[] fields = RegexUtil.COMMA.split(s);

        if (fields.length != 9)
            throw new IllegalArgumentException(String.format("Invalid string format: [%s]", s));

        int    size      = Integer.parseInt(fields[0]);
        double min       = Double.parseDouble(fields[1]);
        double max       = Double.parseDouble(fields[2]);
        double mean      = Double.parseDouble(fields[3]);
        double SD        = Double.parseDouble(fields[4]);
        double median    = Double.parseDouble(fields[5]);
        double MAD       = Double.parseDouble(fields[6]);
        double quartile1 = Double.parseDouble(fields[7]);
        double quartile3 = Double.parseDouble(fields[8]);

        return new StatSummary(size, min, max, mean, SD, median, MAD, quartile1, quartile3);
    }

    /**
     * Returns the sample size.
     *
     * @return the sample size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the minimum value.
     *
     * @return the minimum value.
     */
    public double getMin() {
        return min;
    }

    /**
     * Returns the maximum value.
     *
     * @return the maximum value.
     */
    public double getMax() {
        return max;
    }

    /**
     * Returns the mean value.
     *
     * @return the mean value.
     */
    public double getMean() {
        return mean;
    }

    /**
     * Returns the standard deviation.
     *
     * @return the standard deviation.
     */
    public double getSD() {
        return SD;
    }

    /**
     * Returns the variance.
     *
     * @return the variance.
     */
    public double getVariance() {
        return SD * SD;
    }

    /**
     * Returns an estimate of the standard error of the mean.
     *
     * @return an estimate of the standard error of the mean.
     */
    public double getError() {
        return getSD() / Math.sqrt(getSize());
    }

    /**
     * Returns the median value.
     *
     * @return the median value.
     */
    public double getMedian() {
        return median;
    }

    /**
     * Returns the median absolute deviation.
     *
     * @return the median absolute deviation.
     */
    public double getMAD() {
        return MAD;
    }

    /**
     * Returns the first quartile value.
     *
     * @return the first quartile value.
     */
    public double getQuartile1() {
        return quartile1;
    }

    /**
     * Returns the third quartile value.
     *
     * @return the third quartile value.
     */
    public double getQuartile3() {
        return quartile3;
    }

    /**
     * Returns the inter-quartile range.
     *
     * @return the inter-quartile range.
     */
    public double getInterQuartileRange() {
        return quartile3 - quartile1;
    }

    @Override public String toString() {
        String fmtstr = "%s %12.8f\n";
        StringBuilder builder = new StringBuilder();

        builder.append(String.format(fmtstr, "   Min: ", getMin()));
        builder.append(String.format(fmtstr, "    Q1: ", getQuartile1()));
        builder.append(String.format(fmtstr, "Median: ", getMedian()));
        builder.append(String.format(fmtstr, "  Mean: ", getMean()));
        builder.append(String.format(fmtstr, "    Q3: ", getQuartile3()));
        builder.append(String.format(fmtstr, "   Max: ", getMax()));
        builder.append("\n");
        builder.append(String.format(fmtstr, "    SD: ", getSD()));
        builder.append(String.format(fmtstr, "   MAD: ", getMAD()));
        builder.append(String.format(fmtstr, "   IQR: ", getInterQuartileRange()));

        return builder.toString();
    }
}
