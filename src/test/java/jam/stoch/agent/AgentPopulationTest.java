
package jam.stoch.agent;

import java.util.List;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import org.junit.*;
import static org.junit.Assert.*;

public class AgentPopulationTest {
    private static void assertPopulation(AgentPopulation population, int popA, int popB, int popC) {
        assertEquals(popA, population.count(TestAgent.A));
        assertEquals(popB, population.count(TestAgent.B));
        assertEquals(popC, population.count(TestAgent.C));
    }

    private static AgentPopulation createPopulation(int popA, int popB, int popC) {
        AgentPopulation population = AgentPopulation.create();

        population.add(TestAgent.A, popA);
        population.add(TestAgent.B, popB);
        population.add(TestAgent.C, popC);

        return population;
    }

    @Test public void testAddCount() {
        AgentPopulation population = AgentPopulation.create();

        population.add(TestAgent.A, 5);
        population.add(TestAgent.B, 2);
        population.add(TestAgent.C, 0);

        assertPopulation(population, 5, 2, 0);
    }

    @Test(expected = RuntimeException.class)
    public void testAddCountInvalid() {
        AgentPopulation population = AgentPopulation.create();
        population.add(TestAgent.A, -2);
    }

    @Test public void testAddSingle() {
        AgentPopulation population = AgentPopulation.create();

        population.add(TestAgent.A);
        population.add(TestAgent.A);
        population.add(TestAgent.B);

        assertPopulation(population, 2, 1, 0);
    }

    @Test public void testAddCollection() {
        AgentPopulation population = AgentPopulation.create();

        population.add(List.of(TestAgent.A,
                               TestAgent.A,
                               TestAgent.B));

        assertPopulation(population, 2, 1, 0);
    }

    @Test public void testAddMultiset() {
        AgentPopulation population = AgentPopulation.create();

        population.add(ImmutableMultiset.of(TestAgent.A,
                                            TestAgent.A,
                                            TestAgent.A,
                                            TestAgent.C));

        assertPopulation(population, 3, 0, 1);
    }

    @Test public void testCreateEmpty() {
        AgentPopulation population = AgentPopulation.create();
        assertPopulation(population, 0, 0, 0);
    }

    @Test public void testCreateCollection() {
        AgentPopulation population =
            AgentPopulation.create(List.of(TestAgent.B,
                                           TestAgent.B,
                                           TestAgent.C));

        assertPopulation(population, 0, 2, 1);
    }

    @Test public void testCreateMultiset() {
        AgentPopulation population =
            AgentPopulation.create(ImmutableMultiset.of(TestAgent.B,
                                                        TestAgent.C,
                                                        TestAgent.C));
        assertPopulation(population, 0, 1, 2);
    }

    @Test public void testRemoveCount() {
        AgentPopulation population = createPopulation(3, 5, 10);

        population.remove(TestAgent.B, 3);
        population.remove(TestAgent.C, 2);

        assertPopulation(population, 3, 2, 8);
    }

    @Test public void testRemoveSingle() {
        AgentPopulation population = createPopulation(2, 5, 10);

        population.remove(TestAgent.B);
        population.remove(TestAgent.C);
        population.remove(TestAgent.C);
        population.remove(TestAgent.C);

        assertPopulation(population, 2, 4, 7);
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveInvalid1() {
        AgentPopulation population = createPopulation(0, 5, 10);
        population.remove(TestAgent.A);
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveInvalid2() {
        AgentPopulation population = createPopulation(0, 5, 10);
        population.remove(TestAgent.B, 8);
    }

    @Test public void testSet() {
        AgentPopulation population = createPopulation(3, 5, 10);

        population.set(TestAgent.A, 0);
        population.set(TestAgent.B, 9);
        population.set(TestAgent.C, 6);

        assertPopulation(population, 0, 9, 6);
    }

    @Test(expected = RuntimeException.class)
    public void testSetInvalid() {
        AgentPopulation population = AgentPopulation.create();
        population.set(TestAgent.A, -2);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.AgentPopulationTest");
    }
}
