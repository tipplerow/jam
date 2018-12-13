
package jam.junit;

import jam.peptide.MJAffinityModel;
import jam.peptide.Peptide;
import jam.peptide.Residue;
import jam.peptide.RIM;

import org.junit.*;
import static org.junit.Assert.*;

public class MJAffinityModelTest extends NumericTestBase {
    private static final Residue A = Residue.Ala;
    private static final Residue C = Residue.Cys;
    private static final Residue D = Residue.Asp;
    private static final Residue E = Residue.Glu;
    private static final Residue F = Residue.Phe;
    private static final Residue G = Residue.Gly;
    private static final Residue H = Residue.His;
    private static final Residue I = Residue.Ile;
    private static final Residue K = Residue.Lys;

    private static final RIM MJ = RIM.MiyazawaJernigan;

    private static final MJAffinityModel model =
        new MJAffinityModel() {
            @Override public double getActivationEnergy() {
                return 0.0;
            }
        };

    @Test public void testCompute() {
        Peptide p1 = Peptide.of(A, C);
        Peptide p2 = Peptide.of(K, I);

        assertDouble(MJ.get(A, K) + MJ.get(C, I), model.computeFreeEnergy(p1, p2));
        assertDouble(MJ.get(A, K) + MJ.get(C, I), model.computeFreeEnergy(p2, p1));

        Peptide p3 = Peptide.of(E, F, G);
        Peptide p4 = Peptide.of(H, G, E);
        
        assertDouble(MJ.get(E, H) + MJ.get(F, G) + MJ.get(G, E), model.computeFreeEnergy(p3, p4));
        assertDouble(MJ.get(E, H) + MJ.get(F, G) + MJ.get(G, E), model.computeFreeEnergy(p4, p3));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MJAffinityModelTest");
    }
}
