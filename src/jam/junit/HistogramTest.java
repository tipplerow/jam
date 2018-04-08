
package jam.junit;

import java.util.List;

import jam.hist.Bin;
import jam.hist.Histogram;
import jam.math.DoubleRange;

import org.junit.*;
import static org.junit.Assert.*;

public class HistogramTest extends NumericTestBase {
    @Test public void testDoubles() {
        Histogram hist = Histogram.compute(1.0, 5.0, 4, new double[] { -1.0, 1.1, 2.2, 2.3, 3.4, 3.5, 3.6, 5.5 });
        List<Bin> bins = hist.viewBins();

        assertEquals(6, hist.getTotalCount());
        assertEquals(4, bins.size());
        
        assertEquals(DoubleRange.leftClosed(1.0, 2.0), bins.get(0).getRange());
        assertEquals(DoubleRange.leftClosed(2.0, 3.0), bins.get(1).getRange());
        assertEquals(DoubleRange.leftClosed(3.0, 4.0), bins.get(2).getRange());
        assertEquals(DoubleRange.closed(    4.0, 5.0), bins.get(3).getRange());

        assertEquals(1, bins.get(0).getCount());
        assertEquals(2, bins.get(1).getCount());
        assertEquals(3, bins.get(2).getCount());
        assertEquals(0, bins.get(3).getCount());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.HistogramTest");
    }
}
