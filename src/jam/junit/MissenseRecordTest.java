
package jam.junit;

import java.util.List;

import jam.ensembl.EnsemblTranscript;
import jam.hugo.HugoSymbol;
import jam.peptide.Peptide;
import jam.peptide.ProteinChange;
import jam.tcga.CellFraction;
import jam.tcga.MissenseRecord;
import jam.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class MissenseRecordTest {
    @Test public void testApply() {
        TumorBarcode      barcode    = TumorBarcode.instance("tumor");
        HugoSymbol        symbol     = HugoSymbol.instance("TSEN34");
        EnsemblTranscript transcript = EnsemblTranscript.instance("ENST00000614734");
        CellFraction      CCF        = CellFraction.UNIT;

        ProteinChange change0 = ProteinChange.parse("M1I");
        ProteinChange change1 = ProteinChange.parse("R2K");
        ProteinChange change2 = ProteinChange.parse("R3G");

        MissenseRecord record0 = new MissenseRecord(barcode, symbol, transcript, change0, CCF);
        MissenseRecord record1 = new MissenseRecord(barcode, symbol, transcript, change1, CCF);
        MissenseRecord record2 = new MissenseRecord(barcode, symbol, transcript, change2, CCF);

        Peptide expected =
            Peptide.parse("IKGMLVVEVANGRSLVWGAEAVQALRERLGVGGRTVGALPRGPRQNSRLGLPLLLMPEEA" +
                          "RLLAEIGAVTLVSAPRPDSRHHSLALTSFKRQQEESFQEQSALAAEARETRRQELLEKIT" +
                          "EGQAAKKQKLEQASGASSSQEAGSSQAAKEDETSDGQASGEQEEAGPSSSQAGPSNGVAP" +
                          "LPRSALLVQLATARPRPVKARPLDWRVQSKDWPHAGRPAHELRYSIYRDLWERGFFLSAA" +
                          "GKFGGDFLVYPGDPLRFHAHYIAQCWAPEDTIPLQDLVAAGRLGTSVRKTLLLCSPQPDG" +
                          "FKSQEVSINLVYKCS");

        assertEquals(expected, MissenseRecord.apply(List.of(record0, record1, record2)));
    }

    @Test public void testGermlineProtein() {
        TumorBarcode      barcode    = TumorBarcode.instance("tumor");
        HugoSymbol        symbol     = HugoSymbol.instance("TSEN34");
        EnsemblTranscript transcript = EnsemblTranscript.instance("ENST00000614734");
        ProteinChange     change     = ProteinChange.parse("E8I");
        CellFraction      CCF        = CellFraction.UNIT;

        MissenseRecord record =
            new MissenseRecord(barcode, symbol, transcript, change, CCF);

        Peptide expected =
            Peptide.parse("MRRMLVVEVANGRSLVWGAEAVQALRERLGVGGRTVGALPRGPRQNSRLGLPLLLMPEEA" +
                          "RLLAEIGAVTLVSAPRPDSRHHSLALTSFKRQQEESFQEQSALAAEARETRRQELLEKIT" +
                          "EGQAAKKQKLEQASGASSSQEAGSSQAAKEDETSDGQASGEQEEAGPSSSQAGPSNGVAP" +
                          "LPRSALLVQLATARPRPVKARPLDWRVQSKDWPHAGRPAHELRYSIYRDLWERGFFLSAA" +
                          "GKFGGDFLVYPGDPLRFHAHYIAQCWAPEDTIPLQDLVAAGRLGTSVRKTLLLCSPQPDG" +
                          "FKSQEVSINLVYKCS");

        assertEquals(expected, record.getGermlineProtein());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MissenseRecordTest");
    }
}
