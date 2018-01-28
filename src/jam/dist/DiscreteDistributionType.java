
package jam.dist;

import jam.math.Probability;
import jam.vector.JamVector;

/**
 * Enumerates discrete probability distributions and provides factory
 * methods for them.
 */
public enum DiscreteDistributionType {
    BINOMIAL {
        /**
         * Creates a new binomial distribution.
         *
         * @param param (1) the number of trials and (2) the
         * probability that a single trial succeeds.
         *
         * @return the new binomial distribution.
         *
         * @throws IllegalArgumentException unless the parameter
         * vector has length two and contains a valid number of trials
         * and success probability.
         */
        @Override public DiscreteDistribution create(double... param) {
            if (param.length != 2)
                throw new IllegalArgumentException("Invalid parameter set.");

            return BinomialDistribution.create((int) Math.round(param[0]), Probability.valueOf(param[1]));
        }
    },

    OCCURRENCE {
        /**
         * Creates a new occurrence distribution.
         *
         * @param param (1) the probability that an event occurs on a
         * single trial, and (2) the number of trials.
         *
         * @return the new occurrence distribution.
         *
         * @throws IllegalArgumentException unless the parameter
         * vector has length two and contains a valid event
         * probability and non-negative number of trials.
         */
        @Override public DiscreteDistribution create(double... param) {
            if (param.length != 2)
                throw new IllegalArgumentException("Invalid parameter set.");

            return new OccurrenceDistribution(Probability.valueOf(param[0]), (int) Math.round(param[1]));
        }
    },

    POISSON {
        /**
         * Creates a new Poisson distribution.
         *
         * @param param the mean value.
         *
         * @return the new Poisson distribution.
         *
         * @throws IllegalArgumentException unless the parameter
         * vector has length one and contains a positive mean value.
         */
        @Override public DiscreteDistribution create(double... param) {
            if (param.length != 1)
                throw new IllegalArgumentException("Invalid parameter set.");

            return PoissonDistribution.create(param[0]);
        }
    };

    /**
     * Creates a probability distribution of this type.
     *
     * @param param the parameters required by the constructor for
     * this type of distribution.
     *
     * @return the new distribution instance.
     *
     * @throws IllegalArgumentException unless the parameters are
     * valid for this distribution type.
     */
    public abstract DiscreteDistribution create(double... param);

    /**
     * Creates a probability distribution of this type.
     *
     * @param param parameters required by the constructor for this
     * type of distribution, formatted as a comma-delimited string.
     *
     * @return the new distribution instance.
     *
     * @throws IllegalArgumentException unless the parameters are
     * valid for this distribution type.
     */
    public DiscreteDistribution create(String param) {
        return create(JamVector.parseCSV(param).toNumeric());
    }

    /**
     * Parses a single string that defines a unique probability
     * distribution.
     *
     * @param def a string defining a probability distribution, given
     * in the format: {@code TYPE; param1, param2, ...}, where {@code
     * TYPE} is the enumerated type code and {@code param1, param2,
     * ...} are the comma-separated parameters required to define a
     * distribution of the specified type.
     *
     * @return the probability distribution defined by the input
     * string.
     *
     * @throws IllegalArgumentException unless the input string
     * defines a valid probability distribution.
     */
    public static DiscreteDistribution parse(String def) {
        String[] fields = def.split(";");

        if (fields.length != 2)
            throw new IllegalArgumentException(String.format("Invalid format: [%s].", def));

        String typeField  = fields[0].trim();
        String paramField = fields[1].trim();

        return valueOf(typeField).create(paramField);
    }
}
