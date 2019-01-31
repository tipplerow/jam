
package jam.junit;

import jam.hla.Allele;
import jam.hla.Locus;

import org.junit.*;
import static org.junit.Assert.*;

public class AlleleTest {
    private static final Allele A1 = Allele.instance("A0201");
    private static final Allele A2 = Allele.instance("A3010");

    private static final Allele B1 = Allele.instance("B0702");
    private static final Allele B2 = Allele.instance("B3501");

    private static final Allele C1 = Allele.instance("C0103");
    private static final Allele C2 = Allele.instance("C0702");

    @Test public void testCompare() {
        assertTrue(B1.compareTo(A1)  > 0);
        assertTrue(B1.compareTo(A2)  > 0);
        assertTrue(B1.compareTo(B1) == 0);
        assertTrue(B1.compareTo(B2)  < 0);
        assertTrue(B1.compareTo(C1)  < 0);
        assertTrue(B1.compareTo(C2)  < 0);
    }

    @Test public void testEquals() {
        assertTrue(Allele.instance("HLA-A*02:01").equals(Allele.instance("A0201")));
        assertFalse(Allele.instance("HLA-A*02:01").equals(Allele.instance("A0102")));
    }

    @Test public void testHashCode() {
        assertEquals( 201, A1.hashCode());
        assertEquals(3010, A2.hashCode());

        assertEquals(10702, B1.hashCode());
        assertEquals(13501, B2.hashCode());

        assertEquals(20103, C1.hashCode());
        assertEquals(20702, C2.hashCode());
    }

    @Test public void testParse() {
        assertEquals(A1, Allele.instance("HLA-A*02:01"));
        assertEquals(A1, Allele.instance("HLA-A-02:01"));
        assertEquals(A1, Allele.instance("HLA-A-02-01"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.AlleleTest");
    }
}
