
package jam.tcell;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import jam.app.JamLogger;
import jam.math.DoubleUtil;
import jam.math.StatSummary;
import jam.peptide.Peptide;
import jam.util.MultisetUtil;

/**
 * Assays the immunogenicity and cross-reactivity of a T cell
 * repertoire with respect to a collection of peptides.
 */
public final class TCRAssay {
    private Repertoire repertoire;
    private Collection<? extends Peptide> peptides;

    // Number of TCRs recognizing each peptide...
    private final Multiset<Peptide> immunogenicity = HashMultiset.create();

    // Number of peptides recognized by each TCR...
    private final Multiset<TCR> crossReactivity = HashMultiset.create();

    // For a "progress report" during the assay...
    private long pairsProcessed;
    private long totalPairCount;
    private double percentProcessed;
    private double nextProgressReport;
    private static final double progressReportInterval = 1.0;

    // Fraction of peptides recognized by one or more TCRs...
    private double coveredFrac;

    // Fraction of TCRs that recognize one or more peptides...
    private double reactiveFrac;

    private StatSummary immunogenicitySummary;
    private StatSummary crossReactivitySummary;

    private TCRAssay(Repertoire repertoire, Collection<? extends Peptide> peptides) {
        this.peptides = peptides;
        this.repertoire = repertoire;
    }

    /**
     * Assays the immunogenicity and cross-reactivity of a T cell
     * repertoire with respect to a collection of peptides.
     *
     * @param repertoire the T cell repertoire.
     *
     * @param peptides the challenging peptides.
     *
     * @return the resulting assay.
     */
    public static TCRAssay run(Repertoire repertoire, Collection<? extends Peptide> peptides) {
        TCRAssay assay = new TCRAssay(repertoire, peptides);
        assay.run();
        return assay;
    }

    private void run() {
        JamLogger.info("Running assay on [%d] TCRs and [%d] peptides...", repertoire.size(), peptides.size());

        scanPairs();
        computeCoverage();
        computeReactivity();
        computeSummaries();
    }

    private void scanPairs() {
        pairsProcessed = 0;
        totalPairCount = repertoire.size() * peptides.size();
        nextProgressReport = progressReportInterval;

        for (TCR receptor : repertoire)
            for (Peptide peptide : peptides)
                assayPair(receptor, peptide);
    }

    private void assayPair(TCR receptor, Peptide peptide) {
        if (receptor.isRecognized(peptide)) {
            immunogenicity.add(peptide);
            crossReactivity.add(receptor);
        }

        ++pairsProcessed;
        percentProcessed = 100.0 * DoubleUtil.ratio(pairsProcessed, totalPairCount);

        if (percentProcessed >= nextProgressReport) {
            JamLogger.info("Processed [%d%%] of receptor-peptide pairs...", (int) percentProcessed);
            nextProgressReport += progressReportInterval;
        }
    }

    private void computeCoverage() {
        //
        // The "immunogenicity" set contains only peptides recognized
        // by one or more TCRs...
        //
        int totalCount = peptides.size();
        int coveredCount = MultisetUtil.countUnique(immunogenicity);

        coveredFrac = DoubleUtil.ratio(coveredCount, totalCount);
    }

    private void computeReactivity() {
        //
        // The "crossReactivity" set contains only TCRs that recognize
        // by one or more peptides...
        //
        int totalCount = repertoire.size();
        int reactiveCount = MultisetUtil.countUnique(crossReactivity);

        reactiveFrac = DoubleUtil.ratio(reactiveCount, totalCount);
    }

    @SuppressWarnings("unchecked")
    private void computeSummaries() {
        immunogenicitySummary = StatSummary.compute(immunogenicity, (Collection<Peptide>) peptides);
        crossReactivitySummary = StatSummary.compute(crossReactivity, (Collection<TCR>) repertoire);
    }

    public int countCognatePeptides(TCR receptor) {
        return crossReactivity.count(receptor);
    }

    public int countCognateReceptors(Peptide peptide) {
        return immunogenicity.count(peptide);
    }

    public int countPeptides() {
        return peptides.size();
    }

    public int countReceptors() {
        return repertoire.size();
    }

    public double getCognatePeptideFraction(TCR receptor) {
        return DoubleUtil.ratio(countCognatePeptides(receptor), countPeptides());
    }

    public double getCognateReceptorFraction(Peptide peptide) {
        return DoubleUtil.ratio(countCognateReceptors(peptide), countReceptors());
    }

    public double getCoveredFrac() {
        return coveredFrac;
    }

    public double getReactiveFrac() {
        return reactiveFrac;
    }

    public StatSummary getImmunogenicitySummary() {
        return immunogenicitySummary;
    }

    public StatSummary getCrossReactivitySummary() {
        return crossReactivitySummary;
    }

    public Multiset<Peptide> viewImmunogenicity() {
        return Multisets.unmodifiableMultiset(immunogenicity);
    }

    public Multiset<TCR> viewCrossReactivity() {
        return Multisets.unmodifiableMultiset(crossReactivity);
    }

    public Collection<Peptide> viewPeptides() {
        return Collections.unmodifiableCollection(peptides);
    }

    public Collection<TCR> viewRepertoire() {
        return repertoire;
    }
}
