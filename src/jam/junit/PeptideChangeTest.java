
package jam.junit;

import jam.peptide.Peptide;
import jam.peptide.PeptideChange;
import jam.peptide.Residue;

import org.junit.*;
import static org.junit.Assert.*;

public class PeptideChangeTest {
    private static final Residue L = Residue.Leu;
    private static final Residue V = Residue.Val;

    @Test public void testFormat() {
        PeptideChange pc = new PeptideChange(123, L, V);
        assertEquals("L123V", pc.format());
    }

    @Test public void testParse() {
        PeptideChange pc = PeptideChange.parse("L123V");

        assertEquals(L, pc.getNative());
        assertEquals(V, pc.getMutated());
        assertEquals(123, pc.getPosition());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PeptideChangeTest");
    }
}
