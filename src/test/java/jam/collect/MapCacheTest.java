
package jam.collect;

import org.junit.*;
import static org.junit.Assert.*;

public final class MapCacheTest extends MapTableTestBase {
    private static MapTable<String, TestRecord> hash() {
        return MapTable.hash(rec -> rec.key);
    }

    private static MapTable<String, TestRecord> tree() {
        return MapTable.tree(rec -> rec.key);
    }

    @Test public void testDelete() {
        runDeleteTest(hash());
        runDeleteTest(tree());
    }

    @Test public void testStore() {
        runStoreTest(hash());
        runStoreTest(tree());
    }

    @Test public void testSelectFilter() {
        runFetchFilterTest(hash());
        runFetchFilterTest(tree());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.MapCacheTest");
    }
}
