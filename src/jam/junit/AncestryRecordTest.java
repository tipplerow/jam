
package jam.junit;

import java.util.Arrays;

import jam.bio.AncestryRecord;
import jam.bio.Replicator;

import org.junit.*;
import static org.junit.Assert.*;

public class AncestryRecordTest {
    private static final Replicator cell0 = Replicator.create();
    private static final Replicator cell1 = cell0.replicate();
    private static final Replicator cell2 = cell1.replicate();

    private static final AncestryRecord rec0 = AncestryRecord.create(cell0);
    private static final AncestryRecord rec1 = AncestryRecord.create(cell1);
    private static final AncestryRecord rec2 = AncestryRecord.create(cell2);

    @Test public void testChildIndex() {
        assertEquals(0, rec0.getChildIndex());
        assertEquals(1, rec1.getChildIndex());
        assertEquals(2, rec2.getChildIndex());
    }

    @Test public void testFormat() {
        assertEquals("0",     rec0.format());
        assertEquals("1,0",   rec1.format());
        assertEquals("2,1,0", rec2.format());
    }

    @Test public void testFounderIndex() {
        assertEquals(0, rec0.getFounderIndex());
        assertEquals(0, rec1.getFounderIndex());
        assertEquals(0, rec2.getFounderIndex());
    }

    @Test public void testLineage() {
        assertTrue(Arrays.equals(rec0.viewLineage().toLongArray(), new long[] { 0 }));
        assertTrue(Arrays.equals(rec1.viewLineage().toLongArray(), new long[] { 1, 0 }));
        assertTrue(Arrays.equals(rec2.viewLineage().toLongArray(), new long[] { 2, 1, 0 }));
    }

    @Test public void testParse() {
        assertEquals(rec0, AncestryRecord.parse("0"));
        assertEquals(rec1, AncestryRecord.parse("1, 0"));
        assertEquals(rec2, AncestryRecord.parse("2, 1, 0"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.AncestryRecordTest");
    }
}
