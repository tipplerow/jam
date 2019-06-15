
package jam.junit;

import java.util.Collection;

import jam.ensembl.EnsemblGene;
import jam.ensembl.EnsemblTranscript;
import jam.hugo.HugoMaster;
import jam.hugo.HugoSymbol;

import org.junit.*;
import static org.junit.Assert.*;

public class HugoMasterTest {
    private static HugoSymbol A1BG = HugoSymbol.instance("A1BG");
    private static HugoSymbol A1CF = HugoSymbol.instance("A1CF");
    private static HugoSymbol A2M  = HugoSymbol.instance("A2M");
    private static HugoSymbol ALD1 = HugoSymbol.instance("ALD1");

    @Test public void testLoad() {
        HugoMaster master = HugoMaster.load("data/test/hugo_master_test.tsv");

        assertTrue(master.contains(A1BG));
        assertTrue(master.contains(A1CF));
        assertTrue(master.contains(A2M));
        assertFalse(master.contains(ALD1));

        assertEquals(1, master.getGenes(A1BG).size());
        assertEquals(1, master.getGenes(A1CF).size());
        assertEquals(1, master.getGenes(A2M).size());
        assertEquals(0, master.getGenes(ALD1).size());

        assertEquals(2, master.getTranscripts(A1BG).size());
        assertEquals(3, master.getTranscripts(A1CF).size());
        assertEquals(1, master.getTranscripts(A2M).size());
        assertEquals(0, master.getTranscripts(ALD1).size());

        assertTrue(master.getGenes(A1BG).contains(EnsemblGene.instance("ENSG00000121410")));
        assertTrue(master.getGenes(A1CF).contains(EnsemblGene.instance("ENSG00000148584")));
        assertTrue(master.getGenes(A2M).contains(EnsemblGene.instance("ENSG00000175899")));

        assertTrue(master.getTranscripts(A1BG).contains(EnsemblTranscript.instance("ENST00000596924")));
        assertTrue(master.getTranscripts(A1BG).contains(EnsemblTranscript.instance("ENST00000263100")));
        assertTrue(master.getTranscripts(A1CF).contains(EnsemblTranscript.instance("ENST00000395489")));
        assertTrue(master.getTranscripts(A1CF).contains(EnsemblTranscript.instance("ENST00000282641")));
        assertTrue(master.getTranscripts(A1CF).contains(EnsemblTranscript.instance("ENST00000395495")));
        assertTrue(master.getTranscripts(A2M).contains(EnsemblTranscript.instance("ENST00000543436")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.HugoMasterTest");
    }
}
