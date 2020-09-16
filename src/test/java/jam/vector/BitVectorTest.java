
package jam.vector;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.BitSet;

import jam.junit.NumericTestBase;
import jam.vector.BitVector;

import org.junit.*;
import static org.junit.Assert.*;

public class BitVectorTest extends NumericTestBase {
    private static final BitVector FIXED10 = new BitVector(10);
    private static final BitVector FIXED20 = new BitVector(20);

    @Test public void testBitSetConstructor() {
        BitSet bits = new BitSet();

        bits.set(1);
        bits.set(3);
        bits.set(4);

        BitVector vector = new BitVector(6, bits);
        assertEquals(BitVector.parse("010110"), vector);

        // Ensure that changing the input bit set does not alter the
        // new bit vector...
        bits.set(0);
        bits.set(2);
        assertEquals(BitVector.parse("010110"), vector);
    }

    @Test public void testBooleanConstructor() {
        BitVector vector = new BitVector(true, false, true);

        assertEquals(3, vector.length());

        assertTrue(vector.get(0));
        assertFalse(vector.get(1));
        assertTrue(vector.get(2));
    }

    @Test public void testFillConstructor() {
	BitVector v1 = new BitVector(2, false);
	BitVector v2 = new BitVector(3, true);

	assertEquals(v1, BitVector.parse("00"));
	assertEquals(v2, BitVector.parse("111"));
    }

    @Test public void testCollectionConstructor() {
        ArrayList<Boolean> bits = new ArrayList<Boolean>();

        bits.add(true);
        bits.add(false);
        bits.add(true);
        bits.add(false);

        assertEquals(BitVector.parse("1010"), new BitVector(bits));
    }

    @Test public void testGetSet() {
        BitVector vec = new BitVector(10);

        for (int index = 0; index < vec.length(); index++)
            assertFalse(vec.get(index));

        vec.flip(2);
        assertTrue(vec.get(2));
        assertDouble(1.0, vec.getDouble(2));

        vec.flip(2);
        assertFalse(vec.get(2));
        assertDouble(0.0, vec.getDouble(2));

        vec.set(3);
        assertTrue(vec.get(3));
        assertDouble(1.0, vec.getDouble(3));

        vec.set(3, false);
        assertFalse(vec.get(3));
        assertDouble(0.0, vec.getDouble(3));
    }

    @Test public void testFlip() {
        BitVector v1 = new BitVector(true, false, false, true);
        BitVector v2 = new BitVector(false, true, true, false);

        v1.flip();
        assertEquals(v1, v2);
    }

    @Test public void testCopy() {
        BitVector v1 = BitVector.parse("0101");
        BitVector v2 = v1.copy();

        v2.flip(0);
        v2.flip(1);
        v2.flip(2);
        v2.flip(3);

        assertEquals(BitVector.parse("0101"), v1); // Ensure original is unchanged
        assertEquals(BitVector.parse("1010"), v2);
    }

    @Test public void testConcat() {
        BitVector v1 = BitVector.parse("1");
        BitVector v2 = BitVector.parse("00");
        BitVector v3 = BitVector.parse("111");
        BitVector v4 = BitVector.parse("0000");
        BitVector v5 = BitVector.parse("11111");

        assertEquals(BitVector.parse("100111000011111"), BitVector.concat(v1, v2, v3, v4, v5));
        assertEquals(BitVector.parse("100111000011111"), BitVector.concat(Arrays.asList(v1, v2, v3, v4, v5)));
    }

    @Test public void testCardinality() {
        BitVector vec = new BitVector(10);
        assertEquals(0, vec.cardinality());

        vec.flip(0);
        assertEquals(1, vec.cardinality());

        vec.flip(3);
        assertEquals(2, vec.cardinality());

        vec.flip(8);
        assertEquals(3, vec.cardinality());

        vec.flip(9);
        assertEquals(4, vec.cardinality());
    }

    @Test public void testAnd() {
        BitVector v1 = BitVector.parse("0101");
        BitVector v2 = BitVector.parse("0011");
        BitVector v3 = v1.and(v2);

        assertEquals(BitVector.parse("0101"), v1); // Ensure operands are unchanged
        assertEquals(BitVector.parse("0011"), v2); // Ensure operands are unchanged
        assertEquals(BitVector.parse("0001"), v3);

        BitVector v4 = BitVector.random(100, random());
        BitVector v5 = BitVector.random(100, random());

        assertEquals(v4.and(v5), v5.and(v4));
    }

    @Test public void testFit() {
        String s1 = "0000011111";
        String s2 = "0101010101";

        BitVector v1 = BitVector.parse(s1);
        BitVector v2 = BitVector.parse(s2);

        assertEquals(1.0, v1.fit(v1), 1.0e-12);
        assertEquals(0.6, v1.fit(v2), 1.0e-12);
        assertEquals(0.6, v2.fit(v1), 1.0e-12);

        BitVector v3 = BitVector.random(100, random());
        BitVector v4 = v3.not();

        assertEquals(1.0, v3.fit(v3), 1.0e-12);
        assertEquals(0.0, v3.fit(v4), 1.0e-12);
    }

