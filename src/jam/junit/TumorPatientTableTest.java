
package jam.junit;

import jam.tcga.PatientID;
import jam.tcga.TumorBarcode;
import jam.tcga.TumorPatientTable;

import org.junit.*;
import static org.junit.Assert.*;

public class TumorPatientTableTest {
    private static final PatientID AL4602 = PatientID.instance("AL4602");
    private static final PatientID AU5884 = PatientID.instance("AU5884");
    private static final PatientID BL3403 = PatientID.instance("BL3403");

    private static final TumorBarcode AL4602_T1 = TumorBarcode.instance("AL4602_T1");
    private static final TumorBarcode AL4602_T2 = TumorBarcode.instance("AL4602_T2");
    private static final TumorBarcode AU5884_T  = TumorBarcode.instance("AU5884_T");
    private static final TumorBarcode BL3403_T  = TumorBarcode.instance("BL3403_T");

    static {
        System.setProperty(TumorPatientTable.TABLE_FILE_PROPERTY, "data/test/tumor_patient_map.tsv");
    }

    @Test public void testGlobal() {
        TumorPatientTable table = TumorPatientTable.global();
        assertEquals(4, table.size());

        assertTrue(table.contains(AL4602_T1));
        assertFalse(table.contains(TumorBarcode.instance("Missing")));

        assertEquals(AL4602, table.require(AL4602_T1));
        assertEquals(AL4602, table.require(AL4602_T2));
        assertEquals(AU5884, table.require(AU5884_T));
        assertEquals(BL3403, table.require(BL3403_T));

        assertNull(table.lookup(TumorBarcode.instance("Missing")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TumorPatientTableTest");
    }
}
