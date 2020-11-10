
package jam.lang;

import jam.math.DoubleRange;
import jam.vector.VectorUtil;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

final class DD1 extends DomainDouble implements Comparable<DD1> {
    public DD1(double value) {
        super(value, DoubleRange.POSITIVE);
    }

    @Override public int compareTo(DD1 that) {
        return compare(this, that);
    }
}

final class DD2 extends DomainDouble {
    public DD2(double value) {
        super(value, DoubleRange.POSITIVE);
    }
}

final class DD3 extends DomainDouble {
    public DD3(double value) {
        super(value, DoubleRange.INFINITE);
    }
}

public class DomainDoubleTest {
    @Test public void testAsVectorView() {
        DD1[] array = new DD1[] { new DD1(1.0), new DD1(2.0), new DD1(3.0) };

        VectorView view1 = DomainDouble.asVectorView(array);
        VectorView view2 = VectorView.wrap(new double[] { 1.0, 2.0, 3.0 });

        assertTrue(view1.equals(view2));
    }

    @Test public void testCompareTo() {
        DD1 x1 = new DD1(1.0);
        DD1 x2 = new DD1(4.0 / 3.0);
        DD1 x3 = new DD1(1.333333333333);
        DD1 x4 = new DD1(2.0);

        assertTrue(x1.compareTo(x1) == 0);
        assertTrue(x1.compareTo(x2) <  0);
        assertTrue(x1.compareTo(x3) <  0);
        assertTrue(x1.compareTo(x4) <  0);

        assertTrue(x2.compareTo(x1) > 0);
        assertTrue(x2.compareTo(x2) == 0);
        assertTrue(x2.compareTo(x3) == 0);
        assertTrue(x2.compareTo(x4) <  0);

        assertTrue(x3.compareTo(x1) > 0);
        assertTrue(x3.compareTo(x2) == 0);
        assertTrue(x3.compareTo(x3) == 0);
        assertTrue(x3.compareTo(x4) <  0);

        assertTrue(x4.compareTo(x1) >  0);
        assertTrue(x4.compareTo(x2) >  0);
        assertTrue(x4.compareTo(x3) >  0);
        assertTrue(x4.compareTo(x4) == 0);

        // x2 and x3 are within the default floating-point tolerance
        // but they are not identical floating-point values...
        assertTrue(x2.doubleValue() != x3.doubleValue());
    }

    @Test public void testLT() {
        DD1 x1 = new DD1(1.0);
        DD1 x2 = new DD1(4.0 / 3.0);
        DD1 x3 = new DD1(1.333333333333);
        DD1 x4 = new DD1(2.0);

        assertFalse(x1.LT(x1));
        assertTrue(x1.LT(x2));
        assertTrue(x1.LT(x3));
        assertTrue(x1.LT(x4));

        assertFalse(x2.LT(x1));
        assertFalse(x2.LT(x2));
        assertFalse(x2.LT(x3));
        assertTrue(x2.LT(x4));

        assertFalse(x3.LT(x1));
        assertFalse(x3.LT(x2));
        assertFalse(x3.LT(x3));
        assertTrue(x3.LT(x4));

        assertFalse(x4.LT(x1));
        assertFalse(x4.LT(x2));
        assertFalse(x4.LT(x3));
        assertFalse(x4.LT(x4));
    }

    @Test public void testLE() {
        DD1 x1 = new DD1(1.0);
        DD1 x2 = new DD1(4.0 / 3.0);
        DD1 x3 = new DD1(1.333333333333);
        DD1 x4 = new DD1(2.0);

        assertTrue(x1.LE(x1));
        assertTrue(x1.LE(x2));
        assertTrue(x1.LE(x3));
        assertTrue(x1.LE(x4));

        assertFalse(x2.LE(x1));
        assertTrue(x2.LE(x2));
        assertTrue(x2.LE(x3));
        assertTrue(x2.LE(x4));

        assertFalse(x3.LE(x1));
        assertTrue(x3.LE(x2));
        assertTrue(x3.LE(x3));
        assertTrue(x3.LE(x4));

        assertFalse(x4.LE(x1));
        assertFalse(x4.LE(x2));
        assertFalse(x4.LE(x3));
        assertTrue(x4.LE(x4));
    }

