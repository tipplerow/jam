
package jam.collect;

import org.junit.*;
import static org.junit.Assert.*;

public final class TreeMapTableTest extends MapTableTestBase {
    private static MapTable<String, TestRecord> newTable() {
        return TreeMapTable.create(rec -> rec.key);
    }

    @Test public void testDelete() {
        runDeleteTest(newTable());
    }

    @Test public void testStore() {
        runStoreTest(newTable());
    }

    @Test public void testSelectFilter() {
        runSelectFilterTest(newTable());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.TreeMapTableTest");
    }
}
