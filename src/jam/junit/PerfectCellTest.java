
package jam.junit;

import java.util.ArrayList;
import java.util.List;

import jam.tumor.PerfectCell;

import org.junit.*;
import static org.junit.Assert.*;

public class PerfectCellTest {
    private static final PerfectCell FIRST = PerfectCell.founder();

    @Test public void testFirst() {
        assertEquals(0, FIRST.getIndex());
        assertEquals(null, FIRST.getParent());
        assertTrue(FIRST.isFounder());
    }

    @Test public void testAdvance() {
        List<PerfectCell> step1 = FIRST.advance();

        assertTrue(FIRST.isDead());
        assertEquals(2, step1.size());

        assertEquals(1, step1.get(0).getIndex());
        assertEquals(2, step1.get(1).getIndex());

        assertEquals(1, step1.get(0).getGeneration());
        assertEquals(1, step1.get(1).getGeneration());

        assertEquals(FIRST, step1.get(0).getParent());
        assertEquals(FIRST, step1.get(1).getParent());

        List<PerfectCell> step2 = new ArrayList<PerfectCell>();
        step2.addAll(step1.get(0).advance());
        step2.addAll(step1.get(1).advance());

        assertTrue(step1.get(0).isDead());
        assertTrue(step1.get(1).isDead());
        assertEquals(4, step2.size());

        assertEquals(3, step2.get(0).getIndex());
        assertEquals(4, step2.get(1).getIndex());
        assertEquals(5, step2.get(2).getIndex());
        assertEquals(6, step2.get(3).getIndex());

        assertEquals(2, step2.get(0).getGeneration());
        assertEquals(2, step2.get(1).getGeneration());
        assertEquals(2, step2.get(2).getGeneration());
        assertEquals(2, step2.get(3).getGeneration());

        assertEquals(step1.get(0), step2.get(0).getParent());
        assertEquals(step1.get(0), step2.get(1).getParent());
        assertEquals(step1.get(1), step2.get(2).getParent());
        assertEquals(step1.get(1), step2.get(3).getParent());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PerfectCellTest");
    }
}
