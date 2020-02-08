
package jam.junit;

import java.util.Set;

import jam.hla.Allele;
import jam.hla.Genotype;
import jam.hla.PatientGenotypeTable;
import jam.tcga.PatientID;

import org.junit.*;
import static org.junit.Assert.*;

public class PatientGenotypeTableTest {
    private static final Allele A0201 = Allele.instance("A0201");
    private static final Allele A0301 = Allele.instance("A0301");
    private static final Allele A1101 = Allele.instance("A1101");
    private static final Allele A3201 = Allele.instance("A3201");

    private static final Allele B0702 = Allele.instance("B0702");
    private static final Allele B1501 = Allele.instance("B1501");
    private static final Allele B5201 = Allele.instance("B5201");

    private static final Allele C0303 = Allele.instance("C0303");
    private static final Allele C0702 = Allele.instance("C0702");
    private static final Allele C1202 = Allele.instance("C1202");

    private static final PatientID Pat01 = PatientID.instance("TCGA-02-0047");
    private static final PatientID Pat02 = PatientID.instance("TCGA-02-2483");
    private static final PatientID Pat03 = PatientID.instance("TCGA-02-2486");
    private static final PatientID Pat04 = PatientID.instance("NotPresent");

    private static final String FILE_NAME = "data/test/patient_genotype_table.csv";

    @Test public void testLoad() {
        PatientGenotypeTable table = PatientGenotypeTable.load(FILE_NAME);
        assertEquals(3, table.size());

        assertTrue(table.contains(Pat01));
        assertTrue(table.contains(Pat02));
        assertTrue(table.contains(Pat03));
        assertFalse(table.contains(Pat04));

        assertEquals(Set.of(Pat01, Pat02, Pat03), table.viewPatients());

        assertEquals(Genotype.instance(A0301, B0702, C0702), table.require(Pat03));
        assertEquals(Genotype.instance(A0201, A3201, B1501, C0303), table.require(Pat01));
        assertEquals(Genotype.instance(A0301, A1101, B0702, B5201, C0702, C1202), table.require(Pat02));

        assertNull(table.lookup(Pat04));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PatientGenotypeTableTest");
    }
}