
package jam.junit;

import jam.chem.Concentration;
import jam.peptide.Peptide;
import jam.peptide.PeptideConcentrationProfile;

import org.junit.*;
import static org.junit.Assert.*;

public class PeptideConcentrationProfileTest {
    @Test public void testBasic() {
        Peptide p1 = Peptide.parse("ALY");
        Peptide p2 = Peptide.parse("ILE");

        PeptideConcentrationProfile profile = PeptideConcentrationProfile.create();

        assertFalse(profile.contains(p1));
        assertFalse(profile.contains(p2));

        assertEquals(Concentration.ZERO, profile.lookup(p1));
        assertEquals(Concentration.ZERO, profile.lookup(p2));

        profile.add(p1, Concentration.valueOf(1.0));

        assertTrue(profile.contains(p1));
        assertFalse(profile.contains(p2));

        assertEquals(Concentration.valueOf(1.0), profile.lookup(p1));
        assertEquals(Concentration.ZERO, profile.lookup(p2));

        profile.add(p2, Concentration.valueOf(2.0));

        assertTrue(profile.contains(p1));
        assertTrue(profile.contains(p2));

        assertEquals(Concentration.valueOf(1.0), profile.lookup(p1));
        assertEquals(Concentration.valueOf(2.0), profile.lookup(p2));

        profile.add(p2, Concentration.valueOf(3.0));

        assertTrue(profile.contains(p1));
        assertTrue(profile.contains(p2));

        assertEquals(Concentration.valueOf(1.0), profile.lookup(p1));
        assertEquals(Concentration.valueOf(5.0), profile.lookup(p2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PeptideConcentrationProfileTest");
    }
}
