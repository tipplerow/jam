
package jam.junit;

import java.util.List;

import jam.fasta.FastaReader;
import jam.fasta.FastaRecord;
import jam.peptide.Peptide;

import org.junit.*;
import static org.junit.Assert.*;

public class FastaReaderTest {
    private static final String ENSEMBL_FILE = "data/test/ensembl_test1.fa";

    @Test public void testEnsembl() {
        FastaRecord record;
        FastaReader reader = FastaReader.open(ENSEMBL_FILE);

        assertTrue(reader.hasNext());
        record = reader.next();

        assertEquals("ENSP00000487941.1", record.getKey());
        assertEquals("pep gene:ENSG00000282431.1", record.getComment());
        assertEquals("GTGG", record.getPeptide().formatString());

        assertTrue(reader.hasNext());
        record = reader.next();

        assertEquals("VLXLRLGELSLY", record.getPeptide().formatString());

        String pep2 = reader.next().getPeptide().formatString();
        String pep3 = reader.next().getPeptide().formatString();

        assertEquals(313, pep2.length());
        assertEquals("MPKLNSTFVTEFLFEGFSSFRRQHKLVFFVVFLTLYLLTLSGNVIIMTIIRLDHHLHTPM", pep2.substring(0, 60));
        assertEquals("SAQSRGAKNSVSL", pep2.substring(300, 313));

        assertEquals(374, pep3.length());
        assertEquals("MSLMVIIMACVGFFLLQGAWPQEEVHRKPSFLALPGHLVKSEETVILQCWSDVMFEHFLL", pep3.substring(0, 60));
        assertEquals("QNRVASSHVPAAGI", pep3.substring(360, 374));

        assertFalse(reader.hasNext());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.FastaReaderTest");
    }
}
