
package jam.junit;

import jam.dist.DiracDeltaDistribution;
import jam.dist.LogNormalDistribution;
import jam.dist.LogUniformDistribution;
import jam.dist.NormalDistribution;
import jam.dist.RealDistribution;
import jam.dist.RealDistributionType;
import jam.dist.UniformRealDistribution;

import org.junit.*;
import static org.junit.Assert.*;

public class RealDistributionTypeTest extends NumericTestBase {

    @Test public void testDiracDelta() {
        DiracDeltaDistribution dist1 = new DiracDeltaDistribution(1.0);
        DiracDeltaDistribution dist2 = (DiracDeltaDistribution) RealDistributionType.parse("DIRAC_DELTA; 1.0");

        assertDouble(dist1.getImpulse(), dist2.getImpulse());
    }

    @Test public void testLogNormal() {
        LogNormalDistribution dist1 = new LogNormalDistribution(-1.0, 3.0);
        LogNormalDistribution dist2 = (LogNormalDistribution) RealDistributionType.parse("LOG_NORMAL; -1.0, 3.0");

        assertDouble(dist1.getMeanLog(),  dist2.getMeanLog());
        assertDouble(dist1.getStDevLog(), dist2.getStDevLog());
    }

    @Test public void testLogUniform() {
        LogUniformDistribution dist1 = new LogUniformDistribution(-1.0, 3.0);
        LogUniformDistribution dist2 = (LogUniformDistribution) RealDistributionType.parse("LOG_UNIFORM; -1.0, 3.0");

        assertDouble(dist1.getLowerLog(), dist2.getLowerLog());
        assertDouble(dist1.getUpperLog(), dist2.getUpperLog());
    }

    @Test public void testNormal() {
        NormalDistribution dist1 = new NormalDistribution(-1.0, 3.0);
        NormalDistribution dist2 = (NormalDistribution) RealDistributionType.parse("NORMAL; -1.0, 3.0");

        assertDouble(dist1.mean(),  dist2.mean());
        assertDouble(dist1.stdev(), dist2.stdev());
    }

    @Test public void testUniform() {
        UniformRealDistribution dist1 = new UniformRealDistribution(-1.0, 3.0);
        UniformRealDistribution dist2 = (UniformRealDistribution) RealDistributionType.parse("UNIFORM; -1.0, 3.0");

        assertDouble(dist1.getLower(), dist2.getLower());
        assertDouble(dist1.getUpper(), dist2.getUpper());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.RealDistributionTypeTest");
    }
}
