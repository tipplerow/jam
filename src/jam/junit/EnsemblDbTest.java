
package jam.junit;

import java.io.File;

import jam.ensembl.EnsemblDb;
import jam.ensembl.EnsemblGene;
import jam.ensembl.EnsemblProtein;
import jam.ensembl.EnsemblTranscript;
import jam.hugo.HugoSymbol;

import org.junit.*;
import static org.junit.Assert.*;

public class EnsemblDbTest {
    private static final File ENSEMBL_FILE = new File("data/test/ensembl_test2.fa");

    @Test public void testSample() {
        EnsemblDb db = EnsemblDb.create(ENSEMBL_FILE);

        assertEquals(5, db.size());

        assertEquals(2, db.count(HugoSymbol.instance("KRAS")));
        assertEquals(3, db.count(HugoSymbol.instance("BRAF")));

        assertEquals(2, db.count(EnsemblGene.instance("ENSG00000133703")));
        assertEquals(3, db.count(EnsemblGene.instance("ENSG00000157764")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.EnsemblDbTest");
    }
}
