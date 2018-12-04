
package jam.junit;

import jam.bio.Peptide;
import jam.bio.PeptideBinder;
import jam.bio.Residue;
import jam.bio.RIM;
import jam.math.IntRange;

import org.junit.*;
import static org.junit.Assert.*;

final class MiyaMHC extends PeptideBinder {
    public MiyaMHC(Residue... residues) {
        super(new Peptide(residues));
    }

    @Override public RIM getRIM() {
        return RIM.MiyazawaJernigan;
    }

    @Override public IntRange getTargetRange() {
        return new IntRange(0, 1);
    }
}

final class MiyaTCR extends PeptideBinder {
    public MiyaTCR(Residue... residues) {
        super(new Peptide(residues));
    }

    @Override public RIM getRIM() {
        return RIM.MiyazawaJernigan;
    }

    @Override public IntRange getTargetRange() {
        return new IntRange(2, 4);
    }
}

public class PeptideBinderTest extends NumericTestBase {
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
        MiyaMHC mhc  = new MiyaMHC(A, C);
        MiyaTCR tcr  = new MiyaTCR(E, F, G);
        Peptide pep1 = new Peptide(K, I, H, G, E);
        Peptide pep2 = new Peptide(G, F, K, I, H);

        assertDouble(MJ.get(A, K) + MJ.get(C, I), mhc.computeFreeEnergy(pep1));
        assertDouble(MJ.get(A, G) + MJ.get(C, F), mhc.computeFreeEnergy(pep2));

        assertDouble(MJ.get(E, H) + MJ.get(F, G) + MJ.get(G, E), tcr.computeFreeEnergy(pep1));
        assertDouble(MJ.get(E, K) + MJ.get(F, I) + MJ.get(G, H), tcr.computeFreeEnergy(pep2));
    }

    @Test public void testMean() {
        MiyaMHC mhc = new MiyaMHC(A, C);
        MiyaTCR tcr = new MiyaTCR(E, F, G);

        assertDouble(MJ.mean(A) + MJ.mean(C), mhc.meanFreeEnergy());
        assertDouble(MJ.mean(E) + MJ.mean(F) + MJ.mean(G), tcr.meanFreeEnergy());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PeptideBinderTest");
    }
}
