
package jam.junit;

import jam.chop.GermlinePeptideDb;
import jam.peptide.HugoSymbol;
import jam.peptide.Peptide;

import org.junit.*;
import static org.junit.Assert.*;

public class GermlinePeptideDbTest {
    private static HugoSymbol A1BG = HugoSymbol.instance("A1BG");
    private static HugoSymbol A1CF = HugoSymbol.instance("A1CF");
    private static HugoSymbol A2M  = HugoSymbol.instance("A2M");
    private static HugoSymbol ALD1 = HugoSymbol.instance("ALD1");

    static {
        System.setProperty(GermlinePeptideDb.PEPTIDE_FILE_PROPERTY, "data/test/germline_peptide_db.tsv");
    }

    @Test public void testLoad() {
        GermlinePeptideDb db = GermlinePeptideDb.global();
        assertEquals(6, db.size());

        assertTrue(db.contains(A1BG));
        assertTrue(db.contains(A1CF));
        assertTrue(db.contains(A2M));
        assertFalse(db.contains(ALD1));

        assertEquals(2, db.get(A1BG).size());
        assertEquals(3, db.get(A1CF).size());
        assertEquals(1, db.get(A2M).size());
        assertEquals(0, db.get(ALD1).size());

        assertEquals(Peptide.parse("AAPPPPVLM"),  db.get(A1BG).get(0));
        assertEquals(Peptide.parse("GCLGLMVAK"),  db.get(A1BG).get(1));
        assertEquals(Peptide.parse("CASVDNCRL"),  db.get(A1CF).get(0));
        assertEquals(Peptide.parse("GQDLAAYTTY"), db.get(A1CF).get(1));
        assertEquals(Peptide.parse("LPGMELTPM"),  db.get(A1CF).get(2));
        assertEquals(Peptide.parse("AEYNAPCSK"),  db.get(A2M).get(0));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.GermlinePeptideDbTest");
    }
}
