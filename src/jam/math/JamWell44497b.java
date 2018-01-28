
package jam.math;

import org.apache.commons.math3.random.Well44497b;

final class JamWell44497b extends JamRandom {
    private final Well44497b random;

    // Number of iterations for the "warmup" phase...
    private static final int WARMUP_PERIOD = 100000;

    JamWell44497b(long seed) {
        random = new Well44497b(seed);
        warmup();
    }

    private void warmup() {
	for (int k = 0; k < WARMUP_PERIOD; k++)
	    nextInt();
    }

    @Override public boolean nextBoolean() {
        return random.nextBoolean();
    }

    @Override public double nextDouble() {
        return random.nextDouble();
    }

    @Override public int nextInt() {
        return random.nextInt();
    }

    @Override public int nextInt(int upper) {
        return random.nextInt(upper);
    }

    @Override public double nextGaussian() {
        return random.nextGaussian();
    }

    @Override public void setSeed(long seed) {
        random.setSeed(seed);
    }
}
