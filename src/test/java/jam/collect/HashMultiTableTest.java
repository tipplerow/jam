
package jam.collect;

import org.junit.*;
import static org.junit.Assert.*;

public final class HashMultiTableTest extends MultiTableTestBase {
    @Test public void testHash() {
        runNameKeyTest(HashMultiTable.create(rec -> rec.key.name));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.HashMultiTableTest");
    }
}
