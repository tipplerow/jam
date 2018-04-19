
package jam.junit;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.lattice.Coord;
import jam.math.JamRandom;
import jam.math.RandomWalk;
import jam.math.StatUtil;
import jam.math.VectorMoment;
import jam.matrix.JamMatrix;
import jam.vector.JamVector;

import org.junit.*;
import static org.junit.Assert.*;

public class VectorMomentTest extends NumericTestBase {
    private static double TOLERANCE = 1.0e-12;

    public VectorMomentTest() {
	super(TOLERANCE);
    }

    @Test public void testCoordSmall() {
        Multiset<Coord> coords = HashMultiset.create();

        coords.add(Coord.at(1, 10, 100), 1);
        coords.add(Coord.at(2, 20, 200), 2);
        coords.add(Coord.at(3, 30, 300), 3);

        Collection<JamVector> vectors = new ArrayList<JamVector>();

        for (Coord coord : coords)
            vectors.add(coord.toVector());

        assertEquals(6, vectors.size());

        VectorMoment mom1 = VectorMoment.compute(coords);
        VectorMoment mom2 = VectorMoment.compute(vectors);

        JamVector CM = JamVector.valueOf((  1.0 + 2 *   2.0 + 3 *   3.0) / 6.0,
                                         ( 10.0 + 2 *  20.0 + 3 *  30.0) / 6.0,
                                         (100.0 + 2 * 200.0 + 3 * 300.0) / 6.0);

        assertEquals(CM, mom1.getCM());
        assertEquals(mom2.getCM(), mom1.getCM());
        assertEquals(mom2.getRG(), mom1.getRG());
    }

    @Test public void testOneCoord() {
        Coord coord = Coord.at(1, 2, 3);

        Multiset<Coord> coords = HashMultiset.create();
        coords.add(coord);

        VectorMoment mom = VectorMoment.compute(coords);

        assertEquals(coord.toVector(), mom.getCM());
        assertEquals(JamMatrix.zeros(3, 3), mom.getRG());

        coords.add(coord, 10);

        mom = VectorMoment.compute(coords);

        assertEquals(coord.toVector(), mom.getCM());
        assertEquals(JamMatrix.zeros(3, 3), mom.getRG());
    }

    @Test public void testOneVector() {
        JamVector    vec = JamVector.valueOf(1.0, 2.0, 3.0, 4.0);
        VectorMoment mom = VectorMoment.compute(vec);

        assertEquals(vec, mom.getCM());
        assertEquals(JamMatrix.zeros(4, 4), mom.getRG());
    }

    @Test public void testSmall() {
        JamVector v1 = JamVector.valueOf(1.0, 2.0, 3.0, 4.0);
        JamVector v2 = JamVector.valueOf(2.0, 4.0, 6.0, 8.0);
        JamVector CM = JamVector.valueOf(1.5, 3.0, 4.5, 6.0);
        JamMatrix RG = JamMatrix.byrow(4, 4,
                                       0.25, 0.50, 0.75, 1.00,
                                       0.50, 1.00, 1.50, 2.00,
                                       0.75, 1.50, 2.25, 3.00,
                                       1.00, 2.00, 3.00, 4.00);

        VectorMoment mom1 = VectorMoment.compute(v1);
        VectorMoment mom2 = VectorMoment.compute(v1, v2);

        assertTrue(mom1.getCM().equalsVector(v1));
        assertTrue(mom2.getCM().equalsVector(CM));

        assertTrue(mom1.getRG().equalsMatrix(JamMatrix.zeros(4, 4)));
        assertTrue(mom2.getRG().equalsMatrix(RG));
    }

    @Test public void testRandomNormal() {
        int ndim = 3;
        int nvec = 1000000;
        double stdev = 2.0;
        JamVector[] vectors = new JamVector[nvec];

        for (int ivec = 0; ivec < nvec; ivec++) {
	    JamVector vector = new JamVector(ndim);

	    for (int jdim = 0; jdim < ndim; jdim++)
		vector.set(jdim, random().nextGaussian(0.0, stdev));

	    vectors[ivec] = vector;
	}

        VectorMoment mom = VectorMoment.compute(vectors);
        JamVector CM = JamVector.zeros(ndim);
        JamMatrix RG = JamMatrix.identity(ndim).times(stdev * stdev);

        assertTrue(mom.getCM().equalsVector(CM, 0.01));
        assertTrue(mom.getRG().equalsMatrix(RG, 0.02));

        assertTrue(mom.asphericity() > 0.0);
        assertTrue(mom.asphericity() < 0.02);

        assertTrue(mom.acylindricity() > 0.0);
        assertTrue(mom.acylindricity() < 0.02);

        assertTrue(mom.anisotropy() > 0.0);
        assertTrue(mom.anisotropy() < 0.0001);

        assertEquals(Math.sqrt(12.0), mom.scalar(), 0.01);

        JamVector normR2 = new JamVector(nvec);

        for (int k = 0; k < nvec; ++k)
            normR2.set(k, Math.pow(mom.normR(vectors[k]), 2));

        assertDouble(1.0, StatUtil.mean(normR2));
    }

    @Test public void testRandomWalk() {
        for (int trial = 0; trial < 5; ++trial) {
            int N = 10000;
            VectorMoment mom = VectorMoment.compute(RandomWalk.ideal(N));

            double asphericity   = mom.asphericity() / N;
            double acylindricity = mom.acylindricity() / N;
            double anisotropy    = mom.anisotropy() / N;

            assertTrue(asphericity > 0.0);
            assertTrue(asphericity < 0.5);

            assertTrue(acylindricity > 0.0);
            assertTrue(acylindricity < 0.05);

            assertTrue(anisotropy > 0.0);
            assertTrue(anisotropy < 0.001);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoVectors() {
        VectorMoment.compute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDimensionMismatch() {
        VectorMoment.compute(JamVector.zeros(3), JamVector.zeros(4));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.VectorMomentTest");
    }
}
