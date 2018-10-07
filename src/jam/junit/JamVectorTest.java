
package jam.junit;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.primitives.ArrayDoubleList;

import jam.math.StatUtil;
import jam.matrix.JamMatrix;
import jam.vector.JamVector;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

public class JamVectorTest extends NumericTestBase {
    private static final JamVector DENSE3 = JamVector.valueOf(1.0, 2.0, 3.0);
    private static final JamVector DENSE4 = JamVector.valueOf(1.0, 2.0, 3.0, 4.0);

    private void assertDenseUnchanged() {
        //
        // Assert that the DENSE3 vector is unchanged...
        //
        assertDouble(1.0, DENSE3.get(0));
        assertDouble(2.0, DENSE3.get(1));
        assertDouble(3.0, DENSE3.get(2));
    }

    @Test public void testAddScalar() {
        JamVector v1 = JamVector.valueOf(0.1, 0.2, 0.3);
        JamVector v2 = JamVector.valueOf(2.1, 2.2, 2.3);

        v1.add(2.0);
        assertEquals(v2, v1);
    }

    @Test public void testAddVector() {
        JamVector v1 = JamVector.valueOf(0.1, 0.2, 0.3);
        JamVector v2 = JamVector.valueOf(1.1, 2.2, 3.3);

        v1.add(DENSE3);
        assertEquals(v2, v1);

        assertDenseUnchanged();
    }

    @Test(expected = RuntimeException.class)
    public void testAddVectorInvalid() {
	DENSE3.add(DENSE4);
    }

    @Test public void testConstructorBase() {
        JamVector vec = new JamVector(3);

        assertEquals(3, vec.length());
        assertDouble(0.0, vec.get(0));
        assertDouble(0.0, vec.get(1));
        assertDouble(0.0, vec.get(2));
    }

    @Test public void testConstructorFill() {
        JamVector vec = new JamVector(3, 3.3);

        assertEquals(3, vec.length());
        assertDouble(3.3, vec.get(0));
        assertDouble(3.3, vec.get(1));
        assertDouble(3.3, vec.get(2));
    }

    @Test public void testConstructorBareArray() {
        double[] bare = new double[] { 1.0, 2.0, 3.0 };
        JamVector jam = JamVector.copyOf(bare);

        assertEquals(3, jam.length());
        assertDouble(1.0, jam.get(0));
        assertDouble(2.0, jam.get(1));
        assertDouble(3.0, jam.get(2));

        // Assert changes to the bare vector are not reflected in the
        // JamVector and vice-versa...
        bare[0] = 3.45;
        jam.set(1, 7.89);
        
        assertDouble(1.0, jam.get(0));
        assertDouble(2.0, bare[1]);
    }

    @Test public void testConstructorView() {
        JamVector orig = JamVector.valueOf(1.0, 2.0, 3.0);
        JamVector copy = JamVector.copyOf(orig);

        assertEquals(3, copy.length());
        assertDouble(1.0, copy.get(0));
        assertDouble(2.0, copy.get(1));
        assertDouble(3.0, copy.get(2));

        // Assert changes to the original vector are not reflected in
        // the copy and vice-versa...
        orig.set(0, 3.45);
        copy.set(1, 7.89);
        
        assertDouble(1.0, copy.get(0));
        assertDouble(2.0, orig.get(1));
    }

    @Test public void testConstructorArrayDoubleList() {
        ArrayDoubleList list = new ArrayDoubleList();

        list.add(1.0);
        list.add(2.0);
        list.add(3.0);

        JamVector actual   = JamVector.copyOf(list);
        JamVector expected = JamVector.valueOf(1.0, 2.0, 3.0);
            
        assertEquals(expected, actual);

        // Ensure that changes to the list do not affect the vector...
        list.add(4.0);
        list.add(5.0);
        assertEquals(expected, actual);
    }

    @Test public void testCopy() {
        JamVector orig = JamVector.valueOf(1.0, 2.0, 3.0);
        JamVector copy = orig.copy();

        assertEquals(3, copy.length());
        assertDouble(1.0, copy.get(0));
        assertDouble(2.0, copy.get(1));
        assertDouble(3.0, copy.get(2));

        // Assert changes to the original vector are not reflected in
        // the copy and vice-versa...
        orig.set(0, 3.45);
        copy.set(1, 7.89);
        
        assertDouble(1.0, copy.get(0));
        assertDouble(2.0, orig.get(1));
    }

