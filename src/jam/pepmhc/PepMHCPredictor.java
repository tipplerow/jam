
package jam.pepmhc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    /**
     * Returns a predictor for a given key.
     *
     * @param key the predictor key.
     *
     * @return the predictor with the specified key.
     *
     * @throws RuntimeException unless the specified key defines a
     * valid predictor.
     */
    public static PepMHCPredictor instance(PredictorKey key) {
        return PredictorCache.INSTANCE.get(key);
    }

    /**
     * Returns a predictor for a given method, allele, and peptide
     * length.
     *
     * @param predictionMethod the underlying prediction method.
     *
     * @param alleleCode the key of the MHC allele for the predictor.
     *
     * @param peptideLength the peptide length for the predictor.
     *
     * @return the predictor for the specified method, allele, and
     * peptide length.
     *
     * @throws RuntimeException unless the specified arguments define
     * a valid predictor.
     */
    public static PepMHCPredictor instance(PredictionMethod predictionMethod,
                                           String           alleleCode,
                                           int              peptideLength) {
        return instance(new PredictorKey(predictionMethod, alleleCode, peptideLength));
    }

    /**
     * Returns a predictor for a given method, allele, and peptide
     * length.
     *
     * @param args an array of command-line arguments that specify the
     * prediction method, allele code, and peptide length.
     *
     * @return the predictor for the specified method, allele, and
     * peptide length.
     *
     * @throws RuntimeException unless the specified arguments define
     * a valid predictor.
     */
    public static PepMHCPredictor instance(String... args) {
        if (args.length != 3)
            throw new IllegalArgumentException("Exactly three arguments are required.");

        PredictionMethod method = PredictionMethod.valueOf(args[0]);
        String           allele = args[1].toUpperCase();
        int              length = Integer.parseInt(args[2]);

        return instance(method, allele, length);
    }

    /**
     * Returns a list of predictors for a given method, peptide
     * length, and collection of alleles.
     *
     * @param predictionMethod the underlying prediction method.
     *
     * @param alleleCodes the keys of the MHC alleles for the
     * predictors.
     *
     * @param peptideLength the peptide length for the predictor.
     *
     * @return the predictor for the specified method, allele, and
     * peptide length.
     *
     * @throws RuntimeException unless the specified arguments define
     * valid predictors.
     */
    public static List<PepMHCPredictor> instances(PredictionMethod   predictionMethod,
                                                  Collection<String> alleleCodes,
                                                  int                peptideLength) {
        List<PepMHCPredictor> predictors =
            new ArrayList<PepMHCPredictor>(alleleCodes.size());

        for (String alleleCode : alleleCodes)
            predictors.add(instance(predictionMethod, alleleCode, peptideLength));

        return predictors;
    }

    /**
     * Computes the maximum binding affinity (the minimum nanomolar
     * IC50 concentration) of a given peptide over a collection of
     * predictors.
     *
     * @param predictors the affinity predictors.
     *
     * @param peptide the peptide to predict.
     *
     * @return the minimum IC50 of the specified peptide over the
     * given predictors.
     */
    public static double minimumIC50(Collection<PepMHCPredictor> predictors, Peptide peptide) {
        double result = Double.POSITIVE_INFINITY;

        for (PepMHCPredictor predictor : predictors)
            result = Math.min(result, predictor.predictIC50(peptide));

        return result;
    }
}
