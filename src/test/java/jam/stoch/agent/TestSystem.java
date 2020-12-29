
package jam.stoch.agent;

import java.util.List;

public final class TestSystem extends AgentSystem<TestAgent, AgentProc<TestAgent>> {
    private TestSystem() {
        super(listAgents(), initialPopulation(), listProcesses(), List.of());
    }
        
    public static final int INIT_POP_A = 1000;
    public static final int INIT_POP_B = 2000;
    public static final int INIT_POP_C = 3000;

    public static final double A_BIRTH_RATE = 1.0;
    public static final double B_DEATH_RATE = 2.0;
    public static final double C_TRANS_RATE = 3.0;

    public static final BirthProc<TestAgent> BIRTH_PROC =
        FixedRateBirthProc.create(TestAgent.A, A_BIRTH_RATE);

    public static final DeathProc<TestAgent> DEATH_PROC =
        FixedRateDeathProc.create(TestAgent.B, B_DEATH_RATE);

    public static final TransitionProc<TestAgent> TRANS_PROC =
        FixedRateTransitionProc.create(TestAgent.C, TestAgent.D, C_TRANS_RATE);

    public static List<TestAgent> listAgents() {
        return List.of(TestAgent.A, TestAgent.B, TestAgent.C, TestAgent.D);
    }

    public static AgentPopulation<TestAgent> initialPopulation() {
        AgentPopulation<TestAgent> population = AgentPopulation.create();

        population.set(TestAgent.A, INIT_POP_A);
        population.set(TestAgent.B, INIT_POP_B);
        population.set(TestAgent.C, INIT_POP_C);

        return population;
    }

    public static List<AgentProc<TestAgent>> listProcesses() {
        return List.of(BIRTH_PROC, DEATH_PROC, TRANS_PROC);
    }

    public static TestSystem create() {
        return new TestSystem();
    }
}
