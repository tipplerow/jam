
package jam.junit;

import java.util.List;

import jam.maf.MAFProperties;
import jam.maf.MissenseParser;
import jam.maf.MissenseRecord;

import org.junit.*;
import static org.junit.Assert.*;

public class MissenseParserTest {
    private static final String MIAO_FILE = "data/test/MAF_Miao_sample.maf";
    private static final String TCGA_FILE = "data/test/MAF_TCGA_sample.maf";

    @Test public void testMiao() {
        System.setProperty(MAFProperties.TUMOR_BARCODE_COLUMN_PROPERTY,  "pair_id");
        System.setProperty(MAFProperties.HUGO_SYMBOL_COLUMN_PROPERTY,    "Hugo_Symbol");
        System.setProperty(MAFProperties.TRANSCRIPT_COLUMN_PROPERTY,     "Annotation_Transcript");
        System.setProperty(MAFProperties.CLASSIFICATION_COLUMN_PROPERTY, "Variant_Classification");
        System.setProperty(MAFProperties.VARIANT_TYPE_COLUMN_PROPERTY,   "Variant_Type");
        System.setProperty(MAFProperties.PROTEIN_CHANGE_COLUMN_PROPERTY, "Protein_Change");
        System.setProperty(MAFProperties.CELL_FRACTION_COLUMN_PROPERTY,  "ccf_hat");
        System.setProperty(MAFProperties.CCF_THRESHOLD_PROPERTY,         "0.90");

        List<MissenseRecord> records = MissenseParser.parse(MIAO_FILE);
        assertEquals(5, records.size());

        assertRecord(records.get(0), "AC-DFCI_AC_PD1-1-TP-NT-SM-9LRI9-SM-9LRIA", "ASPM", "ENST00000367409.4", "S162F", 1.0);
        assertRecord(records.get(4), "AC-DFCI_AC_PD1-1-TP-NT-SM-9LRI9-SM-9LRIA", "RAI1", "ENST00000353383.1", "S776L", 0.98765);
    }

    @Test public void testTCGA() {
        System.setProperty(MAFProperties.TUMOR_BARCODE_COLUMN_PROPERTY,  "Tumor_Sample_Barcode");
        System.setProperty(MAFProperties.HUGO_SYMBOL_COLUMN_PROPERTY,    "Hugo_Symbol");
        System.setProperty(MAFProperties.TRANSCRIPT_COLUMN_PROPERTY,     "Transcript_ID");
        System.setProperty(MAFProperties.CLASSIFICATION_COLUMN_PROPERTY, "Variant_Classification");
        System.setProperty(MAFProperties.VARIANT_TYPE_COLUMN_PROPERTY,   "Variant_Type");
        System.setProperty(MAFProperties.PROTEIN_CHANGE_COLUMN_PROPERTY, "HGVSp_Short");

        List<MissenseRecord> records = MissenseParser.parse(TCGA_FILE);
        assertEquals(7, records.size());

        assertRecord(records.get(0), "TCGA-02-0003-01A-01D-1490-08", "TACC2",   "ENST00000369005", "T38M",  1.0);
        assertRecord(records.get(6), "TCGA-02-0003-01A-01D-1490-08", "SLC2A14", "ENST00000543909", "R252Q", 1.0);
    }

    private void assertRecord(MissenseRecord record,
                              String tumorBarcode,
                              String hugoSymbol,
                              String transcriptID,
                              String proteinChange,
                              double cellFraction) {
        assertEquals(tumorBarcode,  record.getTumorBarcode().getKey());
        assertEquals(hugoSymbol,    record.getHugoSymbol().getKey());
        assertEquals(transcriptID,  record.getTranscriptID().getKey());
        assertEquals(proteinChange, record.getProteinChange().format());
        assertTrue(record.getCellFraction().equals(cellFraction));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MissenseParserTest");
    }
}
