
package jam.junit;

import java.util.ArrayList;
import java.util.List;

import jam.peptide.Peptide;
import jam.peptide.ProteinChange;
import jam.peptide.Residue;

import org.junit.*;
import static org.junit.Assert.*;

public class ProteinChangeTest {
    private static final Residue E = Residue.Glu;
    private static final Residue L = Residue.Leu;
    private static final Residue P = Residue.Pro;
    private static final Residue V = Residue.Val;

    @Test public void testApply1() {
        List<Residue> residues = makeResidueList();
        assertEquals(List.of(E, L, P, V), residues);

        ProteinChange pc = new ProteinChange(2, L, V);
        pc.apply(residues);

        assertEquals(List.of(E, V, P, V), residues);
    }

    private List<Residue> makeResidueList() {
        List<Residue> residues = new ArrayList<Residue>();

        residues.add(E);
        residues.add(L);
        residues.add(P);
        residues.add(V);

        return residues;
    }

    @Test(expected = RuntimeException.class)
    public void testApplyInvalidLocation() {
        List<Residue> residues = makeResidueList();
        assertEquals(List.of(E, L, P, V), residues);

        ProteinChange pc = new ProteinChange(11, L, V);
        pc.apply(residues);
    }

    @Test(expected = RuntimeException.class)
    public void testApplyInvalidResidue() {
        List<Residue> residues = makeResidueList();
        assertEquals(List.of(E, L, P, V), residues);

        ProteinChange pc = new ProteinChange(2, V, E);
        pc.apply(residues);
    }

    @Test public void testApply2() {
        List<Residue> residues = makeResidueList();
        assertEquals(List.of(E, L, P, V), residues);

        ProteinChange pc1 = new ProteinChange(4, V, E);
        ProteinChange pc2 = new ProteinChange(2, L, V);

        ProteinChange.apply(List.of(pc1, pc2), residues);
        assertEquals(List.of(E, V, P, E), residues);
    }

    @Test(expected = RuntimeException.class)
    public void testApplyDuplicateLocation() {
        List<Residue> residues = makeResidueList();
        assertEquals(List.of(E, L, P, V), residues);

        ProteinChange pc1 = new ProteinChange(3, P, E);
        ProteinChange pc2 = new ProteinChange(3, P, V);

        ProteinChange.apply(List.of(pc1, pc2), residues);
    }

    @Test public void testFormat() {
        ProteinChange pc = new ProteinChange(123, L, V);
        assertEquals("L123V", pc.format());
    }

    @Test public void testParse() {
        ProteinChange pc = ProteinChange.parse("p.L123V");

        assertEquals(L, pc.getNative());
        assertEquals(V, pc.getMutated());
        assertEquals(123, pc.getPosition());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ProteinChangeTest");
    }
}
