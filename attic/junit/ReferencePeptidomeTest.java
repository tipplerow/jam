
package jam.junit;

import jam.peptide.Peptide;
import jam.peptide.Peptidome;
import jam.peptide.ReferencePeptidome;

import org.junit.*;
import static org.junit.Assert.*;

public class ReferencePeptidomeTest {
    @Test public void testThymic9() {
        Peptidome peptidome = ReferencePeptidome.THYMIC9.getPeptides();
        assertEquals(977642, peptidome.size());

        assertTrue(peptidome.contains(Peptide.parse("AAAAAAAAA")));
        assertTrue(peptidome.contains(Peptide.parse("YYYYLLPGL")));

        assertFalse(peptidome.contains(Peptide.parse("AAAAAAAAI")));
        assertFalse(peptidome.contains(Peptide.parse("YYYYYYYYY")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ReferencePeptidomeTest");
    }
}
