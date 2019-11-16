
package jam.junit;

import jam.mhc.MHCProperties;
import jam.miya.MiyaMHC;
import jam.peptide.Peptide;
import jam.peptide.Residue;
import jam.peptide.RIM;

import org.junit.*;
import static org.junit.Assert.*;

public class MiyaMHCTest extends NumericTestBase {
    private static final Residue A = Residue.Ala;
    private static final Residue C = Residue.Cys;
    private static final Residue D = Residue.Asp;
    private static final Residue E = Residue.Glu;
    private static final Residue F = Residue.Phe;
    private static final Residue G = Residue.Gly;
    private static final Residue L = Residue.Leu;
    private static final Residue V = Residue.Val;

    private static final RIM MJ = RIM.MiyazawaJernigan;

    static {
        System.setProperty(MHCProperties.TIPS_PROPERTY, "4, 5, 6");
        System.setProperty(MHCProperties.ACTIVATION_ENERGY_PROPERTY, "0.0");
        System.setProperty(MHCProperties.AFFINITY_THRESHOLD_PROPERTY, "10.0");
    }

    private final MiyaMHC mhc  = MiyaMHC.of(A, C, D);
    private final Peptide pep1 = Peptide.of(A, A, A, A, E, F, G);
    private final Peptide pep2 = Peptide.of(A, A, A, A, F, L, V);
        
    @Test public void testFreeEnergy() {
        assertDouble(MJ.get(A, E) + MJ.get(C, F) + MJ.get(D, G), mhc.computeFreeEnergy(pep1));
        assertDouble(MJ.get(A, F) + MJ.get(C, L) + MJ.get(D, V), mhc.computeFreeEnergy(pep2));
    }

    @Test public void testIsPresented() {
        assertFalse(mhc.isPresented(pep1)); // affinity =  8.90
        assertTrue(mhc.isPresented(pep2));  // affinity = 13.12
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MiyaMHCTest");
    }
}
