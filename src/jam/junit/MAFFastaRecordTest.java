
package jam.junit;

import jam.fasta.FastaRecord;
import jam.hugo.HugoSymbol;
import jam.maf.MAFFastaRecord;
import jam.peptide.Peptide;
import jam.tcga.CellFraction;
import jam.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class MAFFastaRecordTest {
    @Test public void testParse() {
        Peptide peptide = Peptide.parse("VLXLRLGELSLY");

        FastaRecord record1 =
            new FastaRecord("Tumor_Barcode:BARCODE",
                            "Hugo_Symbol:HUGO CCF:0.123",
                            peptide);
                            
        MAFFastaRecord record2 =
            MAFFastaRecord.parse(record1);

        assertEquals(TumorBarcode.instance("BARCODE"), record2.getTumorBarcode());
        assertEquals(HugoSymbol.instance("HUGO"), record2.getHugoSymbol());
        assertTrue(record2.getCellFraction().equals(0.123));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MAFFastaRecordTest");
    }
}
