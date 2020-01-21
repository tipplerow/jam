
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

        List<MissenseRecord> records = MissenseParser.parse(MIAO_FILE);
        assertEquals(6, records.size());

        assertRecord(records.get(0), "AC-DFCI_AC_PD1-1-TP-NT-SM-9LRI9-SM-9LRIA", "ASPM",  "ENST00000367409.4", "S162F");
        assertRecord(records.get(5), "AC-DFCI_AC_PD1-1-TP-NT-SM-9LRI9-SM-9LRIA", "PTPRD", "ENST00000381196.4", "P1257S");
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

        assertRecord(records.get(0), "TCGA-02-0003-01A-01D-1490-08", "TACC2",   "ENST00000369005", "T38M");
        assertRecord(records.get(6), "TCGA-02-0003-01A-01D-1490-08", "SLC2A14", "ENST00000543909", "R252Q");
    }

    private void assertRecord(MissenseRecord record,
                              String tumorBarcode,
                              String hugoSymbol,
                              String transcriptID,
                              String proteinChange) {
        assertEquals(tumorBarcode,  record.getTumorBarcode().getKey());
        assertEquals(hugoSymbol,    record.getHugoSymbol().getKey());
        assertEquals(transcriptID,  record.getTranscriptID().getKey());
        assertEquals(proteinChange, record.getProteinChange().format());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MissenseParserTest");
    }
}