    @Test public void testHamming() {
        String s1 = "0000011111";
        String s2 = "0101010101";

        BitVector v1 = BitVector.parse(s1);
        BitVector v2 = BitVector.parse(s2);

        assertEquals(4, v1.hamming(v2));
        assertEquals(4, v2.hamming(v1));

        assertEquals(BitVector.parse(s1), v1); // Ensure operands are unchanged
        assertEquals(BitVector.parse(s2), v2); // Ensure operands are unchanged

        v1 = BitVector.random(100, random());
        v2 = v1.copy();

        for (int index = 0; index < v1.length(); index++) {
            assertEquals(index, v1.hamming(v2));
            v1.flip(index);
        }

        BitVector v3 = BitVector.random(100, random());
        BitVector v4 = BitVector.random(100, random());

        assertEquals(v3.hamming(v4), v4.hamming(v3));
    }

    @Test public void testNot() {
        BitVector v1 = BitVector.parse("0101");
        BitVector v2 = v1.not();

        assertEquals(BitVector.parse("0101"), v1); // Ensure original is unchanged
        assertEquals(BitVector.parse("1010"), v2);

        BitVector v3 = BitVector.random(100, random());
        assertEquals(v3, v3.not().not());
    }

    @Test public void testOr() {
        BitVector v1 = BitVector.parse("0101");
        BitVector v2 = BitVector.parse("0011");
        BitVector v3 = v1.or(v2);

        assertEquals(BitVector.parse("0101"), v1); // Ensure operands are unchanged
        assertEquals(BitVector.parse("0011"), v2); // Ensure operands are unchanged
        assertEquals(BitVector.parse("0111"), v3);

        BitVector v4 = BitVector.random(100, random());
        BitVector v5 = BitVector.random(100, random());

        assertEquals(v4.or(v5), v5.or(v4));
    }

    @Test public void testSubvector() {
        BitVector v = BitVector.parse("0000011111");

        assertEquals(BitVector.parse("0"), v.subvector(4, 5));
        assertEquals(BitVector.parse("01"), v.subvector(4, 6));
        assertEquals(BitVector.parse("011"), v.subvector(4, 7));
        assertEquals(v, v.subvector(0, 10));
    }

    @Test public void testXor() {
        BitVector v1 = BitVector.parse("0101");
        BitVector v2 = BitVector.parse("0011");
        BitVector v3 = v1.xor(v2);

        assertEquals(BitVector.parse("0101"), v1); // Ensure operands are unchanged
        assertEquals(BitVector.parse("0011"), v2); // Ensure operands are unchanged
        assertEquals(BitVector.parse("0110"), v3);

        BitVector v4 = BitVector.random(100, random());
        BitVector v5 = BitVector.random(100, random());

        assertEquals(v4.xor(v5), v5.xor(v4));
    }

    @Test public void testEquals() {
        BitVector v1 = new BitVector(10);
        BitVector v2 = new BitVector(10);
        BitVector v3 = new BitVector(10);
        BitVector v4 = new BitVector(20);

        v1.set(1);
        v2.set(1);
        v4.set(1);

        v1.set(8);
        v2.set(8);
        v4.set(8);

        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
        assertFalse(v1.equals(v4)); // Different length
    }

    @Test public void testFormat() {
        BitVector vec = new BitVector(4);
        assertEquals("0000", vec.format());

        vec.flip(2);
        vec.flip(3);
        assertEquals("0011", vec.format());
    }

    @Test public void testParse() {
        BitVector vec = BitVector.parse("0011");
        assertEquals(4, vec.length());

        assertFalse(vec.get(0));
        assertFalse(vec.get(1));

        assertTrue(vec.get(2));
        assertTrue(vec.get(3));
    }

    @Test public void testToLogical() {
        BitVector vector = new BitVector(10);

        vector.set(3);
        vector.set(7);
        vector.set(9);

        boolean[] array = vector.toLogical();
        assertEquals(10, array.length);

        assertFalse(array[0]);
        assertFalse(array[1]);
        assertFalse(array[2]);
        assertFalse(array[4]);
        assertFalse(array[5]);
        assertFalse(array[6]);
        assertFalse(array[8]);

        assertTrue(array[3]);
        assertTrue(array[7]);
        assertTrue(array[9]);
    }

    @Test public void testToNumeric() {
        BitVector bits = BitVector.parse("01001");
        double[]  dbls = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0 };

        assertTrue(Arrays.equals(dbls, bits.toNumeric()));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidBitSetConstructor() {
        BitSet bits = new BitSet();
        bits.set(10);

        new BitVector(6, bits);
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
        FIXED10.set(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSet2() {
        FIXED10.set(FIXED10.length());
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidLength() {
        new BitVector(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidParse() {
        BitVector.parse("0011a");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidAnd() {
        FIXED10.and(FIXED20);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidHamming() {
        FIXED10.hamming(FIXED20);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidOr() {
        FIXED10.or(FIXED20);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidXor() {
        FIXED10.xor(FIXED20);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSubvector1() {
        FIXED10.subvector(-1, 5);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSubvector2() {
        FIXED10.subvector(1, 55);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSubvector3() {
        FIXED10.subvector(7, 2);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.vector.BitVectorTest");
    }
}
