
package jam.junit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jam.math.IntRange;
import jam.peptide.Peptide;
import jam.peptide.Residue;

import org.junit.*;
import static org.junit.Assert.*;

public class PeptideTest {
    @Test public void testAppend() {
        Peptide p1 = Peptide.of(Residue.Ala);
        Peptide p2 = p1.append(Residue.Cys);
        Peptide p3 = p2.append(Residue.Leu);

        assertEquals(1, p1.length());
        assertEquals(2, p2.length());
        assertEquals(3, p3.length());

        assertEquals(Residue.Ala, p1.at(0));

        assertEquals(Residue.Ala, p2.at(0));
        assertEquals(Residue.Cys, p2.at(1));

        assertEquals(Residue.Ala, p3.at(0));
        assertEquals(Residue.Cys, p3.at(1));
        assertEquals(Residue.Leu, p3.at(2));
    }

    @Test public void testBasic() {
        Peptide pep = Peptide.of(Residue.Ala, Residue.Cys, Residue.Leu);

        assertEquals(3, pep.length());

        assertEquals(Residue.Ala, pep.at(0));
        assertEquals(Residue.Cys, pep.at(1));
        assertEquals(Residue.Leu, pep.at(2));
    }

    @Test public void testEnumerate() {
        assertEquals(     20, Peptide.enumerationSize(1));
        assertEquals(    400, Peptide.enumerationSize(2));
        assertEquals(   8000, Peptide.enumerationSize(3));
        assertEquals( 160000, Peptide.enumerationSize(4));
        assertEquals(3200000, Peptide.enumerationSize(5));

        for (int length = 1; length <= 4; ++length) {
            List<Peptide> peptides = Peptide.enumerate(length);
            assertEquals((int) Math.pow(20, length), peptides.size());
        }
    }

    @Test public void testEquals() {
        Peptide p1 = Peptide.of(Residue.His, Residue.Gln);
        Peptide p2 = Peptide.of(Residue.His, Residue.Gln);
        Peptide p3 = Peptide.of(Residue.Gln, Residue.His);
        Peptide p4 = Peptide.of(Residue.His, Residue.Gln, Residue.Gln);

        assertTrue(p1.equals(p1));
        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(p4));
    }

    @Test public void testFragment() {
        Peptide full = Peptide.of(Residue.Ala, Residue.Cys, Residue.Leu, Residue.Phe, Residue.Arg);
        Peptide frag = full.fragment(new IntRange(2, 3));

        assertEquals(2, frag.length());
        assertEquals(Residue.Leu, frag.at(0));
        assertEquals(Residue.Phe, frag.at(1));
    }

    @Test(expected = RuntimeException.class)
    public void testFragmentInvalid() {
        Peptide full = Peptide.of(Residue.Ala, Residue.Cys, Residue.Leu, Residue.Phe, Residue.Arg);
        Peptide frag = full.fragment(new IntRange(2, 8));
    }

    @Test public void testHashCode() {
        Peptide p1 = Peptide.of(Residue.His, Residue.Gln);
        Peptide p2 = Peptide.of(Residue.His, Residue.Gln);
        Peptide p3 = Peptide.of(Residue.Gln, Residue.His);
        Peptide p4 = Peptide.of(Residue.His, Residue.Gln, Residue.Gln);

        assertTrue(p1.hashCode() == p1.hashCode());
        assertTrue(p1.hashCode() == p2.hashCode());
        assertTrue(p1.hashCode() != p3.hashCode());
        assertTrue(p1.hashCode() != p4.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstructor1() {
        Peptide.of();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstructor2() {
        Peptide.of(List.of());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PeptideTest");
    }
}
