
package jam.peptide;

import java.util.Collection;

public interface PeptideBinder {
    public abstract double computeFreeEnergy(Peptide target);

    public default double computeAffinity(double freeEnergy) {
        return getActivationEnergy() - freeEnergy;
    }

    public default double computeAffinity(Peptide target) {
        return computeAffinity(computeFreeEnergy(target));
    }

    public default double computeMeanAffinity(Collection<Peptide> targets) {
        double total = 0.0;

        for (Peptide target : targets)
            total += computeAffinity(target);

        return total / targets.size();
    }        

    public default double getActivationEnergy() {
        return 0.0;
    }
}
