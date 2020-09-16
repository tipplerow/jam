
package jam.sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class StepRecordCacheTest {
    private static final class TestRecord extends StepRecord {
        private TestRecord(int trialIndex, int timeStep) {
            super(trialIndex, timeStep);
        }
    }
            
    private static final TestRecord rec13 = new TestRecord(1, 3);
    private static final TestRecord rec21 = new TestRecord(2, 1);
    private static final TestRecord rec22 = new TestRecord(2, 2);
    private static final TestRecord rec23 = new TestRecord(2, 3);
    private static final TestRecord rec54 = new TestRecord(5, 4);
    private static final TestRecord rec58 = new TestRecord(5, 8);
    private static final TestRecord rec79 = new TestRecord(7, 9);

    @Test public void testBasic() {
        StepRecordCache<TestRecord> cache = StepRecordCache.create();
        assertContents(cache);

        cache.add(rec54);
        assertContents(cache, rec54);

        cache.add(rec22);
        assertContents(cache, rec22, rec54);

        cache.add(rec79);
        assertContents(cache, rec22, rec54, rec79);

        cache.add(rec13);
        assertContents(cache, rec13, rec22, rec54, rec79);

        cache.add(rec23);
        assertContents(cache, rec13, rec22, rec23, rec54, rec79);

        cache.add(rec21);
        assertContents(cache, rec13, rec21, rec22, rec23, rec54, rec79);

        cache.add(rec58);
        assertContents(cache, rec13, rec21, rec22, rec23, rec54, rec58, rec79);

        assertTrue(cache.lookupTrial(0).isEmpty());
        assertTrue(cache.lookupTrial(3).isEmpty());
        assertTrue(cache.lookupTrial(4).isEmpty());
        assertTrue(cache.lookupTrial(6).isEmpty());
        assertTrue(cache.lookupTrial(8).isEmpty());

        assertCollection(cache.lookupTrial(1), rec13);
        assertCollection(cache.lookupTrial(7), rec79);
        assertCollection(cache.lookupTrial(5), rec54, rec58);
        assertCollection(cache.lookupTrial(2), rec21, rec22, rec23);

        assertTrue(cache.lookupTimeStep(0).isEmpty());
        assertTrue(cache.lookupTimeStep(5).isEmpty());
        assertTrue(cache.lookupTimeStep(6).isEmpty());
        assertTrue(cache.lookupTimeStep(7).isEmpty());

        assertCollection(cache.lookupTimeStep(1), rec21);
        assertCollection(cache.lookupTimeStep(2), rec22);
        assertCollection(cache.lookupTimeStep(3), rec13, rec23);
    }

    private void assertContents(StepRecordCache<TestRecord> cache, TestRecord... records) {
        if (records.length == 0)
            assertTrue(cache.isEmpty());
        else
            assertFalse(cache.isEmpty());

        assertEquals(records.length, cache.size());

        for (TestRecord record : records)
            assertTrue(cache.contains(record));

        Iterator<TestRecord> iterator = cache.iterator();

        for (int k = 0; k < records.length; ++k) {
            assertTrue(iterator.hasNext());
            assertEquals(records[k], iterator.next());
        }

        assertFalse(iterator.hasNext());
    }

    private void assertCollection(Collection<TestRecord> actual, TestRecord... expected) {
        assertEquals(List.of(expected), new ArrayList<TestRecord>(actual));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sim.StepRecordCacheTest");
    }
}
