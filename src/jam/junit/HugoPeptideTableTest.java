
package jam.junit;

import java.util.Set;

import jam.peptide.HugoSymbol;
import jam.peptide.HugoPeptideTable;
import jam.peptide.Peptide;

import org.junit.*;
import static org.junit.Assert.*;

public class HugoPeptideTableTest {
    private static HugoSymbol A1BG = HugoSymbol.instance("A1BG");
    private static HugoSymbol A1CF = HugoSymbol.instance("A1CF");
    private static HugoSymbol A2M  = HugoSymbol.instance("A2M");
    private static HugoSymbol ALD1 = HugoSymbol.instance("ALD1");

    @Test public void testLoad() {
        HugoPeptideTable table = HugoPeptideTable.load("data/test/hugo_peptide_table.tsv");
        assertEquals(6, table.size());

        assertTrue(table.contains(A1BG));
        assertTrue(table.contains(A1CF));
        assertTrue(table.contains(A2M));
        assertFalse(table.contains(ALD1));

        assertEquals(2, table.get(A1BG).size());
        assertEquals(3, table.get(A1CF).size());
        assertEquals(1, table.get(A2M).size());
        assertEquals(0, table.get(ALD1).size());

        assertEquals(Peptide.parse("AAPPPPVLM"),  table.get(A1BG).get(0));
        assertEquals(Peptide.parse("GCLGLMVAK"),  table.get(A1BG).get(1));
        assertEquals(Peptide.parse("CASVDNCRL"),  table.get(A1CF).get(0));
        assertEquals(Peptide.parse("GQDLAAYTTY"), table.get(A1CF).get(1));
        assertEquals(Peptide.parse("LPGMELTPM"),  table.get(A1CF).get(2));
        assertEquals(Peptide.parse("AEYNAPCSK"),  table.get(A2M).get(0));

        Set<Peptide> peptides = table.collectUniquePeptides();
        assertEquals(6, peptides.size());

        assertTrue(peptides.contains(Peptide.parse("AAPPPPVLM")));
        assertTrue(peptides.contains(Peptide.parse("GCLGLMVAK")));
        assertTrue(peptides.contains(Peptide.parse("CASVDNCRL")));
        assertTrue(peptides.contains(Peptide.parse("GQDLAAYTTY")));
        assertTrue(peptides.contains(Peptide.parse("LPGMELTPM")));
        assertTrue(peptides.contains(Peptide.parse("AEYNAPCSK")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.HugoPeptideTableTest");
    }
}
