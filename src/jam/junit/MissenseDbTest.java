
package jam.junit;

import java.util.List;
import java.util.Map;

import jam.peptide.HugoSymbol;
import jam.tcga.MissenseDb;
import jam.tcga.MissenseRecord;
import jam.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class MissenseDbTest {
    private static final String MIAO_FILE = "data/test/Miao_Missense.maf";

    private static final TumorBarcode barcode1 = TumorBarcode.instance("AC-DFCI_AC_PD1-1-Tumor-SM-9LRI9");
    private static final TumorBarcode barcode2 = TumorBarcode.instance("Y2087_T");
    private static final TumorBarcode barcode3 = TumorBarcode.instance("NotFound");

    private static final HugoSymbol ALMS1  = HugoSymbol.instance("ALMS1");
    private static final HugoSymbol ASPM   = HugoSymbol.instance("ASPM");
    private static final HugoSymbol PRRC1  = HugoSymbol.instance("PRRC1");
    private static final HugoSymbol RINT1  = HugoSymbol.instance("RINT1");
    private static final HugoSymbol RNF31  = HugoSymbol.instance("RNF31");
    private static final HugoSymbol RXFP3  = HugoSymbol.instance("RXFP3");
    private static final HugoSymbol TTC39B = HugoSymbol.instance("TTC39B");

    private static final MissenseDb DB = MissenseDb.load(MIAO_FILE);

    @Test public void testMiao() {
        assertTrue(DB.contains(barcode1));
        assertTrue(DB.contains(barcode2));
        assertFalse(DB.contains(barcode3));

        assertTrue(DB.contains(barcode1, ASPM));
        assertFalse(DB.contains(barcode1, ALMS1));

        assertFalse(DB.contains(barcode2, ASPM));
        assertTrue(DB.contains(barcode2, ALMS1));

        assertFalse(DB.contains(barcode3, ASPM));
        assertFalse(DB.contains(barcode3, ALMS1));

        Map<HugoSymbol, List<MissenseRecord>> hugoMap = DB.lookup(barcode1);
        assertEquals(4, hugoMap.size());

        assertEquals(1, hugoMap.get(ASPM).size());
        assertEquals(1, hugoMap.get(RINT1).size());
        assertEquals(3, hugoMap.get(RNF31).size());
        assertEquals(1, hugoMap.get(RXFP3).size());

        assertRecord(hugoMap.get(ASPM).get(0),  barcode1, ASPM,  "ENST00000367409", "S162F");
        assertRecord(hugoMap.get(RINT1).get(0), barcode1, RINT1, "ENST00000257700", "P3T");
        assertRecord(hugoMap.get(RNF31).get(0), barcode1, RNF31, "ENST00000324103", "E346K");
        assertRecord(hugoMap.get(RNF31).get(1), barcode1, RNF31, "ENST00000324103", "E506Q");
        assertRecord(hugoMap.get(RNF31).get(2), barcode1, RNF31, "ENST00000324103", "E518K");
        assertRecord(hugoMap.get(RXFP3).get(0), barcode1, RXFP3, "ENST00000330120", "D296N");

        hugoMap = DB.lookup(barcode2);
        assertEquals(3, hugoMap.size());

        assertEquals(1, hugoMap.get(ALMS1).size());
        assertEquals(1, hugoMap.get(PRRC1).size());
        assertEquals(1, hugoMap.get(TTC39B).size());

        assertRecord(hugoMap.get(ALMS1).get(0),  barcode2, ALMS1,  "ENST00000264448", "G3501E");
        assertRecord(hugoMap.get(PRRC1).get(0),  barcode2, PRRC1,  "ENST00000442138", "A208P");
        assertRecord(hugoMap.get(TTC39B).get(0), barcode2, TTC39B, "ENST00000512701", "A47S");

        assertTrue(DB.lookup(barcode3).isEmpty());

        List<MissenseRecord> recordList = DB.lookup(barcode1, ASPM);
        assertEquals(1, recordList.size());

        assertRecord(recordList.get(0), barcode1, ASPM, "ENST00000367409", "S162F");

        recordList = DB.lookup(barcode1, RNF31);
        assertEquals(3, recordList.size());

        assertRecord(recordList.get(0), barcode1, RNF31, "ENST00000324103", "E346K");
        assertRecord(recordList.get(1), barcode1, RNF31, "ENST00000324103", "E506Q");
        assertRecord(recordList.get(2), barcode1, RNF31, "ENST00000324103", "E518K");

        assertTrue(DB.lookup(barcode1, PRRC1).isEmpty());
        assertTrue(DB.lookup(barcode2, RINT1).isEmpty());
        assertTrue(DB.lookup(barcode3, PRRC1).isEmpty());
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

    @Test(expected = UnsupportedOperationException.class)
    public void testModifyHugoMap() {
        DB.lookup(barcode1).put(ASPM, List.of());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testModifyRecordList() {
        DB.lookup(barcode1, ASPM).remove(0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MissenseDbTest");
    }
}
