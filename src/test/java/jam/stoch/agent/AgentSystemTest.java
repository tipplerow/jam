
package jam.stoch.agent;

import jam.stoch.StochEvent;
import jam.stoch.StochTime;

import org.junit.*;
import static org.junit.Assert.*;

public class AgentSystemTest {
    private static void assertState(TestSystem system,
                                    int eventCount, StochTime eventTime,
                                    int popA, int popB, int popC, int popD) {
        assertEquals(eventCount, system.countEvents());
        assertEquals(eventTime, system.lastEventTime());

        assertEquals(popA, system.countAgent(TestAgent.A));
        assertEquals(popB, system.countAgent(TestAgent.B));
        assertEquals(popC, system.countAgent(TestAgent.C));
        assertEquals(popD, system.countAgent(TestAgent.D));
    }

    @Test public void testCreate() {
        TestSystem system = TestSystem.create();

        assertState(system,
                    0, StochTime.ZERO,
                    TestSystem.INIT_POP_A, TestSystem.INIT_POP_B, TestSystem.INIT_POP_C, 0);
    }

    @Test public void testUpdateState() {
        TestSystem system = TestSystem.create();

        StochTime time1 = StochTime.valueOf(0.1);
        StochTime time2 = StochTime.valueOf(0.3);
        StochTime time3 = StochTime.valueOf(0.9);

        StochEvent event1 = StochEvent.mark(TestSystem.TRANS_PROC, time1);
        StochEvent event2 = StochEvent.mark(TestSystem.DEATH_PROC, time2);
        StochEvent event3 = StochEvent.mark(TestSystem.BIRTH_PROC, time3);

        int initA = TestSystem.INIT_POP_A;
        int initB = TestSystem.INIT_POP_B;
        int initC = TestSystem.INIT_POP_C;
        int initD = 0;

        system.updateState(event1);
        assertState(system, 1, time1, initA, initB, initC - 1, initD + 1);

        system.updateState(event2);
        assertState(system, 2, time2, initA, initB - 1, initC - 1, initD + 1);

        system.updateState(event3);
        assertState(system, 3, time3, initA + 1, initB - 1, initC - 1, initD + 1);
    }
}
