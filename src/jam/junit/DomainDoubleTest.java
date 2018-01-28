
package jam.junit;

import jam.lang.DomainDouble;
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

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DomainDoubleTest");
    }
}
