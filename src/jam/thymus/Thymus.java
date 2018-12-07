
package jam.thymus;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jam.app.JamLogger;
import jam.bio.Peptide;
import jam.bio.TCR;
import jam.lang.ObjectFactory;
import jam.math.DoubleRange;
import jam.math.DoubleUtil;

/**
 * Executes the positive and negative selection of T cell receptors
 * and maintains statistics necessary to compute selection rates.
 */
public abstract class Thymus<V extends TCR> {
    // Processed receptors and their fates...
    private Map<ThymicOutcome, Set<V>> receptors;

    // Realized TCR selection rates...
    private double positivePassRate;
    private double negativePassRate;
    private double netSelectionRate;

    private static final int LOG_INTERVAL = 1000;
    private static final int VALIDATION_INTERVAL = 10000;

    /**
     * Returns the creator of pre-selection ("double negative") T cells.
     *
     * @return the creator of pre-selection ("double negative") T cells.
     */
    public abstract ObjectFactory<V> getReceptorFactory();

    /**
     * Returns the MHC-restricted collection of self-peptides present
     * in the thymic cortex.
     *
     * @return the MHC-restricted collection of self-peptides present
     * in the thymic cortex.
     */
    public abstract Collection<Peptide> getCortexPeptides();

    /**
     * Returns the MHC-restricted collection of self-peptides present
     * in the thymic medulla.
     *
     * @return the MHC-restricted collection of self-peptides present
     * in the thymic medulla.
     */
    public abstract Collection<Peptide> getMedullaPeptides();

    /**
     * Returns the acceptable range of positive selection rates.
     *
     * @return the acceptable range of positive selection rates.
     */
    public abstract DoubleRange getPositivePassRange();

    /**
     * Returns the acceptable range of negative selection rates.
     *
     * @return the acceptable range of negative selection rates.
     */
    public abstract DoubleRange getNegativePassRange();
                          
    /**
     * Returns the acceptable range of net selection rates.
     *
     * @return the acceptable range of net selection rates.
     */
    public abstract DoubleRange getNetSelectionRange();

    /**
     * Returns the number of naive T cells to export from the thymus.
     *
     * @return the number of naive T cells to export from the thymus.
     */
    public abstract int getRepertoireSize();

    /**
     * Returns the total number of receptors processed.
     *
     * @return the total number of receptors processed.
     */
    public int countReceptors() {
        int count = 0;

        for (ThymicOutcome outcome : ThymicOutcome.values())
            count += countReceptors(outcome);

        return count;
    }

    /**
     * Returns the number of receptors with a given outcome.
     *
     * @param outcome the outcome of interest.
     *
     * @return the number of receptors with the specified outcome.
     */
    public int countReceptors(ThymicOutcome outcome) {
        return receptors.get(outcome).size();
    }

    /**
     * Returns the fraction of trial receptors that passed at least
     * positive selection phase.
     *
     * @return the fraction of trial receptors that passed at least
     * positive selection phase.
     */
    public double getPositivePassRate() {
        return positivePassRate;
    }

    /**
     * Returns the fraction of trial receptors that passed the
     * negative selection phase <em>after passing the positive
     * phase</em>.
     *
     * @return the fraction of trial receptors that passed the
     * negative selection phase <em>after passing the positive
     * phase</em>.
     */
    public double getNegativePassRate() {
        return negativePassRate;
    }

    /**
     * Returns the overall selection rate.
     *
     * @return the overall selection rate.
     */
    public double getNetSelectionRate() {
        return netSelectionRate;
    }

    /**
     * Returns a read-only view of the receptors with a given outcome.
     *
     * @param outcome the outcome of interest.
     *
     * @return a read-only view of the receptors with the specified
     * outcome.
     */
    public Set<V> viewReceptors(ThymicOutcome outcome) {
        return Collections.unmodifiableSet(receptors.get(outcome));
    }

    /**
     * Simulates the thymic selection.
     *
     * @throws IllegalStateException unless the selection rates are
     * within the allowed ranges.
     */
    public void select() {
        initialize();

        while (continueSelection())
            processOne();

        logStatus();
        validateRates();
    }

    private void initialize() {
        receptors = new EnumMap<ThymicOutcome, Set<V>>(ThymicOutcome.class);

        for (ThymicOutcome outcome : ThymicOutcome.values())
            receptors.put(outcome, new LinkedHashSet<V>());

        positivePassRate = Double.NaN;
        negativePassRate = Double.NaN;
        netSelectionRate = Double.NaN;
    }

    private boolean continueSelection() {
        return countReceptors(ThymicOutcome.EXPORTED) < getRepertoireSize();
    }

    private void processOne() {
        V receptor = getReceptorFactory().newInstance();
        ThymicOutcome outcome = classify(receptor);

        receptors.get(outcome).add(receptor);

        int attemptCount  = countReceptors();
        int posPassCount  = attemptCount - countReceptors(ThymicOutcome.FAILED_POSITIVE);
        int negPassCount  = posPassCount - countReceptors(ThymicOutcome.FAILED_NEGATIVE);
        int exportedCount = countReceptors(ThymicOutcome.EXPORTED);

        positivePassRate = DoubleUtil.ratio(posPassCount,  attemptCount);
        negativePassRate = DoubleUtil.ratio(negPassCount,  posPassCount);
        netSelectionRate = DoubleUtil.ratio(exportedCount, attemptCount);

        if (attemptCount % LOG_INTERVAL == 0)
            logStatus();

        if (attemptCount % VALIDATION_INTERVAL == 0)
            validateRates();
    }

    private ThymicOutcome classify(TCR receptor) {
        if (failPositive(receptor))
            return ThymicOutcome.FAILED_POSITIVE;
        else if (failNegative(receptor))
            return ThymicOutcome.FAILED_NEGATIVE;
        else
            return ThymicOutcome.EXPORTED;
    }
    
    private boolean failPositive(TCR receptor) {
        for (Peptide peptide : getCortexPeptides())
            if (passPositive(receptor, peptide))
                return false;

        return true;
    }

    private boolean passPositive(TCR receptor, Peptide peptide) {
        return receptor.bindsWeakly(peptide);
    }

    private boolean failNegative(TCR receptor) {
        for (Peptide peptide : getMedullaPeptides())
            if (failNegative(receptor, peptide))
                return true;

        return false;
    }

    private boolean failNegative(TCR receptor, Peptide peptide) {
        return receptor.bindsStrongly(peptide);
    }

    private void logStatus() {
        JamLogger.info("Attempts:           [%d]",   countReceptors());
        JamLogger.info("Failed positive:    [%d]",   countReceptors(ThymicOutcome.FAILED_POSITIVE));
        JamLogger.info("Failed negative:    [%d]",   countReceptors(ThymicOutcome.FAILED_NEGATIVE));
        JamLogger.info("Selected:           [%d]",   countReceptors(ThymicOutcome.EXPORTED));
        JamLogger.info("Positive pass rate: [%.3f]", positivePassRate);
        JamLogger.info("Negative pass rate: [%.3f]", negativePassRate);
        JamLogger.info("Net selection rate: [%.3f]", netSelectionRate);
    }

    private void validateRates() {
        if (!getPositivePassRange().contains(positivePassRate))
	    throw new IllegalStateException("Positive pass rate is outside the valid range.");

	if (!getNegativePassRange().contains(negativePassRate))
            throw new IllegalStateException("Negative pass rate is outside the valid range.");

        if (!getNetSelectionRange().contains(netSelectionRate))
            throw new IllegalStateException("Net selection rate is outside the valid range.");
    }
}
