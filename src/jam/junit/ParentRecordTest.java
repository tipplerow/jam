
package jam.junit;

import jam.bio.ParentRecord;
import jam.bio.Propagator;
import jam.bio.Replicator;

import org.junit.*;
import static org.junit.Assert.*;

public class ParentRecordTest {
    private static final Replicator cell0 = Replicator.create();
    private static final Replicator cell1 = cell0.replicate();
    private static final Replicator cell2 = cell0.replicate();
    private static final Replicator cell3 = cell1.replicate();

    @Test public void testIndexing() {
        runIndexTest(cell0, 0, 0);
        runIndexTest(cell1, 1, 0);
        runIndexTest(cell2, 2, 0);
        runIndexTest(cell3, 3, 1);
    }

    private void runIndexTest(Propagator propagator, long childIndex, long parentIndex) {
        ParentRecord record = ParentRecord.create(propagator);

        assertEquals(childIndex,  record.getChildIndex());
        assertEquals(parentIndex, record.getParentIndex());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ParentRecordTest");
    }
}