    @Test public void testCosine() {
        VectorView v1 = JamVector.valueOf( 1.0,  1.0, 0.0);
        VectorView v2 = JamVector.valueOf(-2.0,  2.0, 0.0);
        VectorView v3 = JamVector.valueOf(-3.0, -3.0, 0.0);
        VectorView v4 = JamVector.valueOf( 4.0, -4.0, 0.0);

        assertDouble( 1.0, JamVector.cosine(v1, v1));
        assertDouble( 0.0, JamVector.cosine(v1, v2));
        assertDouble(-1.0, JamVector.cosine(v1, v3));
        assertDouble( 0.0, JamVector.cosine(v1, v4));

        VectorView x = JamVector.valueOf(1.0, 2.0, 3.0);
        VectorView y = JamVector.valueOf(4.0, 5.0, 6.0);

        assertEquals(0.974632, JamVector.cosine(x, y), 1.0E-06);
    }

    @Test public void testCross() {
	VectorView ex = JamVector.UNIT_X;
	VectorView ey = JamVector.UNIT_Y;
	VectorView ez = JamVector.UNIT_Z;
	JamVector  v0 = JamVector.zeros(3);

	assertEquals(v0, JamVector.cross(ex, ex));
	assertEquals(v0, JamVector.cross(ey, ey));
	assertEquals(v0, JamVector.cross(ez, ez));

	assertEquals(ex, JamVector.cross(ey, ez));
	assertEquals(ey, JamVector.cross(ez, ex));
	assertEquals(ez, JamVector.cross(ex, ey));
    }

    @Test(expected = RuntimeException.class)
    public void testCrossInvalid() {
	DENSE4.cross(DENSE4);
    }

    @Test public void testDot() {
	assertDouble(15.4, DENSE3.dot(JamVector.valueOf(1.1, 2.2,  3.3)));
	assertDouble(-5.0, DENSE3.dot(JamVector.valueOf(1.0, 0.0, -2.0)));
    }

    @Test public void testDotStatic() {
	assertDouble(15.4, JamVector.dot(DENSE3, JamVector.valueOf(1.1, 2.2,  3.3)));
	assertDouble(-5.0, JamVector.dot(DENSE3, JamVector.valueOf(1.0, 0.0, -2.0)));
    }

    @Test(expected = RuntimeException.class)
    public void testDotInvalid() {
	DENSE3.dot(DENSE4);
    }

    @Test public void testDivideScalar() {
        JamVector v1 = JamVector.valueOf(0.1, 0.2, 0.3);
        JamVector v2 = JamVector.valueOf(0.5, 1.0, 1.5);

        v2.divide(5.0);
        assertEquals(v1, v2);
    }

    @Test public void testDivideEBE() {
        JamVector v1 = JamVector.valueOf(1.0, 2.0,  3.3);
        JamVector v2 = JamVector.valueOf(2.0, 8.0,  0.5);
        JamVector v3 = JamVector.valueOf(0.5, 0.25, 6.6);

        assertEquals(v3, JamVector.divideEBE(v1, v2));
    }

    @Test(expected = RuntimeException.class)
    public void testDivideEBEInvalid() {
	JamVector.divideEBE(DENSE3, DENSE4);
    }

    @Test public void testEqualsVector() {
        assertTrue(DENSE3.equalsVector(DENSE3));
        assertTrue(DENSE3.equalsVector(JamVector.valueOf(1.0, 2.0, 3.0)));
        assertFalse(DENSE3.equalsVector(JamVector.valueOf(1.0, 2.0)));
        assertFalse(DENSE3.equalsVector(JamVector.valueOf(1.0, 2.0, 3.0, 4.0)));
    }

    @Test public void testGet() {
        assertDouble(1.0, DENSE3.get(0));
        assertDouble(2.0, DENSE3.get(1));
        assertDouble(3.0, DENSE3.get(2));
    }

