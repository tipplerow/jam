
package jam.junit;

import java.util.List;

import jam.hist.Bin;
import jam.math.DoubleRange;

import org.junit.*;
import static org.junit.Assert.*;

public class BinTest extends NumericTestBase {
    @Test public void testCreate1() {
        List<Bin> bins = Bin.create(1.0, 1.2);

        assertEquals(1, bins.size());

        assertEquals(0, bins.get(0).getCount());
        assertFalse(bins.get(0).isFrozen());
        assertEquals(DoubleRange.closed(1.0, 1.2), bins.get(0).getRange());
    }

    @Test public void testCreate2() {
        List<Bin> bins = Bin.create(1.0, 1.1, 1.5);

        assertEquals(2, bins.size());

        for (Bin bin : bins)
            assertEquals(0, bin.getCount());

        for (Bin bin : bins)
            assertFalse(bin.isFrozen());

        assertEquals(DoubleRange.leftClosed(1.0, 1.1), bins.get(0).getRange());
        assertEquals(DoubleRange.closed(    1.1, 1.5), bins.get(1).getRange());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalid1() {
        Bin.create(1.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalid2() {
        Bin.create(1.0, 2.0, 3.0, 2.5, 3.5);
    }

    @Test public void testSpan() {
        List<Bin> bins = Bin.span(1.0, 3.0, 4);

        assertEquals(4, bins.size());

        for (Bin bin : bins)
            assertEquals(0, bin.getCount());

        for (Bin bin : bins)
            assertFalse(bin.isFrozen());

        assertEquals(DoubleRange.leftClosed(1.0, 1.5), bins.get(0).getRange());
        assertEquals(DoubleRange.leftClosed(1.5, 2.0), bins.get(1).getRange());
        assertEquals(DoubleRange.leftClosed(2.0, 2.5), bins.get(2).getRange());
        assertEquals(DoubleRange.closed(    2.5, 3.0), bins.get(3).getRange());
    }

    @Test public void testSpanLog() {
        List<Bin> bins = Bin.spanLog(0.01, 10.0, 3);

        assertEquals(3, bins.size());

        for (Bin bin : bins)
            assertEquals(0, bin.getCount());

        for (Bin bin : bins)
            assertFalse(bin.isFrozen());

        assertEquals(DoubleRange.leftClosed(0.01, 0.1), bins.get(0).getRange());
        assertEquals(DoubleRange.leftClosed(0.1,  1.0), bins.get(1).getRange());
        assertEquals(DoubleRange.closed(    1.0, 10.0), bins.get(2).getRange());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.BinTest");
    }
}