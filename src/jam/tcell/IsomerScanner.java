
package jam.tcell;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Multiset;

import jam.app.JamApp;
import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.math.StatUtil;
import jam.peptide.Peptide;
import jam.peptide.RIM;
import jam.report.LineBuilder;
import jam.vector.VectorView;

/**
 * Provides a command-line application to compute the number of
 * selecting peptides for each distinct T cell receptor isomer.
 */
public class IsomerScanner extends JamApp {
    private PrintWriter writer;

    private List<Peptide> targets;
    private Multiset<String> isomers;

    private final int blockIndex;
    private final int blockSize;

    private final double activationEnergy;
    private final double positiveThreshold;
    private final double negativeThreshold;

    private static final int LOG_INTERVAL = 1;

    /**
     * Name of the detail file.
     */
    public static final String DETAIL_NAME = "isomer-detail.csv";

    /**
     * Creates a new isomer scanner.
     *
     * @param args the command-line arguments.
     */
    protected IsomerScanner(String[] args) {
        super(args[0]);

        this.blockIndex = Integer.parseInt(args[1]);
        this.blockSize  = Integer.parseInt(args[2]);

        this.activationEnergy = TCellProperties.getActivationEnergy();
        this.positiveThreshold = TCellProperties.getPositiveThreshold();
        this.negativeThreshold = TCellProperties.getNegativeThreshold();
    }

    /**
     * Returns the pairwise residue interaction potential.
     *
     * @return the pairwise residue interaction potential.
     */
    protected RIM getRIM() {
        return RIM.MiyazawaJernigan;
    }

    /**
     * Simulates thymic selection and reports the selection rates.
     */
    protected void run() {
        JamLogger.info("Generating target peptides...");
        targets = Peptide.enumerate(TCellProperties.getReceptorLength());

        JamLogger.info("Counting isomers...");
        isomers = Peptide.mapIsomers(targets);

        writeHeader();
        writeData();
        autoClose();

        JamLogger.info("DONE!");
    }

    private void writeHeader() {
        LineBuilder builder = LineBuilder.csv();

        builder.append("isomerString");
        builder.append("instanceCount");
        builder.append("affinityMean");
        builder.append("affinitySD");
        builder.append("selectorCount");
        builder.append("deletorCount");

        writer = openWriter(DETAIL_NAME);
        writer.println(builder.toString());
    }

    private void writeData() {
        List<String> isomerList = new ArrayList<String>(isomers.elementSet());

        int fromIndex = (blockIndex - 1) * blockSize;
        int toIndex   = Math.min(fromIndex + blockSize, isomerList.size());

        for (int index = fromIndex; index < toIndex; ++index) {
            JamLogger.info("Processing isomer [%d]...", index);
            writeLine(isomerList.get(index));
        }
    }

    private void writeLine(String isomer) {
        Peptide receptor = Peptide.parse(isomer);

        double meanAffinity =
            activationEnergy - getRIM().computeMeanNearest(receptor);

        int deletorCount = 0;
        int selectorCount = 0;

        double[] affinities = computeAffinities(receptor);

        double affinityMean = StatUtil.mean(VectorView.wrap(affinities));
        double affinitySD   = StatUtil.stdev(VectorView.wrap(affinities));

        for (double affinity : affinities) {
            if (affinity >= negativeThreshold)
                ++deletorCount;
            else if (affinity >= positiveThreshold)
                ++selectorCount;
        }

        LineBuilder builder = LineBuilder.csv();

        builder.append(isomer);
        builder.append(isomers.count(isomer));
        builder.append(affinityMean, "%.4f");
        builder.append(affinitySD, "%.4f");
        builder.append(selectorCount);
        builder.append(deletorCount);

        writer.println(builder.toString());
        writer.flush();
    }

    private double[] computeAffinities(Peptide receptor) {
        double[] affinities = new double[targets.size()];

        for (int k = 0; k < targets.size(); ++k)
            affinities[k] = activationEnergy - getRIM().computeNearest(receptor, targets.get(k));

        return affinities;
    }

    public static void main(String[] args) {
        IsomerScanner scanner = new IsomerScanner(args);
        scanner.run();
    }
}
