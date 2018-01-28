
package jam.tumor;

/**
 * Generates mutations in a carrier.
 */
public interface Mutator {
    /**
     * Stochastically generates the mutations that originate in a
     * single daughter carrier produced during cell division.
     *
     * <p>Note that the list will frequently be empty because
     * mutations are typically rare.
     *
     * @param timeStep the index of the current time step.
     *
     * @return the mutations that originated in the daughter carrier
     * (or an empty list if no mutations occurred).
     */
    public abstract MutationList generate(int timeStep);

    /**
     * A mutator that always returns an empty mutation list.
     */
    public static final Mutator EMPTY = EmptyMutator.INSTANCE;

    /**
     * Creates a neutral mutator with a fixed mutation rate.
     *
     * @param mutationRate the fixed mutation rate.
     *
     * @return the neutral mutator.
     */
    public static Mutator neutral(MutationRate mutationRate) {
        return new NeutralMutator(mutationRate);
    }
}

final class EmptyMutator implements Mutator {
    private EmptyMutator() {
    }

    static final Mutator INSTANCE = new EmptyMutator();

    @Override public MutationList generate(int timeStep) {
        return MutationList.EMPTY;
    }
}
