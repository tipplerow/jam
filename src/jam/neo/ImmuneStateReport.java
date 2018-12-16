
package jam.neo;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;

import jam.app.JamLogger;
import jam.mhc.Genotype;
import jam.peptide.Peptide;
import jam.peptide.Peptidome;
import jam.tcell.Repertoire;
import jam.util.MultisetUtil;

/**
 * Classifies the immune state of a collection of target peptides.
 */
public final class ImmuneStateReport {
    private final Genotype   genotype;
    private final Repertoire repertoire;
    private final Peptidome  peptidome;

    private final Map<Peptide, ImmuneState> peptideState  = new HashMap<Peptide, ImmuneState>();
    private final Map<ImmuneState, Double>  stateFraction = new EnumMap<ImmuneState, Double>(ImmuneState.class);

    private static final int LOG_INTERVAL = 1000;

    private ImmuneStateReport(Genotype genotype, Repertoire repertoire, Peptidome peptidome) {
        this.genotype   = genotype;
        this.repertoire = repertoire;
        this.peptidome  = peptidome;
    }

    /**
     * Runs an immune state report.
     *
     * @param genotype the peptide-presenting MHC genotype.
     *
     * @param repertoire the T cells available for immune surveillance.
     *
     * @param peptidome the target peptidome to classify.
     *
     * @return the immune state report.
     */
    public static ImmuneStateReport run(Genotype genotype, Repertoire repertoire, Peptidome peptidome) {
        ImmuneStateReport report = new ImmuneStateReport(genotype, repertoire, peptidome);
        report.run();
        return report;
    }

    private void run() {
        Multiset<ImmuneState> counter = EnumMultiset.create(ImmuneState.class);

        for (Peptide peptide : peptidome) {
            ImmuneState state = classify(peptide);

            counter.add(state);
            peptideState.put(peptide, state);

            if (counter.size() % LOG_INTERVAL == 0)
                JamLogger.info("Classified [%d] immune states...", counter.size());
        }

        for (ImmuneState state : ImmuneState.values())
            stateFraction.put(state, MultisetUtil.frequency(counter, state));
    }

    private ImmuneState classify(Peptide peptide) {
        if (!genotype.isPresented(peptide))
            return ImmuneState.NOT_PRESENTED;

        if (repertoire.isRecognized(peptide))
            return ImmuneState.PRESENTED_AND_RECOGNIZED;

        return ImmuneState.PRESENTED_NOT_RECOGNIZED;
    }

    public double getFraction(ImmuneState state) {
        return stateFraction.get(state);
    }

    public ImmuneState getState(Peptide peptide) {
        return peptideState.get(peptide);
    }
}
