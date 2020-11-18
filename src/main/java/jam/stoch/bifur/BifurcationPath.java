
package jam.stoch.bifur;

import jam.stoch.StochProc;
import jam.stoch.StochRate;

/**
 * Represents a bifurcation pathway where transitions from a source to
 * a sink occur with a fixed unit rate.
 */
public final class BifurcationPath implements StochProc {
    private final int index;
    private final double unitRate;
    private final BifurcationSink sink;

    private BifurcationPath(int index, BifurcationSink sink, double unitRate) {
        this.index = index;
        this.sink = sink;
        this.unitRate = unitRate;
    }

    /**
     * Creates a new bifurcation pathway with a fixed source and
     * transition rate; a new sink state is also created as the
     * destination state for the pathway.
     *
     * @param index the ordinal index for the pathway.
     *
     * @param source the origination (source) state for the pathway.
     *
     * @param unitRate the unit rate constant for events on the path.
     *
     * @return a new bifurcation pathway with the specified source
     * state and transition rate and a newly created sink state.
     */
    public static BifurcationPath create(int index, BifurcationSource source, double unitRate) {
        return new BifurcationPath(index, BifurcationSink.create(source), unitRate);
    }

    /**
     * Returns the destination state for this pathway.
     *
     * @return the destination state for this pathway.
     */
    public BifurcationSink getSink() {
        return sink;
    }

    /**
     * Returns the origination state for this pathway.
     *
     * @return the origination state for this pathway.
     */
    public BifurcationSource getSource() {
        return sink.getSource();
    }

    /**
     * Returns the transition rate constant for this pathway.
     *
     * @return the transition rate constant for this pathway.
     */
    public double getUnitRate() {
        return unitRate;
    }

    @Override public int getIndex() {
        return index;
    }

    @Override public StochRate getStochRate() {
        return StochRate.valueOf(getSource().getPopulation() * unitRate);
    }
}
