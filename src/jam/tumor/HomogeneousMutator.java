
package jam.tumor;

/**
 * Generates homogeneous mutations (of a single type) at a fixed rate.
 */
public abstract class HomogeneousMutator implements Mutator {
    private final MutationRate mutationRate;

    /**
     * Creates a homogeneous mutator that will generate mutations at a
     * fixed rate.
     *
     * @param mutationRate the fixed mutation rate.
     */
    protected HomogeneousMutator(MutationRate mutationRate) {
        this.mutationRate = mutationRate;
    }

    /**
     * Generates a single mutation of the type appropriate for this
     * generator.
     *
     * @param timeStep the index of the current time step.
     *
     * @return the new mutation.
     */
    protected abstract Mutation generateOne(int timeStep);

    /**
     * Returns the fixed mutation rate for this generator.
     *
     * @return the fixed mutation rate.
     */
    public final MutationRate getRate() {
        return mutationRate;
    }

    @Override public MutationList generate(int timeStep) {
        int mutationCount = mutationRate.sample();

        switch (mutationCount) {
            //
            // For maximum efficiency, explicitly enumerate the most
            // common mutation counts...
            //
        case 0:
            return MutationList.EMPTY;

        case 1:
            return MutationList.create(generateOne(timeStep));

        case 2:
            return MutationList.create(generateOne(timeStep), generateOne(timeStep));

        default:
            return mutate(mutationCount, timeStep);
        }
    }

    private MutationList mutate(int mutationCount, int timeStep) {
        Mutation[] mutations = new Mutation[mutationCount];

        for (int index = 0; index < mutationCount; ++index)
            mutations[index] = generateOne(timeStep);

        return MutationList.create(mutations);
    }
}
