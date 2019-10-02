
package jam.junit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

import jam.vector.VectorUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class VectorUtilTest extends NumericTestBase {
    private final double TOLERANCE = 1.0e-12;

    @Test public void testBracket() {
        double[] array = new double[] { 0.0, 1.0, 2.0, 3.0 };

        assertDouble(-1, VectorUtil.bracket(array, -0.01));
        assertDouble( 0, VectorUtil.bracket(array,  0.0));
        assertDouble( 0, VectorUtil.bracket(array,  0.01));
        assertDouble( 0, VectorUtil.bracket(array,  0.99));
        assertDouble( 1, VectorUtil.bracket(array,  1.0));
        assertDouble( 1, VectorUtil.bracket(array,  1.01));
        assertDouble( 1, VectorUtil.bracket(array,  1.99));
        assertDouble( 2, VectorUtil.bracket(array,  2.0));
        assertDouble( 2, VectorUtil.bracket(array,  2.01));
        assertDouble( 2, VectorUtil.bracket(array,  2.99));
        assertDouble( 3, VectorUtil.bracket(array,  3.0));
        assertDouble( 4, VectorUtil.bracket(array,  3.01));
    }

    @Test public void testCopy() {
        double[] v1 = VectorUtil.create(10, 1.234);
        double[] v2 = VectorUtil.copy(v1);

        assertEquals(10, v2.length);

	for (int k = 0; k < 10; k++)
	    assertEquals(1.234, v2[k], TOLERANCE);

        // Ensure that changes to the original are not reflected in
        // the copy, and vice versa...
        v1[0] = 3.333;
        v2[1] = 5.678;

        assertEquals(1.234, v2[0], TOLERANCE);
        assertEquals(1.234, v1[1], TOLERANCE);
    }

    @Test public void testCreate() {
	int length = 10;
	double fill = 1.234;

	double[] vector = VectorUtil.create(length, fill);
	assertEquals(length, vector.length);

	for (int k = 0; k < vector.length; k++)
	    assertEquals(fill, vector[k], TOLERANCE);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateInvalid() {
	VectorUtil.create(-1, -1.1);
    }

    @Test public void testEquals() {
        double[] x1 = new double[] { 1.0, 2.0, 3.0 };
        double[] x2 = new double[] { 1.0, 2.0, 3.0 };
        double[] x3 = new double[] { 3.0, 2.0, 1.0 };
        double[] x4 = new double[] { 1.0, 2.0 };
        double[] x5 = new double[] { 1.0, 2.0, 3.0, 4.0 };

        assertTrue(VectorUtil.equals( x1, x2, TOLERANCE));
        assertFalse(VectorUtil.equals(x1, x3, TOLERANCE));
        assertFalse(VectorUtil.equals(x1, x4, TOLERANCE));
        assertFalse(VectorUtil.equals(x1, x5, TOLERANCE));
    }

    @Test public void testMean() {
        assertDouble(1.0, VectorUtil.mean(1.0));
        assertDouble(1.5, VectorUtil.mean(1.0, 2.0));
        assertDouble(2.5, VectorUtil.mean(new double [] { 1.0, 2.0, 3.0, 4.0 }));
    }

    @Test public void testNormalize() {
        double[] values = new double[] { 1.0, -2.0, 5.0 };

        VectorUtil.normalize(values);
        assertTrue(VectorUtil.equals(new double[] { 0.25, -0.50, 1.25 }, values, TOLERANCE));
    }

    @Test public void testSequence() {
        double[] sequence = VectorUtil.sequence(1.0, 3.0, 5);

        assertEquals(5, sequence.length);
        assertDouble(1.0, sequence[0]);
        assertDouble(1.5, sequence[1]);
        assertDouble(2.0, sequence[2]);
        assertDouble(2.5, sequence[3]);
        assertDouble(3.0, sequence[4]);
    }

    @Test public void testSequenceLog() {
        double[] sequence = VectorUtil.sequenceLog(0.01, 10.0, 4);

        assertEquals(4, sequence.length);
        assertDouble( 0.01, sequence[0]);
        assertDouble( 0.1,  sequence[1]);
        assertDouble( 1.0,  sequence[2]);
        assertDouble(10.0,  sequence[3]);
    }

    @Test public void testSum() {
        assertDouble(0.0, VectorUtil.sum());
        assertDouble(1.0, VectorUtil.sum(1.0));
        assertDouble(3.0, VectorUtil.sum(1.0, 2.0));
        assertDouble(6.0, VectorUtil.sum(new double [] { 1.0, 2.0, 3.0 }));
    }

    @Test public void testListToArray() {
        ArrayList<Double> arrayList = new ArrayList<Double>();
        LinkedList<Double> linkedList = new LinkedList<Double>();

        arrayList.add(1.0);
        arrayList.add(2.0);
        arrayList.add(3.0);

        linkedList.add(1.0);
        linkedList.add(2.0);
        linkedList.add(3.0);

        assertTrue(VectorUtil.equals(new double[] { 1.0, 2.0, 3.0 }, VectorUtil.toArray(arrayList), TOLERANCE));
        assertTrue(VectorUtil.equals(new double[] { 1.0, 2.0, 3.0 }, VectorUtil.toArray(linkedList), TOLERANCE));
    }

    @Test public void testTreeSetToArray() {
        TreeSet<Double> set = new TreeSet<Double>();

        set.add(3.0);
        set.add(2.0);
        set.add(1.0);

        assertTrue(VectorUtil.equals(new double[] { 1.0, 2.0, 3.0 }, VectorUtil.toArray(set), TOLERANCE));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.VectorUtilTest");
    }
}