    @Test(expected = RuntimeException.class)
    public void testGetInvalid1() {
        DENSE3.get(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testGetInvalid2() {
        DENSE3.get(10);
    }

    @Test public void testFill() {
        JamVector vec = JamVector.valueOf(1.0, 2.0, 3.0);

        vec.fill(3.33);
        assertEquals(JamVector.valueOf(3.33, 3.33, 3.33), vec);
    }

    @Test public void testFirst() {
        assertDouble(1.0, DENSE3.first());
    }

    @Test public void testLast() {
        assertDouble(3.0, DENSE3.last());
    }

    @Test public void testLength() {
        assertEquals(3, DENSE3.length());
    }

    @Test public void testMinusScalar() {
        JamVector vec = DENSE3.minus(1.1);

        assertEquals(3, vec.length());
        assertDouble(-0.1, vec.get(0));
        assertDouble( 0.9, vec.get(1));
        assertDouble( 1.9, vec.get(2));

        assertDenseUnchanged();
    }

    @Test public void testMinusVector() {
        JamVector vec = DENSE3.minus(DENSE3);

        assertEquals(JamVector.zeros(3), vec);
        assertDenseUnchanged();
    }

    @Test public void testMinusScalarStatic() {
        JamVector vec = JamVector.minus(DENSE3, 1.1);

        assertEquals(3, vec.length());
        assertDouble(-0.1, vec.get(0));
        assertDouble( 0.9, vec.get(1));
        assertDouble( 1.9, vec.get(2));

        assertDenseUnchanged();
    }

    @Test public void testMinusVectorStatic() {
        JamVector vec = JamVector.minus(DENSE3, DENSE3);

        assertEquals(JamVector.zeros(3), vec);
        assertDenseUnchanged();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinusVectorInvalid() {
        DENSE3.minus(DENSE4);
    }

    @Test public void testMultiplyScalar() {
        JamVector v1 = JamVector.valueOf(0.1, 0.2, 0.3);
        JamVector v2 = JamVector.valueOf(0.5, 1.0, 1.5);

        v1.multiply(5.0);
        assertEquals(v2, v1);
    }

    @Test public void testMultiplyEBE() {
        JamVector v1 = JamVector.valueOf(1.0,  2.0, 3.0);
        JamVector v2 = JamVector.valueOf(2.0,  8.0, 0.5);
        JamVector v3 = JamVector.valueOf(2.0, 16.0, 1.5);

        assertEquals(v3, JamVector.multiplyEBE(v1, v2));
        assertEquals(v3, JamVector.multiplyEBE(v2, v1));
    }

    @Test(expected = RuntimeException.class)
    public void testMultiplyEBEInvalid() {
	JamVector.multiplyEBE(DENSE3, DENSE4);
    }

    @Test public void testNorm() {
        JamVector v3 = JamVector.valueOf(-1.0,  2.0, -3.0);
        JamVector v4 = JamVector.valueOf( 1.0, -2.0,  3.0, -4.0);

        assertDouble(Math.sqrt(14.0), v3.norm());
        assertDouble(Math.sqrt(30.0), v4.norm());

        assertDouble( 6.0, v3.norm(1));
        assertDouble(10.0, v4.norm(1));

        assertDouble(Math.sqrt(14.0), v3.norm(2));
        assertDouble(Math.sqrt(30.0), v4.norm(2));
    }

    @Test public void testNormalize() {
        JamVector vec = JamVector.copyOf(DENSE3);
        vec.normalize();

        assertDouble(1.0 / 6.0, vec.get(0));
        assertDouble(2.0 / 6.0, vec.get(1));
        assertDouble(3.0 / 6.0, vec.get(2));
    }

    @Test public void testParseCSV() {
        JamVector vec1 = JamVector.valueOf(1.0, 2.0, 3.0);
        JamVector vec2 = JamVector.parseCSV("1.0, 2.0, 3.0");

        assertEquals(vec1, vec2);
    }

    @Test public void testPlusScalar() {
        JamVector vec = DENSE3.plus(1.1);

        assertEquals(3, vec.length());
        assertDouble(2.1, vec.get(0));
        assertDouble(3.1, vec.get(1));
        assertDouble(4.1, vec.get(2));

        assertDenseUnchanged();
    }

    @Test public void testPlusVector() {
        JamVector vec = DENSE3.plus(DENSE3);

        assertEquals(3, vec.length());
        assertDouble(2.0, vec.get(0));
        assertDouble(4.0, vec.get(1));
        assertDouble(6.0, vec.get(2));

        assertDenseUnchanged();
    }

    @Test public void testPlusScalarStatic() {
        JamVector vec = JamVector.plus(DENSE3, 1.1);

        assertEquals(3, vec.length());
        assertDouble(2.1, vec.get(0));
        assertDouble(3.1, vec.get(1));
        assertDouble(4.1, vec.get(2));

        assertDenseUnchanged();
    }

    @Test public void testPlusVectorStatic() {
        JamVector vec = JamVector.plus(DENSE3, DENSE3);

        assertEquals(3, vec.length());
        assertDouble(2.0, vec.get(0));
        assertDouble(4.0, vec.get(1));
        assertDouble(6.0, vec.get(2));

        assertDenseUnchanged();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlusVectorInvalid() {
        DENSE3.plus(JamVector.valueOf(1.1, 2.2, 3.3, 4.4));
    }

    @Test public void testSet() {
        JamVector vec = JamVector.valueOf(1.0, 2.0, 3.0);

        vec.set(0, 5.5);
        vec.set(1, 4.4);
        vec.set(2, 3.3);

        assertDouble(5.5, vec.get(0));
        assertDouble(4.4, vec.get(1));
        assertDouble(3.3, vec.get(2));
    }

    @Test(expected = RuntimeException.class)
    public void testSetInvalid1() {
        DENSE3.set(-1, 0.0);
    }

    @Test(expected = RuntimeException.class)
    public void testSetInvalid2() {
        DENSE3.set(10, 0.0);
    }

    @Test public void testSubtractScalar() {
        JamVector v1 = JamVector.valueOf(0.1, 0.2, 0.3);
        JamVector v2 = JamVector.valueOf(2.1, 2.2, 2.3);

        v2.subtract(2.0);
        assertEquals(v1, v2);
    }

    @Test public void testSubtractVector() {
        JamVector v1 = JamVector.valueOf(0.1, 0.2, 0.3);
        JamVector v2 = JamVector.valueOf(1.1, 2.2, 3.3);

        v2.subtract(DENSE3);
        assertEquals(v1, v2);

        assertDenseUnchanged();
    }

    @Test public void testSubVector() {
        JamVector v1 = JamVector.valueOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        JamVector v2 = v1.subvector(0);
        JamVector v3 = v1.subvector(4);
        JamVector v4 = v1.subvector(2, 2);
        JamVector v5 = v1.subvector(2, 4);

        assertEquals(v1, JamVector.valueOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0));
        assertEquals(v2, JamVector.valueOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0));
        assertEquals(v3, JamVector.valueOf(4.0, 5.0, 6.0));
        assertEquals(v4, JamVector.valueOf());
        assertEquals(v5, JamVector.valueOf(2.0, 3.0));
    }

    @Test(expected = RuntimeException.class)
    public void testSubvectorInvalid1() {
        DENSE4.subvector(8);
    }

    @Test(expected = RuntimeException.class)
    public void testSubvectorInvalid2() {
        DENSE4.subvector(2, 1);
    }

    @Test public void testSum() {
        assertDouble( 6.0, DENSE3.sum());
        assertDouble(10.0, DENSE4.sum());
    }

    
    @Test public void testTimesScalar() {
        JamVector vec = DENSE3.times(2.0);

        assertEquals(3, vec.length());
        assertDouble(2.0, vec.get(0));
        assertDouble(4.0, vec.get(1));
        assertDouble(6.0, vec.get(2));

        assertDenseUnchanged();
    }

    @Test public void testTimesScalarStatic() {
        JamVector vec = JamVector.times(DENSE3, 2.0);

        assertEquals(3, vec.length());
        assertDouble(2.0, vec.get(0));
        assertDouble(4.0, vec.get(1));
        assertDouble(6.0, vec.get(2));

        assertDenseUnchanged();
    }

    @Test public void testUnit() {
        JamVector unit = JamVector.unit(DENSE3);

        assertDouble(1.0, StatUtil.norm2(unit));
        assertDouble(0.0, StatUtil.norm2(DENSE3.cross(unit)));
    }

    @Test public void testIterator() {
        Iterator<Double> iter = DENSE3.iterator();

        assertTrue(iter.hasNext());
        assertDouble(1.0, iter.next());

        assertTrue(iter.hasNext());
        assertDouble(2.0, iter.next());

        assertTrue(iter.hasNext());
        assertDouble(3.0, iter.next());

        assertFalse(iter.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorNoNext() {
        Iterator<Double> iter = DENSE3.iterator();

        iter.next();
        iter.next();
        iter.next();
        iter.next();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JamVectorTest");
    }
}
