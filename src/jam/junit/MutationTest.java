
package jam.junit;

import java.util.Arrays;

import jam.tumor.GrowthRate;
import jam.tumor.Mutation;

import org.junit.*;
import static org.junit.Assert.*;

public class MutationTest extends NumericTestBase {
    private static final Mutation neutral1 = Mutation.neutral(10);
    private static final Mutation neutral2 = Mutation.neutral(11);
    private static final Mutation neutral3 = Mutation.neutral(12);

    @Test public void testCreationTime() {
        assertEquals(10, neutral1.getCreationTime());
        assertEquals(11, neutral2.getCreationTime());
        assertEquals(12, neutral3.getCreationTime());
    }

    @Test public void testInstanceCount() {
        assertEquals(0, Mutation.TRANSFORMER.getIndex());
        assertEquals(1, neutral1.getIndex());
        assertEquals(2, neutral2.getIndex());
        assertEquals(3, neutral3.getIndex());
    }

    @Test public void testNeutral() {
        GrowthRate r1 = new GrowthRate(0.55, 0.45);
        GrowthRate r2 = neutral1.mutate(r1);
        GrowthRate r3 = Mutation.mutate(Arrays.asList(neutral1, neutral2, neutral3), r1);

        assertEquals(r1, r2);
        assertEquals(r1, r3);

        assertTrue(neutral1.isNeutral());
        assertFalse(neutral1.isSelective());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MutationTest");
    }
}
