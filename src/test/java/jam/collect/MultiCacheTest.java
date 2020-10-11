
package jam.collect;

import org.junit.*;
import static org.junit.Assert.*;

public final class MultiCacheTest extends MultiTableTestBase {
    @Test public void testHash() {
        runNameKeyTest(MultiTable.hash(rec -> rec.key.name));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.MultiCacheTest");
    }
}
