
package jam.junit;

import jam.tumor.GrowthRate;
import jam.tumor.PerfectCell;
import jam.tumor.TumorEnv;

import org.junit.*;
import static org.junit.Assert.*;

public class TumorEnvTest extends NumericTestBase {
    private static final GrowthRate rate3065 = new GrowthRate(0.30, 0.65);
    private static final GrowthRate rate4060 = new GrowthRate(0.40, 0.60);
    private static final GrowthRate rate5545 = new GrowthRate(0.55, 0.45);
    private static final GrowthRate rate7020 = new GrowthRate(0.70, 0.20);

    @Test public void testNoBirth() {
        TumorEnv env = TumorEnv.unrestricted(PerfectCell.founder()).noBirth();

        assertFalse(env.allowCellDivision());
        assertFalse(env.allowDemeDivision());

        assertRate(0.0, 0.65, env.adjustGrowthRate(rate3065));
        assertRate(0.0, 0.60, env.adjustGrowthRate(rate4060));
        assertRate(0.0, 0.45, env.adjustGrowthRate(rate5545));
        assertRate(0.0, 0.20, env.adjustGrowthRate(rate7020));
    }

    private void assertRate(double expectedBirth, double expectedDeath, GrowthRate actualRate) {
        assertDouble(expectedBirth, actualRate.getBirthRate().doubleValue());
        assertDouble(expectedDeath, actualRate.getDeathRate().doubleValue());
    }

    @Test public void testNoDivision() {
        TumorEnv env0 = TumorEnv.unrestricted(PerfectCell.founder());
        TumorEnv env1 = env0.noDemeDivision();

        assertTrue(env0.allowDemeDivision());
        assertFalse(env1.allowDemeDivision());
    }

    @Test public void testNoGrowth() {
        TumorEnv env0 = TumorEnv.unrestricted(PerfectCell.founder());
        TumorEnv env1 = env0.noGrowth();
        TumorEnv env2 = env1.noGrowth();

        assertRate(0.30, 0.65, env1.adjustGrowthRate(rate3065));
        assertRate(0.40, 0.60, env1.adjustGrowthRate(rate4060));
        assertRate(0.50, 0.50, env1.adjustGrowthRate(rate5545));
        assertRate(0.45, 0.45, env1.adjustGrowthRate(rate7020));

        // Adding another no-growth environment should not have any
        // additional effect...
        assertRate(0.30, 0.65, env2.adjustGrowthRate(rate3065));
        assertRate(0.40, 0.60, env2.adjustGrowthRate(rate4060));
        assertRate(0.50, 0.50, env2.adjustGrowthRate(rate5545));
        assertRate(0.45, 0.45, env2.adjustGrowthRate(rate7020));
    }

    @Test public void testSlowGrowth() {
        double fac1 = 0.8;
        double fac2 = 0.7;
        double fac3 = 0.6;

        TumorEnv env0 = TumorEnv.unrestricted(PerfectCell.founder());
        TumorEnv env1 = env0.slowGrowth(fac1);
        TumorEnv env2 = env1.slowGrowth(fac2);
        TumorEnv env3 = env2.slowGrowth(fac3);

        assertTrue(env0.allowCellDivision());
        assertTrue(env1.allowCellDivision());
        assertTrue(env2.allowCellDivision());
        assertTrue(env3.allowCellDivision());

        assertTrue(env0.allowDemeDivision());
        assertTrue(env1.allowDemeDivision());
        assertTrue(env2.allowDemeDivision());
        assertTrue(env3.allowDemeDivision());

        GrowthRate R0 = rate5545;
        GrowthRate R1 = env1.adjustGrowthRate(R0);
        GrowthRate R2 = env2.adjustGrowthRate(R0);
        GrowthRate R3 = env3.adjustGrowthRate(R0);

        assertDouble(fac1               * R0.getGrowthFactor(), R1.getGrowthFactor());
        assertDouble(fac1 * fac2        * R0.getGrowthFactor(), R2.getGrowthFactor());
        assertDouble(fac1 * fac2 * fac3 * R0.getGrowthFactor(), R3.getGrowthFactor());
    }

    @Test public void testUnrestricted() {
        TumorEnv env = TumorEnv.unrestricted(PerfectCell.founder());

        assertTrue(env.allowCellDivision());
        assertTrue(env.allowDemeDivision());

        assertEquals(rate5545, env.adjustGrowthRate(rate5545));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TumorEnvTest");
    }
}
