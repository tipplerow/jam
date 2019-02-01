
package jam.junit;

import java.util.Set;

import jam.hla.Allele;
import jam.hla.Genotype;

import org.junit.*;
import static org.junit.Assert.*;

public class GenotypeTest {
    private static final Allele A1 = Allele.instance("A0201");
    private static final Allele A2 = Allele.instance("A3010");

    private static final Allele B1 = Allele.instance("B0702");
    private static final Allele B2 = Allele.instance("B3501");

    private static final Allele C1 = Allele.instance("C0103");
    private static final Allele C2 = Allele.instance("C0702");

    private static final Genotype homoA2 = Genotype.instance(A2, A2, B1, B2, C1, C2);
    private static final Genotype homoB1 = Genotype.instance(A1, A2, B1, B1, C1, C2);
    private static final Genotype hetero = Genotype.instance(A1, A2, B1, B2, C1, C2);

    @Test public void testDelete() {
        assertEquals(Genotype.instance(A1, A2, B1, C1, C2), hetero.delete(B2));
        assertTrue(hetero.contains(B2));

        assertEquals(Genotype.instance(A2, A2, B1, C2), homoA2.delete(B2, C1));
        assertTrue(homoA2.contains(B2));
        assertTrue(homoA2.contains(C1));

        assertEquals(Genotype.instance(B1, B2, C1, C2), homoA2.delete(A2));
        assertTrue(homoA2.contains(A2));
    }

    @Test public void testEnumerateLOH3() {
        Genotype hetero = Genotype.instance(A1, B1, C1);

        Set<Genotype> LOH1 = hetero.enumerateLOH(1);
        Set<Genotype> LOH2 = hetero.enumerateLOH(2);

        assertEquals(3, LOH1.size());
        assertEquals(3, LOH2.size());
        
        assertTrue(LOH1.contains(Genotype.instance(A1, B1)));
        assertTrue(LOH1.contains(Genotype.instance(A1, C1)));
        assertTrue(LOH1.contains(Genotype.instance(B1, C1)));
        
        assertTrue(LOH2.contains(Genotype.instance(A1)));
        assertTrue(LOH2.contains(Genotype.instance(B1)));
        assertTrue(LOH2.contains(Genotype.instance(C1)));
    }

    @Test public void testEnumerateLOH4() {
        Genotype hetero = Genotype.instance(A1, A2, B1, C1);

        Set<Genotype> LOH1 = hetero.enumerateLOH(1);
        Set<Genotype> LOH2 = hetero.enumerateLOH(2);
        Set<Genotype> LOH3 = hetero.enumerateLOH(3);

        assertEquals(4, LOH1.size());
        assertEquals(6, LOH2.size());
        assertEquals(4, LOH3.size());
        
        assertTrue(LOH1.contains(Genotype.instance(A1, A2, B1)));
        assertTrue(LOH1.contains(Genotype.instance(A1, A2, C1)));
        assertTrue(LOH1.contains(Genotype.instance(A1, B1, C1)));
        assertTrue(LOH1.contains(Genotype.instance(A2, B1, C1)));
        
        assertTrue(LOH2.contains(Genotype.instance(A1, A2)));
        assertTrue(LOH2.contains(Genotype.instance(A1, B1)));
        assertTrue(LOH2.contains(Genotype.instance(A1, C1)));
        assertTrue(LOH2.contains(Genotype.instance(A2, B1)));
        assertTrue(LOH2.contains(Genotype.instance(A2, C1)));
        assertTrue(LOH2.contains(Genotype.instance(B1, C1)));
        
        assertTrue(LOH3.contains(Genotype.instance(A1)));
        assertTrue(LOH3.contains(Genotype.instance(A2)));
        assertTrue(LOH3.contains(Genotype.instance(B1)));
        assertTrue(LOH3.contains(Genotype.instance(C1)));
    }

    @Test public void testEquals() {
        assertEquals(hetero, Genotype.instance(C2, C1, A1, A2, B2, B1));
    }

    @Test public void testHashCode() {
        assertEquals(hetero.hashCode(), Genotype.instance(C2, C1, A1, A2, B2, B1).hashCode());
    }

    @Test public void testHeteroHomo() {
        assertTrue(homoA2.isHomozygous());
        assertTrue(homoB1.isHomozygous());
        assertTrue(hetero.isHeterozygous());

        assertFalse(homoA2.isHeterozygous());
        assertFalse(homoB1.isHeterozygous());
        assertFalse(hetero.isHomozygous());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.GenotypeTest");
    }
}
