
package jam.spin;

import java.util.Arrays;
import java.util.ArrayList;

import jam.junit.NumericTestBase;

import org.junit.*;
import static org.junit.Assert.*;

public class SpinVectorTest extends NumericTestBase {
    private static final Spin UP = Spin.UP;
    private static final Spin DN = Spin.DOWN;

    private static final SpinVector FIXED10 = new SpinVector(10);
    private static final SpinVector FIXED20 = new SpinVector(20);

    @Test public void testConcat() {
        SpinVector v1 = new SpinVector(UP);
        SpinVector v2 = new SpinVector(DN, DN);
        SpinVector v3 = new SpinVector(UP, UP, UP);
        SpinVector v4 = new SpinVector(UP, DN, DN, UP, UP, UP);

        assertEquals(v4, SpinVector.concat(v1, v2, v3));
        assertEquals(v4, SpinVector.concat(Arrays.asList(v1, v2, v3)));
    }

    @Test public void testConstructorDefault() {
        SpinVector vector = new SpinVector(3);

        assertEquals(3, vector.length());

        assertEquals(DN, vector.get(0));
        assertEquals(DN, vector.get(1));
        assertEquals(DN, vector.get(2));
    }

    @Test public void testConstructorEmpty() {
        assertEquals(0, new SpinVector(0).length());
    }

    @Test public void testConstructorSpinArray() {
        SpinVector vector = new SpinVector(UP, DN, UP, DN);

        assertEquals(4, vector.length());

        assertEquals(UP, vector.get(0));
        assertEquals(DN, vector.get(1));
        assertEquals(UP, vector.get(2));
        assertEquals(DN, vector.get(3));
    }

    @Test public void testConstructorCollection() {
        ArrayList<Spin> spins = new ArrayList<Spin>();

        spins.add(UP);
        spins.add(DN);
        spins.add(UP);
        spins.add(DN);

        assertEquals(SpinVector.parse("+-+-"), new SpinVector(spins));
    }

    @Test public void testConstructorFill() {
        assertEquals(SpinVector.parse("+++"), new SpinVector(3, UP));
        assertEquals(SpinVector.parse("-----"), new SpinVector(5, DN));
    }

    @Test public void testParse() {
        SpinVector vector = SpinVector.parse("+-+-");

        assertEquals(4, vector.length());

        assertEquals(UP, vector.get(0));
        assertEquals(DN, vector.get(1));
        assertEquals(UP, vector.get(2));
        assertEquals(DN, vector.get(3));
    }

    @Test public void testGetSetFlip() {
        SpinVector vec = new SpinVector(10);

        vec.flip(2);
        assertEquals(UP, vec.get(2));
        assertDouble(+1.0, vec.getDouble(2));

        vec.flip(2);
        assertEquals(DN, vec.get(2));
        assertDouble(-1.0, vec.getDouble(2));

        vec.set(3, UP);
        assertEquals(UP, vec.get(3));
        assertDouble(+1.0, vec.getDouble(3));

        vec.set(3, DN);
        assertEquals(DN, vec.get(3));
        assertDouble(-1.0, vec.getDouble(3));
    }

    @Test public void testCopy() {
        SpinVector v1 = new SpinVector(DN, UP, DN, UP);
        SpinVector v2 = v1.copy();

        v2.flip();

        assertEquals(new SpinVector(DN, UP, DN, UP), v1); // Ensure original is unchanged
        assertEquals(new SpinVector(UP, DN, UP, DN), v2);
    }

    @Test public void testEquals() {
        SpinVector v1 = new SpinVector(UP, UP, UP, DN, DN);
        SpinVector v2 = new SpinVector(UP, UP, UP, DN, DN);
        SpinVector v3 = new SpinVector(UP, UP, DN, DN, DN);
        SpinVector v4 = new SpinVector(UP, UP, UP, DN, DN, DN);

        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
        assertFalse(v1.equals(v4)); // Different length
    }

    @Test public void testFit() {
        SpinVector v1 = SpinVector.parse("----------");
        SpinVector v2 = SpinVector.parse("---+----+-");
        SpinVector v3 = SpinVector.parse("++-+--+-++");
        SpinVector v4 = SpinVector.parse("++++++++++");

        assertDouble(1.0, v1.fit(v1));
        assertDouble(0.8, v1.fit(v2));
        assertDouble(0.4, v1.fit(v3));
        assertDouble(0.0, v1.fit(v4));
    }

    @Test public void testHamming() {
        SpinVector v1 = SpinVector.parse("----------");
        SpinVector v2 = SpinVector.parse("---+----+-");
        SpinVector v3 = SpinVector.parse("++-+--+-++");
        SpinVector v4 = SpinVector.parse("++++++++++");

        assertEquals( 0, v1.hamming(v1));
        assertEquals( 2, v1.hamming(v2));
        assertEquals( 6, v1.hamming(v3));
        assertEquals(10, v1.hamming(v4));
    }

    @Test public void testFormat() {
        SpinVector vec = new SpinVector(4);
        assertEquals("----", vec.format());

        vec.flip(2);
        vec.flip(3);
        assertEquals("--++", vec.format());
    }

    @Test public void testToArray() {
        SpinVector vector = new SpinVector(UP, DN, UP, UP, DN, DN);

        Spin[] array = vector.toArray();
        assertEquals(6, array.length);

        assertEquals(UP, array[0]);
        assertEquals(DN, array[1]);
        assertEquals(UP, array[2]);
        assertEquals(UP, array[3]);
        assertEquals(DN, array[4]);
        assertEquals(DN, array[5]);
    }

    @Test public void testToNumeric() {
        SpinVector spins = SpinVector.parse("-+--+");
        double[]   dbls  = new double[] { -1.0, 1.0, -1.0, -1.0, 1.0 };

        assertTrue(Arrays.equals(dbls, spins.toNumeric()));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidFlip1() {
        FIXED10.flip(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidFlip2() {
        FIXED10.flip(FIXED10.length());
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidGet1() {
        FIXED10.get(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidGet2() {
        FIXED10.get(FIXED10.length());
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSet1() {
        FIXED10.set(-1, UP);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSet2() {
        FIXED10.set(FIXED10.length(), DN);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidLength() {
        new SpinVector(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidParse() {
        SpinVector.parse("+-+-F");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.spin.SpinVectorTest");
    }
}
