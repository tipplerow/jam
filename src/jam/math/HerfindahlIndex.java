
package jam.math;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import jam.util.ListUtil;
import jam.vector.JamVector;
import jam.vector.VectorAggregator;
import jam.vector.VectorView;

/**
 * Computes the Herfindahl index that quantifies the diversity (or
 * concentration) within a population.
 */
public final class HerfindahlIndex {
    private final int count;
    private final double index;

    private HerfindahlIndex(int count, double index) {
        this.count = count;
        this.index = index;
    }

    /**
     * Computes the Herfindahl index for a data vector.
     *
     * @param data the data to process.
     *
     * @return the Herfindahl index for the input data.
     */
    public static HerfindahlIndex compute(double... data) {
        return compute(VectorView.wrap(data));
    }

    /**
     * Computes the Herfindahl index for a data list.
     *
     * @param data the data to process.
     *
     * @return the Herfindahl index for the input data.
     */
    public static HerfindahlIndex compute(List<Double> data) {
        return compute(VectorView.wrap(data));
    }

    /**
     * Computes the Herfindahl index for a data set.
     *
     * @param <V> the runtime object type.
     *
     * @param objects from which a numerical attribute may be
     * extracted.
     *
     * @param attribute a function to extract the numerical attribute
     * from each object.
     *
     * @return the Herfindahl index for the input data.
     */
    public static <V> HerfindahlIndex compute(Collection<V> objects, Function<V, Double> attribute) {
        return compute(ListUtil.apply(objects, attribute));
    }

    /**
     * Computes the Herfindahl index for a data vector.
     *
     * @param data the data to process.
     *
     * @return the Herfindahl index for the input data.
     */
    public static HerfindahlIndex compute(VectorView data) {
        JamVector frac = JamVector.copyOf(data).normalize();

        int    count = frac.length();
        double index = VectorAggregator.sumsqr(frac);

        return new HerfindahlIndex(count, index);
    }

    /**
     * Returns the number of data items used to create the index.
     *
     * @return the number of data items used to create the index.
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the raw (unnormalized) Herfindahl index.
     *
     * @return the raw (unnormalized) Herfindahl index.
     */
    public double getRaw() {
        return index;
    }

    /**
     * Returns the normalized Herfindahl index.
     *
     * @return the normalized Herfindahl index.
     */
    public double getNormalized() {
        if (count == 1)
            return index;

        double countInv = DoubleUtil.ratio(1, count);
        return (index - countInv) / (1.0 - countInv);
    }
}
