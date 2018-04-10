
package jam.junit;

import jam.hist.Bin;
import jam.math.DoubleRange;

import org.junit.*;
import static org.junit.Assert.*;

public class BinTest {
    @Test public void testCompare() {
        DoubleRange range1 = DoubleRange.closed(  1.0, 2.0);
        DoubleRange range2 = DoubleRange.leftOpen(2.0, 3.0);
        
        Bin bin1 = Bin.empty(range1);
        Bin bin2 = Bin.empty(range2);

        assertFalse(bin1.contains(0.0));
        assertTrue( bin1.contains(1.0));
        assertTrue( bin1.contains(2.0));
        assertFalse(bin1.contains(3.0));
        assertFalse(bin1.contains(4.0));

        assertFalse(bin2.contains(0.0));
        assertFalse(bin2.contains(1.0));
        assertFalse(bin2.contains(2.0));
        assertTrue( bin2.contains(3.0));
        assertFalse(bin2.contains(4.0));

        assertTrue(bin1.compare(0.0)  < 0);
        assertTrue(bin1.compare(1.0) == 0);
        assertTrue(bin1.compare(2.0) == 0);
        assertTrue(bin1.compare(3.0)  > 0);
        assertTrue(bin1.compare(4.0)  > 0);

        assertTrue(bin2.compare(0.0)  < 0);
        assertTrue(bin2.compare(1.0)  < 0);
        assertTrue(bin2.compare(2.0)  < 0);
        assertTrue(bin2.compare(3.0) == 0);
        assertTrue(bin2.compare(4.0)  > 0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.BinTest");
    }
}
