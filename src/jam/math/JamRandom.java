
package jam.math;

import java.io.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.random.Well44497b;

import jam.app.JamLogger;
import jam.app.JamProperties;

/**
 * Provides a generic interface for random number generation and a
 * default implementation more suitable for Monte Carlo simulation
 * than the built-in {@code java.util.Random}.
 *
 * <p>The current default implementation is the WELL44497b generator
 * from Panneton, L'Ecuyer, and Matsumoto: Improved Long-Period
 * Generators Based on Linear Recurrences Modulo 2, ACM Transactions
 * on Mathematical Software (TOMS), Volume 32 Issue 1, March 2006,
 * Pages 1-16.
 */
public abstract class JamRandom {
    private static JamRandom global = null;
    
    // Source for nearly unique seeds...
    private static final File DEV_URANDOM = new File("/dev/urandom");

    /**
     * Name of the system property specifying the seed for the global
     * instance.
     */
    public static final String SEED_PROPERTY = "jam.math.JamRandom.seed";

    /**
     * Creates a new generator with a seed that is unlikely to be
     * repeated.
     *
     * @return a new generator (independent of the global instance).
     */
    public static JamRandom generator() {
	return generator(randomSeed());
    }
    
    /**
     * Creates a new generator with a specified seed.
     *
     * @param seed the new seed to set.
     *
     * @return a new generator (independent of the global instance).
     */
    public static JamRandom generator(long seed) {
        return new JamWell44497b(seed);
    }

    /**
     * Returns the global shared instance.
     *
     * <p>If the global instance has not been accessed previously,
     * this method creates the global instance with a seed given by
     * the system property named {@code jam.math.JamRandom.seed}.
     *
     * @return the global shared instance.
     *
     * @throws IllegalStateException unless the instance has been
     * initialized.
     */
    public static JamRandom global() {
        if (global == null)
            global = global(globalSeed());

        return global;
    }

    private static int globalSeed() {
        return JamProperties.getRequiredInt(SEED_PROPERTY);
    }

    /**
     * Initializes or resets the global shared instance.
     *
     * @param seed the global seed.
     *
     * @return the global shared instance.
     */
    public static JamRandom global(long seed) {
        global = new JamWell44497b(seed);
        return global;
    }

    /**
     * Generates a seed that is likely to be unique for each call.
     *
     * <p>This implementation reads bits from {@code /dev/urandom}
     * if it exists (e.g., on Linux and OSX); otherwise it returns
     * the value of {@code System.nanoTime()}.
     *
     * <p>Please use this method <em>only</em> for an initial seed
     * (or to reseed another generator); it is not suitable on its
     * own for long random sequences.
     *
     * @return a suitable seed value.
     */
    public static long randomSeed() {
	long result = System.nanoTime();

	if (DEV_URANDOM.canRead()) {
	    DataInputStream stream = null;
	    
	    try {
		stream = new DataInputStream(new FileInputStream(DEV_URANDOM));
		result = stream.readLong();
	    }
	    catch (IOException ioex) {
		JamLogger.warn("Failed to read [%s]; using system time.", DEV_URANDOM);
	    }
	    finally {
		IOUtils.closeQuietly(stream);
	    }
	}

	return result;
    }

    /**
     * Accepts a trial event with a specified probability.
     *
     * @param acceptanceProb the probability that the trial will be
     * accepted.
     *
     * @return the randomly generated acceptance decision.
     */
    public boolean accept(double acceptanceProb) {
        return nextDouble() < acceptanceProb;
    }
    
    /**
     * Randomly selects one of the two nearest integers to a
     * floating-point value.
     *
     * <p>Let {@code k} be the integer {@code k <= x < k + 1}.  This
     * method selects {@code k} with probability {@code 1 + k - x} and
     * {@code k + 1} with probability {@code x - k}.
     *
     * @param x the floating-point value to discretize.
     *
     * @return the discretized value.
     */
    public int discretize(double x) {
        int k = (int) Math.floor(x);

        if (accept(x - k))
            return k + 1;
        else
            return k;
    }

    /**
     * Returns the next uniformly distributed {@code boolean} value.
     *
     * @return the next uniformly distributed {@code boolean} value.
     */
    public abstract boolean nextBoolean();

