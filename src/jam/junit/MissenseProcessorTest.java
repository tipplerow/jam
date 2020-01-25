
package jam.junit;

import java.io.File;
import java.util.List;
import java.util.Set;

import jam.hugo.HugoSymbol;
import jam.maf.MAFFastaRecord;
import jam.maf.MAFFastaTable;
import jam.maf.MAFProperties;
import jam.maf.MissenseProcessor;
import jam.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class MissenseProcessorTest {
    private static final String MAF_FILE   = "data/test/TCGA_Missense.maf";
    private static final String PROP_FILE  = "data/tcga/tcga_missense_processor.prop";
    private static final String FASTA_FILE = "MissenseProcessorTest.fa";

    @Test public void testTCGA() {
        System.setProperty(MAFProperties.MAF_FILE_PROPERTY, MAF_FILE);
        System.setProperty(MAFProperties.FASTA_FILE_PROPERTY, FASTA_FILE);

        MissenseProcessor.run(PROP_FILE);

        // Note that the output file is written in the same directory
        // as the property file...
        File fastaFile = new File("data/tcga", FASTA_FILE);
        MAFFastaTable table = MAFFastaTable.load(fastaFile.getAbsolutePath());

        assertEquals(Set.of(TumorBarcode.instance("TCGA-02-0003-01A"),
                            TumorBarcode.instance("TCGA-02-0033-01A"),
                            TumorBarcode.instance("TCGA-02-0047-01A")),
                     table.viewBarcodes());

        assertRecord(table, "TCGA-02-0033-01A", "SLC22A9", "MAFQDLLGHA", "EDPRVEVTQF");
        fastaFile.delete();
    }

    private void assertRecord(MAFFastaTable table, String barcode, String symbol, String pepStart, String pepEnd) {
        MAFFastaRecord record = table.lookup(TumorBarcode.instance(barcode), HugoSymbol.instance(symbol));

        assertTrue(record.getPeptide().formatString().startsWith(pepStart));
        assertTrue(record.getPeptide().formatString().endsWith(pepEnd));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MissenseProcessorTest");
    }
}
