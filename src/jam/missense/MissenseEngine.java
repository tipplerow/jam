
package jam.missense;

import java.util.ArrayList;
import java.util.List;

import jam.ensembl.EnsemblDb;
import jam.ensembl.EnsemblRecord;
import jam.ensembl.EnsemblTranscript;
import jam.hugo.HugoMaster;
import jam.hugo.HugoSymbol;
import jam.lang.JamException;
import jam.maf.MAFFastaRecord;
import jam.peptide.Peptide;
import jam.peptide.ProteinChange;
import jam.tcga.CellFraction;
import jam.tcga.TumorBarcode;
import jam.util.ListUtil;

/**
 * Processes MAF files and generates the protein structures generated
 * by missense mutations.
 */
final class MissenseEngine {
    private final HugoSymbol symbol;
    private final TumorBarcode barcode;

    private final EnsemblDb ensemblDb;
    private final HugoMaster hugoMaster;

    private final CellFraction ccfThreshold;
    private final MissenseTable missenseTable;

    private List<MissenseRecord> missenseRecords;

    MissenseEngine(TumorBarcode  barcode,
                   HugoSymbol    symbol,
                   EnsemblDb     ensemblDb,
                   HugoMaster    hugoMaster,
                   CellFraction  ccfThreshold,
                   MissenseTable missenseTable) {

        this.symbol = symbol;
        this.barcode = barcode;

        this.ensemblDb = ensemblDb;
        this.hugoMaster = hugoMaster;

        this.ccfThreshold = ccfThreshold;
        this.missenseTable = missenseTable;
    }

    MAFFastaRecord process() {
        missenseRecords = missenseTable.lookup(barcode, symbol);

        if (missenseRecords.isEmpty())
            throw JamException.runtime("No missense mutation records.");

        missenseRecords = MissenseRecord.filterCellFraction(missenseRecords, ccfThreshold);

        if (missenseRecords.isEmpty())
            return null;

        Peptide germline = getGermlinePeptide();
        Peptide mutated  = germline.mutate(getProteinChanges());

        return new MAFFastaRecord(barcode, symbol, CellFraction.UNIT, mutated);
    }

    private Peptide getGermlinePeptide() {
        if (haveTranscripts())
            return getGermlinePeptide(getTranscript());
        else
            return matchNativePeptide();
    }

    private boolean haveTranscripts() {
        //
        // Most data sets (TCGA) have Ensembl transcripts, but
        // some do not (Liu, Nature Medicine).  We can process
        // either case, but not one with partially missing
        // transcripts...
        //
        boolean firstNull = isNullTranscript(missenseRecords.get(0));

        for (int index = 1; index < missenseRecords.size(); ++index)
            if (isNullTranscript(missenseRecords.get(index)) != firstNull)
                throw JamException.runtime("Missing transcript ID.");

        // Either all records had null transcripts or none did...
        return firstNull;
    }

    private static boolean isNullTranscript(MissenseRecord record) {
        return record.getTranscriptID() == null;
    }

    private EnsemblTranscript getTranscript() {
        missenseRecords = MissenseRecord.filterPrimaryTranscript(missenseRecords);

        if (missenseRecords.isEmpty())
            throw JamException.runtime("No primary transcript.");
        else
            return missenseRecords.get(0).getTranscriptID();
    }

    private Peptide getGermlinePeptide(EnsemblTranscript transcriptID) {
        EnsemblRecord ensemblRecord = ensemblDb.get(transcriptID);

        if (ensemblRecord != null)
            return ensemblRecord.getPeptide();
        else
            throw JamException.runtime("Unmapped transcript: [%s].", transcriptID.getKey());
    }

    private Peptide matchNativePeptide() {
        //
        // Okay, no transcript identifier, so we use the first peptide
        // with a sequence that is consistent with the protein changes...
        //
        List<EnsemblRecord> ensemblRecords = getEnsemblRecords();
        List<ProteinChange> proteinChanges = getProteinChanges();

        for (EnsemblRecord ensemblRecord : ensemblRecords) {
            Peptide peptide = ensemblRecord.getPeptide();

            if (ProteinChange.isNative(peptide, proteinChanges))
                return peptide;
        }

        throw JamException.runtime("No consistent native Ensembl records.");
    }

    private List<EnsemblRecord> getEnsemblRecords() {
        //
        // Two ways to match HUGO symbols with Ensembl records:
        // through the HUGO master table and through the Ensembl
        // database itself...
        //
        List<EnsemblRecord> ensemblRecords = new ArrayList<EnsemblRecord>();

        ensemblRecords.addAll(ensemblDb.get(symbol));
        ensemblRecords.addAll(ensemblDb.get(hugoMaster.get(symbol)));

        if (ensemblRecords.isEmpty())
            throw JamException.runtime("No matching Ensembl records.");

        return ensemblRecords;
    }

    private List<ProteinChange> getProteinChanges() {
        return ListUtil.apply(missenseRecords, x -> x.getProteinChange());
    }
}
