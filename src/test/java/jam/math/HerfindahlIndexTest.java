
package jam.math;

import jam.junit.NumericTestBase;

import org.junit.*;
import static org.junit.Assert.*;

public class HerfindahlIndexTest extends NumericTestBase {
    @Test public void testSingle() {
        HerfindahlIndex index = HerfindahlIndex.compute(10);

        assertEquals(1,   index.getCount());
        assertDouble(1.0, index.getRaw());
        assertDouble(1.0, index.getNormalized());
    }

    @Test public void testEqual() {
        HerfindahlIndex index = HerfindahlIndex.compute(2, 2, 2, 2, 2);

        assertEquals(5,   index.getCount());
        assertDouble(0.2, index.getRaw());
        assertDouble(0.0, index.getNormalized());
    }

    @Test public void testUnequal() {
        HerfindahlIndex index = HerfindahlIndex.compute(1, 2, 3, 4);

        assertEquals(4, index.getCount());
        assertDouble(0.3, index.getRaw());
        assertEquals(0.0667, index.getNormalized(), 0.0001);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.math.HerfindahlIndexTest");
    }
}
