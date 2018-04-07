
package jam.junit;

import java.util.List;

import jam.hist.Bin;
import jam.math.DoubleRange;

import org.junit.*;
import static org.junit.Assert.*;

public class BinTest extends NumericTestBase {
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

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.BinTest");
    }
}
