
package jam.collect;

import org.junit.*;
import static org.junit.Assert.*;

public final class MapTableTest extends JamTableTestBase {
    private static JamTable<String, TestRecord> hash() {
        return MapTable.hash(rec -> rec.key);
    }

    private static JamTable<String, TestRecord> tree() {
        return MapTable.tree(rec -> rec.key);
    }

    @Test public void testDelete() {
        runDeleteTest(hash());
        runDeleteTest(tree());
    }

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

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.MapTableTest");
    }
}
