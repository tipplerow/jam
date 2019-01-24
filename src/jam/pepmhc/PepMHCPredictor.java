
package jam.pepmhc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jam.lang.JamException;
import jam.peptide.Peptide;
import jam.pepmhc.net.NetMHCPan;
import jam.pepmhc.smm.SMMPredictor;
import jam.pepmhc.smm.SMMPmbecPredictor;

public interface PepMHCPredictor {
    /**
     * Returns a predictor for a given prediction method.
     *
     * @param method the prediction method.
     *
     * @return the predictor with the specified prediction method.
     */
    public static PepMHCPredictor instance(PredictionMethod method) {
        switch (method) {
        case NETMHCPAN:
            return NetMHCPan.INSTANCE;

        case SMM:
            return SMMPredictor.INSTANCE;

        case SMM_PMBEC:
            return SMMPmbecPredictor.INSTANCE;

        default:
            throw JamException.runtime("Unknown prediction method: [%s].", method);
        }
    }

    /**
     * Returns the prediction method for this predictor.
     *
     * @return the prediction method for this predictor.
     */
    public abstract PredictionMethod getMethod();

    /**
     * Predicts the binding affinity (as the nanomolar IC50
     * concentration) of a given peptide to an allele.
     *
     * @param allele the code of the MHC allele presenting the
     * peptide.
     *
     * @param peptide the peptide being presented.
     *
     * @return the binding affinity of the specified peptide to
     * the specified allele.
     */
    public abstract double predictIC50(String allele, Peptide peptide);

    /**
     * Predicts the binding affinities (as nanomolar IC50
     * concentrations) for a collection of peptides binding
     * to a single MHC allele.
     *
     * @param allele the code of the MHC allele presenting the
     * peptides.
     *
     * @param peptides the peptides being presented.
     *
     * @return a mapping from each peptide to its binding affinity.
     */
    public default Map<Peptide, Double> predictIC50(String allele, Collection<Peptide> peptides) {
        Map<Peptide, Double> ic50 = new LinkedHashMap<Peptide, Double>(peptides.size());

        for (Peptide peptide : peptides)
            ic50.put(peptide, predictIC50(allele, peptide));

        return ic50;
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
    /*
    public static double minimumIC50(Collection<PepMHCPredictor> predictors, Peptide peptide) {
        double result = Double.POSITIVE_INFINITY;

        for (PepMHCPredictor predictor : predictors)
            result = Math.min(result, predictor.predictIC50(peptide));

        return result;
    }
    */
}
