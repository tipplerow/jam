
package jam.tcga;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jam.agpro.AntigenProcessor;
import jam.agpro.SelfPeptideDb;
import jam.app.JamLogger;
import jam.chem.Concentration;
import jam.hugo.HugoSymbol;
import jam.peptide.Peptide;
import jam.peptide.PeptideConcentrationProfile;
import jam.rna.ConcentrationModel;
import jam.rna.Expression;
import jam.rna.ExpressionProfile;
import jam.util.SetUtil;

/**
 * Assembles protein concentration profiles for self-peptides and
 * neo-peptides within tumors using the standard global databases.
 */
public final class TumorPeptideConcentrationProfile {
    private final TumorBarcode barcode;

    private final Set<Peptide> neoPeptides = new HashSet<Peptide>();
    private final Set<Peptide> selfPeptides = new HashSet<Peptide>();

    private final MissenseTable missenseTable = MissenseTable.global();
    private final SelfPeptideDb selfPeptideDb = SelfPeptideDb.global();
    private final AntigenProcessor antigenProcessor = selfPeptideDb.antigenProcessor();
    private final ExpressionProfile expressionProfile = ExpressionProfile.global();
    private final ConcentrationModel concentrationModel = ConcentrationModel.global();

    private final PeptideConcentrationProfile concentrationProfile = PeptideConcentrationProfile.create();

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
     * Returns the concentration of a peptide in the tumor.
     *
     * @param peptide the peptide of interest.
     *
     * @return the concentration of the specified peptide in this
     * tumor ({@code Concentration.ZERO} if the specified peptide
     * is not present in this tumor).
     */
    public Concentration getConcentration(Peptide peptide) {
        return concentrationProfile.lookup(peptide);
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
     * Returns a read-only view of all peptides (neo and self)
     * presented in the tumor.
     *
     * @return an unmodifiable set containing every peptide (neo
     * and self) presented in the tumor.
     */
    public Set<Peptide> viewPeptides() {
        return concentrationProfile.viewPeptides();
    }

    /**
     * Returns a read-only view of the neo-peptides presented in
     * the tumor.
     *
     * @return an unmodifiable set containing every neo-peptide
     * presented in the tumor.
     */
    public Set<Peptide> viewNeoPeptides() {
        return Collections.unmodifiableSet(neoPeptides);
    }

    /**
     * Returns a read-only view of the self-peptides presented in
     * the tumor.
     *
     * @return an unmodifiable set containing every self-peptide
     * presented in the tumor.
     */
    public Set<Peptide> viewSelfPeptides() {
        return Collections.unmodifiableSet(selfPeptides);
    }

    private void process() {
        Set<HugoSymbol> symbols = selfPeptideDb.geneSet();

        for (HugoSymbol symbol : symbols)
            processGene(symbol);

        if (SetUtil.countShared(neoPeptides, selfPeptides) > 0)
            throw new IllegalStateException("Overlap between self-peptides and neo-peptides.");
    }

    private void processGene(HugoSymbol symbol) {
        Expression expression =
            expressionProfile.lookup(barcode, symbol);

        if (expression != null)
            processExpressedGene(symbol, expression);
    }

    private void processExpressedGene(HugoSymbol symbol, Expression expression) {
        Concentration concentration =
            concentrationModel.translate(expression);

        if (concentration.isPositive())
            processTranslatedGene(symbol, concentration);
    }

    private void processTranslatedGene(HugoSymbol symbol, Concentration concentration) {
        //
        // This gene is expressed and translated in appreciable
        // amounts...
        //
        ++expressedGeneCount;
            
        List<MissenseRecord> records =
            missenseTable.lookup(barcode, symbol);

        if (records.isEmpty())
            processGermlineGene(symbol, concentration);
        else
            processMutatedGene(symbol, concentration, records);
    }

    private void processGermlineGene(HugoSymbol symbol, Concentration concentration) {
        //
        // All peptides are self-peptides from the germline
        // protein...
        //
        List<Peptide> genePeptides = selfPeptideDb.lookup(symbol);

        selfPeptides.addAll(genePeptides);
        concentrationProfile.addAll(genePeptides, concentration);
    }

    private void processMutatedGene(HugoSymbol symbol,
                                    Concentration concentration,
                                    List<MissenseRecord> missenseRecords) {
        // This gene is mutated...
        ++mutatedGeneCount;
        totalMutationCount += missenseRecords.size();

        // Determine the mutated form of the protein...
        Peptide mutatedProtein = MissenseRecord.apply(missenseRecords);

        // Run the mutated protein through the antigen processing
        // machinery...
        JamLogger.info("Processing mutated protein from gene [%s]...", symbol.getKey());
        List<Peptide> peptides = antigenProcessor.process(mutatedProtein);

        classifyPeptides(peptides);
        concentrationProfile.addAll(peptides, concentration);
    }

    private void classifyPeptides(List<Peptide> peptides) {
        //
        // Split the fragments into self-peptides and neo-peptides...
        //
        for (Peptide peptide : peptides)
            if (selfPeptideDb.contains(peptide))
                selfPeptides.add(peptide);
            else
                neoPeptides.add(peptide);
    }
}
