
package jam.pepmhc;

import jam.peptide.Peptide;

public interface PepMHCPredictor {
    /**
     * Returns the key for this predictor.
     *
     * @return the key for this predictor.
     */
    public abstract PredictorKey getKey();

    /**
     * Predicts the binding affinity (as the nanomolar IC50
     * concentration) of a given peptide to the allele.
     *
     * @param peptide the peptide to predict.
     *
     * @return the binding affinity of the specified peptide.
     */
    public abstract double predictIC50(Peptide peptide);
}
