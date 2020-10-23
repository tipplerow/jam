
package jam.math;

import java.util.List;

import jam.junit.NumericTestBase;

import org.junit.*;
import static org.junit.Assert.*;

public class RegularSequenceTest extends NumericTestBase {
    private static final RegularSequence seq1 = RegularSequence.create(2.0, 5.0, 0.5);

    @Test public void testAll() {
        assertEquals(7, seq1.size());
        assertDouble(2.0, seq1.first());
        assertDouble(5.0, seq1.last());
        assertDouble(0.5, seq1.step());
        assertDouble(new double[] { 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0 }, seq1.toArray());
        assertDouble(List.of(2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0), seq1.toList());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.math.RegularSequenceTest");
    }
}
