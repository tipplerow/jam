
package jam.vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jam.junit.NumericTestBase;
import jam.vector.VectorUtil;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

public class VectorViewTest extends NumericTestBase {
    private static final double TOLERANCE = 1.0e-12;
    private static final VectorView FIXED = VectorView.wrap(1.0, 2.0, 3.0);

    @Test public void testElements() {
        int count = 0;
        double total = 0.0;

        for (double value : FIXED.elements()) {
            count++;
            total += value;
        }

        assertEquals(count, 3);
        assertDouble(total, 6.0);
    }

    @Test public void testEmpty() {
        assertEquals(0, VectorView.EMPTY.length());
    }

    @Test public void testEquals() {
        VectorView v1 = VectorView.wrap(1.0, 2.0, 3.0);
        VectorView v2 = VectorView.wrap(1.0, 2.0, 3.0);
        VectorView v3 = VectorView.wrap(1.0, 2.0, 3.0001);
        VectorView v4 = VectorView.wrap(1.0, 2.0);
        VectorView v5 = VectorView.wrap(1.0, 2.0, 3.0, 4.0);

        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
        assertFalse(v1.equals(v4));
        assertFalse(v1.equals(v5));

        assertTrue( v1.equalsVector(v3, 0.001));
        assertFalse(v1.equalsVector(v3, 0.00001));
    }

    @Test public void testToNumeric() {
        double[] arr1 = new double[] { 1.0, 2.0, 3.0 };
        double[] arr2 = new double[] { 1.0, 2.0, 3.0 };

        VectorView view = VectorView.wrap(arr1);

        double[] arr3 = view.toNumeric();
        assertTrue(VectorUtil.equals(arr1, arr3, TOLERANCE));

        // Verify that the original array and view are unchanged...
        arr3[1] = -1.234;
        assertTrue(VectorUtil.equals(arr1, arr2, TOLERANCE));
        assertTrue(VectorUtil.equals(arr1, view.toNumeric(), TOLERANCE));
    }

    @Test public void testWrapArray() {
        double[] array = new double[] { 1.0, 2.0, 3.0 };
        VectorView view = VectorView.wrap(array);

        assertEquals(3, view.length());

        assertDouble(1.0, view.getDouble(0));
        assertDouble(2.0, view.getDouble(1));
        assertDouble(3.0, view.getDouble(2));

        array[0] = 5.5;
        array[1] = 4.4;
        array[2] = 3.3;

	// Verify that changes to the underlying bare array are
	// reflected in the view...
        assertDouble(5.5, view.getDouble(0));
        assertDouble(4.4, view.getDouble(1));
        assertDouble(3.3, view.getDouble(2));

        view = VectorView.wrap(4.5, 6.7);

	assertEquals(2, view.length());
        assertDouble(4.5, view.getDouble(0));
        assertDouble(6.7, view.getDouble(1));
    }

    @Test public void testMap() {
        List<String> list = Arrays.asList("abcd", "efg", "hijk", "lmnop");
        VectorView   view = VectorView.map(list, x -> x.length());

        assertEquals(4, view.length());
        assertDouble(4.0, view.getDouble(0));
        assertDouble(3.0, view.getDouble(1));
        assertDouble(4.0, view.getDouble(2));
        assertDouble(5.0, view.getDouble(3));
    }

    @Test public void testSubVector() {
        VectorView v1 = VectorView.wrap(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        VectorView v2 = v1.subvector(0);
        VectorView v3 = v1.subvector(4);
        VectorView v4 = v1.subvector(2, 2);
        VectorView v5 = v1.subvector(2, 4);

        assertTrue(v2.equalsVector(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0));
        assertTrue(v3.equalsVector(4.0, 5.0, 6.0));
        assertTrue(v4.equalsVector());
        assertTrue(v5.equalsVector(2.0, 3.0));
    }

    @Test(expected = RuntimeException.class)
    public void testSubvectorInvalid1() {
        VectorView.wrap(1.0, 2.0, 3.0, 4.0).subvector(-2);
    }

    @Test(expected = RuntimeException.class)
    public void testSubvectorInvalid2() {
        VectorView.wrap(1.0, 2.0, 3.0, 4.0).subvector(8);
    }

    @Test(expected = RuntimeException.class)
    public void testSubvectorInvalid3() {
        VectorView.wrap(1.0, 2.0, 3.0, 4.0).subvector(2, 1);
    }

    @Test public void testWrapArrayList() {
        List<Double> list = new ArrayList<Double>();
        VectorView   view = VectorView.wrap(list);

        assertEquals(0, view.length());

        list.add(1.0);

        assertEquals(1, view.length());
        assertDouble(1.0, view.getDouble(0));

        list.add(2.0);

        assertEquals(2, view.length());
        assertDouble(1.0, view.getDouble(0));
        assertDouble(2.0, view.getDouble(1));

        list.add(3.0);

        assertEquals(3, view.length());
        assertDouble(1.0, view.getDouble(0));
        assertDouble(2.0, view.getDouble(1));
        assertDouble(3.0, view.getDouble(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrapLinkedList() {
        VectorView.wrap(new LinkedList<Double>());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.vector.VectorViewTest");
    }
}
