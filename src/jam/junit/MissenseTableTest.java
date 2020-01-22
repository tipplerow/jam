
package jam.junit;

import java.util.List;
import java.util.Map;

import jam.hugo.HugoSymbol;
import jam.maf.MAFProperties;
import jam.maf.MissenseRecord;
import jam.maf.MissenseTable;
import jam.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class MissenseTableTest {
    private static final String TCGA_MAF = "data/test/MAF_TCGA_sample200.maf";

    private static final TumorBarcode barcode1 = TumorBarcode.instance("TCGA-02-0003-01A-01D-1490-08");
    private static final TumorBarcode barcode2 = TumorBarcode.instance("TCGA-02-0033-01A-01D-1490-08");
    private static final TumorBarcode barcode3 = TumorBarcode.instance("TCGA-02-0047-01A-01D-1490-08");
    private static final TumorBarcode barcode4 = TumorBarcode.instance("TCGA-02-0055-01A-01D-1490-08");

    private static final HugoSymbol ABR     = HugoSymbol.instance("ABR");
    private static final HugoSymbol ACADS   = HugoSymbol.instance("ACADS");
    private static final HugoSymbol ADAMTS2 = HugoSymbol.instance("ADAMTS2");
    private static final HugoSymbol GPR158  = HugoSymbol.instance("GPR158");
    private static final HugoSymbol ZNF385D = HugoSymbol.instance("ZNF385D");
    private static final HugoSymbol ZNF583  = HugoSymbol.instance("ZNF583");

    @Test public void testTCGA() {
        System.setProperty(MAFProperties.TUMOR_BARCODE_COLUMN_PROPERTY,  "Tumor_Sample_Barcode");
        System.setProperty(MAFProperties.HUGO_SYMBOL_COLUMN_PROPERTY,    "Hugo_Symbol");
        System.setProperty(MAFProperties.TRANSCRIPT_COLUMN_PROPERTY,     "Transcript_ID");
        System.setProperty(MAFProperties.CLASSIFICATION_COLUMN_PROPERTY, "Variant_Classification");
        System.setProperty(MAFProperties.VARIANT_TYPE_COLUMN_PROPERTY,   "Variant_Type");
        System.setProperty(MAFProperties.PROTEIN_CHANGE_COLUMN_PROPERTY, "HGVSp_Short");

        MissenseTable table = MissenseTable.load(TCGA_MAF);

        assertTrue(table.contains(barcode1));
        assertTrue(table.contains(barcode2));
        assertTrue(table.contains(barcode3));
        assertTrue(table.contains(barcode4));
        assertFalse(table.contains(TumorBarcode.instance("no such")));

        assertEquals(44, table.count(barcode1));
        assertEquals(23, table.count(barcode2));
        assertEquals(56, table.count(barcode3));
        assertEquals(8,  table.count(barcode4));
        assertEquals(0,  table.count(TumorBarcode.instance("no such")));

        assertTrue(table.contains(barcode1, ZNF583));
        assertTrue(table.contains(barcode2, ACADS));
        assertFalse(table.contains(barcode1, ACADS));
        assertFalse(table.contains(barcode2, ZNF583));

        Map<HugoSymbol, List<MissenseRecord>> hugoMap = table.lookup(barcode3);

        // Not 56 because there are two genes with double mutations...
        assertEquals(54, hugoMap.size());

        assertEquals(1, hugoMap.get(ABR).size());
        assertEquals(2, hugoMap.get(ADAMTS2).size());
        assertEquals(2, hugoMap.get(GPR158).size());
        assertEquals(1, hugoMap.get(ZNF385D).size());

        assertRecord(hugoMap.get(ABR).get(0),     barcode3, ABR,     "ENST00000302538", "G532S");
        assertRecord(hugoMap.get(ADAMTS2).get(0), barcode3, ADAMTS2, "ENST00000251582", "T805M");
        assertRecord(hugoMap.get(ADAMTS2).get(1), barcode3, ADAMTS2, "ENST00000251582", "D361N");
        assertRecord(hugoMap.get(GPR158).get(0),  barcode3, GPR158,  "ENST00000376351", "D778Y");
        assertRecord(hugoMap.get(GPR158).get(1),  barcode3, GPR158,  "ENST00000376351", "G784R");
        assertRecord(hugoMap.get(ZNF385D).get(0), barcode3, ZNF385D, "ENST00000281523", "R377W");
    }

    private void assertRecord(MissenseRecord record,
                              TumorBarcode   tumorBarcode,
                              HugoSymbol     hugoSymbol,
                              String         transcriptID,
                              String         proteinChange) {
        assertEquals(tumorBarcode,  record.getTumorBarcode());
        assertEquals(hugoSymbol,    record.getHugoSymbol());
        assertEquals(transcriptID,  record.getTranscriptID().getKey());
        assertEquals(proteinChange, record.getProteinChange().format());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MissenseTableTest");
    }
}
