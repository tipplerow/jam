
package jam.junit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jam.ensembl.EnsemblGene;
import jam.hugo.HugoMaster;
import jam.hugo.HugoSymbol;

import org.junit.*;
import static org.junit.Assert.*;

public class HugoMasterTest {
    private static final HugoSymbol A1BG   = HugoSymbol.instance("A1BG");
    private static final HugoSymbol A1CF   = HugoSymbol.instance("A1CF");
    private static final HugoSymbol A2M    = HugoSymbol.instance("A2M");
    private static final HugoSymbol ACF    = HugoSymbol.instance("ACF");
    private static final HugoSymbol ACF64  = HugoSymbol.instance("ACF64");
    private static final HugoSymbol ASP    = HugoSymbol.instance("ASP");
    private static final HugoSymbol FWP007 = HugoSymbol.instance("FWP007");
    private static final HugoSymbol XYZ    = HugoSymbol.instance("XYZ");

    private static final EnsemblGene A1BG_GENE = EnsemblGene.instance("ENSG00000121410");
    private static final EnsemblGene A1CF_GENE = EnsemblGene.instance("ENSG00000148584");
    private static final EnsemblGene A2M_GENE  = EnsemblGene.instance("ENSG00000175899");

    private static final HugoMaster master = HugoMaster.load("data/test/hugo_master_test.tsv");

    @Test public void testContains() {
        assertTrue(master.contains(A1BG));
        assertTrue(master.contains(A1CF));
        assertTrue(master.contains(A2M));
        assertTrue(master.contains(ACF));
        assertTrue(master.contains(ASP));
        assertFalse(master.contains(XYZ));
    }

    @Test public void testGetGene() {
        assertEquals(A1BG_GENE, master.getGene(A1BG));
        assertEquals(A1CF_GENE, master.getGene(A1CF));
        assertEquals(A1CF_GENE, master.getGene(ACF));
        assertEquals(A1CF_GENE, master.getGene(ACF64));
        assertEquals(A1CF_GENE, master.getGene(ASP));
        assertEquals(A2M_GENE,  master.getGene(A2M));
        assertEquals(A2M_GENE,  master.getGene(FWP007));
        assertNull(master.getGene(XYZ));
    }

    @Test public void testGetAliases() {
        assertTrue(master.getAliases(A1BG).isEmpty());
        assertTrue(master.getAliases(XYZ).isEmpty());

        assertAlias(A1CF,  ACF,  ACF64, ASP);
        assertAlias(ACF,   A1CF, ACF64, ASP);
        assertAlias(ACF64, A1CF, ACF,   ASP);
        assertAlias(ASP,   A1CF, ACF,   ACF64);

        assertAlias(A2M, FWP007);
        assertAlias(FWP007, A2M);
    }

    private void assertAlias(HugoSymbol primary, HugoSymbol... aliases) {
        assertEquals(Set.of(aliases), new HashSet<HugoSymbol>(master.getAliases(primary)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.HugoMasterTest");
    }
}
