
package jam.collect;

import org.junit.*;
import static org.junit.Assert.*;

public final class HashMapTableTest extends MapTableTestBase {
    private static MapTable<String, TestRecord> newTable() {
        return HashMapTable.create(rec -> rec.key);
    }

    @Test public void testDelete() {
        runDeleteTest(newTable());
    }

    @Test public void testStore() {
        runStoreTest(newTable());
    }

    @Test public void testSelectFilter() {
        runFetchFilterTest(newTable());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.HashMapTableTest");
    }
}
