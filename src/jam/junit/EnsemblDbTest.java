
package jam.junit;

import java.io.File;

import jam.ensembl.EnsemblDb;
import jam.ensembl.EnsemblGene;
import jam.ensembl.EnsemblTranscript;
import jam.hugo.HugoSymbol;

import org.junit.*;
import static org.junit.Assert.*;

public class EnsemblDbTest {
    private static final File ENSEMBL_FILE = new File("data/test/ensembl_test2.fa");

    @Test public void testSample() {
        EnsemblDb db = EnsemblDb.create(ENSEMBL_FILE);

        HugoSymbol BRAF_Hugo = HugoSymbol.instance("BRAF");
        HugoSymbol KRAS_Hugo = HugoSymbol.instance("KRAS");

        EnsemblGene BRAF_Gene = EnsemblGene.instance("ENSG00000157764");
        EnsemblGene KRAS_Gene = EnsemblGene.instance("ENSG00000133703");

        EnsemblTranscript BRAF_Trans1 = EnsemblTranscript.instance("ENST00000496384");
        EnsemblTranscript BRAF_Trans2 = EnsemblTranscript.instance("ENST00000644969");
        EnsemblTranscript BRAF_Trans3 = EnsemblTranscript.instance("ENST00000646891");

        EnsemblTranscript KRAS_Trans1 = EnsemblTranscript.instance("ENST00000311936");
        EnsemblTranscript KRAS_Trans2 = EnsemblTranscript.instance("ENST00000256078");

        assertEquals(5, db.size());

        assertEquals(2, db.count(KRAS_Hugo));
        assertEquals(3, db.count(BRAF_Hugo));

        assertEquals(2, db.count(KRAS_Gene));
        assertEquals(3, db.count(BRAF_Gene));

        assertEquals(BRAF_Hugo, db.getHugo(BRAF_Gene));
        assertEquals(KRAS_Hugo, db.getHugo(KRAS_Gene));

        assertEquals(BRAF_Hugo, db.getHugo(BRAF_Trans1));
        assertEquals(BRAF_Hugo, db.getHugo(BRAF_Trans2));
        assertEquals(BRAF_Hugo, db.getHugo(BRAF_Trans3));
        assertEquals(KRAS_Hugo, db.getHugo(KRAS_Trans1));
        assertEquals(KRAS_Hugo, db.getHugo(KRAS_Trans2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.EnsemblDbTest");
    }
}
