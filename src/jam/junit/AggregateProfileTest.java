
package jam.junit;

import jam.peptide.HugoSymbol;
import jam.rna.Expression;
import jam.rna.ExpressionProfile;
import jam.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class AggregateProfileTest {
    static {
        System.setProperty(ExpressionProfile.EXPRESSION_PROFILE_TYPE_PROPERTY, "AGGREGATE");
        System.setProperty(ExpressionProfile.EXPRESSION_PROFILE_FILE_NAME_PROPERTY, "data/test/RNA_Median.tsv");
    }

    @Test public void testAll() {
        TumorBarcode tumor1 = TumorBarcode.instance("tumor1");
        TumorBarcode tumor2 = TumorBarcode.instance("tumor2");

        HugoSymbol A1BG  = HugoSymbol.instance("A1BG");
        HugoSymbol A1CF  = HugoSymbol.instance("A1CF");
        HugoSymbol A2BP1 = HugoSymbol.instance("A2BP1");
        HugoSymbol A2LD1 = HugoSymbol.instance("A2LD1");
        HugoSymbol A2M   = HugoSymbol.instance("A2M");
        HugoSymbol BRAF  = HugoSymbol.instance("BRAF");

        ExpressionProfile profile = ExpressionProfile.global();

        assertTrue(profile.lookup(tumor1, A1BG).equals(   79.566));
        assertTrue(profile.lookup(tumor1, A1CF).equals(    0.0));
        assertTrue(profile.lookup(tumor1, A2BP1).equals(   0.544));
        assertTrue(profile.lookup(tumor1, A2LD1).equals(  95.357));
        assertTrue(profile.lookup(tumor1, A2M).equals(  9740.280));

        assertTrue(profile.lookup(tumor2, A1BG).equals(   79.566));
        assertTrue(profile.lookup(tumor2, A1CF).equals(    0.0));
        assertTrue(profile.lookup(tumor2, A2BP1).equals(   0.544));
        assertTrue(profile.lookup(tumor2, A2LD1).equals(  95.357));
        assertTrue(profile.lookup(tumor2, A2M).equals(  9740.280));

        assertNull(profile.lookup(tumor1, BRAF));
        assertNull(profile.lookup(tumor2, BRAF));
    }

    @Test(expected = RuntimeException.class)
    public void testMissing() {
        HugoSymbol   BRAF   = HugoSymbol.instance("BRAF");
        TumorBarcode tumor1 = TumorBarcode.instance("tumor1");

        ExpressionProfile.global().require(tumor1, BRAF);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.AggregateProfileTest");
    }
}
