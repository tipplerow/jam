
package jam.junit;

import jam.bio.PIPairwiseBinder;
import jam.bio.Peptide;
import jam.bio.Residue;
import jam.bio.RIM;
import jam.math.IntRange;

import org.junit.*;
import static org.junit.Assert.*;

abstract class LocalBinder implements PIPairwiseBinder {
    private final Peptide binder;

    public LocalBinder(Residue... residues) {
        this.binder = Peptide.of(residues);
    }

    @Override public double getActivationEnergy() {
        return 0.0;
    }

    @Override public Peptide getBinderPeptide() {
        return binder;
    }

    @Override public double getFreeEnergy(Residue r1, Residue r2) {
        return RIM.MiyazawaJernigan.get(r1, r2);
    }
}

final class LocalMHC extends LocalBinder {
    public LocalMHC(Residue... residues) {
        super(residues);
    }

    @Override public IntRange getTargetRange() {
        return new IntRange(0, 1);
    }
}

final class LocalTCR extends LocalBinder {
    public LocalTCR(Residue... residues) {
        super(residues);
    }

    @Override public IntRange getTargetRange() {
        return new IntRange(2, 4);
    }
}

public class PIPairwiseBinderTest extends NumericTestBase {
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

    @Test public void testCompute() {
        LocalMHC mhc = new LocalMHC(A, C);
        LocalTCR tcr = new LocalTCR(E, F, G);
        Peptide pep1 = Peptide.of(K, I, H, G, E);
        Peptide pep2 = Peptide.of(G, F, K, I, H);

        assertDouble(MJ.get(A, K) + MJ.get(C, I), mhc.computeFreeEnergy(pep1));
        assertDouble(MJ.get(A, G) + MJ.get(C, F), mhc.computeFreeEnergy(pep2));

        assertDouble(MJ.get(E, H) + MJ.get(F, G) + MJ.get(G, E), tcr.computeFreeEnergy(pep1));
        assertDouble(MJ.get(E, K) + MJ.get(F, I) + MJ.get(G, H), tcr.computeFreeEnergy(pep2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PIPairwiseBinderTest");
    }
}