    /**
     * Returns the next uniformly distributed {@code double} value
     * between {@code 0.0} and {@code 1.0}.
     *
     * @return the next uniformly distributed {@code double} value
     * between {@code 0.0} and {@code 1.0}.
     */
    public abstract double nextDouble();

    /**
     * Returns the next uniformly distributed {@code double} value
     * between specified bounds.
     *
     * @param lower lower bound of the uniform distribution.
     * @param upper upper bound of the uniform distribution.
     *
     * @return the next uniformly distributed {@code double} value
     * on the interval {@code (lower, upper)}.
     *
     * @throws IllegalArgumentException if {@code lower >= upper}.
     */
    public double nextDouble(double lower, double upper) {
        if (lower >= upper)
            throw new IllegalArgumentException("Invalid range.");

        return lower + (upper - lower) * nextDouble();
    }

    /**
     * Returns the next uniformly distributed {@code int} value
     * between {@code Integer.MIN_VALUE} and {@code Integer.MAX_VALUE}.
     *
     * @return the next uniformly distributed {@code int} value
     * between {@code Integer.MIN_VALUE} and {@code Integer.MAX_VALUE}.
     */
    public abstract int nextInt();

    /**
     * Returns the next uniformly distributed {@code int} value in the
     * half-open interval {@code [0, upper)}.
     *
     * @param upper upper bound (exclusive) on the value returned.
     *
     * @return the next uniformly distributed {@code int} value in the
     * half-open interval {@code [0, upper)}.
     */
    public abstract int nextInt(int upper);

    /**
     * Returns the next uniformly distributed {@code int} value in the
     * half-open interval {@code [lower, upper)}.
     *
     * @param lower lower bound (inclusive) on the value returned.
     *
     * @param upper upper bound (exclusive) on the value returned.
     *
     * @return the next uniformly distributed {@code int} value in the
     * half-open interval {@code [lower, upper)}.
     *
     * @throws IllegalArgumentException if the upper bound is less
     * than the lower bound.
     */
    public int nextInt(int lower, int upper) {
        if (upper < lower)
            throw new IllegalArgumentException("Invalid interval.");

        return lower + nextInt(upper - lower);
    }

    /**
     * Returns the next value from a normal distribution with zero
     * mean and unit variance.
     *
     * @return the next value from a normal distribution with zero
     * mean and unit variance.
     */
    public abstract double nextGaussian();

    /**
     * Returns the next value from a normal distribution with
     * specified mean and standard deviation.
     *
     * @param mean mean of the distribution to draw from.
     * @param stdev standard deviation of the distribution to draw from.
     *
     * @return the next value from a normal distribution with
     * specified mean and standard deviation.
     */
    public double nextGaussian(double mean, double stdev) {
        return mean + stdev * nextGaussian();
    }

    /**
     * Selects one event from a set of mutually exclusive events.
     *
     * <p>In the interest of efficiency, the distribution function is
     * not validated.
     *
     * @param eventCDF the cumulative probability distribution for the
     * set of events: {@code CDF[k]} is the probability that any event
     * in the range {@code [0, 1, ..., k]} occurs.
     *
     * @return the zero-based index of the randomly selected event.
     */
    public int selectCDF(double[] eventCDF) {
        int    eventIndex = 0;
        double randomDraw = nextDouble();

        while (randomDraw >= eventCDF[eventIndex])
            eventIndex++;

        return eventIndex;
    }

    /**
     * Selects one event from a set of mutually exclusive events.
     *
     * <p>In the interest of efficiency, the event probabilities are
     * not validated.
     *
     * @param eventPDF the probability of each event.
     *
     * @return the zero-based index of the randomly selected event.
     */
    public int selectPDF(double[] eventPDF) {
        int    eventIndex = 0;
        double eventTotal = eventPDF[0];
        double randomDraw = nextDouble();

        while (randomDraw >= eventTotal) {
            eventIndex++;
            eventTotal += eventPDF[eventIndex];
        }

        return eventIndex;
    }

    /**
     * Resets the seed of the generator.
     *
     * @param seed the new seed to set.
     */
    public abstract void setSeed(long seed);
}
