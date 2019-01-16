
package jam.pepmhc;

import java.util.Objects;

/**
 * Provides a unique identifier for peptide-MHC bindind affinity
 * predictors.
 */
public final class PredictorKey {
    private final PredictionMethod predictionMethod;
    private final String alleleCode;
    private final Integer peptideLength;

    /**
     * Creates a new predictor key.
     *
     * @param predictionMethod the underlying prediction method.
     *
     * @param alleleCode the key of the MHC allele for the predictor.
     *
     * @param peptideLength the peptide length for the predictor.
     */
    public PredictorKey(PredictionMethod predictionMethod, String alleleCode, Integer peptideLength) {
        this.predictionMethod = predictionMethod;
        this.alleleCode       = alleleCode;
        this.peptideLength    = peptideLength;
    }

    /**
     * Returns the underlying prediction method.
     *
     * @return the underlying prediction method.
     */
    public PredictionMethod getPredictionMethod() {
        return predictionMethod;
    }

    /**
     * Returns the key of the MHC allele for the predictor.
     *
     * @return the key of the MHC allele for the predictor.
     */
    public String getAlleleCode() {
        return alleleCode;
    }

    /**
     * Returns the peptide length for the predictor.
     *
     * @return the peptide length for the predictor.
     */
    public int getPeptideLength() {
        return peptideLength;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof PredictorKey) && equalsKey((PredictorKey) obj);
    }

    private boolean equalsKey(PredictorKey that) {
        return this.predictionMethod.equals(that.predictionMethod)
            && this.alleleCode.equals(that.alleleCode)
            && this.peptideLength.equals(that.peptideLength);
    }

    @Override public int hashCode() {
        return Objects.hash(predictionMethod, alleleCode, peptideLength);
    }
}
