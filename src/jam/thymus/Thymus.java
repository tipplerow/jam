
package jam.thymus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jam.app.JamLogger;
import jam.bio.Peptide;
import jam.lang.ObjectFactory;
import jam.math.DoubleRange;
import jam.math.DoubleUtil;
import jam.tcell.TCR;
import jam.tcell.TCellProperties;

/**
 * Executes the positive and negative selection of T cell receptors
 * and maintains statistics necessary to compute selection rates.
 */
public class Thymus {
    private final int repertoireSize;
    private final DoubleRange positivePassRange;
    private final DoubleRange negativePassRange;
    private final DoubleRange netSelectionRange;
    private final ObjectFactory<? extends TCR> receptorFactory;
    private final Collection<? extends Peptide> sharedPeptides;
    private final Collection<? extends Peptide> cortexPeptides;  // Private to the cortex
    private final Collection<? extends Peptide> medullaPeptides; // Private to the medulla

    // Processed receptors and their fates...
    private Map<ThymicOutcome, Set<TCR>> receptors;

    // Realized TCR selection rates...
    private double positivePassRate;
    private double negativePassRate;
    private double netSelectionRate;

    private static final int LOG_INTERVAL = 1000;
    private static final int VALIDATION_INTERVAL = 10000;

    private Thymus(int repertoireSize,
                   DoubleRange positivePassRange,
                   DoubleRange negativePassRange,
                   DoubleRange netSelectionRange,
                   ObjectFactory<? extends TCR> receptorFactory,
                   Collection<? extends Peptide> sharedPeptides,
                   Collection<? extends Peptide> cortexPeptides,
                   Collection<? extends Peptide> medullaPeptides) {
        this.repertoireSize    = repertoireSize;
        this.positivePassRange = positivePassRange;
        this.negativePassRange = negativePassRange;
        this.netSelectionRange = netSelectionRange;
        this.receptorFactory   = receptorFactory;
        this.sharedPeptides    = sharedPeptides;
        this.cortexPeptides    = cortexPeptides;
        this.medullaPeptides   = medullaPeptides;
    }

    /**
     * Simulates thymic selection using parameters specified by the
     * global system properties.
     *
     * @param peptideFactory the creator of self-peptides.
     *
     * @param receptorFactory the creator of pre-selection ("double
     * negative") T cells.
     *
     * @return the thymus containing all receptors grouped by their
     * thymic outcome.
     *
     * @throws IllegalStateException unless the selection rates are
     * within the allowed ranges.
     */
    public static Thymus select(ObjectFactory<? extends Peptide> peptideFactory,
                                ObjectFactory<? extends TCR>     receptorFactory) {
        int repertoireSize = TCellProperties.getRepertoireSize();

        DoubleRange positivePassRange = ThymusProperties.getPositivePassRange();
        DoubleRange negativePassRange = ThymusProperties.getNegativePassRange();
        DoubleRange netSelectionRange = ThymusProperties.getNetSelectionRange();

        int sharedPeptideCount = ThymusProperties.getSharedPeptideCount();
        int cortexPrivateCount = ThymusProperties.getCortexPrivateCount();
        int medullaPrivateCount = ThymusProperties.getMedullaPrivateCount();

        Collection<? extends Peptide> sharedPeptides = peptideFactory.newInstances(sharedPeptideCount);
        Collection<? extends Peptide> cortexPeptides = peptideFactory.newInstances(cortexPrivateCount);
        Collection<? extends Peptide> medullaPeptides = peptideFactory.newInstances(medullaPrivateCount);

        return select(repertoireSize,
                      positivePassRange,
                      negativePassRange,
                      netSelectionRange,
                      receptorFactory,
                      sharedPeptides,
                      cortexPeptides,
                      medullaPeptides);
    }

    /**
     * Simulates thymic selection.
     *
     * @param repertoireSize the number of T cells to export from the
     * thymus.
     *
     * @param positivePassRange the acceptable range of positive
     * selection rates.
     *
     * @param negativePassRange the acceptable range of negative
     * selection rates.
     *
     * @param netSelectionRange the acceptable range of net selection
     * rates.
     *
     * @param receptorFactory the creator of pre-selection ("double
     * negative") T cells.
     *
     * @param sharedPeptides the MHC-restricted self-peptides present
     * in both the thymic cortex and medulla.
     *
     * @param cortexPeptides the MHC-restricted self-peptides present
     * only in the thymic cortex.
     *
     * @param medullaPeptides the MHC-restricted self-peptides present
     * only in the thymic medulla.
     *
     * @return the thymus containing all receptors grouped by their
     * thymic outcome.
     *
     * @throws IllegalStateException unless the selection rates are
     * within the allowed ranges.
     */
    public static Thymus select(int repertoireSize,
                                DoubleRange positivePassRange,
                                DoubleRange negativePassRange,
                                DoubleRange netSelectionRange,
                                ObjectFactory<? extends TCR> receptorFactory,
                                Collection<? extends Peptide> sharedPeptides,
                                Collection<? extends Peptide> cortexPeptides,
                                Collection<? extends Peptide> medullaPeptides) {
        Thymus thymus =
            new Thymus(repertoireSize,
                       positivePassRange,
                       negativePassRange,
                       netSelectionRange,
                       receptorFactory,
                       sharedPeptides,
                       cortexPeptides,
                       medullaPeptides);

        thymus.select();
        return thymus;
    }

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
     * Returns the number of MHC-restricted self-peptides present in
     * both the thymic cortex and medulla.
     *
     * @return the number of MHC-restricted self-peptides present in
     * both the thymic cortex and medulla.
     */
    public int countSharedPeptides() {
        return sharedPeptides.size();
    }

