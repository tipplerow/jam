
package jam.tcga;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jam.agpro.AntigenProcessor;
import jam.agpro.SelfPeptideDb;
import jam.app.JamLogger;
import jam.chem.Concentration;
import jam.peptide.HugoSymbol;
import jam.peptide.Peptide;
import jam.peptide.PeptideConcentrationProfile;
import jam.rna.ConcentrationModel;
import jam.rna.Expression;
import jam.rna.ExpressionProfile;

/**
 * Assembles protein concentration profiles for self-peptides and
 * neo-peptides within tumors using the standard global databases.
 */
public final class TumorPeptideConcentrationProfile {
    private final TumorBarcode barcode;

    private final MissenseTable missenseTable = MissenseTable.global();
    private final SelfPeptideDb selfPeptideDb = SelfPeptideDb.global();
    private final AntigenProcessor antigenProcessor = selfPeptideDb.antigenProcessor();
    private final ExpressionProfile expressionProfile = ExpressionProfile.global();
    private final ConcentrationModel concentrationModel = ConcentrationModel.global();

    private final PeptideConcentrationProfile neoConc = PeptideConcentrationProfile.create();
    private final PeptideConcentrationProfile selfConc = PeptideConcentrationProfile.create();

    private int mutatedGeneCount = 0;
    private int expressedGeneCount = 0;
    private int totalMutationCount = 0;

    private TumorPeptideConcentrationProfile(TumorBarcode barcode) {
        this.barcode = barcode;
    }

    /**
     * Creates the peptide concentration profile for a given tumor.
     *
     * @param barcode the barcode of the tumor to analyze.
     *
     * @return the peptide concentration profile for the specified
     * tumor.
     */
    public static TumorPeptideConcentrationProfile process(TumorBarcode barcode) {
        TumorPeptideConcentrationProfile profile =
            new TumorPeptideConcentrationProfile(barcode);

        profile.process();
        return profile;
    }

    /**
     * Returns the barcode of the tumor that was analyzed.
     *
     * @return the barcode of the tumor that was analyzed.
     */
    public TumorBarcode getBarcode() {
        return barcode;
    }

    /**
     * Returns the number of expressed genes in the tumor.
     *
     * @return the number of expressed genes in the tumor.
     */
    public int getExpressedGeneCount() {
        return expressedGeneCount;
    }

    /**
     * Returns the number of mutated genes in the tumor.
     *
     * @return the number of mutated genes in the tumor.
     */
    public int getMutatedGeneCount() {
        return mutatedGeneCount;
    }

    /**
     * Returns the total number of mutations in the tumor.
     *
     * @return the total number of mutations in the tumor.
     */
    public int getTotalMutationCount() {
        return totalMutationCount;
    }

    /**
     * Returns the concentration of a given neo-peptide in the tumor.
     *
     * @param peptide a neo-peptide of interest.
     *
     * @return the concentration of the specified neo-peptide in this
     * tumor ({@code Concentration.ZERO} if the specified peptide is
     * not a neo-peptide in the tumor).
     */
    public Concentration getNeoPeptideConcentration(Peptide peptide) {
        return neoConc.lookup(peptide);
    }

    /**
     * Returns the concentration of a given self-peptide in the tumor.
     *
     * @param peptide a self-peptide of interest.
     *
     * @return the concentration of the specified self-peptide in this
     * tumor ({@code Concentration.ZERO} if the specified peptide is
     * not a self-peptide in the tumor).
     */
    public Concentration getSelfPeptideConcentration(Peptide peptide) {
        return selfConc.lookup(peptide);
    }

    /**
     * Returns a read-only view of the neo-peptides presented in
     * the tumor.
     *
     * @return an unmodifiable set containing every neo-peptide
     * presented in the tumor.
     */
    public Set<Peptide> viewNeoPeptides() {
        return neoConc.viewPeptides();
    }

    /**
     * Returns a read-only view of the self-peptides presented in
     * the tumor.
     *
     * @return an unmodifiable set containing every self-peptide
     * presented in the tumor.
     */
    public Set<Peptide> viewSelfPeptides() {
        return selfConc.viewPeptides();
    }

    private void process() {
        Set<HugoSymbol> symbols = selfPeptideDb.geneSet();

        for (HugoSymbol symbol : symbols)
            processGene(symbol);
    }

    private void processGene(HugoSymbol symbol) {
        Expression expression =
            expressionProfile.lookup(barcode, symbol);

        if (expression == null)
            return;

        Concentration concentration =
            concentrationModel.translate(expression);

        if (!concentration.isPositive())
            return;

        // THIS GENE IS EXPRESSED
        ++expressedGeneCount;
            
        List<MissenseRecord> records =
            missenseTable.lookup(barcode, symbol);

        if (records.isEmpty()) {
            //
            // All peptides are self-peptides from the germline
            // protein...
            //
            selfConc.add(selfPeptideDb.lookup(symbol), concentration);
            return;
        }

        // THIS GENE IS MUTATED
        ++mutatedGeneCount;
        totalMutationCount += records.size();

        // Determine the mutated form of the protein...
        Peptide mutatedProtein = MissenseRecord.apply(records);

        // Run the mutated protein through the antigen processing
        // machinery...
        JamLogger.info("Processing mutated protein from gene [%s]...", symbol.getKey());
        List<Peptide> peptides = antigenProcessor.process(mutatedProtein);

        // Split the fragments into self-peptides and neo-peptides...
        Set<Peptide> neoPep = new HashSet<Peptide>();
        Set<Peptide> selfPep = new HashSet<Peptide>();

        for (Peptide peptide : peptides) {
            if (selfPeptideDb.contains(peptide))
                selfPep.add(peptide);
            else
                neoPep.add(peptide);
        }

        JamLogger.info("Found [%d] self-peptides and [%d] neo-peptides.", selfPep.size(), neoPep.size());

        neoConc.add(neoPep, concentration);
        selfConc.add(selfPep, concentration);
    }
}
