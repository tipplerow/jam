
package jam.junit;

import jam.hla.Allele;
import jam.hla.Genotype;
import jam.hla.GenotypeDb;
import jam.tcga.PatientID;

import org.junit.*;
import static org.junit.Assert.*;

public class GenotypeDbTest {
    private static final Allele A0101 = Allele.instance("A0101");
    private static final Allele A0201 = Allele.instance("A0201");
    private static final Allele A0301 = Allele.instance("A0301");
    private static final Allele A1101 = Allele.instance("A1101");
    private static final Allele A2601 = Allele.instance("A2601");
    private static final Allele A3201 = Allele.instance("A3201");

    private static final Allele B0702 = Allele.instance("B0702");
    private static final Allele B1501 = Allele.instance("B1501");
    private static final Allele B4402 = Allele.instance("B4402");
    private static final Allele B4403 = Allele.instance("B4403");
    private static final Allele B5201 = Allele.instance("B5201");

    private static final Allele C0303 = Allele.instance("C0303");
    private static final Allele C0401 = Allele.instance("C0401");
    private static final Allele C0501 = Allele.instance("C0501");
    private static final Allele C0602 = Allele.instance("C0602");
    private static final Allele C0702 = Allele.instance("C0702");
    private static final Allele C1202 = Allele.instance("C1202");
    private static final Allele C1601 = Allele.instance("C1601");

    private static final PatientID Pat01 = PatientID.instance("Pat01");
    private static final PatientID Pat02 = PatientID.instance("Pat02");
    private static final PatientID Pat03 = PatientID.instance("Pat03");
    private static final PatientID Pat04 = PatientID.instance("Pat04");
    private static final PatientID Pat05 = PatientID.instance("Pat05");

    static {
        System.setProperty(GenotypeDb.FILE_NAME_PROPERTY, "data/test/genotype1.csv");
    }

    @Test public void test1() {
        GenotypeDb db = GenotypeDb.global();
        assertEquals(4, db.size());

        assertTrue(db.contains(Pat01));
        assertFalse(db.contains(Pat05));

        assertEquals(Genotype.instance(A0201, A2601, B4402, B4403, C0501, C1601), db.require(Pat01));
        assertEquals(Genotype.instance(A0101, A0201, B4402, C0401, C0501), db.require(Pat02));
        assertEquals(Genotype.instance(A1101, A3201, B4402, C0501), db.require(Pat03));
        assertEquals(Genotype.instance(A0101, B0702, C0602, C0702), db.require(Pat04));
    }

    @Test public void test2() {
        GenotypeDb db = GenotypeDb.load("data/test/genotype2.csv");
        assertEquals(2, db.size());

        assertEquals(Genotype.instance(A0201, A3201, B1501, B1501, C0303, C0303), db.require(Pat01));
        assertEquals(Genotype.instance(A0301, A1101, B0702, B5201, C0702, C1202), db.require(Pat02));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.GenotypeDbTest");
    }
}
