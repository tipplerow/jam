
package jam.junit;

import jam.tcga.CancerType;
import jam.tcga.CancerTypeDb;
import jam.tcga.PatientID;

import org.junit.*;
import static org.junit.Assert.*;

public class CancerTypeDbTest {
    private static final CancerType ACC = CancerType.ACC;
    private static final CancerType UVM = CancerType.UVM;

    private static final PatientID A5J1 = PatientID.instance("TCGA-OR-A5J1");
    private static final PatientID A5J2 = PatientID.instance("TCGA-OR-A5J2");
    private static final PatientID A5J9 = PatientID.instance("TCGA-OR-A5J9");
    private static final PatientID A885 = PatientID.instance("TCGA-WC-A885");
    private static final PatientID A888 = PatientID.instance("TCGA-WC-A888");

    @Test public void testAll() {
        CancerTypeDb db = CancerTypeDb.load("data/test/cancer_type.tsv");
        assertEquals(5, db.size());

        assertTrue(db.contains(A5J1));
        assertFalse(db.contains(PatientID.instance("Missing")));

        assertEquals(ACC, db.require(A5J1));
        assertEquals(ACC, db.require(A5J2));
        assertEquals(ACC, db.require(A5J9));
        assertEquals(UVM, db.require(A885));
        assertEquals(UVM, db.require(A888));

        assertNull(db.lookup(PatientID.instance("Missing")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.CancerTypeDbTest");
    }
}
