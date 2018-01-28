
package jam.dist;

import jam.vector.JamVector;

/**
 * Enumerates real probability distributions and provides factory
 * methods for them.
 */
public enum RealDistributionType {
    DIRAC_DELTA {
        /**
         * Creates a new Dirac delta distribution.
         *
         * @param param the location of the impulse.
         *
         * @return the new Dirac delta distribution.
         *
         * @throws IllegalArgumentException unless the parameter
         * vector has length one.
         */
        @Override public RealDistribution create(double... param) {
            if (param.length != 1)
                throw new IllegalArgumentException("Invalid parameter set.");

            return new DiracDeltaDistribution(param[0]);
        }
    },

    FRECHET {
        /**
         * Creates a new Frechet distribution.
         *
         * @param param the location, scale, and shape parameters in
         * that order.
         *
         * @return the new Frechet distribution.
         *
         * @throws IllegalArgumentException unless the parameter
         * vector has length three.
         */
        @Override public RealDistribution create(double... param) {
            if (param.length != 3)
                throw new IllegalArgumentException("Invalid parameter set.");

            return new FrechetDistribution(param[0], param[1], param[2]);
        }
    },

    GEV {
        /**
         * Creates a new generalized extreme value distribution.
         *
         * @param param the location, scale, and shape parameters in
         * that order.
         *
         * @return the new generalized extreme value distribution.
         *
         * @throws IllegalArgumentException unless the parameter
         * vector has length three.
         */
        @Override public RealDistribution create(double... param) {
            if (param.length != 3)
                throw new IllegalArgumentException("Invalid parameter set.");

            return GEVDistribution.instance(param[0], param[1], param[2]);
        }
    },

    GUMBEL {
        /**
         * Creates a new Gumbel distribution.
         *
         * @param param the location and scale parameters in that
         * order.
         *
         * @return the new Gumbel distribution.
         *
         * @throws IllegalArgumentException unless the parameter
         * vector has length two.
         */
        @Override public RealDistribution create(double... param) {
            if (param.length != 2)
                throw new IllegalArgumentException("Invalid parameter set.");

            return new GumbelDistribution(param[0], param[1]);
        }
    },

    LOG_NORMAL {
        /**
         * Creates a new log-normal distribution.
         *
         * @param param the mean and standard deviation of the
         * logarithm of the distribution, in that order.
         *
         * @return the new log-normal distribution.
         *
         * @throws IllegalArgumentException unless the parameters
         * define a valid log-normal distribution.
         */
        @Override public RealDistribution create(double... param) {
            if (param.length != 2)
                throw new IllegalArgumentException("Invalid parameter set.");

            return new LogNormalDistribution(param[0], param[1]);
        }
    },

    LOG_UNIFORM {
        /**
         * Creates a new log-uniform distribution.
         *
         * @param param the logarithmic lower and upper bounds for the
         * distribution, in that order.
         *
         * @return the new log-uniform distribution.
         *
         * @throws IllegalArgumentException unless the parameters
         * define a valid log-uniform distribution.
         */
        @Override public RealDistribution create(double... param) {
            if (param.length != 2)
                throw new IllegalArgumentException("Invalid parameter set.");

            return new LogUniformDistribution(param[0], param[1]);
        }
    },

    NORMAL {
        /**
         * Creates a new normal distribution.
         *
         * @param param the mean and standard deviation of the
         * distribution, in that order.
         *
         * @return the new normal distribution.
         *
         * @throws IllegalArgumentException unless the parameters
         * define a valid normal distribution.
         */
        @Override public RealDistribution create(double... param) {
            if (param.length != 2)
                throw new IllegalArgumentException("Invalid parameter set.");

            return new NormalDistribution(param[0], param[1]);
        }
    },

    REVERSE_WEIBULL {
        /**
         * Creates a new reverse Weibull distribution.
         *
         * @param param the location, scale, and shape parameters in
         * that order.
         *
         * @return the new reverse Weibull distribution.
         *
         * @throws IllegalArgumentException unless the parameter
         * vector has length three.
         */
        @Override public RealDistribution create(double... param) {
            if (param.length != 3)
                throw new IllegalArgumentException("Invalid parameter set.");

            return new ReverseWeibullDistribution(param[0], param[1], param[2]);
        }
    },

    UNIFORM {
        /**
         * Creates a new uniform real distribution.
         *
         * @param param the lower and upper bounds for the
         * distribution, in that order.
         *
         * @return the new uniform distribution.
         *
         * @throws IllegalArgumentException unless the parameters
         * define a valid uniform distribution.
         */
        @Override public RealDistribution create(double... param) {
            if (param.length != 2)
                throw new IllegalArgumentException("Invalid parameter set.");

            return new UniformRealDistribution(param[0], param[1]);
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
    public abstract RealDistribution create(double... param);

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
    public RealDistribution create(String param) {
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
    public static RealDistribution parse(String def) {
        String[] fields = def.split(";");

        if (fields.length != 2)
            throw new IllegalArgumentException(String.format("Invalid format: [%s].", def));

        String typeField  = fields[0].trim();
        String paramField = fields[1].trim();

        return valueOf(typeField).create(paramField);
    }
}