    /**
     * Returns the number of MHC-restricted self-peptides present only
     * in the thymic cortex.
     *
     * @return the number of MHC-restricted self-peptides present only
     * in the thymic cortex.
     */
    public int countCortexPeptides() {
        return cortexPeptides.size();
    }

    /**
     * Returns the number of MHC-restricted self-peptides present only
     * in the thymic medulla.
     *
     * @return the number of MHC-restricted self-peptides present only
     * in the thymic medulla.
     */
    public int countMedullaPeptides() {
        return medullaPeptides.size();
    }

    /**
     * Returns the creator of pre-selection ("double negative") T cells.
     *
     * @return the creator of pre-selection ("double negative") T cells.
     */
    public ObjectFactory<? extends TCR> getReceptorFactory() {
        return receptorFactory;
    }

    /**
     * Returns the acceptable range of positive selection rates.
     *
     * @return the acceptable range of positive selection rates.
     */
    public DoubleRange getPositivePassRange() {
        return positivePassRange;
    }

    /**
     * Returns the acceptable range of negative selection rates.
     *
     * @return the acceptable range of negative selection rates.
     */
    public DoubleRange getNegativePassRange() {
        return negativePassRange;
    }
                          
    /**
     * Returns the acceptable range of net selection rates.
     *
     * @return the acceptable range of net selection rates.
     */
    public DoubleRange getNetSelectionRange() {
        return netSelectionRange;
    }


    /**
     * Returns the number of naive T cells to export from the thymus.
     *
     * @return the number of naive T cells to export from the thymus.
     */
    public int getRepertoireSize() {
        return repertoireSize;
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
     * Returns the MHC-restricted self-peptides present in both the
     * thymic cortex and medulla.
     *
     * @return the MHC-restricted self-peptides present in both the
     * thymic cortex and medulla.
     */
    public Collection<? extends Peptide> viewSharedPeptides() {
        return Collections.unmodifiableCollection(sharedPeptides);
    }

    /**
     * Returns the MHC-restricted self-peptides present only in the
     * thymic cortex.
     *
     * @return the MHC-restricted self-peptides present only in the
     * thymic cortex.
     */
    public Collection<? extends Peptide> viewCortexPeptides() {
        return Collections.unmodifiableCollection(cortexPeptides);
    }

    /**
     * Returns the MHC-restricted self-peptides present only in the
     * thymic medulla.
     *
     * @return the MHC-restricted self-peptides present only in the
     * thymic medulla.
     */
    public Collection<? extends Peptide> viewMedullaPeptides() {
        return Collections.unmodifiableCollection(medullaPeptides);
    }

    /**
     * Returns a read-only view of the receptors with a given outcome.
     *
     * @param outcome the outcome of interest.
     *
     * @return a read-only view of the receptors with the specified
     * outcome.
     */
    public Set<TCR> viewReceptors(ThymicOutcome outcome) {
        return Collections.unmodifiableSet(receptors.get(outcome));
    }

    /**
     * Returns a read-only view of the exported receptors.
     *
     * @return a read-only view of the exported receptors.
     */
    public Collection<? extends TCR> viewRepertoire() {
        return Collections.unmodifiableCollection(receptors.get(ThymicOutcome.EXPORTED));
    }

    private void select() {
        initialize();

        while (continueSelection())
            processOne();

        logStatus();
        validateRates();
    }

    private void initialize() {
        receptors = new EnumMap<ThymicOutcome, Set<TCR>>(ThymicOutcome.class);

        for (ThymicOutcome outcome : ThymicOutcome.values())
            receptors.put(outcome, new LinkedHashSet<TCR>());

        positivePassRate = Double.NaN;
        negativePassRate = Double.NaN;
        netSelectionRate = Double.NaN;
    }

    private boolean continueSelection() {
        return countReceptors(ThymicOutcome.EXPORTED) < repertoireSize;
    }

    private void processOne() {
        TCR receptor = receptorFactory.newInstance();
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
        for (Peptide peptide : sharedPeptides)
            if (receptor.isSelector(peptide))
                return false;

        for (Peptide peptide : cortexPeptides)
            if (receptor.isSelector(peptide))
                return false;

        return true;
    }

    private boolean failNegative(TCR receptor) {
        for (Peptide peptide : sharedPeptides)
            if (receptor.isDeletor(peptide))
                return true;

        for (Peptide peptide : medullaPeptides)
            if (receptor.isDeletor(peptide))
                return true;

        return false;
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
        if (!positivePassRange.contains(positivePassRate))
	    throw new IllegalStateException("Positive pass rate is outside the valid range.");

	if (!negativePassRange.contains(negativePassRate))
            throw new IllegalStateException("Negative pass rate is outside the valid range.");

        if (!netSelectionRange.contains(netSelectionRate))
            throw new IllegalStateException("Net selection rate is outside the valid range.");
    }
}
