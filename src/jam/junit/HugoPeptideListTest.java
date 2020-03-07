
package jam.junit;

import java.util.ArrayList;
import java.util.List;

import jam.hugo.HugoSymbol;
import jam.hugo.HugoPeptideList;
import jam.peptide.Peptide;

import org.junit.*;
import static org.junit.Assert.*;

public class HugoPeptideListTest {
    private static final HugoSymbol hugo = HugoSymbol.instance("A1BG");

    private static final Peptide pep0 = Peptide.instance("AAPPPPVLM");
    private static final Peptide pep1 = Peptide.instance("ADSANYSCV");
    private static final Peptide pep2 = Peptide.instance("YWSLLTSLV");

    @Test public void testBasic() {
        HugoPeptideList list = HugoPeptideList.wrap(hugo, List.of(pep0, pep1, pep2));

        assertEquals(3, list.size());

        assertEquals(hugo, list.getSymbol());
        assertEquals(pep0, list.getPeptide(0));
        assertEquals(pep1, list.getPeptide(1));
        assertEquals(pep2, list.getPeptide(2));

        assertEquals(List.of(pep0, pep1, pep2), list.viewPeptides());
    }

    @Test public void testModify() {
        List<Peptide> peptides = new ArrayList<Peptide>();

        peptides.add(pep0);
        peptides.add(pep1);
        
        HugoPeptideList list = HugoPeptideList.wrap(hugo, peptides);

        assertEquals(2, list.size());
        assertEquals(List.of(pep0, pep1), list.viewPeptides());

        peptides.add(pep2);

        assertEquals(3, list.size());
        assertEquals(List.of(pep0, pep1, pep2), list.viewPeptides());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.HugoPeptideListTest");
    }
}
