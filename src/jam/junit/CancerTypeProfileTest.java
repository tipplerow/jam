
package jam.junit;

import jam.peptide.HugoSymbol;
import jam.rna.CancerTypeProfile;
import jam.rna.Expression;
import jam.rna.ExpressionProfile;
import jam.tcga.PatientCancerTypeTable;
import jam.tcga.TumorBarcode;
import jam.tcga.TumorPatientTable;

import org.junit.*;
import static org.junit.Assert.*;

public class CancerTypeProfileTest {
    static {
        System.setProperty(ExpressionProfile.PROFILE_TYPE_PROPERTY, "CANCER_TYPE");
        System.setProperty(CancerTypeProfile.PROFILE_FILE_NAME_PROPERTY, "data/test/rna_median_by_cancer_type.csv");

        System.setProperty(PatientCancerTypeTable.TABLE_FILE_PROPERTY, "data/test/patient_cancer_type_map.tsv");
        System.setProperty(TumorPatientTable.TABLE_FILE_PROPERTY, "data/test/tumor_patient_map.tsv");
    }

    @Test public void testGlobal() {
        ExpressionProfile profile = ExpressionProfile.global();

        TumorBarcode AU5884_T = TumorBarcode.instance("AU5884_T");

        HugoSymbol A1BG  = HugoSymbol.instance("A1BG");
        HugoSymbol A2M   = HugoSymbol.instance("A2M");
        HugoSymbol A2ML1 = HugoSymbol.instance("A2ML1");
        HugoSymbol BRAF  = HugoSymbol.instance("BRAF");

        assertTrue(profile.lookup(AU5884_T,  A1BG).equals(   90.79));
        assertTrue(profile.lookup(AU5884_T,   A2M).equals(20501.45));
        assertTrue(profile.lookup(AU5884_T, A2ML1).equals(    0.93));

        assertNull(profile.lookup(AU5884_T, BRAF));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.CancerTypeProfileTest");
    }
}
