
package jam.junit;

import java.util.List;

import jam.tcga.MissenseReader;
import jam.tcga.MissenseRecord;

import org.junit.*;
import static org.junit.Assert.*;

public class MissenseReaderTest {
    private static final String MC3_FILE = "data/test/MC3_Missense.maf";
    private static final String MIAO_FILE = "data/test/Miao_Missense.maf";

    @Test public void testMC3() {
        MissenseReader reader = MissenseReader.open(MC3_FILE);

        assertTrue(reader.hasNext());
        assertRecord(reader.next(), "TCGA-02-0003", "TCGA-02-0003-01A-01D-1490-08", "TACC2", "ENST00000369005", "T38M");

        assertTrue(reader.hasNext());
        assertRecord(reader.next(), "TCGA-02-0003", "TCGA-02-0003-01A-01D-1490-08", "PANX3", "ENST00000284288", "R296Q");

        assertFalse(reader.hasNext());
    }

    private void assertRecord(MissenseRecord record,
                              String patientID,
                              String tumorBarcode,
                              String hugoSymbol,
                              String transcriptID,
                              String proteinChange) {
        assertEquals(patientID,     record.getPatientID().getKey());
        assertEquals(tumorBarcode,  record.getTumorBarcode().getKey());
        assertEquals(hugoSymbol,    record.getHugoSymbol().getKey());
        assertEquals(transcriptID,  record.getTranscriptID().getKey());
        assertEquals(proteinChange, record.getProteinChange().format());
    }

    @Test public void testMiao() {
        List<MissenseRecord> records = MissenseReader.read(MIAO_FILE);
        assertEquals(9, records.size());

        assertRecord(records.get(0), "AC_PD1-1", "AC-DFCI_AC_PD1-1-Tumor-SM-9LRI9", "ASPM", "ENST00000367409", "S162F");
        assertRecord(records.get(8), "Y2087", "Y2087_T", "ALMS1", "ENST00000264448", "G3501E");
    }                     

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MissenseReaderTest");
    }
}