    @Test public void testGE() {
        DD1 x1 = new DD1(1.0);
        DD1 x2 = new DD1(4.0 / 3.0);
        DD1 x3 = new DD1(1.333333333333);
        DD1 x4 = new DD1(2.0);

        assertTrue(x1.GE(x1));
        assertFalse(x1.GE(x2));
        assertFalse(x1.GE(x3));
        assertFalse(x1.GE(x4));

        assertTrue(x2.GE(x1));
        assertTrue(x2.GE(x2));
        assertTrue(x2.GE(x3));
        assertFalse(x2.GE(x4));

        assertTrue(x3.GE(x1));
        assertTrue(x3.GE(x2));
        assertTrue(x3.GE(x3));
        assertFalse(x3.GE(x4));

        assertTrue(x4.GE(x1));
        assertTrue(x4.GE(x2));
        assertTrue(x4.GE(x3));
        assertTrue(x4.GE(x4));
    }

    @Test public void testGT() {
        DD1 x1 = new DD1(1.0);
        DD1 x2 = new DD1(4.0 / 3.0);
        DD1 x3 = new DD1(1.333333333333);
        DD1 x4 = new DD1(2.0);

        assertFalse(x1.GT(x1));
        assertFalse(x1.GT(x2));
        assertFalse(x1.GT(x3));
        assertFalse(x1.GT(x4));

        assertTrue(x2.GT(x1));
        assertFalse(x2.GT(x2));
        assertFalse(x2.GT(x3));
        assertFalse(x2.GT(x4));

        assertTrue(x3.GT(x1));
        assertFalse(x3.GT(x2));
        assertFalse(x3.GT(x3));
        assertFalse(x3.GT(x4));

        assertTrue(x4.GT(x1));
        assertTrue(x4.GT(x2));
        assertTrue(x4.GT(x3));
        assertFalse(x4.GT(x4));
    }

    @Test public void testDoubleValues() {
        DD1[] array1 = new DD1[] { new DD1(1.0), new DD1(2.0), new DD1(3.0) };
        
        double[] array2 = DomainDouble.doubleValues(array1);
        double[] array3 = new double[] { 1.0, 2.0, 3.0 };

        assertTrue(VectorUtil.equals(array2, array3, 1.0e-12));
    }

    @Test public void testEqualsDouble() {
        DD1 dd1a = new DD1(1.0);
        DD1 dd1b = new DD1(1.0 + 1.0E-15);
        DD1 dd1c = new DD1(1.000001);

        assertTrue(dd1a.equals(1.0));
        assertTrue(dd1b.equals(1.0));
        assertFalse(dd1c.equals(1.0));
    }

    @Test public void testEqualsObject() {
        DD1 dd1a = new DD1(1.0);
        DD1 dd1b = new DD1(1.0 + 1.0E-15);
        DD1 dd1c = new DD1(1.000001);
        DD2 dd2  = new DD2(1.0);

        assertTrue(dd1a.equals(dd1b));
        assertFalse(dd1a.equals(dd1c));
        assertFalse(dd1a.equals(dd2));
        assertEquals(dd1a, dd1b);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testHashCode() {
        DD1 dd1 = new DD1(1.0);
        dd1.hashCode();
    }

    @Test public void testSign() {
        DD3 x1 = new DD3(-0.000001);
        DD3 x2 = new DD3( 0.0);
        DD3 x3 = new DD3( 0.000001);

        assertEquals(-1, x1.sign());
        assertEquals( 0, x2.sign());
        assertEquals( 1, x3.sign());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.DomainDoubleTest");
    }
}
