
package jam.pepmhc.smm;

import jam.pepmhc.PepMHCPredictor;
import jam.pepmhc.PredictorKey;
import jam.peptide.Peptide;

public final class SMMPredictor implements PepMHCPredictor {
    private final PredictorKey key;
    private final StabilizedMatrix matrix;

    private SMMPredictor(PredictorKey key, StabilizedMatrix matrix) {
        this.key = key;
        this.matrix = matrix;
    }

    /**
     * Returns the SMM predictor for a given key.
     *
     * @param key the key of the desired predictor.
     *
     * @return the SMM predictor for the specified key.
     */
    public static SMMPredictor instance(PredictorKey key) {
        return new SMMPredictor(key, StabilizedMatrix.instance(key));
    }

    @Override public PredictorKey getKey() {
        return key;
    }

    @Override public double predictIC50(Peptide peptide) {
        return matrix.computeIC50(peptide);
    }
}
