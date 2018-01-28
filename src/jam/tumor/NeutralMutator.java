
package jam.tumor;

/**
 * Generates neutral mutations at a fixed rate.
 */
public final class NeutralMutator extends HomogeneousMutator {
    /**
     * Creates a neutral mutator with a fixed mutation rate.
     *
     * @param mutationRate the fixed mutation rate.
     */
    public NeutralMutator(MutationRate mutationRate) {
        super(mutationRate);
    }

    @Override protected Mutation generateOne(int stepIndex) {
        return Mutation.neutral(stepIndex);
    }
}
