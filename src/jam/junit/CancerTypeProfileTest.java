
package jam.junit;

import jam.peptide.HugoSymbol;
import jam.rna.CancerTypeProfile;
import jam.rna.Expression;
import jam.rna.ExpressionProfile;
import jam.tcga.CancerTypeDb;
import jam.tcga.TumorBarcode;

import org.junit.*;
import static org.junit.Assert.*;

public class CancerTypeProfileTest {
    static {
        System.setProperty(CancerTypeDb.TYPE_FILE_PROPERTY, "data/test/cancer_type2.tsv");
        System.setProperty(ExpressionProfile.PROFILE_TYPE_PROPERTY, "CANCER_TYPE");
        System.setProperty(CancerTypeProfile.PROFILE_FILE_NAME_PROPERTY, "data/test/rna_by_cancer_type.csv");
    }

    @Test public void testAll() {
        TumorBarcode tumor1 = TumorBarcode.instance("Tumor1");
        TumorBarcode tumor2 = TumorBarcode.instance("Tumor2");
        TumorBarcode tumor3 = TumorBarcode.instance("Tumor3");
        TumorBarcode tumor4 = TumorBarcode.instance("Tumor4");
        TumorBarcode tumor5 = TumorBarcode.instance("Tumor5");

        HugoSymbol A1BG  = HugoSymbol.instance("A1BG");
        HugoSymbol A2M   = HugoSymbol.instance("A2M");
        HugoSymbol BRAF  = HugoSymbol.instance("BRAF");

        ExpressionProfile profile = ExpressionProfile.global();

        assertTrue(profile.lookup(tumor1, A1BG).equals(  48.41));
        assertTrue(profile.lookup(tumor1,  A2M).equals(7941.70));

        assertTrue(profile.lookup(tumor2, A1BG).equals(  48.41));
        assertTrue(profile.lookup(tumor2,  A2M).equals(7941.70));

        assertTrue(profile.lookup(tumor3, A1BG).equals(  35.51));
        assertTrue(profile.lookup(tumor3,  A2M).equals(6190.17));

        assertTrue(profile.lookup(tumor4, A1BG).equals(  131.58));
        assertTrue(profile.lookup(tumor4,  A2M).equals(12101.10));

        assertTrue(profile.lookup(tumor5, A1BG).equals(  131.58));
        assertTrue(profile.lookup(tumor5,  A2M).equals(12101.10));

        assertNull(profile.lookup(tumor1, BRAF));
    }

    @Test(expected = RuntimeException.class)
    public void testMissingSymbol() {
        HugoSymbol   BRAF   = HugoSymbol.instance("BRAF");
        TumorBarcode tumor1 = TumorBarcode.instance("Tumor1");

        ExpressionProfile.global().require(tumor1, BRAF);
    }

    @Test(expected = RuntimeException.class)
    public void testMissingTumor() {
        HugoSymbol   A1BG   = HugoSymbol.instance("A1BG");
        TumorBarcode tumorX = TumorBarcode.instance("TumorX");

        ExpressionProfile.global().require(tumorX, A1BG);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.CancerTypeProfileTest");
    }
}
