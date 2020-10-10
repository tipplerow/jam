
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
        //runDeleteTest(hash());
        //runDeleteTest(tree());
    }
    /*
    @Test public void testInsert() {
        runInsertTest(hash());
        runInsertTest(tree());
    }

    @Test public void testUpdate() {
        runUpdateTest(hash());
        runUpdateTest(tree());
    }

    @Test public void testUpsert() {
        runUpsertTest(hash());
        runUpsertTest(tree());
    }

    @Test public void testSelectFilter() {
        runSelectFilterTest(hash());
        runSelectFilterTest(tree());
    }
    */
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.MapCacheTest");
    }
}
