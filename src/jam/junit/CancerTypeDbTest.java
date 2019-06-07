
package jam.junit;

import jam.tcga.CancerType;
import jam.tcga.CancerTypeDb;
import jam.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class CancerTypeDbTest {
    private static final CancerType LUAD = CancerType.LUAD;
    private static final CancerType SKCM = CancerType.SKCM;

    private static final TumorBarcode AL4602_T  = TumorBarcode.instance("AL4602_T");
    private static final TumorBarcode AU5884_T  = TumorBarcode.instance("AU5884_T");
    private static final TumorBarcode BL3403_T  = TumorBarcode.instance("BL3403_T");
    private static final TumorBarcode LSD0167_T = TumorBarcode.instance("LSD0167_T");
    private static final TumorBarcode LSD2057_T = TumorBarcode.instance("LSD2057_T");

    @Test public void testAll() {
        CancerTypeDb db = CancerTypeDb.load("data/test/cancer_type.tsv");
        assertEquals(5, db.size());

        assertTrue(db.contains(AL4602_T));
        assertFalse(db.contains(TumorBarcode.instance("Missing")));

        assertEquals(LUAD, db.require(AL4602_T));
        assertEquals(LUAD, db.require(AU5884_T));
        assertEquals(LUAD, db.require(BL3403_T));
        assertEquals(SKCM, db.require(LSD0167_T));
        assertEquals(SKCM, db.require(LSD2057_T));

        assertNull(db.lookup(TumorBarcode.instance("Missing")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.CancerTypeDbTest");
    }
}
