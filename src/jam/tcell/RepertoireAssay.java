
package jam.tcell;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.app.JamLogger;
import jam.math.DoubleUtil;
import jam.math.StatSummary;
import jam.peptide.Peptide;
import jam.report.LineBuilder;

public final class RepertoireAssay {
    // Number of times a receptor appears in a selected repertoire...
    private final Multiset<TCR> receptorCount = HashMultiset.create();

    // Number of neoantigens recognized by a receptor...
    private final Multiset<TCR> neoCrossReact = HashMultiset.create();

    // Number of viral antigens recognized by a receptor...
    private final Multiset<TCR> viralCrossReact = HashMultiset.create();

    // Neoantigen recognition rate for each repertoire...
    private final List<Double> neoRecogRates = new ArrayList<Double>();

    // Viral antigen recognition rate for each repertoire...
    private final List<Double> viralRecogRates = new ArrayList<Double>();

    // Number of repertoires processed...
    private int repProcessed = 0;

    // Number of neoantigens processed...
    private int neoProcessed = 0;

    // Number of viral antigens processed...
    private int viralProcessed = 0;

    // Report progress at this iteration interval...
    private static final int LOG_INTERVAL = 1000;

    private RepertoireAssay() {
    }

    public static RepertoireAssay create() {
        return new RepertoireAssay();
    }

    public void process(Collection<? extends TCR> repertoire,
                        Collection<? extends Peptide> neoPeptides,
                        Collection<? extends Peptide> viralPeptides) {
        // Running iteration count for this repertoire...
        int receptorIter = 0;

        // Neoantigens and viral antigens recognized by one or more
        // receptors...
        Set<Peptide> neoRecognized = new HashSet<Peptide>();
        Set<Peptide> viralRecognized = new HashSet<Peptide>();
        //Multiset<Peptide> neoRecognized = HashMultiset.create();
        //Multiset<Peptide> viralRecognized = HashMultiset.create();

        for (TCR receptor : repertoire) {
            ++receptorIter;
            receptorCount.add(receptor);

            for (Peptide neoPep : neoPeptides) {
                if (receptor.isRecognized(neoPep)) {
                    neoRecognized.add(neoPep);
                    neoCrossReact.add(receptor);
                }
            }

            for (Peptide viralPep : viralPeptides) {
                if (receptor.isRecognized(viralPep)) {
                    viralRecognized.add(viralPep);
                    viralCrossReact.add(receptor);
                }
            }

            if (receptorIter % LOG_INTERVAL == 0)
                JamLogger.info("Processed [%d] of [%d] receptors...", receptorIter, repertoire.size());
        }

        ++repProcessed;
        neoProcessed += neoPeptides.size();
        viralProcessed += viralPeptides.size();

        neoRecogRates.add(DoubleUtil.ratio(neoRecognized.size(), neoPeptides.size()));
        viralRecogRates.add(DoubleUtil.ratio(viralRecognized.size(), viralPeptides.size()));
        //neoRecogRates.add(DoubleUtil.ratio(neoRecognized.elementSet().size(), neoPeptides.size()));
        //viralRecogRates.add(DoubleUtil.ratio(viralRecognized.elementSet().size(), viralPeptides.size()));
    }

    public void reportReceptorDetail(PrintWriter writer) {
        writer.println("receptor,occurrence,meanAffinity,neoCrossReact,viralCrossReact");

        for (TCR receptor : receptorCount.elementSet())
            writer.println(String.format("%s,%d,%.4f,%d,%d",
                                         receptor.formatString(),
                                         receptorCount.count(receptor),
                                         ((PairwiseTCR) receptor).computeMeanAffinity(),
                                         neoCrossReact.count(receptor),
                                         viralCrossReact.count(receptor)));
    }

    public void reportRepertoireRecognition(PrintWriter writer) {
        LineBuilder builder;

        builder = LineBuilder.csv();
        builder.append("repProcessed");
        builder.append("neoRecogMean");
        builder.append("neoRecogErr");
        builder.append("viralRecogMean");
        builder.append("viralRecogErr");

        writer.println(builder.toString());

        StatSummary neoRecogSummary = StatSummary.compute(neoRecogRates);
        StatSummary viralRecogSummary = StatSummary.compute(viralRecogRates);

        builder = LineBuilder.csv();
        builder.append(repProcessed);
        builder.append(neoRecogSummary.getMean());
        builder.append(neoRecogSummary.getError());
        builder.append(viralRecogSummary.getMean());
        builder.append(viralRecogSummary.getError());

        writer.println(builder.toString());
    }
}
